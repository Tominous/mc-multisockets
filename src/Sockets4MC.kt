package fr.rhaz.minecraft

import fr.rhaz.sockets.*
import net.md_5.bungee.api.ProxyServer
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import java.io.File
import java.util.logging.Logger
import net.md_5.bungee.api.plugin.Event as BungeeEvent
import net.md_5.bungee.api.plugin.Plugin as BungeePlugin
import org.bukkit.event.Event as BukkitEvent
import org.bukkit.plugin.java.JavaPlugin as BukkitPlugin
import org.spongepowered.api.plugin.Plugin as SpongePlugin

open class Sockets4Bukkit: BukkitPlugin() {
    val sockets = Sockets4MC()
    override fun onEnable() = sockets.bukkit(this)
}

open class Sockets4Bungee: BungeePlugin() {
    val sockets = Sockets4MC()
    override fun onEnable() = sockets.bungee(this)
}

class Sockets4MC(){
    lateinit var plugin: Any
    lateinit var servertype: String
    lateinit var logger: Logger
    lateinit var dataFolder: File

    fun bungee(plugin: Sockets4Bungee){
        this.plugin = plugin
        servertype = "Bungee"
        logger = plugin.logger
        dataFolder = plugin.dataFolder
        start()
    }

    fun bukkit(plugin: Sockets4Bukkit){
        this.plugin = plugin
        servertype = "Bukkit"
        logger = plugin.logger
        dataFolder = plugin.dataFolder
        start()
    }

    fun start() {
        logger.info("Successfully enabled Sockets4MC for $servertype")
        SocketClient(client, "Test", "localhost", 25598, "hello").apply {
            config.buffer=100
            config.timeout=2000
        }.start()
        SocketServer(server, "Test", 25598, "hello").apply {
            config.buffer=100
            config.timeout=2000
        }.start()
    }

    val server = object: SocketApp.Server(){
        override fun log(err: String) = logger.info(err)
        override fun onMessage(mess: SocketMessenger, map: JSONMap) {
            when(servertype.lc) {
                "bukkit" -> SocketEvent.Bukkit.Server.Message().apply {
                    messenger = mess
                    message = map
                    channel = map.getExtra<String>("channel") ?: "unknown"
                    Bukkit.getPluginManager().callEvent(this)
                }
                "bungee" -> SocketEvent.Bungee.Server.Message().apply {
                    messenger = mess
                    message = map
                    channel = map.getExtra<String>("channel") ?: "unknown"
                    ProxyServer.getInstance().pluginManager.callEvent(this)
                }
            }
        }

        override fun onConnect(mess: SocketMessenger){

        }

        override fun onHandshake(mess: SocketMessenger, name: String){

        }

        override fun onDisconnect(mess: SocketMessenger){

        }
    }

    val client = object: SocketApp.Client(){
        override fun log(err: String) = logger.info(err)
        override fun onMessage(client: SocketClient, map: JSONMap) {
            when(servertype.lc) {
                "bukkit" -> SocketEvent.Bukkit.Client.Message().apply {
                    this.client = client
                    message = map
                    channel = map.getExtra<String>("channel") ?: "unknown"
                    Bukkit.getPluginManager().callEvent(this)
                }
                "bungee" -> SocketEvent.Bungee.Client.Message().apply {
                    this.client = client
                    message = map
                    channel = map.getExtra<String>("channel") ?: "unknown"
                    ProxyServer.getInstance().pluginManager.callEvent(this)
                }
            }
        }

        override fun onConnect(client: SocketClient){
            when(servertype.lc) {
                "bukkit" -> SocketEvent.Bukkit.Client.Connected().apply {
                    this.client = client
                    Bukkit.getPluginManager().callEvent(this)
                }
                "bungee" -> SocketEvent.Bungee.Client.Connected().apply {
                    this.client = client
                    ProxyServer.getInstance().pluginManager.callEvent(this)
                }
            }
        }

        override fun onHandshake(client: SocketClient){
            when(servertype.lc) {
                "bukkit" -> SocketEvent.Bukkit.Client.Handshake().apply {
                    this.client = client
                    Bukkit.getPluginManager().callEvent(this)
                }
                "bungee" -> SocketEvent.Bungee.Client.Handshake().apply {
                    this.client = client
                    ProxyServer.getInstance().pluginManager.callEvent(this)
                }
            }
        }

        override fun onDisconnect(client: SocketClient){
            when(servertype.lc) {
                "bukkit" -> SocketEvent.Bukkit.Client.Disconnected().apply {
                    this.client = client
                    Bukkit.getPluginManager().callEvent(this)
                }
                "bungee" -> SocketEvent.Bungee.Client.Disconnected().apply {
                    this.client = client
                    ProxyServer.getInstance().pluginManager.callEvent(this)
                }
            }
        }
    }

}

interface SocketEvent {
    enum class Side{ server, client }
    enum class Type{ message, connected, disconnected, handshake }
    val side: Side ; val type: Type

    open class Bukkit(
        override val side: Side, override val type: Type
    ): SocketEvent, BukkitEvent() {

        open class Server(override val type: Type): Bukkit(Side.server, type) {
            open class Message: Server(Type.message){
                lateinit var messenger: SocketMessenger
                lateinit var message: JSONMap
                lateinit var channel: String
            }
            open class Connected: Server(Type.connected){
                lateinit var messenger: SocketMessenger
            }
            open class Disconnected: Server(Type.disconnected){
                lateinit var messenger: SocketMessenger
            }
            open class Handshake: Server(Type.handshake){
                lateinit var messenger: SocketMessenger
            }
        }

        open class Client(override val type: Type): Bukkit(Side.client, type){
            open class Message: Client(Type.message){
                lateinit var client: SocketClient
                lateinit var message: JSONMap
                lateinit var channel: String
            }
            open class Connected: Client(Type.connected){
                lateinit var client: SocketClient
            }
            open class Disconnected: Client(Type.disconnected){
                lateinit var client: SocketClient
            }
            open class Handshake: Client(Type.handshake){
                lateinit var client: SocketClient
            }
        }

        val _handlers = HandlerList()
        override fun getHandlers() = _handlers
    }

    open class Bungee(
        override val side: Side, override val type: Type
    ): SocketEvent, BungeeEvent(){

        open class Server(override val type: Type): Bungee(Side.server, type){
            open class Message: Server(Type.message){
                lateinit var messenger: SocketMessenger
                lateinit var message: JSONMap
                lateinit var channel: String
            }
            open class Connected: Server(Type.connected){
                lateinit var messenger: SocketMessenger
            }
            open class Disconnected: Server(Type.disconnected){
                lateinit var messenger: SocketMessenger
            }
            open class Handshake: Server(Type.handshake){
                lateinit var messenger: SocketMessenger
            }
        }

        open class Client(override val type: Type): Bungee(Side.client, type){
            open class Message: Client(Type.message){
                lateinit var client: SocketClient
                lateinit var message: JSONMap
                lateinit var channel: String
            }
            open class Connected: Client(Type.connected){
                lateinit var client: SocketClient
            }
            open class Disconnected: Client(Type.disconnected){
                lateinit var client: SocketClient
            }
            open class Handshake: Client(Type.handshake){
                lateinit var client: SocketClient
            }
        }
    }
}


val String.lc get() = toLowerCase()