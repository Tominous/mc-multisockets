@file:JvmName("Sockets4MC")
@file:JvmMultifileClass
package fr.rhaz.minecraft.sockets

import fr.rhaz.minecraft.kotlin.*
import fr.rhaz.sockets.*

fun BungeePlugin.onSocketServerEnable(listener: SocketServer.(String) -> Unit) =
    listen<SocketEvent.Bungee.Server.Enabled>{
        listener(it.socket, it.key)
    }

fun BungeePlugin.onSocketServerEnable(key: String, listener: SocketServer.() -> Unit) =
    listen<SocketEvent.Bungee.Server.Enabled>{
        if(it.key == key)
        listener(it.socket)
    }



fun BungeePlugin.onSocketClientEnable(listener: SocketClient.(String) -> Unit) =
    listen<SocketEvent.Bungee.Client.Enabled>{
        listener(it.socket, it.key)
    }

fun BungeePlugin.onSocketClientEnable(key: String, listener: SocketClient.() -> Unit) =
    listen<SocketEvent.Bungee.Client.Enabled>{
        if(it.key == key)
        listener(it.socket)
    }



fun BukkitPlugin.onSocketServerEnable(listener: SocketServer.(String) -> Unit) =
    listen<SocketEvent.Bukkit.Server.Enabled>{
        listener(it.socket, it.key)
    }

fun BukkitPlugin.onSocketServerEnable(key: String, listener: SocketServer.() -> Unit) =
    listen<SocketEvent.Bukkit.Server.Enabled>{
        if(it.key == key)
            listener(it.socket)
    }



fun BukkitPlugin.onSocketClientEnable(listener: SocketClient.(String) -> Unit) =
    listen<SocketEvent.Bukkit.Client.Enabled>{
        listener(it.socket, it.key)
    }

fun BukkitPlugin.onSocketClientEnable(key: String, listener: SocketClient.() -> Unit) =
    listen<SocketEvent.Bukkit.Client.Enabled>{
        if(it.key == key)
        listener(it.socket)
    }



fun SocketClient.onMessage(
    plugin: BungeePlugin,
    listener: SocketClient.(String, jsonMap) -> Unit
) = plugin.listen<SocketEvent.Bungee.Client.Message>{
    if(it.client == this)
    listener(it.channel, it.message)
}

fun SocketClient.onMessage(
    plugin: BungeePlugin, channel: String,
    listener: SocketClient.(jsonMap) -> Unit
) = plugin.listen<SocketEvent.Bungee.Client.Message> {
    if(it.client == this)
    if(it.channel == channel)
    listener(it.message)
}

fun SocketClient.onHandshake(
    plugin: BungeePlugin,
    listener: SocketClient.() -> Unit
) = plugin.listen<SocketEvent.Bungee.Client.Handshake> {
    if(it.client == this)
    listener()
}

fun SocketClient.onConnect(
    plugin: BungeePlugin,
    listener: SocketClient.() -> Unit
) = plugin.listen<SocketEvent.Bungee.Client.Connected> {
    if(it.client == this)
    listener()
}

fun SocketClient.onDisconnect(
    plugin: BungeePlugin,
    listener: SocketClient.() -> Unit
) = plugin.listen<SocketEvent.Bungee.Client.Disconnected> {
    if(it.client == this)
    listener()
}



fun SocketClient.onMessage(
    plugin: BukkitPlugin,
    listener: SocketClient.(String, jsonMap) -> Unit
) = plugin.listen<SocketEvent.Bukkit.Client.Message> {
    if(it.client == this)
    listener(it.channel, it.message)
}

fun SocketClient.onMessage(
    plugin: BukkitPlugin, channel: String,
    listener: SocketClient.(jsonMap) -> Unit
) = plugin.listen<SocketEvent.Bukkit.Client.Message> {
    if(it.client == this)
    if(it.channel == channel)
    listener(it.message)
}

fun SocketClient.onHandshake(
    plugin: BukkitPlugin,
    listener: SocketClient.() -> Unit
) = plugin.listen<SocketEvent.Bukkit.Client.Handshake> {
    if(it.client == this)
    listener()
}

fun SocketClient.onConnect(
    plugin: BukkitPlugin,
    listener: SocketClient.() -> Unit
) = plugin.listen<SocketEvent.Bukkit.Client.Connected> {
    if(it.client == this)
    listener()
}

fun SocketClient.onDisconnect(
    plugin: BukkitPlugin,
    listener: SocketClient.() -> Unit
) = plugin.listen<SocketEvent.Bukkit.Client.Disconnected> {
    if(it.client == this)
    listener()
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

fun SocketServer.onMessage(plugin: BukkitPlugin, listener: SocketMessenger.(String, jsonMap) -> Unit): BukkitListener {
    val mess = this
    return object: BukkitListener{
        @BukkitEventHandler
        fun onMessage(e: SocketEvent.Bukkit.Server.Message){
            if(e.messenger != mess) return
            listener(e.messenger, e.channel, e.message)
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