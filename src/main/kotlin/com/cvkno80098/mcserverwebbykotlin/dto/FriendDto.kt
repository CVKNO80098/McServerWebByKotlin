package com.cvkno80098.mcserverwebbykotlin.dto

import com.cvkno80098.mcserverwebbykotlin.entity.FriendStatus
import java.time.LocalDateTime

/** 当前用户视角下的一条好友关系，避免把密码哈希等内部字段暴露给前端。 */
data class FriendResponse(
    val id: Long,
    val user: UserResponse,
    val status: FriendStatus,
    val requesterId: Long,
    val createdAt: LocalDateTime,
    val acceptedAt: LocalDateTime?,
)
