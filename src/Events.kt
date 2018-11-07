package fr.rhaz.minecraft.sockets

import fr.rhaz.minecraft.kotlin.BukkitEvent
import fr.rhaz.minecraft.kotlin.BungeeEvent
import fr.rhaz.sockets.*
import org.bukkit.event.HandlerList

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