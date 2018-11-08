@file:JvmName("Sockets4MC")
@file:JvmMultifileClass
package fr.rhaz.minecraft.sockets

import fr.rhaz.minecraft.kotlin.BukkitPlugin
import fr.rhaz.minecraft.kotlin.BungeePlugin
import fr.rhaz.minecraft.kotlin.listen
import fr.rhaz.sockets.MultiSocket
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