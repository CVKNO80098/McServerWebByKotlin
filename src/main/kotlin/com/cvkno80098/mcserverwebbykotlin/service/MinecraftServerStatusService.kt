package com.cvkno80098.mcserverwebbykotlin.service

import com.cvkno80098.mcserverwebbykotlin.repository.ServerRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * 通过 MCPing 库探测 Minecraft 服务器是否在线。
 * 探测失败（超时、拒绝连接等）一律视为离线，不向上抛出异常。
 */
@Service
class MinecraftServerStatusService(private val serverRepository: ServerRepository) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * @param host 服务器地址（域名或 IP）
     * @param port 端口，Java 版默认 25565
     * @return 能成功 Ping 通则返回 true
     */
    fun isServerOnline(host: String, port: Int): Boolean {
        return serverRepository.findByHostAndPort(host, port)?.active ?: false
    }
}
