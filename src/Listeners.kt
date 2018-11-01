@file:JvmName("Sockets4MC")
@file:JvmMultifileClass
package fr.rhaz.minecraft.sockets

import fr.rhaz.minecraft.kotlin.*
import fr.rhaz.sockets.*

fun onSocketClientEnable(plugin: BungeePlugin, listener: SocketClient.(String) -> Unit): BungeeListener{
    return object:BungeeListener{
        @BungeeEventHandler
        fun onEnable(e: SocketEvent.Bungee.Client.Enabled){
            listener(e.socket, e.key)
        }
    }.also { plugin.proxy.pluginManager.registerListener(plugin, it) }
}

fun onSocketServerEnable(plugin: BungeePlugin, listener: SocketServer.(String) -> Unit): BungeeListener{
    return object:BungeeListener{
        @BungeeEventHandler
        fun onEnable(e: SocketEvent.Bungee.Server.Enabled){
            listener(e.socket, e.key)
        }
    }.also { plugin.proxy.pluginManager.registerListener(plugin, it) }
}

fun onSocketServerEnable(plugin: BukkitPlugin, listener: SocketServer.(String) -> Unit): BukkitListener{
    return object:BukkitListener{
        @BukkitEventHandler
        fun onEnable(e: SocketEvent.Bukkit.Server.Enabled){
            listener(e.socket, e.key)
        }
    }.also { plugin.server.pluginManager.registerEvents(it, plugin) }
}

fun onSocketClientEnable(plugin: BukkitPlugin, listener: SocketClient.(String) -> Unit): BukkitListener{
    return object:BukkitListener{
        @BukkitEventHandler
        fun onEnable(e: SocketEvent.Bukkit.Client.Enabled){
            listener(e.socket, e.key)
        }
    }.also { plugin.server.pluginManager.registerEvents(it, plugin) }
}

fun onSocketClientEnable(plugin: BungeePlugin, key: String, listener: SocketClient.() -> Unit): BungeeListener{
    return object:BungeeListener{
        @BungeeEventHandler
        fun onEnable(e: SocketEvent.Bungee.Client.Enabled){
            if(e.key != key) return
            listener(e.socket)
        }
    }.also { plugin.proxy.pluginManager.registerListener(plugin, it) }
}

fun onSocketServerEnable(plugin: BungeePlugin, key: String, listener: SocketServer.() -> Unit): BungeeListener{
    return object:BungeeListener{
        @BungeeEventHandler
        fun onEnable(e: SocketEvent.Bungee.Server.Enabled){
            if(e.key != key) return
            listener(e.socket)
        }
    }.also { plugin.proxy.pluginManager.registerListener(plugin, it) }
}

fun onSocketServerEnable(plugin: BukkitPlugin, key: String, listener: SocketServer.() -> Unit): BukkitListener{
    return object:BukkitListener{
        @BukkitEventHandler
        fun onEnable(e: SocketEvent.Bukkit.Server.Enabled){
            if(e.key != key) return
            listener(e.socket)
        }
    }.also { plugin.server.pluginManager.registerEvents(it, plugin) }
}

fun onSocketClientEnable(plugin: BukkitPlugin, key: String, listener: SocketClient.() -> Unit): BukkitListener{
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