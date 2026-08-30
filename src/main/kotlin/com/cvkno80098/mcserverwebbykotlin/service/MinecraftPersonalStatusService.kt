package com.cvkno80098.mcserverwebbykotlin.service

import com.cvkno80098.mcserverwebbykotlin.repository.ServerRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime

@Service
class MinecraftPersonalStatusService(private val serverRepository: ServerRepository) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun isServerOnline(host: String, port: Int): Boolean {
        //由于思路转变，不再主动检测，转为直接接收值并数据库更新，因此直接查询数据库值

        return serverRepository.findByHostAndPort(host, port)?.updatedAt?.let { Duration.between(it, LocalDateTime.now()) > Duration.ofSeconds(9) } ?: false
    }
}