@file:JvmName("Sockets4Bungee")
package fr.rhaz.minecraft.sockets

import fr.rhaz.minecraft.kotlin.*
import fr.rhaz.minecraft.kotlin.bungee.ConfigFile
import fr.rhaz.minecraft.kotlin.bungee.ConfigSection
import fr.rhaz.sockets.MultiSocket
import net.md_5.bungee.api.ChatColor.LIGHT_PURPLE
import java.util.function.BiConsumer
import java.util.function.Consumer

fun BungeePlugin.onSocketEnable(
        listener: MultiSocket.(String) -> Unit
) = listen<SocketEvent.Bungee.Enabled>{
    listener(it.socket, it.id)
}

fun BungeePlugin.onSocketEnable(
        listener: BiConsumer<MultiSocket, String>
) = listen<SocketEvent.Bungee.Enabled>{
    listener.accept(it.socket, it.id)
}

fun BungeePlugin.onSocketEnable(
        id: String,
        listener: MultiSocket.() -> Unit
) = listen<SocketEvent.Bungee.Enabled>{
    if(it.id == id)
        listener(it.socket)
}

fun BungeePlugin.onSocketEnable(
        id: String,
        listener: Consumer<MultiSocket>
) = listen<SocketEvent.Bungee.Enabled>{
    if(it.id == id)
        listener.accept(it.socket)
}

lateinit var sockets4Bungee: Sockets4BungeePlugin
open class Sockets4BungeePlugin: BungeePlugin() {

    override fun onEnable(){
        sockets4Bungee = this
        schedule{enable()}
    }

    private fun enable() = catch<Exception> {
        update(15938, LIGHT_PURPLE)
        config = Config()
        config.init(this, "config/bungee.yml")
        for (socket in config.sockets) socket.mk()
    }

    override fun onDisable() = disable()

    lateinit var config: Config
    inner class Config: ConfigFile("config"){
        val sockets get() = config.keys.map(::SocketConfig)
    }

    inner class SocketConfig(val id: String): ConfigSection(config, id){
        val name by string("name")
        val port by int("port")
        val password by string("password")
        val bootstrap by stringList("bootstrap")
        val enabled by boolean("enabled", true)
        val timeout by long("timeout", 1000)
        val discovery by boolean("discovery", true)

        fun mk(){
            enabled.not(false) ?: return
            info("Starting $name (#$id)...")
            val socket = MultiSocket(name, port, password, timeout, discovery)
            socket.apply {
                sockets[id] = this
                logger = {name, ex -> warning("$name: ${ex.message}")}
                connect(bootstrap)
                accept(true)
            }
            info("Successfully started socket $name (#$id)")
            SocketEvent.Bungee.Enabled().also {
                it.socket = socket; it.id = id
                proxy.pluginManager.callEvent(it)
            }
        }
    }
}