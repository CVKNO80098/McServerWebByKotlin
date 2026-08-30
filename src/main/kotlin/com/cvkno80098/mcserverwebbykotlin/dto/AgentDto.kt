package com.cvkno80098.mcserverwebbykotlin.dto


data class AgentDto (
    val roomId: String,
    val agentVersion: String,
    val status: String,
    val verified: Boolean,
    val verificationLevel: String,
    val minecraft: Minecraft,
    val virtualNetworks: List<VirtualNetworks>,
    val timestamp: Long
)

data class Minecraft (
    val reachable: Boolean,
    val latencyMs: Int,
    val playersOnline: Int,
    val playersMax: Int,
    val version: String
)

data class VirtualNetworks (
    val provider: String,
    val interfaceName: String,
    val ipv4: String,
    val up: Boolean
)