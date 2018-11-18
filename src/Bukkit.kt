@file:JvmName("Sockets4Bukkit")
package fr.rhaz.minecraft.sockets

import fr.rhaz.minecraft.kotlin.bukkit.*
import fr.rhaz.minecraft.kotlin.catch
import fr.rhaz.minecraft.kotlin.not
import fr.rhaz.sockets.MultiSocket
import net.md_5.bungee.api.ChatColor.LIGHT_PURPLE
import org.bukkit.Bukkit
import java.util.function.BiConsumer
import java.util.function.Consumer
import kotlin.collections.map
import kotlin.collections.set

fun BukkitPlugin.onSocketEnable(
        listener: MultiSocket.(String) -> Unit
) = listen<SocketEvent.Bukkit.Enabled>{
    listener(it.socket, it.id)
}

fun BukkitPlugin.onSocketEnable(
        listener: BiConsumer<MultiSocket, String>
) = listen<SocketEvent.Bukkit.Enabled>{
    listener.accept(it.socket, it.id)
}

fun BukkitPlugin.onSocketEnable(
        id: String,
        listener: MultiSocket.() -> Unit
) = listen<SocketEvent.Bukkit.Enabled>{
    if(it.id == id)
        listener(it.socket)
}

fun BukkitPlugin.onSocketEnable(
        id: String,
        listener: Consumer<MultiSocket>
) = listen<SocketEvent.Bukkit.Enabled>{
    if(it.id == id)
        listener.accept(it.socket)
}

lateinit var sockets4Bukkit: Sockets4BukkitPlugin
open class Sockets4BukkitPlugin: BukkitPlugin() {

    override fun onEnable(){
        sockets4Bukkit = this
        schedule{enable()}
    }

    private fun enable() = catch<Exception>(::severe) {
        update(15938, LIGHT_PURPLE)
        config = Config()
        config.init(this, "config/bukkit.yml")
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
        val debugEnabled by boolean("debug", false)

        fun mk(){
            enabled.not(false) ?: return
            info("Starting $name (#$id)...")
            val socket = MultiSocket(name, port, password, timeout, discovery)
            socket.apply {
                sockets[id] = this
                logger = {name, ex -> warning("$name: ${ex.message}")}
                if(debugEnabled) debug = {name, msg -> info("$name: $msg") }
                connect(bootstrap)
                accept(true)
            }
            info("Successfully started socket $name (#$id)")
            SocketEvent.Bukkit.Enabled().also {
                it.socket = socket; it.id = id
                Bukkit.getPluginManager().callEvent(it)
            }
        }
    }
}