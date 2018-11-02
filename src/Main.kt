@file:JvmName("Sockets4MC")
@file:JvmMultifileClass
package fr.rhaz.minecraft

import fr.rhaz.sockets.SocketHandler

var debug = false

val sockets = mutableMapOf<String, SocketHandler>()

fun getSocket(key: String) = sockets[key]

fun disable(){
    sockets.values.forEach{it.interrupt()}
    sockets.clear()
}