package com.cvkno80098.mcserverwebbykotlin.controller

import com.cvkno80098.mcserverwebbykotlin.dto.UserResponse
import com.cvkno80098.mcserverwebbykotlin.repository.UserAccountRepository
import com.cvkno80098.mcserverwebbykotlin.service.AuthService
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/** 用户搜索接口；响应只暴露可公开展示的账户信息。 */
@RestController
@RequestMapping("/users")
class UserController(
    private val userRepository: UserAccountRepository,
    private val authService: AuthService,
) {
    @GetMapping
    fun search(
        @RequestParam(defaultValue = "") keyword: String,
        authentication: Authentication,
    ): List<UserResponse> {
        val currentUserId = authentication.principal as Long
        val normalized = keyword.trim()
        if (normalized.isBlank()) return emptyList()
        return userRepository.findByUsernameContainingIgnoreCaseOrDisplayNameContainingIgnoreCase(normalized, normalized)
            .asSequence()
            .filter { it.enabled && it.id != currentUserId }    // 不要查自己
            .map(authService::toResponse)
            .toList()
    }
}
