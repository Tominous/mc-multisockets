@file:JvmName("Sockets4MC")
@file:JvmMultifileClass

package fr.rhaz.minecraft.sockets

import fr.rhaz.minecraft.kotlin.*
import fr.rhaz.sockets.SocketClient
import fr.rhaz.sockets.SocketServer
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import java.util.concurrent.TimeUnit

open class Sockets4Bukkit: BukkitPlugin() {

    override fun onEnable() = schedule{enable()}.unit
    override fun onDisable() = disable()

    lateinit var config: BukkitYamlConfiguration

    private fun enable() = catch<Exception>(::severe){

        update(15938, ChatColor.LIGHT_PURPLE)

        config = load(dataFolder["config.yml"])
                ?: throw ex("Could not load config")

        debug = config.getBoolean("debug", false)
        for (key in config.keys) if (key != "debug") config.section(key).mk()
    }

    private fun BukkitConfigurationSection.mk() {
        getBoolean("enabled", true).not(false) ?: return

        val name = getString("name", "MyBukkit")
        val port = getInt("port", 25598)
        val password = getString("password", "mypassword")

        info("Starting $name (#${this.name})...")
        when (getString("type", "server").lc) {
            "server" -> SocketServer(socketServerApp, name, port, password).mk(this)
            "client" -> {
                val host = getString("host", "localhost").lc
                SocketClient(socketClientApp, name, host, port, password).mk(this)
            }
        }
    }

    private fun SocketServer.mk(section: BukkitConfigurationSection) {
        val key = section.name
        config.buffer = section.getInt("buffer", 100)
        config.timeout = section.getLong("timeout", 1000)
        val security = section.getString("security", "aes")
        config.security = when(security) {
            "none" -> 0; "aes" -> 1
            "rsa" -> 2; else -> 1
        }
        sockets[key] = this
        this.start()
        info("Successfully started server $name (#$key)")
        SocketEvent.Bukkit.Server.Enabled().also {
            it.socket = this
            it.key = key
            Bukkit.getPluginManager().callEvent(it)
        }
    }

    private fun SocketClient.mk(section: BukkitConfigurationSection){
        val key = section.name
        config.buffer = section.getInt("buffer", 100)
        config.timeout = section.getLong("timeout", 1000)
        sockets[key] = this
        this.start()
        logger.info("Successfully started client $name (#$key)")
        SocketEvent.Bukkit.Client.Enabled().also {
            it.socket = this
            it.key = key
            Bukkit.getPluginManager().callEvent(it)
        }
    }

}

open class Sockets4Bungee: BungeePlugin() {
    override fun onEnable() = schedule(delay = 8, unit = TimeUnit.SECONDS){enable()}.unit
    override fun onDisable() = disable()

    lateinit var config: BungeeConfiguration

    private fun enable() = catch<Exception>(::severe){
        update(15938, ChatColor.AQUA)

        config = load(dataFolder["config.yml"])
                ?: throw ex("Could not load config")

        debug = config.getBoolean("debug", false)

        for (key in config.keys) if (key != "debug")
            config.section(key).mk(key)
    }

    private fun BungeeConfiguration.mk(key: String){
        getBoolean("enabled", true).not(false) ?: return

        val name = getString("name", "MyProxy")
        val port = getInt("port", 25598)
        val password = getString("password", "mypassword")

        info("Starting $name (#$key)...")
        when (getString("type", "server").lc) {
            "server" -> SocketServer(socketServerApp, name, port, password).mk(this, key)
            "client" -> {
                val host = getString("host", "localhost").lc
                SocketClient(socketClientApp, name, host, port, password).mk(this, key)
            }
        }
    }

    private fun SocketServer.mk(section: BungeeConfiguration, key: String){

        config.buffer = section.getInt("buffer", 100)
        config.timeout = section.getLong("timeout", 1000)
        val security = section.getString("security", "aes")
        config.security = when(security) {
            "none" -> 0; "aes" -> 1
            "rsa" -> 2; else -> 1
        }
        sockets[key] = this
        this.start()
        info("Successfully started server $name (#$key)")
        SocketEvent.Bungee.Server.Enabled().also {
            it.socket = this
            it.key = key
            proxy.pluginManager.callEvent(it)
        }
    }

    private fun SocketClient.mk(section: BungeeConfiguration, key: String){
        config.buffer = section.getInt("buffer", 100)
        config.timeout = section.getLong("timeout", 1000)
        sockets[key] = this
        this.start()
        logger.info("Successfully started client $name (#$key)")
        SocketEvent.Bungee.Client.Enabled().also {
            it.socket = this
            it.key = key
            proxy.pluginManager.callEvent(it)
        }
    }
}

