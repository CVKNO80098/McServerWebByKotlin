package com.cvkno80098.mcserverwebbykotlin.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Date
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import javax.crypto.SecretKey

/** JWT 的签发、解析与注销管理。注销列表存于内存，服务重启后令牌会恢复有效。 */
@Service
class JwtService(
    @Value("\${app.jwt.secret}") private val secret: String,
    @Value("\${app.jwt.expiration-minutes}") private val expirationMinutes: Long,
) {
    private val revokedTokenIds = ConcurrentHashMap.newKeySet<String>()
    private val key: SecretKey by lazy { Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)) }

    fun createToken(userId: Long): String {
        val now = Instant.now()
        return Jwts.builder()
            .subject(userId.toString())
            .id(UUID.randomUUID().toString())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(expirationMinutes * 60)))
            .signWith(key)
            .compact()
    }

    /** 返回合法且未注销令牌中的用户 ID；解析失败时统一返回 null。 */
    fun userIdFromToken(token: String): Long? = try {
        val claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
        if (claims.id in revokedTokenIds) null else claims.subject.toLongOrNull()
    } catch (_: Exception) {
        null
    }

    /** 把当前令牌的 jti 加入黑名单，使其在有效期内立即失效。 */
    fun revoke(token: String) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload.id?.let(revokedTokenIds::add)
        } catch (_: Exception) {
            // 无效令牌本身无法访问受保护端点，登出时无需泄露其具体解析错误。
        }
    }
}
