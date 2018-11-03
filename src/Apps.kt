@file:JvmName("Sockets4MC")
@file:JvmMultifileClass
package fr.rhaz.minecraft.sockets

import fr.rhaz.minecraft.kotlin.*
import fr.rhaz.sockets.*

val BukkitPlugin.socketServerApp get() = run here@{
    object: SocketApp.Server() {

        override fun run(runnable: Runnable) {
            schedule(async = true){runnable.run()}
        }

        override fun log(err: String){ if(debug) info(err) }

        override fun onMessage(mess: SocketMessenger, map: jsonMap) {
            SocketEvent.Bukkit.Server.Message().apply {
                messenger = mess
                message = map
                channel = map["channel"] as? String ?: "unknown"
                server.pluginManager.callEvent(this)
            }
        }

        override fun onConnect(mess: SocketMessenger){
            SocketEvent.Bukkit.Server.Connected().apply {
                messenger = mess
                server.pluginManager.callEvent(this)
            }
        }

        override fun onHandshake(mess: SocketMessenger, name: String){
            info("§aThe connection ${mess.name}<->$name is available")
            SocketEvent.Bukkit.Server.Handshake().apply {
                messenger = mess
                server.pluginManager.callEvent(this)
            }
        }

        override fun onDisconnect(mess: SocketMessenger){
            SocketEvent.Bukkit.Server.Disconnected().apply {
                messenger = mess
                server.pluginManager.callEvent(this)
            }
        }
    }
}

val BungeePlugin.socketServerApp get() = run here@{
    object : SocketApp.Server() {

        override fun run(runnable: Runnable) {
            schedule(async = true){runnable.run()}
        }

        override fun log(err: String) { if(debug) info(err) }

        override fun onMessage(mess: SocketMessenger, map: jsonMap) {
            SocketEvent.Bungee.Server.Message().apply {
                messenger = mess
                message = map
                channel = map["channel"] as? String ?: "unknown"
                proxy.pluginManager.callEvent(this)
            }
        }

        override fun onConnect(mess: SocketMessenger) {
            SocketEvent.Bungee.Server.Connected().apply {
                messenger = mess
                proxy.pluginManager.callEvent(this)
            }
        }

        override fun onHandshake(mess: SocketMessenger, name: String) {
            logger.info("&aThe connection ${mess.name}<->$name is available")
            SocketEvent.Bungee.Server.Handshake().apply {
                messenger = mess
                proxy.pluginManager.callEvent(this)
            }
        }

        override fun onDisconnect(mess: SocketMessenger) {
            SocketEvent.Bungee.Server.Disconnected().apply {
                messenger = mess
                proxy.pluginManager.callEvent(this)
            }
        }
    }
}

val BukkitPlugin.socketClientApp get() = run here@ {
    object : SocketApp.Client() {

        override fun run(runnable: Runnable) {
            schedule(async = true){runnable.run()}
        }

        override fun log(err: String) {
            if (debug) info(err)
        }

        override fun onMessage(client: SocketClient, map: jsonMap) {
            SocketEvent.Bukkit.Client.Message().apply {
                this.client = client
                message = map
                channel = map["channel"] as? String ?: "unknown"
                server.pluginManager.callEvent(this)
            }
        }

        override fun onConnect(client: SocketClient) {
            SocketEvent.Bukkit.Client.Connected().apply {
                this.client = client
                server.pluginManager.callEvent(this)
            }
        }

        override fun onHandshake(client: SocketClient) {
            info("&aThe connection ${client.name}<->${client.target.name} is available")
            SocketEvent.Bukkit.Client.Handshake().apply {
                this.client = client
                server.pluginManager.callEvent(this)
            }
        }

        override fun onDisconnect(client: SocketClient) {
            SocketEvent.Bukkit.Client.Disconnected().apply {
                this.client = client
                server.pluginManager.callEvent(this)
            }
        }
    }
}

val BungeePlugin.socketClientApp get() = run here@{
    object: SocketApp.Client(){

        override fun run(runnable: Runnable) {
            schedule(async = true){runnable.run()}
        }

        override fun log(err: String){ if(debug) info(err) }

        override fun onMessage(client: SocketClient, map: jsonMap) {
            SocketEvent.Bungee.Client.Message().apply {
                this.client = client
                message = map
                channel = map["channel"] as? String ?: "unknown"
                proxy.pluginManager.callEvent(this)
            }
        }

        override fun onConnect(client: SocketClient){
            SocketEvent.Bungee.Client.Connected().apply {
                this.client = client
                proxy.pluginManager.callEvent(this)
            }
        }

        override fun onHandshake(client: SocketClient){
            info("&aThe connection ${client.name}<->${client.target.name} is available")
            SocketEvent.Bungee.Client.Handshake().apply {
                this.client = client
                proxy.pluginManager.callEvent(this)
            }
        }

        override fun onDisconnect(client: SocketClient){
            SocketEvent.Bungee.Client.Disconnected().apply {
                this.client = client
                proxy.pluginManager.callEvent(this)
            }
        }
    }
}
