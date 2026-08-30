package com.cvkno80098.mcserverwebbykotlin.service

import com.cvkno80098.mcserverwebbykotlin.dto.ServerStatusDto
import com.cvkno80098.mcserverwebbykotlin.entity.Server
import com.cvkno80098.mcserverwebbykotlin.repository.ServerRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/** 每页条数 */
private const val PAGE_SIZE = 10

@Service
class ServerService(
    private val serverRepository: ServerRepository,
    private val minecraftServerStatusService: MinecraftServerStatusService,
    private val minecraftPersonalStatusService: MinecraftPersonalStatusService,
) {

    /**
     * 分页查询已启用的服务器，并附带 MCPing 在线状态。
     *
     * @param page 页码，从 0 开始（与 Spring Data 一致）
     */
    fun getActiveServers(page: Int): Page<Server> {
        val safePage = page.coerceAtLeast(0)

        val pageRequest:PageRequest = PageRequest.of(safePage, PAGE_SIZE, Sort.by("active").descending().and(Sort.by("id").ascending()))
        val serverPage = serverRepository.findAll(pageRequest)

        return serverPage.map { server ->
            val online = if (server.host.isNotBlank()) {
                if (server.serverFlag) {
                    minecraftServerStatusService.isServerOnline(server.host, server.port)
                } else {
                    // 应该完成了吧...
                    minecraftPersonalStatusService.isServerOnline(server.host, server.port)
                }
            } else {
                false
            }
            server.active = online
            serverRepository.save(server)
        }
    }

    /** 保存或更新一条服务器记录 */
    @Transactional
    fun save(server: Server): Server = serverRepository.save(server)
}
