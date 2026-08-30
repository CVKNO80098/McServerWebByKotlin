package com.cvkno80098.mcserverwebbykotlin.entity

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Minecraft 服务器条目，对应数据库表 `server`。
 *
 * - [mods] 说明联机方式（若为内网穿透则为Null）
 * - [host] / [port]：用于 MCPing 探测在线状态
 * - [code]：用于小蓝盾等第三方内网联机码
 * - [active]：是否在列表中展示
 * - [serverFlag]：预留标记位，可用于区分 Java/基岩版等
 * - [ownerId]：创建者用户 ID，外键关联 `user_account.id`
 */
@Entity
@Table(
    name = "server",
    indexes = [
        Index(name = "idx_server_owner_active", columnList = "owner_id, active")
    ]
)
class Server {

    /** 主键；使用 AUTO 策略以兼容已有 MySQL 表（server_seq 序列表） */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null

    /** 服务器显示名称 */
    @Column(nullable = false)
    var name: String = ""

    /** 简介，可为空 */
    var description: String? = null

    /** 联机选项 **/
    var mods: String? = null

    /** 游戏服务器地址（域名或 IP） */
    @Column(nullable = false)
    var host: String = ""

    /** 游戏服务器端口，Java 版默认 25565 */
    @Column(nullable = false)
    var port: Int = 25565

    /** 用于小蓝盾等第三方内网联机码 */
    var code: String? = null

    /** 是否在列表中启用 */
    @Column(nullable = false)
    var active: Boolean = true

    /** 预留扩展标记 */
    @Column(nullable = false)
    var serverFlag: Boolean = false

    // ========== 新增字段 ==========

    /** 创建者用户 ID，外键关联 user_account.id，不可为空 */
    @Column(name = "owner_id", nullable = false)
    var ownerId: Long = 0

    /** 创建时间，不可更新 */
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    /** 更新时间，每次更新时自动维护 */
    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()

    // ========== 生命周期回调 ==========

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