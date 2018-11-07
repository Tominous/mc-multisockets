@file:JvmName("Sockets4MC")
@file:JvmMultifileClass
package fr.rhaz.minecraft.sockets

import fr.rhaz.sockets.MultiSocket

val sockets = mutableMapOf<String, MultiSocket>()

fun getSocket(key: String) = sockets[key]

fun disable(){
    sockets.values.forEach{it.interrupt()}
    sockets.clear()
}