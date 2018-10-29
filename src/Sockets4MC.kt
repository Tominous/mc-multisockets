package fr.rhaz.minecraft.sockets

import BukkitEvent
import fr.rhaz.minecraft.kotlin.*
import fr.rhaz.sockets.*
import net.md_5.bungee.api.ChatColor.LIGHT_PURPLE
import net.md_5.bungee.api.ProxyServer
import org.bukkit.Bukkit
import java.io.File
import java.util.concurrent.TimeUnit

open class Sockets4Bukkit: BukkitPlugin() {
    override fun onEnable() {
        server.scheduler.runTask(this){Sockets4MC.bukkit(this)}
    }
    override fun onDisable() = Sockets4MC.disable()
}

open class Sockets4Bungee: BungeePlugin() {
    override fun onEnable() {
        proxy.scheduler.schedule(this, {Sockets4MC.bungee(this)}, 8, TimeUnit.SECONDS)
    }
    override fun onDisable() = Sockets4MC.disable()
}

object Sockets4MC {

    var debug = false
    val sockets = mutableMapOf<String, SocketHandler>()

    fun disable() = sockets.values.forEach {
        when(it){
            is SocketClient -> it.interrupt()
            is SocketServer -> it.close()
        }
    }.also { sockets.clear() }

    fun bungee(plugin: Sockets4Bungee) = plugin.apply{

        update(15938, LIGHT_PURPLE)

        (load(File(dataFolder, "config.yml"))
            ?: return logger.warning("Could not load configuration"))
        .apply {

            debug = getBoolean("debug", false)

            fun init(key: String) = getSection(key).apply section@{

                val enabled = getBoolean("enabled", true)
                if(!enabled) return@section
                val name = getString("name", "MyProxy")
                val port = getInt("port", 25598)
                val password = getString("password", "mypassword")

                fun mkserver() = SocketServer(socketServer, name, port, password)
                .apply {
                    config.buffer = getInt("buffer", 100)
                    config.timeout = getLong("timeout", 1000)
                    config.security = when(getString("security", "aes")){
                        "none" -> 0; "aes" -> 1; "rsa" -> 2
                        else -> logger.info(
                                "Unknown security for $name." +
                                        "Accepted: none, aes, rsa"
                        ).let{1}
                    }
                    sockets[key] = this
                    start()
                    logger.info("Successfully started server $name (#$key)")
                    SocketEvent.Bungee.Server.Enabled().also {
                        it.socket = this
                        it.key = key
                        ProxyServer.getInstance().pluginManager.callEvent(it)
                    }
                }

                fun mkclient() = SocketClient(socketClient, name, getString("host", "localhost").lc, port, password)
                .apply {
                    config.buffer = getInt("buffer", 100)
                    config.timeout = getLong("timeout", 1000)
                    sockets[key] = this
                    start()
                    logger.info("Successfully started client $name (#$key)")
                    SocketEvent.Bungee.Client.Enabled().also {
                        it.socket = this
                        it.key = key
                        ProxyServer.getInstance().pluginManager.callEvent(it)
                    }
                }

                logger.info("Starting $name (#$key)...")
                when(getString("type", "server").lc)
                {"server" -> mkserver(); "client" -> mkclient() }
            }

            for(key in keys) if(key != "debug") init(key)

        }
    }.let{Unit}

