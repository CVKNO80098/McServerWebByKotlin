package com.cvkno80098.mcserverwebbykotlin.controller

import com.cvkno80098.mcserverwebbykotlin.dto.AuthResponse
import com.cvkno80098.mcserverwebbykotlin.dto.LoginRequest
import com.cvkno80098.mcserverwebbykotlin.dto.RegisterRequest
import com.cvkno80098.mcserverwebbykotlin.dto.UserResponse
import com.cvkno80098.mcserverwebbykotlin.security.JwtService
import com.cvkno80098.mcserverwebbykotlin.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/** 注册、登录、登出和当前用户接口。 */
@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
    private val jwtService: JwtService,
) {
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody request: RegisterRequest): AuthResponse = authService.register(request)

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): AuthResponse = authService.login(request)

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logout(@RequestHeader("Authorization") authorization: String) {
        // 安全过滤器已验证令牌；此处将它加入注销列表以便立即失效。
        jwtService.revoke(authorization.substringAfter(' ').trim())
    }

    @GetMapping("/me")
    fun me(authentication: Authentication): UserResponse = authService.toResponse(
        authService.currentUser(authentication.principal as Long)
    )
}
