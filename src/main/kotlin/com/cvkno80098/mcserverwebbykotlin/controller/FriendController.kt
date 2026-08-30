package com.cvkno80098.mcserverwebbykotlin.controller

import com.cvkno80098.mcserverwebbykotlin.dto.FriendResponse
import com.cvkno80098.mcserverwebbykotlin.service.FriendService
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/** 好友请求、接受、查询及删除接口。 */
@RestController
@RequestMapping("/friends")
class FriendController(
    private val friendService: FriendService,
) {
    @GetMapping
    fun list(authentication: Authentication): List<FriendResponse> = friendService.list(authentication.principal as Long)

    @PostMapping("/{userId}/request")
    @ResponseStatus(HttpStatus.CREATED)
    fun request(@PathVariable userId: Long, authentication: Authentication): FriendResponse =
        friendService.request(authentication.principal as Long, userId)

    @PostMapping("/{friendshipId}/accept")
    fun accept(@PathVariable friendshipId: Long, authentication: Authentication): FriendResponse =
        friendService.accept(authentication.principal as Long, friendshipId)

    @DeleteMapping("/{friendshipId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable friendshipId: Long, authentication: Authentication) {
        friendService.delete(authentication.principal as Long, friendshipId)
    }
}
