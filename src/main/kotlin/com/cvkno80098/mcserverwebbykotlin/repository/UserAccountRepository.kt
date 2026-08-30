package com.cvkno80098.mcserverwebbykotlin.repository

import com.cvkno80098.mcserverwebbykotlin.entity.UserAccount
import org.springframework.data.jpa.repository.JpaRepository

interface UserAccountRepository : JpaRepository<UserAccount, Long> {
    fun existsByUsername(username: String): Boolean
    fun findByUsername(username: String): UserAccount?
    fun findByUsernameContainingIgnoreCaseOrDisplayNameContainingIgnoreCase(
        username: String,
        displayName: String
    ): List<UserAccount>
}