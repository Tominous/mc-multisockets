import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Event as BungeeEvent
import net.md_5.bungee.api.plugin.Listener as BungeeListener
import net.md_5.bungee.api.plugin.Plugin as BungeePlugin
import net.md_5.bungee.config.YamlConfiguration as BungeeYaml
import net.md_5.bungee.event.EventHandler as BungeeEventHandler
import org.bukkit.event.Event as BukkitEvent
import org.bukkit.event.EventHandler as BukkitEventHandler
import org.bukkit.event.Listener as BukkitListener
import org.bukkit.plugin.java.JavaPlugin as BukkitPlugin

// *** Example for BungeeCord plugin ***
// Let's call our server Alice and our client Bob
//
import fr.rhaz.minecraft.sockets.* // Sockets4MC
import fr.rhaz.sockets.* // RHazSockets

class BungeeTest: BungeePlugin(){

    override fun onEnable() {
        val plugin = this

        // We will try to use the default socket in the S4MC configuration
        // There can be multiple sockets used within the same environment (Bukkit, Bungee)

        // A socket can be a server or a client
        // we don't know if the user has configured its default socket as a server or a client
        // you need to implement each case

        // If the default socket is a server, this will be executed
        onServerEnable(plugin, key = "default") alice@{

            // --- In this context ---
            // this = this@alice = the default socket (which is a server, so Alice)

            // We listen for incoming connections from Bob ("Bob" is the name in the S4MC configuration)
            onHandshake(plugin, name = "Bob") bob@{

                // --- In this context ---
                // this = this@bob = Bob's messenger, can be used to write to Bob
                // this@alice = Alice (our socket server)

                // We save Bob's messenger in order to send him messages later
                val bob = this@bob

                // We listen for incoming messages from Bob over the channel "Test"
                onMessage(plugin, channel = "Test"){

                    // --- In this context ---
                    // this = still Bob's messenger
                    // it = Bob's message

                    // Get the message from the "data" extra
                    val data = it.getExtra<String>("data")
                    if(data == "How are you?")
                        write("Test", "It works!")
                }

                write("Test", "Hello world!")
                // Send him a message

                // You can also send complex objects such as infos about a player
                fun sendPlayerInfo(player: ProxiedPlayer){

                    // Ensure the connection is ready
                    if(!bob.run{ready && handshaked}) return

                    // This message will be sent over the channel "PlayerInfo"
                    bob.write("PlayerInfo", mapOf(
                        "displayname" to player.displayName,
                        // You'll need to do getExtra<String>("displayName")
                        "ping" to player.ping,
                        // You'll need to do getExtra<Int>("ping")
                        "uuid" to player.uniqueId.toString()
                        // It is preferred to use primitives instead of whole objects
                        // So we convert it to the String primitive,
                        // then we will convert it back to an UUID
                        // You'll need to do getExtra<String>("uuid").let{UUID.fromString(it)}
                    ).let{JSONMap(it)})
                }
            }

        }

        // If the default socket is a client, this will be executed
        onClientEnable(plugin, key = "default") bob@{

            // --- In this context ---
            // this = this@bob = our default socket (which is a client, so Bob)

            // So, we are in the context of Bob sending messages to Alice

            // We listen for incoming messages over the channel "Test"
            onMessage(plugin, "Test"){

                // --- In this context ---
                // this = this@bob = still our client
                // it = Alice's message

                val data = it.getExtra<String>("data")

                if(data == "Hello world!")
                    write("Test", "How are you?")

                if(data == "It works!")
                    logger.info("Yay! It works!")
            }
        }

    }
}