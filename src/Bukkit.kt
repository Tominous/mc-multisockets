@file:JvmName("Sockets4MC")
@file:JvmMultifileClass
package fr.rhaz.minecraft.sockets

import fr.rhaz.minecraft.kotlin.*
import fr.rhaz.minecraft.kotlin.bukkit.ConfigFile
import fr.rhaz.minecraft.kotlin.bukkit.ConfigSection
import fr.rhaz.sockets.MultiSocket
import net.md_5.bungee.api.ChatColor.LIGHT_PURPLE
import org.bukkit.Bukkit

lateinit var sockets4Bukkit: Sockets4Bukkit
open class Sockets4Bukkit: BukkitPlugin() {

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
        val debug by boolean("debug")
        val sockets get() = config.keys.filter{it!="debug"}.map(::SocketConfig)
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
                logger = {warning(it)}
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