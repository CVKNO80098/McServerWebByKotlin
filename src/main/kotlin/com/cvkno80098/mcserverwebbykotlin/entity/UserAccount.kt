package com.cvkno80098.mcserverwebbykotlin.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

import java.time.LocalDateTime

@Entity
@Table(name = "user_account",
    uniqueConstraints = [UniqueConstraint(columnNames = ["username"])]) // 额外确保唯一约束
class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null

    @Column(nullable = false, unique = true, length = 50)   // 数据库非空、唯一、长度限制
    @NotBlank(message = "用户名不能为空")                    // Bean Validation 非空校验
    @Size(max = 50, message = "用户名长度不能超过50")
    var username: String = ""

    @Column(nullable = false, length = 100)                // 数据库非空、长度限制
    @NotBlank(message = "显示名称不能为空")
    @Size(max = 100, message = "显示名称长度不能超过100")
    var displayName: String = ""

    @Column(nullable = false, length = 128)                // 数据库非空、长度限制（BCrypt 哈希长度通常 60）
    @NotBlank(message = "密码哈希不能为空")
    @Size(max = 128, message = "密码哈希长度不能超过128")
    var passwordHash: String = ""

    @Column(length = 512)                                  // 可空，但限制长度
    @Size(max = 512, message = "头像URL长度不能超过512")
    var avatarUrl: String? = null

    @Column(nullable = false, updatable = false)           // 创建时间不可更新，数据库非空
    var createdAt: LocalDateTime = LocalDateTime.now()

    @Column(nullable = false)                              // 更新时间可更新，数据库非空
    var updatedAt: LocalDateTime = LocalDateTime.now()

    @Column(nullable = false)                              // 数据库非空，默认 false
    var enabled: Boolean = true
}