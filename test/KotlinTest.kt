import fr.rhaz.minecraft.kotlin.* // Kotlin4MC
import fr.rhaz.minecraft.sockets.* // Sockets4MC
import fr.rhaz.sockets.* // Sockets.kt
import net.md_5.bungee.api.connection.ProxiedPlayer
import org.bukkit.entity.Player

class BungeeTest: BungeePlugin(){

    override fun onEnable() {
        onSocketEnable(id = "default"){
            info("Socket #default is available")

            // When any connection is ready
            onReady{
                info("Connection to $targetName is available")
                // Send a message over the channel "Test"
                msg(channel = "Test", data = "How are you?")
            }

            // When a message is received over the channel "Test"
            onMessage(channel = "Test"){ msg ->
                if(msg["data"] == "It works fine!")
                    info("$targetName works fine!")
            }
        }
    }

    fun sendInfo(player: ProxiedPlayer) {
        val serverName = player.server.info.name

        val socket = getSocket(id = "default")
        ?: throw Exception("Socket #default is not available")

        val connection = socket.getConnection(target = serverName)
        ?: throw Exception("Connection to $serverName is not available")

        connection.msg(channel = "PlayerInfo", data = jsonMap(
            "uuid" to player.uniqueId,
            "name" to player.name,
            "displayname" to player.displayName
        ))
    }
}

class BukkitTest: BukkitPlugin(){
    override fun onEnable() {
        onSocketEnable(id = "default"){
            info("Socket #default is available")

            onReady {
                info("Connection to $targetName is available")
            }

            onMessage(channel = "Test"){ msg ->
                if(msg["data"] == "How are you?")
                msg(channel = "Test", data = "It works fine!")
            }
        }
    }

    fun sendInfo(player: Player){
        val proxyName = "MyProxy"

        val socket = getSocket(id = "default")
        ?: throw Exception("Socket #default is not available")

        val connection = socket.getConnection(target = proxyName)
        ?: throw Exception("Connection to $proxyName is not available")

        connection.msg(channel = "PlayerInfo", data = jsonMap(
            "displayname" to player.displayName,
            "uuid" to player.uniqueId,
            "name" to player.name,
            "exp" to player.exp
        ))
    }
}