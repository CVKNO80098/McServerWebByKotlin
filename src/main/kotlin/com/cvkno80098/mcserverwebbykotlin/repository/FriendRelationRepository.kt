package com.cvkno80098.mcserverwebbykotlin.repository

import com.cvkno80098.mcserverwebbykotlin.entity.FriendRelation
import org.springframework.data.jpa.repository.JpaRepository

/** 好友关系数据访问层；一条记录的两端用户 ID 始终按大小排序保存。 */
interface FriendRelationRepository: JpaRepository<FriendRelation, Long> {
    fun findByUserAIdOrUserBId(userAId: Long, userBId: Long): List<FriendRelation>

    fun findByUserAIdAndUserBId(userAId: Long, userBId: Long): FriendRelation?
}
