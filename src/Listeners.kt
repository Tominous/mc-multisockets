@file:JvmName("Sockets4MC")
@file:JvmMultifileClass
package fr.rhaz.minecraft

import fr.rhaz.minecraft.kotlin.*
import fr.rhaz.sockets.*

fun BungeePlugin.onSocketServerEnable(
    listener: SocketServer.(String) -> Unit
) = listen<SocketEvent.Bungee.Server.Enabled>{
    listener(it.socket, it.key)
}

fun BungeePlugin.onSocketServerEnable(
    key: String,
    listener: SocketServer.() -> Unit
) = listen<SocketEvent.Bungee.Server.Enabled>{
    if(it.key == key)
    listener(it.socket)
}

fun BungeePlugin.onSocketClientEnable(
    listener: SocketClient.(String) -> Unit
) = listen<SocketEvent.Bungee.Client.Enabled>{
    listener(it.socket, it.key)
}

fun BungeePlugin.onSocketClientEnable(
    key: String,
    listener: SocketClient.() -> Unit
) = listen<SocketEvent.Bungee.Client.Enabled>{
    if(it.key == key)
    listener(it.socket)
}

fun BukkitPlugin.onSocketServerEnable(
    listener: SocketServer.(String) -> Unit
) = listen<SocketEvent.Bukkit.Server.Enabled>{
    listener(it.socket, it.key)
}

fun BukkitPlugin.onSocketServerEnable(
    key: String,
    listener: SocketServer.() -> Unit
) = listen<SocketEvent.Bukkit.Server.Enabled>{
    if(it.key == key)
    listener(it.socket)
}

fun BukkitPlugin.onSocketClientEnable(
    listener: SocketClient.(String) -> Unit
) = listen<SocketEvent.Bukkit.Client.Enabled>{
    listener(it.socket, it.key)
}

fun BukkitPlugin.onSocketClientEnable(
    key: String,
    listener: SocketClient.() -> Unit
) = listen<SocketEvent.Bukkit.Client.Enabled>{
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

fun SocketServer.onHandshake(
    plugin: BukkitPlugin,
    listener: SocketMessenger.(String) -> Unit
) = plugin.listen<SocketEvent.Bukkit.Server.Handshake>{
    if(it.messenger.server == this)
    listener(it.messenger, it.messenger.target.name)
}

fun SocketServer.onHandshake(
    plugin: BukkitPlugin, name: String,
    listener: SocketMessenger.() -> Unit
) = plugin.listen<SocketEvent.Bukkit.Server.Handshake> {
    if(it.messenger.server == this)
    if(it.messenger.target.name == name)
    listener(it.messenger)
}

fun SocketServer.onConnect(
    plugin: BukkitPlugin,
    listener: SocketMessenger.() -> Unit
) = plugin.listen<SocketEvent.Bukkit.Server.Connected> {
    if(it.messenger.server == this)
    listener(it.messenger)
}

fun SocketServer.onDisconnect(
    plugin: BukkitPlugin,
    listener: SocketMessenger.(String) -> Unit
) = plugin.listen<SocketEvent.Bukkit.Server.Disconnected> {
    if(it.messenger.server == this)
    listener(it.messenger, it.messenger.target.name)
}

fun SocketMessenger.onMessage(
    plugin: BukkitPlugin,
    listener: SocketMessenger.(String, jsonMap) -> Unit
) = plugin.listen<SocketEvent.Bukkit.Server.Message> {
    if(it.messenger == this)
    listener(it.messenger, it.channel, it.message)
}

fun SocketMessenger.onMessage(
    plugin: BukkitPlugin, channel: String,
    listener: SocketMessenger.(jsonMap) -> Unit
) = plugin.listen<SocketEvent.Bukkit.Server.Message> {
    if(it.messenger == this)
    if(it.channel == channel)
    listener(it.messenger, it.message)
}

fun SocketServer.onHandshake(
    plugin: BungeePlugin, name: String,
    listener: SocketMessenger.() -> Unit
) = plugin.listen<SocketEvent.Bungee.Server.Handshake> {
    if(it.messenger.server == this)
    if(it.messenger.target.name != name)
    listener(it.messenger)
}

fun SocketServer.onHandshake(
    plugin: BungeePlugin,
    listener: SocketMessenger.(String) -> Unit
) = plugin.listen<SocketEvent.Bungee.Server.Handshake> {
    if(it.messenger.server == this)
    listener(it.messenger, it.messenger.target.name)
}

fun SocketServer.onConnect(
    plugin: BungeePlugin,
    listener: SocketMessenger.() -> Unit
) = plugin.listen<SocketEvent.Bungee.Server.Connected> {
    if(it.messenger.server == this)
    listener(it.messenger)
}

fun SocketServer.onDisconnect(
    plugin: BungeePlugin,
    listener: SocketMessenger.(String) -> Unit
) = plugin.listen<SocketEvent.Bungee.Server.Disconnected> {
    if (it.messenger.server == this)
    listener(it.messenger, it.messenger.target.name)
}

fun SocketMessenger.onMessage(
    plugin: BungeePlugin,
    listener: SocketMessenger.(String, jsonMap) -> Unit
) = plugin.listen<SocketEvent.Bungee.Server.Message> {
    if(it.messenger == this)
    listener(it.messenger, it.channel, it.message)
}

fun SocketMessenger.onMessage(
    plugin: BungeePlugin, channel: String,
    listener: SocketMessenger.(jsonMap) -> Unit
) = plugin.listen<SocketEvent.Bungee.Server.Message> {
    if(it.messenger == this)
    if(it.channel == channel)
    listener(it.messenger, it.message)
}