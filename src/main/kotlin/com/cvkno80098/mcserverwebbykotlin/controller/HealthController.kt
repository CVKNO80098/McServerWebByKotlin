package com.cvkno80098.mcserverwebbykotlin.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/** 根路径健康检查，便于确认服务已启动 */
@RestController
class HealthController {

    // 服务器心跳
    @GetMapping("/")
    fun index(): Map<String, String> = mapOf(
        "service" to "McServerWebByKotlin",
        "status" to "running",
    )
}
