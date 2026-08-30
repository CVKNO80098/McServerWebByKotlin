package com.cvkno80098.mcserverwebbykotlin.service

import com.cvkno80098.mcserverwebbykotlin.dto.AuthResponse
import com.cvkno80098.mcserverwebbykotlin.dto.LoginRequest
import com.cvkno80098.mcserverwebbykotlin.dto.RegisterRequest
import com.cvkno80098.mcserverwebbykotlin.dto.UserResponse
import com.cvkno80098.mcserverwebbykotlin.entity.UserAccount
import com.cvkno80098.mcserverwebbykotlin.repository.UserAccountRepository
import com.cvkno80098.mcserverwebbykotlin.security.JwtService
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

/** 处理账户注册、登录，以及把实体转换为安全的响应 DTO。 */
@Service
class AuthService(
    private val userRepository: UserAccountRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
) {
    @Transactional
    fun register(request: RegisterRequest): AuthResponse {
        val username = request.username.trim()
        if (userRepository.existsByUsername(username)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "用户名已存在")
        }

        val user = UserAccount().apply {
            this.username = username
            displayName = request.displayName.trim()
            // Spring Security 的 Java 接口标注为可空；正常编码结果必须存在。
            passwordHash = requireNotNull(passwordEncoder.encode(request.password))
        }
        val saved = userRepository.save(user)
        return authenticatedResponse(saved)
    }

    fun login(request: LoginRequest): AuthResponse {

        val user = userRepository.findByUsername(request.username.trim())
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名或密码错误")
        if (!user.enabled || !passwordEncoder.matches(request.password, user.passwordHash)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名或密码错误")
        }
        return authenticatedResponse(user)
    }

    /** 根据安全上下文携带的 ID 查询当前启用中的用户。 */
    fun currentUser(userId: Long): UserAccount = userRepository.findById(userId)
        .filter { it.enabled }
        .orElseThrow { ResponseStatusException(HttpStatus.UNAUTHORIZED, "登录已失效") }

    fun toResponse(user: UserAccount): UserResponse = UserResponse(
        id = requireNotNull(user.id),
        username = user.username,
        displayName = user.displayName,
        avatarUrl = user.avatarUrl,
    )

    private fun authenticatedResponse(user: UserAccount): AuthResponse = AuthResponse(
        token = jwtService.createToken(requireNotNull(user.id)),
        user = toResponse(user),
    )
}
