@file:JvmName("Sockets4MC")
@file:JvmMultifileClass

package fr.rhaz.minecraft.sockets

import fr.rhaz.sockets.SocketClient
import fr.rhaz.sockets.SocketHandler
import fr.rhaz.sockets.SocketServer

var debug = false

val sockets = mutableMapOf<String, SocketHandler>()


fun disable() = sockets.values.forEach {
    when(it){
        is SocketClient -> it.interrupt()
        is SocketServer -> it.close()
    }
}.also { sockets.clear() }