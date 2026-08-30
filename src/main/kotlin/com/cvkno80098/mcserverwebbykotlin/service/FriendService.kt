package com.cvkno80098.mcserverwebbykotlin.service

import com.cvkno80098.mcserverwebbykotlin.dto.FriendResponse
import com.cvkno80098.mcserverwebbykotlin.entity.FriendRelation
import com.cvkno80098.mcserverwebbykotlin.entity.FriendStatus
import com.cvkno80098.mcserverwebbykotlin.repository.FriendRelationRepository
import com.cvkno80098.mcserverwebbykotlin.repository.UserAccountRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

/** 好友关系业务规则：请求只允许由接收者接受，双方任意一人可删除关系。 */
@Service
class FriendService(
    private val friendRepository: FriendRelationRepository,
    private val userRepository: UserAccountRepository,
    private val authService: AuthService,
) {
    @Transactional(readOnly = true)
    fun list(userId: Long): List<FriendResponse> = friendRepository.findByUserAIdOrUserBId(userId, userId)
        .sortedByDescending { it.updatedAt }
        .map { relation -> toResponse(relation, userId) }

    @Transactional
    fun request(requesterId: Long, targetId: Long): FriendResponse {
        if (requesterId == targetId) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "不能向自己发送好友请求")
        }
        val target = userRepository.findById(targetId)
            .filter { it.enabled }
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在") }
        val (userAId, userBId) = orderedPair(requesterId, targetId)
        if (friendRepository.findByUserAIdAndUserBId(userAId, userBId) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "好友关系或请求已存在")
        }
        val relation = FriendRelation().apply {
            this.userAId = userAId
            this.userBId = userBId
            this.requesterId = requesterId
            status = FriendStatus.PENDING
        }
        return toResponse(friendRepository.save(relation), requesterId, target)
    }

    @Transactional
    fun accept(currentUserId: Long, friendshipId: Long): FriendResponse {
        val relation = relation(friendshipId)
        if (relation.status != FriendStatus.PENDING) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "该好友请求无法接受")
        }
        if (relation.requesterId == currentUserId || !containsUser(relation, currentUserId)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "只有请求接收者可以接受好友请求")
        }
        relation.status = FriendStatus.ACCEPTED
        relation.acceptedAt = LocalDateTime.now()
        return toResponse(friendRepository.save(relation), currentUserId)
    }

    @Transactional
    fun delete(currentUserId: Long, friendshipId: Long) {
        val relation = relation(friendshipId)
        if (!containsUser(relation, currentUserId)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除该好友关系")
        }
        friendRepository.delete(relation)
    }

    private fun relation(id: Long): FriendRelation = friendRepository.findById(id)
        .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "好友关系不存在") }

    private fun toResponse(relation: FriendRelation, currentUserId: Long, knownOther: com.cvkno80098.mcserverwebbykotlin.entity.UserAccount? = null): FriendResponse {
        val otherUserId = if (relation.userAId == currentUserId) relation.userBId else relation.userAId
        val otherUser = knownOther ?: userRepository.findById(otherUserId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "好友用户不存在") }
        return FriendResponse(
            id = requireNotNull(relation.id),
            user = authService.toResponse(otherUser),
            status = relation.status,
            requesterId = relation.requesterId,
            createdAt = relation.createdAt,
            acceptedAt = relation.acceptedAt,
        )
    }

    private fun orderedPair(first: Long, second: Long): Pair<Long, Long> =
        if (first < second) first to second else second to first

    private fun containsUser(relation: FriendRelation, userId: Long): Boolean =
        relation.userAId == userId || relation.userBId == userId
}