    fun bukkit(plugin: Sockets4Bukkit) = plugin.apply{

        update(15938, LIGHT_PURPLE)

        (load(File(dataFolder, "config.yml"))
            ?: return logger.warning("Could not load configuration"))
        .apply {

            debug = getBoolean("debug", false)

            fun init(key: String) = getConfigurationSection(key).apply section@{

                val enabled = getBoolean("enabled", true)
                if(!enabled) return@section
                val name = getString("name", "MyBukkit")
                val port = getInt("port", 25598)
                val password = getString("password", "mypassword")

                fun mkserver() = SocketServer(socketServer, name, port, password)
                .apply {
                    config.buffer = getInt("buffer", 100)
                    config.timeout = getLong("timeout", 1000)
                    config.security = when(getString("security", "aes")){
                        "none" -> 0; "aes" -> 1; "rsa" -> 2
                        else -> logger.info(
                                "Unknown security for $name." +
                                        "Accepted: none, aes, rsa"
                        ).let{1}
                    }
                    sockets[key] = this
                    start()
                    logger.info("Successfully started server $name (#$key)")
                    SocketEvent.Bukkit.Server.Enabled().also {
                        it.socket = this
                        it.key = key
                        Bukkit.getPluginManager().callEvent(it)
                    }
                }

                fun mkclient() = SocketClient(socketClient, name, getString("host", "localhost").lc, port, password)
                .apply {
                    config.buffer = getInt("buffer", 100)
                    config.timeout = getLong("timeout", 1000)
                    sockets[key] = this
                    start()
                    logger.info("Successfully started client $name (#$key)")
                    SocketEvent.Bukkit.Client.Enabled().also {
                        it.socket = this
                        it.key = key
                        Bukkit.getPluginManager().callEvent(it)
                    }
                }

                logger.info("Starting $name (#$key)...")
                when(getString("type", "server").lc)
                    {"server" -> mkserver(); "client" -> mkclient() }
            }

            for (key in getKeys(false)) if(key != "debug") init(key)

        }
    }.let{Unit}

    val BukkitPlugin.socketServer get() = run here@{
        object: SocketApp.Server() {

            override fun run(runnable: Runnable) {
                Bukkit.getScheduler().runTaskAsynchronously(this@here, runnable)
            }

            override fun log(err: String){ if(debug) logger.info(err) }

            override fun onMessage(mess: SocketMessenger, map: jsonMap) {
                SocketEvent.Bukkit.Server.Message().apply {
                    messenger = mess
                    message = map
                    channel = map["channel"] as? String ?: "unknown"
                    Bukkit.getPluginManager().callEvent(this)
                }
            }

            override fun onConnect(mess: SocketMessenger){
                SocketEvent.Bukkit.Server.Connected().apply {
                    messenger = mess
                    Bukkit.getPluginManager().callEvent(this)
                }
            }

            override fun onHandshake(mess: SocketMessenger, name: String){
                logger.info("§aThe connection ${mess.name}<->${name} is available")
                SocketEvent.Bukkit.Server.Handshake().apply {
                    messenger = mess
                    Bukkit.getPluginManager().callEvent(this)
                }
            }

            override fun onDisconnect(mess: SocketMessenger){
                SocketEvent.Bukkit.Server.Disconnected().apply {
                    messenger = mess
                    Bukkit.getPluginManager().callEvent(this)
                }
            }
        }
    }

    val BungeePlugin.socketServer get() = run here@{
        object : SocketApp.Server() {

            override fun run(runnable: Runnable) {
                ProxyServer.getInstance().scheduler.runAsync(this@here, runnable)
            }

            override fun log(err: String) {
                if (debug) logger.info(err)
            }

            override fun onMessage(mess: SocketMessenger, map: jsonMap) {
                SocketEvent.Bungee.Server.Message().apply {
                    messenger = mess
                    message = map
                    channel = map["channel"] as? String ?: "unknown"
                    ProxyServer.getInstance().pluginManager.callEvent(this)
                }
            }

            override fun onConnect(mess: SocketMessenger) {
                SocketEvent.Bungee.Server.Connected().apply {
                    messenger = mess
                    ProxyServer.getInstance().pluginManager.callEvent(this)
                }
            }

            override fun onHandshake(mess: SocketMessenger, name: String) {
                logger.info("§aThe connection ${mess.name}<->${name} is available")
                SocketEvent.Bungee.Server.Handshake().apply {
                    messenger = mess
                    ProxyServer.getInstance().pluginManager.callEvent(this)
                }
            }

            override fun onDisconnect(mess: SocketMessenger) {
                SocketEvent.Bungee.Server.Disconnected().apply {
                    messenger = mess
                    ProxyServer.getInstance().pluginManager.callEvent(this)
                }
            }
        }
    }

