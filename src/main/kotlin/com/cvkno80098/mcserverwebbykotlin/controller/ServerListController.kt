package com.cvkno80098.mcserverwebbykotlin.controller

import com.cvkno80098.mcserverwebbykotlin.dto.ServerStatusDto
import com.cvkno80098.mcserverwebbykotlin.entity.Server
import com.cvkno80098.mcserverwebbykotlin.repository.ServerRepository
import com.cvkno80098.mcserverwebbykotlin.service.MinecraftPersonalStatusService
import com.cvkno80098.mcserverwebbykotlin.service.MinecraftServerStatusService
import com.cvkno80098.mcserverwebbykotlin.service.AuthService
import com.cvkno80098.mcserverwebbykotlin.service.ServerService
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import kotlin.jvm.optionals.toList

/**
 * 服务器列表 REST 接口。
 *
 * - GET  /server/list/{page}  分页列表（page 从 0 开始）
 * - POST /server/list         新增服务器
 */
@RestController
@RequestMapping("/server/list")
class ServerListController(
    private val serverService: ServerService,
    private val serverRepository: ServerRepository,
    private val minecraftServerStatusService: MinecraftServerStatusService,
    private val minecraftPersonalStatusService: MinecraftPersonalStatusService,
    private val authService: AuthService,
) {

    @GetMapping("/{page}")
    fun getServers(@PathVariable page: Int): Page<Server> {
        return serverService.getActiveServers(page)
    }

    @PostMapping("/create")
    fun createServer(
        @RequestBody server: Server,
        authentication: Authentication,
    ): ResponseEntity<Server> {
        // SecurityConfig 已要求登录；再次读取账户可确保已禁用用户不能创建服务器。
        val currentUser = authService.currentUser(authentication.principal as Long)

        // 在探测网络状态和写库之前先校验基础输入。
        if (server.name.isBlank() || server.host.isBlank()) {
            return ResponseEntity.badRequest().build()
        }

        // ownerId 只能由后端根据当前令牌设置，忽略客户端传入的值。
        server.ownerId = requireNotNull(currentUser.id)

        if (server.serverFlag)
            // 主动查询，而不是被动更新，active每次查询更新
            server.active = minecraftServerStatusService.isServerOnline(server.host, server.port)
        else
            // 实现变更，由主动查询变为心跳更新时间，active无用
            server.active = minecraftPersonalStatusService.isServerOnline(server.host, server.port)
        return ResponseEntity.ok(serverService.save(server))
    }

    @GetMapping("/servers/{id}")
    fun getServer(@PathVariable id: Long): ResponseEntity<ServerStatusDto> {
        val server = serverRepository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }

        val dto = ServerStatusDto.from(server, online = minecraftServerStatusService.isServerOnline(server.host, server.port))
        return ResponseEntity.ok(dto)
    }
}
