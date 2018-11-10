@file:JvmName("Sockets4MC")
@file:JvmMultifileClass
package fr.rhaz.minecraft.sockets

import fr.rhaz.minecraft.kotlin.BukkitEvent
import fr.rhaz.minecraft.kotlin.BungeeEvent
import fr.rhaz.sockets.MultiSocket
import org.bukkit.event.HandlerList

val sockets = mutableMapOf<String, MultiSocket>()

fun getSocket(id: String) = sockets[id]

fun disable(){
    sockets.values.forEach{it.interrupt()}
    sockets.clear()
}

interface SocketEvent {

    object Bukkit{
        open class Enabled: SocketEvent, BukkitEvent(){
            lateinit var socket: MultiSocket
            lateinit var id: String

            override fun getHandlers(): HandlerList = getHandlerList()
            companion object {
                @JvmStatic private val handlers = HandlerList()
                @JvmStatic fun getHandlerList() = handlers
            }
        }
    }

    object Bungee{
        open class Enabled: SocketEvent, BungeeEvent(){
            lateinit var socket: MultiSocket
            lateinit var id: String
        }
    }
}