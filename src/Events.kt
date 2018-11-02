package fr.rhaz.minecraft

import fr.rhaz.minecraft.kotlin.BungeeEvent
import fr.rhaz.sockets.SocketClient
import fr.rhaz.sockets.SocketMessenger
import fr.rhaz.sockets.SocketServer
import fr.rhaz.sockets.jsonMap
import BukkitSocketEvent

interface SocketEvent {
    enum class Side{ server, client }
    enum class Type{ message, connected, disconnected, handshake, enabled }
    val side: Side ; val type: Type

    open class Bukkit(
        override val side: Side, override val type: Type
    ): SocketEvent, BukkitSocketEvent() {

        open class Server(override val type: Type): Bukkit(Side.server, type) {
            open class Enabled: Server(Type.enabled){
                lateinit var socket: SocketServer
                lateinit var key: String
            }
            open class Message: Server(Type.message){
                lateinit var messenger: SocketMessenger
                lateinit var message: jsonMap
                lateinit var channel: String
            }
            open class Connected: Server(Type.connected){
                lateinit var messenger: SocketMessenger
            }
            open class Disconnected: Server(Type.disconnected){
                lateinit var messenger: SocketMessenger
            }
            open class Handshake: Server(Type.handshake){
                lateinit var messenger: SocketMessenger
            }
        }

        open class Client(override val type: Type): Bukkit(Side.client, type){
            open class Enabled: Client(Type.enabled){
                lateinit var socket: SocketClient
                lateinit var key: String
            }
            open class Message: Client(Type.message){
                lateinit var client: SocketClient
                lateinit var message: jsonMap
                lateinit var channel: String
            }
            open class Connected: Client(Type.connected){
                lateinit var client: SocketClient
            }
            open class Disconnected: Client(Type.disconnected){
                lateinit var client: SocketClient
            }
            open class Handshake: Client(Type.handshake){
                lateinit var client: SocketClient
            }
        }
    }

    open class Bungee(
            override val side: Side, override val type: Type
    ): SocketEvent, BungeeEvent(){

        open class Server(override val type: Type): Bungee(Side.server, type){
            open class Enabled: Server(Type.enabled){
                lateinit var socket: SocketServer
                lateinit var key: String
            }
            open class Message: Server(Type.message){
                lateinit var messenger: SocketMessenger
                lateinit var message: jsonMap
                lateinit var channel: String
            }
            open class Connected: Server(Type.connected){
                lateinit var messenger: SocketMessenger
            }
            open class Disconnected: Server(Type.disconnected){
                lateinit var messenger: SocketMessenger
            }
            open class Handshake: Server(Type.handshake){
                lateinit var messenger: SocketMessenger
            }
        }

        open class Client(override val type: Type): Bungee(Side.client, type){
            open class Enabled: Client(Type.enabled){
                lateinit var socket: SocketClient
                lateinit var key: String
            }
            open class Message: Client(Type.message){
                lateinit var client: SocketClient
                lateinit var message: jsonMap
                lateinit var channel: String
            }
            open class Connected: Client(Type.connected){
                lateinit var client: SocketClient
            }
            open class Disconnected: Client(Type.disconnected){
                lateinit var client: SocketClient
            }
            open class Handshake: Client(Type.handshake){
                lateinit var client: SocketClient
            }
        }
    }
}