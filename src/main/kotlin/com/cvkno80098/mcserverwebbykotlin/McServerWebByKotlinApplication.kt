package com.cvkno80098.mcserverwebbykotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.web.config.EnableSpringDataWebSupport

/** Spring Boot 入口；扫描当前包及子包下的组件 */
@SpringBootApplication
@EnableSpringDataWebSupport(
    pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO
)
class McServerWebByKotlinApplication

fun main(args: Array<String>) {
    runApplication<McServerWebByKotlinApplication>(*args)
}
