package io.github.chrislo27.rhre3.discord

import club.minnced.discord.rpc.DiscordEventHandlers
import club.minnced.discord.rpc.DiscordRPC
import club.minnced.discord.rpc.DiscordRichPresence
import kotlin.concurrent.thread


object DiscordHelper {

    const val DISCORD_APP_ID = "278329593012682754"
    const val DEFAULT_LARGE_IMAGE = "square_logo"
    const val EXPANSION_LARGE_IMAGE = "logo_ex"
    const val DEFAULT_SMALL_IMAGE = "32_as_512"
    private var inited = false

    private val lib: DiscordRPC
        get() = DiscordRPC.INSTANCE
    private var queuedPresence: DiscordRichPresence? = null
    private var lastSent: DiscordRichPresence? = null
    @Volatile
    var enabled = true
        set(value) {
            field = value
            if (value) {
                signalUpdate()
            } else {
                clearPresence()
            }
        }

    @Synchronized
    fun init(enabled: Boolean = this.enabled) {
        if (inited)
            return
        inited = true
        this.enabled = enabled

        lib.Discord_Initialize(DISCORD_APP_ID, DiscordEventHandlers(), true, "")

        Runtime.getRuntime().addShutdownHook(
                thread(start = false, name = "Discord-RPC Shutdown", block = lib::Discord_Shutdown))

        thread(isDaemon = true, name = "Discord-RPC Callback Handler") {
            while (!Thread.currentThread().isInterrupted) {
                try {
                    Thread.sleep(2000L)
                } catch (ignored: InterruptedException) {
                }
                lib.Discord_RunCallbacks()
            }
        }
    }

    @Synchronized
    private fun signalUpdate() {
        if (enabled) {
            val queued = queuedPresence
            val lastSent = lastSent
            if (queued !== null && lastSent !== queued) {
                lib.Discord_UpdatePresence(queued)
                this.lastSent = queued
                queuedPresence = null
            }
        }
    }

    @Synchronized
    fun clearPresence() {
        lib.Discord_ClearPresence()
    }

    @Synchronized
    fun updatePresence(presence: DiscordRichPresence) {
        queuedPresence = presence
        signalUpdate()
    }

    @Synchronized
    fun updatePresence(presenceState: PresenceState) {
        updatePresence(DefaultRichPresence(presenceState))
    }

}
