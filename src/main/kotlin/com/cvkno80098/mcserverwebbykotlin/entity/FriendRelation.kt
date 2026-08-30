package com.cvkno80098.mcserverwebbykotlin.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

@Entity
@Table(
    name = "friend_relation",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_friend_pair",
            columnNames = ["user_a_id", "user_b_id"]
        )
    ],
    indexes = [
        Index(name = "idx_friend_user_a", columnList = "user_a_id"),
        Index(name = "idx_friend_user_b", columnList = "user_b_id")
    ]
)
class FriendRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null

    @Column(name = "user_a_id", nullable = false)
    @NotNull(message = "用户A ID不能为空")
    var userAId: Long = 0

    @Column(name = "user_b_id", nullable = false)
    @NotNull(message = "用户B ID不能为空")
    var userBId: Long = 0

    @Column(name = "requester_id", nullable = false)
    @NotNull(message = "请求发起者ID不能为空")
    var requesterId: Long = 0

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "状态不能为空")
    var status: FriendStatus = FriendStatus.PENDING

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "accepted_at")
    var acceptedAt: LocalDateTime? = null

    // 自动维护时间
    @PrePersist
    fun prePersist() {
        createdAt = LocalDateTime.now()
        updatedAt = LocalDateTime.now()
    }

    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }
}

// 状态枚举
enum class FriendStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    BLOCKED
}