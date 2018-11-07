@file:JvmName("Sockets4MC")
@file:JvmMultifileClass
package fr.rhaz.minecraft.sockets

import fr.rhaz.minecraft.kotlin.BukkitPlugin
import fr.rhaz.minecraft.kotlin.BungeePlugin
import fr.rhaz.minecraft.kotlin.listen
import fr.rhaz.sockets.MultiSocket


fun BungeePlugin.onSocketEnabled(
    listener: MultiSocket.(String) -> Unit
) = listen<SocketEvent.Bungee.Enabled>{
    listener(it.socket, it.id)
}

fun BungeePlugin.onSocketEnabled(
    id: String,
    listener: MultiSocket.() -> Unit
) = listen<SocketEvent.Bungee.Enabled>{
    if(it.id == id)
    listener(it.socket)
}

fun BukkitPlugin.onSocketEnabled(
    listener: MultiSocket.(String) -> Unit
) = listen<SocketEvent.Bukkit.Enabled>{
    listener(it.socket, it.id)
}

fun BukkitPlugin.onSocketEnabled(
    key: String,
    listener: MultiSocket.() -> Unit
) = listen<SocketEvent.Bukkit.Enabled>{
    if(it.id == key)
    listener(it.socket)
}