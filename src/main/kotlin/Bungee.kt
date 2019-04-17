package hazae41.minecraft.sockets.bungee

import hazae41.minecraft.kotlin.bungee.*
import hazae41.minecraft.kotlin.textOf
import hazae41.minecraft.sockets.Sockets.onSocketEnable
import hazae41.minecraft.sockets.Sockets.sockets
import hazae41.minecraft.sockets.Sockets.socketsNotifiers
import hazae41.sockets.*
import io.ktor.http.cio.websocket.send
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ClickEvent.Action.SUGGEST_COMMAND
import java.util.concurrent.TimeUnit.SECONDS

class Plugin : BungeePlugin(){

    override fun onEnable() {
        init(Config)

        SocketsConfig.config.keys.forEach {
            sockets[it] = start(SocketConfig(it))
        }

        command("sockets", permission = "sockets.list"){ args ->
            msg("Available sockets:")
            msg(sockets.keys.joinToString(", "))
        }

        command("socket", permission = "sockets.info"){ args ->

            val name = args.getOrNull(0)
            ?: return@command msg("/socket <name> | /socket <name> key")

            val socket = sockets[name]
            ?: return@command msg("Unknown socket")

            if(args.getOrNull(1) == "key"){
                val key = socket.key
                ?: return@command msg("This socket has no key")

                val keyStr = AES.toString(key)
                textOf("Click here to copy: $keyStr"){
                    clickEvent = ClickEvent(SUGGEST_COMMAND, keyStr)
                    msg(this)
                }
            }

            else {
                msg("Available connections for $name:")
                msg(socket.connections.keys.joinToString(", "))
            }
        }

        if(Config.test){
            command("test"){ args ->
                val (socket, name, path) = args
                val key = SocketConfig(socket).key.aes()
                val connection = sockets[socket]?.connections?.get(name) ?: return@command
                connection.conversation("/$path"){
                    val (_, decrypt) = aes(key)
                    println(readMessage().decrypt())
                }
            }

            onSocketEnable {

                onConversation("/test"){
                    val (encrypt) = aes()
                    send("it works!".encrypt())
                }

                onConversation("/test/hello"){
                    send("it works!")
                }

                onConnection(filter = "bungee"){
                    conversation("/test/hello"){
                        println(readMessage())
                    }
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
    socketsNotifiers.forEach { it(socket) }

    schedule(delay = 0, unit = SECONDS) {
        socket.start()
        info("Started ${config.path}")
        socket.connectTo(config.peers)
    }

    return socket
}