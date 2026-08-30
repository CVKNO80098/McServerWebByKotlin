package com.cvkno80098.mcserverwebbykotlin.service

import com.cvkno80098.mcserverwebbykotlin.repository.ServerRepository
import net.lenni0451.mcping.MCPing
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.logging.Logger

@Service
class ScheduledFetchService(
    private val serverRepository: ServerRepository
) {

    // 每 10 分钟执行一次
    @Scheduled(fixedRate = 10 * 60 * 1000)
    fun fetchData() {

        val serverList = serverRepository.findAllByServerFlagIsTrue()

        for (server in serverList) {

            val active = try {

                MCPing.pingModern()
                    .address(server.host, server.port)
                    .sync

                true

            } catch (e: Exception) {
                false
            }

            if (server.active != active) {
                server.active = active
                serverRepository.save(server)
            }
        }
    }
}