    val BukkitPlugin.socketClient get() = run here@ {
        object : SocketApp.Client() {

            override fun run(runnable: Runnable) {
                Bukkit.getScheduler().runTaskAsynchronously(this@here, runnable)
            }

            override fun log(err: String) {
                if (debug) logger.info(err)
            }

            override fun onMessage(client: SocketClient, map: jsonMap) {
                SocketEvent.Bukkit.Client.Message().apply {
                    this.client = client
                    message = map
                    channel = map["channel"] as? String ?: "unknown"
                    Bukkit.getPluginManager().callEvent(this)
                }
            }

            override fun onConnect(client: SocketClient) {
                SocketEvent.Bukkit.Client.Connected().apply {
                    this.client = client
                    Bukkit.getPluginManager().callEvent(this)
                }
            }

            override fun onHandshake(client: SocketClient) {
                logger.info("§aThe connection ${client.name}<->${client.target.name} is available")
                SocketEvent.Bukkit.Client.Handshake().apply {
                    this.client = client
                    Bukkit.getPluginManager().callEvent(this)
                }
            }

            override fun onDisconnect(client: SocketClient) {
                SocketEvent.Bukkit.Client.Disconnected().apply {
                    this.client = client
                    Bukkit.getPluginManager().callEvent(this)
                }
            }
        }
    }

    val BungeePlugin.socketClient get() = run here@{
        object: SocketApp.Client(){

            override fun run(runnable: Runnable) {
                ProxyServer.getInstance().scheduler.runAsync(this@here, runnable)
            }

            override fun log(err: String){ if(debug) logger.info(err) }

            override fun onMessage(client: SocketClient, map: jsonMap) {
                SocketEvent.Bungee.Client.Message().apply {
                    this.client = client
                    message = map
                    channel = map["channel"] as? String ?: "unknown"
                    ProxyServer.getInstance().pluginManager.callEvent(this)
                }
            }

            override fun onConnect(client: SocketClient){
                SocketEvent.Bungee.Client.Connected().apply {
                    this.client = client
                    ProxyServer.getInstance().pluginManager.callEvent(this)
                }
            }

            override fun onHandshake(client: SocketClient){
                logger.info("§aThe connection ${client.name}<->${client.target.name} is available")
                SocketEvent.Bungee.Client.Handshake().apply {
                    this.client = client
                    ProxyServer.getInstance().pluginManager.callEvent(this)
                }
            }

            override fun onDisconnect(client: SocketClient){
                SocketEvent.Bungee.Client.Disconnected().apply {
                    this.client = client
                    ProxyServer.getInstance().pluginManager.callEvent(this)
                }
            }
        }
    }

}

interface SocketEvent {
    enum class Side{ server, client }
    enum class Type{ message, connected, disconnected, handshake, enabled }
    val side: Side ; val type: Type

