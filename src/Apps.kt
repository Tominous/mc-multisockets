@file:JvmName("Sockets4MC")
@file:JvmMultifileClass

package fr.rhaz.minecraft.sockets

import fr.rhaz.minecraft.kotlin.BukkitPlugin
import fr.rhaz.minecraft.kotlin.BungeePlugin
import fr.rhaz.sockets.SocketApp
import fr.rhaz.sockets.SocketClient
import fr.rhaz.sockets.SocketMessenger
import fr.rhaz.sockets.jsonMap

val BukkitPlugin.socketServerApp get() = run here@{
    object: SocketApp.Server() {

        override fun run(runnable: Runnable) {
            org.bukkit.Bukkit.getScheduler().runTaskAsynchronously(this@here, runnable)
        }

        override fun log(err: String){ if(fr.rhaz.minecraft.sockets.debug) logger.info(err) }

        override fun onMessage(mess: SocketMessenger, map: jsonMap) {
            fr.rhaz.minecraft.sockets.SocketEvent.Bukkit.Server.Message().apply {
                messenger = mess
                message = map
                channel = map["channel"] as? String ?: "unknown"
                org.bukkit.Bukkit.getPluginManager().callEvent(this)
            }
        }

        override fun onConnect(mess: SocketMessenger){
            fr.rhaz.minecraft.sockets.SocketEvent.Bukkit.Server.Connected().apply {
                messenger = mess
                org.bukkit.Bukkit.getPluginManager().callEvent(this)
            }
        }

        override fun onHandshake(mess: SocketMessenger, name: String){
            logger.info("§aThe connection ${mess.name}<->$name is available")
            fr.rhaz.minecraft.sockets.SocketEvent.Bukkit.Server.Handshake().apply {
                messenger = mess
                org.bukkit.Bukkit.getPluginManager().callEvent(this)
            }
        }

        override fun onDisconnect(mess: SocketMessenger){
            fr.rhaz.minecraft.sockets.SocketEvent.Bukkit.Server.Disconnected().apply {
                messenger = mess
                org.bukkit.Bukkit.getPluginManager().callEvent(this)
            }
        }
    }
}

val BungeePlugin.socketServerApp get() = run here@{
    object : SocketApp.Server() {

        override fun run(runnable: Runnable) {
            net.md_5.bungee.api.ProxyServer.getInstance().scheduler.runAsync(this@here, runnable)
        }

        override fun log(err: String) {
            if (fr.rhaz.minecraft.sockets.debug) logger.info(err)
        }

        override fun onMessage(mess: SocketMessenger, map: jsonMap) {
            fr.rhaz.minecraft.sockets.SocketEvent.Bungee.Server.Message().apply {
                messenger = mess
                message = map
                channel = map["channel"] as? String ?: "unknown"
                net.md_5.bungee.api.ProxyServer.getInstance().pluginManager.callEvent(this)
            }
        }

        override fun onConnect(mess: SocketMessenger) {
            fr.rhaz.minecraft.sockets.SocketEvent.Bungee.Server.Connected().apply {
                messenger = mess
                net.md_5.bungee.api.ProxyServer.getInstance().pluginManager.callEvent(this)
            }
        }

        override fun onHandshake(mess: SocketMessenger, name: String) {
            logger.info("§aThe connection ${mess.name}<->${name} is available")
            fr.rhaz.minecraft.sockets.SocketEvent.Bungee.Server.Handshake().apply {
                messenger = mess
                net.md_5.bungee.api.ProxyServer.getInstance().pluginManager.callEvent(this)
            }
        }

        override fun onDisconnect(mess: SocketMessenger) {
            fr.rhaz.minecraft.sockets.SocketEvent.Bungee.Server.Disconnected().apply {
                messenger = mess
                net.md_5.bungee.api.ProxyServer.getInstance().pluginManager.callEvent(this)
            }
        }
    }
}

val BukkitPlugin.socketClientApp get() = run here@ {
    object : SocketApp.Client() {

        override fun run(runnable: Runnable) {
            org.bukkit.Bukkit.getScheduler().runTaskAsynchronously(this@here, runnable)
        }

        override fun log(err: String) {
            if (fr.rhaz.minecraft.sockets.debug) logger.info(err)
        }

        override fun onMessage(client: SocketClient, map: jsonMap) {
            fr.rhaz.minecraft.sockets.SocketEvent.Bukkit.Client.Message().apply {
                this.client = client
                message = map
                channel = map["channel"] as? String ?: "unknown"
                org.bukkit.Bukkit.getPluginManager().callEvent(this)
            }
        }

        override fun onConnect(client: SocketClient) {
            fr.rhaz.minecraft.sockets.SocketEvent.Bukkit.Client.Connected().apply {
                this.client = client
                org.bukkit.Bukkit.getPluginManager().callEvent(this)
            }
        }

        override fun onHandshake(client: SocketClient) {
            logger.info("§aThe connection ${client.name}<->${client.target.name} is available")
            fr.rhaz.minecraft.sockets.SocketEvent.Bukkit.Client.Handshake().apply {
                this.client = client
                org.bukkit.Bukkit.getPluginManager().callEvent(this)
            }
        }

        override fun onDisconnect(client: SocketClient) {
            fr.rhaz.minecraft.sockets.SocketEvent.Bukkit.Client.Disconnected().apply {
                this.client = client
                org.bukkit.Bukkit.getPluginManager().callEvent(this)
            }
        }
    }
}

val BungeePlugin.socketClientApp get() = run here@{
    object: SocketApp.Client(){

        override fun run(runnable: Runnable) {
            net.md_5.bungee.api.ProxyServer.getInstance().scheduler.runAsync(this@here, runnable)
        }

        override fun log(err: String){ if(fr.rhaz.minecraft.sockets.debug) logger.info(err) }

        override fun onMessage(client: SocketClient, map: jsonMap) {
            fr.rhaz.minecraft.sockets.SocketEvent.Bungee.Client.Message().apply {
                this.client = client
                message = map
                channel = map["channel"] as? String ?: "unknown"
                net.md_5.bungee.api.ProxyServer.getInstance().pluginManager.callEvent(this)
            }
        }

        override fun onConnect(client: SocketClient){
            fr.rhaz.minecraft.sockets.SocketEvent.Bungee.Client.Connected().apply {
                this.client = client
                net.md_5.bungee.api.ProxyServer.getInstance().pluginManager.callEvent(this)
            }
        }

        override fun onHandshake(client: SocketClient){
            logger.info("§aThe connection ${client.name}<->${client.target.name} is available")
            fr.rhaz.minecraft.sockets.SocketEvent.Bungee.Client.Handshake().apply {
                this.client = client
                net.md_5.bungee.api.ProxyServer.getInstance().pluginManager.callEvent(this)
            }
        }

        override fun onDisconnect(client: SocketClient){
            fr.rhaz.minecraft.sockets.SocketEvent.Bungee.Client.Disconnected().apply {
                this.client = client
                net.md_5.bungee.api.ProxyServer.getInstance().pluginManager.callEvent(this)
            }
        }
    }
}
