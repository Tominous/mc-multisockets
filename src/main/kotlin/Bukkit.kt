package hazae41.minecraft.sockets.bukkit

import hazae41.minecraft.kotlin.bukkit.*
import hazae41.minecraft.kotlin.textOf
import hazae41.minecraft.sockets.Sockets
import hazae41.minecraft.sockets.Sockets.onSocketEnable
import hazae41.sockets.AES
import hazae41.sockets.Socket
import hazae41.sockets.aes
import hazae41.sockets.readMessage
import io.ktor.http.cio.websocket.send
import net.md_5.bungee.api.chat.ClickEvent
import java.util.concurrent.TimeUnit

class Plugin : BukkitPlugin() {

    override fun onEnable() {
        update(15938)

        init(Config)

        SocketsConfig.config.keys.forEach {
            Sockets.sockets[it] = start(SocketConfig(it))
        }

        command("sockets", permission = "sockets.list"){ args ->
            msg("Available sockets:")
            msg(Sockets.sockets.keys.joinToString(", "))
        }

        command("socket", permission = "sockets.info"){ args ->

            val name = args.getOrNull(0)
            ?: return@command msg("/socket <name> | /socket <name> key")

            val socket = Sockets.sockets[name]
            ?: return@command msg("Unknown socket")

            if(args.getOrNull(1) == "key"){
                val key = socket.key
                ?: return@command msg("This socket has no key")

                val keyStr = AES.toString(key)
                textOf("Click here to copy: $keyStr"){
                    clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, keyStr)
                    msg(this)
                }
            }

            else {
                msg("Available connections for $name:")
                msg(socket.connections.keys.joinToString(", "))
            }
        }

        if(Config.test){
           
            onSocketEnable {
                onConversation("/test"){
                    val (encrypt) = aes()
                    send("it works!".encrypt())
                }

                onConversation("/test/hello"){
                    println(readMessage())
                    send("hello back from $name")
                }
            }
        }
    }
}

object Config: ConfigFile("config"){
    val test by boolean("test")
}

object SocketsConfig: ConfigSection(Config, "sockets")

class SocketConfig(name: String): ConfigSection(SocketsConfig, name){
    val port by int("port")
    var key by string("key")
    val peers by stringList("peers")
}

fun Plugin.start(config: SocketConfig): Socket {
    val key = config.key.aes()
    if(config.key.isBlank()) config.key = AES.toString(key)

    val socket = Socket(config.path, config.port, key)
    socket.connectTo(config.peers)
    Sockets.socketsNotifiers.forEach { it(socket) }

    schedule(delay = 0, unit = TimeUnit.SECONDS) {
        socket.start()
        info("Started ${config.path}")
    }

    return socket
}
