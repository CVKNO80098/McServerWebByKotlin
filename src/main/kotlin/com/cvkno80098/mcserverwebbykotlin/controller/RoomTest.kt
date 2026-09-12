package com.cvkno80098.mcserverwebbykotlin.controller

import com.cvkno80098.mcserverwebbykotlin.dto.AgentDto
import com.cvkno80098.mcserverwebbykotlin.entity.Server
import com.cvkno80098.mcserverwebbykotlin.repository.ServerRepository
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/room")

class RoomTest(private val serverRepository: ServerRepository) {
    @RequestMapping("/agent/heartbeat")
    fun heartbeat(@RequestBody agentDto: AgentDto) {
        val logger = LoggerFactory.getLogger(RoomTest::class.java)
        logger.info("收到心跳$agentDto")

        try {
            val server: Server? = serverRepository.findById(agentDto.roomId.toLong()).orElse(null)

            server?.let {
                it.updatedAt = LocalDateTime.now()
                serverRepository.save(it)
            }
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
        }
        ResponseEntity.status(HttpStatus.OK)
    }
}