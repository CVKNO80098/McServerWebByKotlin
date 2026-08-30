package com.cvkno80098.mcserverwebbykotlin.repository

import com.cvkno80098.mcserverwebbykotlin.entity.Server
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/** 服务器数据访问层 */
interface ServerRepository : JpaRepository<Server, Long> {

    /** 按名称模糊搜索（仅启用条目） */
    @Query("select s from Server s where s.name like concat('%', :name, '%') and s.active = true")
    fun findByNameContaining(name: String, pageable: Pageable): Page<Server>

    /** 分页查询所有启用中的服务器 */
    fun findByActiveTrue(pageable: Pageable): Page<Server>

    fun findByHostAndPort(host: String, port: Int): Server?

    fun findAllByServerFlagIsTrue(): List<Server>
}
