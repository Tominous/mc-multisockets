@file:JvmName("Sockets4MC")
@file:JvmMultifileClass
package fr.rhaz.minecraft.sockets

import fr.rhaz.sockets.MultiSocket

val sockets = mutableMapOf<String, MultiSocket>()

fun getSocket(id: String) = sockets[id]

fun disable(){
    sockets.values.forEach{it.interrupt()}
    sockets.clear()
}