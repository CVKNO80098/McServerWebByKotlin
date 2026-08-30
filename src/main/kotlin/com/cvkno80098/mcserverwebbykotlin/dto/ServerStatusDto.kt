package com.cvkno80098.mcserverwebbykotlin.dto

import com.cvkno80098.mcserverwebbykotlin.entity.Server

/**
 * 返回给前端的单条服务器信息，附带实时在线状态。
 */
data class ServerStatusDto(
    val id: Long?,
    val name: String,
    val description: String?,
    val host: String,
    val port: Int,
    val code: String?,
    val active: Boolean,
    val serverFlag: Boolean,
) {
    companion object {
        fun from(server: Server, online: Boolean) = ServerStatusDto(
            id = server.id,
            name = server.name,
            description = server.description,
            host = server.host,
            port = server.port,
            code = server.code,
            active = online,
            serverFlag = server.serverFlag,
        )
    }
}
