package com.cvkno80098.mcserverwebbykotlin.dto

import com.cvkno80098.mcserverwebbykotlin.entity.UserAccount
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:NotBlank @field:Size(max = 50) val username: String,
    @field:NotBlank @field:Size(max = 100) val displayName: String,
    @field:NotBlank @field:Size(min = 6, max = 72) val password: String,
)

data class LoginRequest(
    @field:NotBlank val username: String,
    @field:NotBlank val password: String
)

data class AuthResponse(
    val token: String,
    val user: UserResponse
)

data class UserResponse(
    val id: Long,
    val username: String,
    val displayName: String,
    val avatarUrl: String?,
)