    open class Bukkit(
        override val side: Side, override val type: Type
    ): SocketEvent, BukkitEvent() {

        open class Server(override val type: Type): Bukkit(Side.server, type) {
            open class Enabled: Server(Type.enabled){
                lateinit var socket: SocketServer
                lateinit var key: String
            }
            open class Message: Server(Type.message){
                lateinit var messenger: SocketMessenger
                lateinit var message: jsonMap
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
            open class Enabled: Client(Type.enabled){
                lateinit var socket: SocketClient
                lateinit var key: String
            }
            open class Message: Client(Type.message){
                lateinit var client: SocketClient
                lateinit var message: jsonMap
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

    open class Bungee(
        override val side: Side, override val type: Type
    ): SocketEvent, BungeeEvent(){

        open class Server(override val type: Type): Bungee(Side.server, type){
            open class Enabled: Server(Type.enabled){
                lateinit var socket: SocketServer
                lateinit var key: String
            }
            open class Message: Server(Type.message){
                lateinit var messenger: SocketMessenger
                lateinit var message: jsonMap
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
            open class Enabled: Client(Type.enabled){
                lateinit var socket: SocketClient
                lateinit var key: String
            }
            open class Message: Client(Type.message){
                lateinit var client: SocketClient
                lateinit var message: jsonMap
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

fun onClientEnable(plugin: BungeePlugin, listener: SocketClient.(String) -> Unit): BungeeListener{
    return object:BungeeListener{
        @BungeeEventHandler
        fun onEnable(e: SocketEvent.Bungee.Client.Enabled){
            listener(e.socket, e.key)
        }
    }.also { plugin.proxy.pluginManager.registerListener(plugin, it) }
}

fun onServerEnable(plugin: BungeePlugin, listener: SocketServer.(String) -> Unit): BungeeListener{
    return object:BungeeListener{
        @BungeeEventHandler
        fun onEnable(e: SocketEvent.Bungee.Server.Enabled){
            listener(e.socket, e.key)
        }
    }.also { plugin.proxy.pluginManager.registerListener(plugin, it) }
}

fun onServerEnable(plugin: BukkitPlugin, listener: SocketServer.(String) -> Unit): BukkitListener{
    return object:BukkitListener{
        @BukkitEventHandler
        fun onEnable(e: SocketEvent.Bukkit.Server.Enabled){
            listener(e.socket, e.key)
        }
    }.also { plugin.server.pluginManager.registerEvents(it, plugin) }
}

fun onClientEnable(plugin: BukkitPlugin, listener: SocketClient.(String) -> Unit): BukkitListener{
    return object:BukkitListener{
        @BukkitEventHandler
        fun onEnable(e: SocketEvent.Bukkit.Client.Enabled){
            listener(e.socket, e.key)
        }
    }.also { plugin.server.pluginManager.registerEvents(it, plugin) }
}

fun onClientEnable(plugin: BungeePlugin, key: String, listener: SocketClient.() -> Unit): BungeeListener{
    return object:BungeeListener{
        @BungeeEventHandler
        fun onEnable(e: SocketEvent.Bungee.Client.Enabled){
            if(e.key != key) return
            listener(e.socket)
        }
    }.also { plugin.proxy.pluginManager.registerListener(plugin, it) }
}

fun onServerEnable(plugin: BungeePlugin, key: String, listener: SocketServer.() -> Unit): BungeeListener{
    return object:BungeeListener{
        @BungeeEventHandler
        fun onEnable(e: SocketEvent.Bungee.Server.Enabled){
            if(e.key != key) return
            listener(e.socket)
        }
    }.also { plugin.proxy.pluginManager.registerListener(plugin, it) }
}

fun onServerEnable(plugin: BukkitPlugin, key: String, listener: SocketServer.() -> Unit): BukkitListener{
    return object:BukkitListener{
        @BukkitEventHandler
        fun onEnable(e: SocketEvent.Bukkit.Server.Enabled){
            if(e.key != key) return
            listener(e.socket)
        }
    }.also { plugin.server.pluginManager.registerEvents(it, plugin) }
}

fun onClientEnable(plugin: BukkitPlugin, key: String, listener: SocketClient.() -> Unit): BukkitListener{
    return object:BukkitListener{
        @BukkitEventHandler
        fun onEnable(e: SocketEvent.Bukkit.Client.Enabled){
            if(e.key != key) return
            listener(e.socket)
        }
    }.also { plugin.server.pluginManager.registerEvents(it, plugin) }
}

fun SocketClient.onMessage(plugin: BungeePlugin, listener: SocketClient.(String, jsonMap) -> Unit): BungeeListener{
    val socket = this;
    return object:BungeeListener{
        @BungeeEventHandler
        fun onMessage(e: SocketEvent.Bungee.Client.Message){
            if(e.client != socket) return
            listener(e.channel, e.message)
        }
    }.also { plugin.proxy.pluginManager.registerListener(plugin, it) }
}

fun SocketClient.onMessage(plugin: BungeePlugin, channel: String, listener: SocketClient.(jsonMap) -> Unit): BungeeListener{
    val socket = this;
    return object:BungeeListener{
        @BungeeEventHandler
        fun onMessage(e: SocketEvent.Bungee.Client.Message){
            if(e.client != socket) return
            if(e.channel != channel) return
            listener(e.message)
        }
    }.also { plugin.proxy.pluginManager.registerListener(plugin, it) }
}

fun SocketClient.onHandshake(plugin: BungeePlugin, listener: SocketClient.() -> Unit): BungeeListener{
    val socket = this;
    return object:BungeeListener{
        @BungeeEventHandler
        fun onHandshake(e: SocketEvent.Bungee.Client.Handshake){
            if(e.client != socket) return
            listener()
        }
    }.also { plugin.proxy.pluginManager.registerListener(plugin, it) }
}

fun SocketClient.onConnect(plugin: BungeePlugin, listener: SocketClient.() -> Unit): BungeeListener{
    val socket = this;
    return object:BungeeListener{
        @BungeeEventHandler
        fun onConnect(e: SocketEvent.Bungee.Client.Connected){
            if(e.client != socket) return
            listener()
        }
    }.also { plugin.proxy.pluginManager.registerListener(plugin, it) }
}

fun SocketClient.onDisconnect(plugin: BungeePlugin, listener: SocketClient.() -> Unit): BungeeListener{
    val socket = this;
    return object:BungeeListener{
        @BungeeEventHandler
        fun onDisconnect(e: SocketEvent.Bungee.Client.Disconnected){
            if(e.client != socket) return
            listener()
        }
    }.also { plugin.proxy.pluginManager.registerListener(plugin, it) }
}

fun SocketClient.onMessage(plugin: BukkitPlugin, listener: SocketClient.(String, jsonMap) -> Unit): BukkitListener{
    val socket = this
    return object: BukkitListener{
        @BukkitEventHandler
        fun onMessage(e: SocketEvent.Bukkit.Client.Message){
            if(e.client != socket) return;
            listener(e.channel, e.message)
        }
    }.also { plugin.server.pluginManager.registerEvents(it, plugin) }
}

fun SocketClient.onMessage(plugin: BukkitPlugin, channel: String, listener: SocketClient.(jsonMap) -> Unit){
    val socket = this
    plugin.server.pluginManager.registerEvents(object: BukkitListener{
        @BukkitEventHandler
        fun onMessage(e: SocketEvent.Bukkit.Client.Message){
            if(e.client != socket) return
            if(e.channel != channel) return
            listener(e.message)
        }
    }, plugin)
}

fun SocketClient.onHandshake(plugin: BukkitPlugin, listener: SocketClient.() -> Unit){
    val socket = this
    plugin.server.pluginManager.registerEvents(object: BukkitListener{
        @BukkitEventHandler
        fun onHandshake(e: SocketEvent.Bukkit.Client.Handshake){
            if(e.client != socket) return;
            listener()
        }
    }, plugin)
}

fun SocketClient.onConnect(plugin: BukkitPlugin, listener: SocketClient.() -> Unit): BukkitListener{
    val socket = this
    return object: BukkitListener{
        @BukkitEventHandler
        fun onConnect(e: SocketEvent.Bukkit.Client.Connected){
            if(e.client != socket) return;
            listener()
        }
    }.also { plugin.server.pluginManager.registerEvents(it, plugin) }
}

fun SocketClient.onDisconnect(plugin: BukkitPlugin, listener: SocketClient.() -> Unit){
    val socket = this
    plugin.server.pluginManager.registerEvents(object: BukkitListener{
        @BukkitEventHandler
        fun onDisconnect(e: SocketEvent.Bukkit.Client.Disconnected){
            if(e.client != socket) return;
            listener()
        }
    }, plugin)
}

fun SocketServer.onHandshake(plugin: BukkitPlugin, listener: SocketMessenger.(String) -> Unit): BukkitListener{
    val socket = this
    return object: BukkitListener{
        @BukkitEventHandler
        fun onHandshake(e: SocketEvent.Bukkit.Server.Handshake){
            if(e.messenger.server != socket) return
            listener(e.messenger, e.messenger.target.name ?: return)
        }
    }.also { plugin.server.pluginManager.registerEvents(it, plugin) }
}

fun SocketServer.onHandshake(plugin: BukkitPlugin, name: String, listener: SocketMessenger.() -> Unit): BukkitListener{
    val socket = this
    return object: BukkitListener{
        @BukkitEventHandler
        fun onHandshake(e: SocketEvent.Bukkit.Server.Handshake){
            if(e.messenger.server != socket) return
            if(e.messenger.target.name != name) return
            listener(e.messenger)
        }
    }.also { plugin.server.pluginManager.registerEvents(it, plugin) }
}

fun SocketServer.onConnect(plugin: BukkitPlugin, listener: SocketMessenger.() -> Unit): BukkitListener{
    val socket = this
    return object: BukkitListener{
        @BukkitEventHandler
        fun onConnect(e: SocketEvent.Bukkit.Server.Connected){
            if(e.messenger.server != socket) return
            listener(e.messenger)
        }
    }.also { plugin.server.pluginManager.registerEvents(it, plugin) }
}

fun SocketServer.onDisconnect(plugin: BukkitPlugin, listener: SocketMessenger.(String) -> Unit): BukkitListener{
    val socket = this
    return object: BukkitListener{
        @BukkitEventHandler
        fun onDisconnect(e: SocketEvent.Bukkit.Server.Disconnected){
            if(e.messenger.server != socket) return
            listener(e.messenger, e.messenger.target.name ?: return)
        }
    }.also { plugin.server.pluginManager.registerEvents(it, plugin) }
}

fun SocketMessenger.onMessage(plugin: BukkitPlugin, listener: SocketMessenger.(String, jsonMap) -> Unit): BukkitListener {
    val mess = this
    return object: BukkitListener{
        @BukkitEventHandler
        fun onMessage(e: SocketEvent.Bukkit.Server.Message){
            if(e.messenger != mess) return
            listener(e.messenger, e.channel, e.message)
        }
    }.also { plugin.server.pluginManager.registerEvents(it, plugin) }
}

fun SocketMessenger.onMessage(plugin: BukkitPlugin, channel: String, listener: SocketMessenger.(jsonMap) -> Unit): BukkitListener {
    val mess = this
    return object: BukkitListener{
        @BukkitEventHandler
        fun onMessage(e: SocketEvent.Bukkit.Server.Message){
            if(e.messenger != mess) return
            if(e.channel != channel) return
            listener(e.messenger, e.message)
        }
    }.also { plugin.server.pluginManager.registerEvents(it, plugin) }
}

fun SocketServer.onHandshake(plugin: BungeePlugin, name: String, listener: SocketMessenger.() -> Unit): BungeeListener {
    val socket = this
    return object: BungeeListener{
        @BungeeEventHandler
        fun onHandshake(e: SocketEvent.Bungee.Server.Handshake){
            if(e.messenger.server != socket) return;
            if(e.messenger.target.name != name) return
            listener(e.messenger)
        }
    }.also { plugin.proxy.pluginManager.registerListener(plugin, it) }
}

fun SocketServer.onHandshake(plugin: BungeePlugin, listener: SocketMessenger.(String) -> Unit): BungeeListener{
    val socket = this
    return object: BungeeListener{
        @BungeeEventHandler
        fun onHandshake(e: SocketEvent.Bungee.Server.Handshake){
            if(e.messenger.server != socket) return;
            listener(e.messenger, e.messenger.target.name ?: return)
        }
    }.also { plugin.proxy.pluginManager.registerListener(plugin, it) }
}

fun SocketServer.onConnect(plugin: BungeePlugin, listener: SocketMessenger.() -> Unit): BungeeListener{
    val socket = this
    return object: BungeeListener{
        @BungeeEventHandler
        fun onConnect(e: SocketEvent.Bungee.Server.Connected){
            if(e.messenger.server != socket) return;
            listener(e.messenger)
        }
    }.also { plugin.proxy.pluginManager.registerListener(plugin, it) }
}

fun SocketServer.onDisconnect(plugin: BungeePlugin, listener: SocketMessenger.(String) -> Unit): BungeeListener{
    val socket = this
    return object: BungeeListener {
        @BungeeEventHandler
        fun onDisconnect(e: SocketEvent.Bungee.Server.Disconnected) {
            if (e.messenger.server != socket) return;
            listener(e.messenger, e.messenger.target.name ?: return)
        }
    }.also { plugin.proxy.pluginManager.registerListener(plugin, it) }
}

fun SocketMessenger.onMessage(plugin: BungeePlugin, listener: SocketMessenger.(String, jsonMap) -> Unit): BungeeListener{
    val mess = this
    return object: BungeeListener{
        @BungeeEventHandler
        fun onMessage(e: SocketEvent.Bungee.Server.Message){
            if(e.messenger != mess) return
            listener(e.messenger, e.channel, e.message)
        }
    }.also { plugin.proxy.pluginManager.registerListener(plugin, it) }
}

fun SocketMessenger.onMessage(plugin: BungeePlugin, channel: String, listener: SocketMessenger.(jsonMap) -> Unit): BungeeListener{
    val mess = this
    return object: BungeeListener{
        @BungeeEventHandler
        fun onMessage(e: SocketEvent.Bungee.Server.Message){
            if(e.messenger != mess) return
            if(e.channel != channel) return
            listener(e.messenger, e.message)
        }
    }.also { plugin.proxy.pluginManager.registerListener(plugin, it) }
}