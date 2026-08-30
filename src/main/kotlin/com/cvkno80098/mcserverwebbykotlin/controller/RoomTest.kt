package com.cvkno80098.mcserverwebbykotlin.controller

import com.cvkno80098.mcserverwebbykotlin.dto.AgentDto
import com.cvkno80098.mcserverwebbykotlin.entity.Server
import com.cvkno80098.mcserverwebbykotlin.repository.ServerRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import java.time.LocalDateTime
import java.util.Optional
import java.util.logging.Logger

@Controller
@RequestMapping("/room")

class RoomTest(private val serverRepository: ServerRepository) {
    @RequestMapping("/agent/heartbeat")
    fun heartbeat(@RequestBody agentDto: AgentDto) {
        val logger = LoggerFactory.getLogger(RoomTest::class.java)
        logger.info("收到心跳$agentDto")

        val server: Server? = serverRepository.findById(agentDto.roomId.toLong()).orElse(null)

        server?.let {
            it.updatedAt = LocalDateTime.now()
            serverRepository.save(it)
        }
    }
}