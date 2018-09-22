import fr.rhaz.minecraft.SocketEvent
import fr.rhaz.minecraft.sockets
import fr.rhaz.sockets.JSONMap
import fr.rhaz.sockets.SocketClient
import fr.rhaz.sockets.SocketMessenger
import fr.rhaz.sockets.SocketServer
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
class BungeeTest: BungeePlugin(){

    override fun onEnable() {

        val default = sockets.sockets["default"]
        // Get the default socket in the S4MC configuration
        // There can be multiple sockets used within the same environment (Bukkit, Bungee)

        // A socket can be a server or a client
        // we don't know if the user has configured its default socket as a server or a client
        // you need to implement each case
        when(default){

            // If the default socket is a server
            is SocketServer -> {

                // Here, the default socket is a server, so let's call it Alice
                val alice = default

                // Bob will only be initialized if Bob has already sent a "Hello world!" message
                lateinit var bob: SocketMessenger

                // We register an event for incoming messages from clients
                proxy.pluginManager.registerListener(this, object:BungeeListener{
                    // The events are very simple to use
                    // They follow the syntax SocketEvent.environment.side.type
                    // Your IDE shows you all possible events when you start typing SocketEvent
                    @BungeeEventHandler
                    fun onMessage(e: SocketEvent.Bungee.Server.Message){

                        if(e.messenger.server != alice) return
                        // Ignore other servers than Alice
                        // A server owns multiple messengers for each connected client
                        // do not misuse the two, use .server when checking

                        if(e.channel != "Test") return
                        // Only use our channel
                        // Other channels will be ignored

                        val name = e.message.getExtra<String>("name")
                        // Get the name of the client

                        if(name != "Bob") return;
                        // We only want to process messages sent by Bob
                        // Other names will be ignored

                        val data = e.message.getExtra<String>("data")
                        // Get the string named "data"

                        if(data == "Hello world!")
                            e.messenger.write("Test", "It works!")
                        // Using write(channel, data: String)
                        // will write a message in the same channel
                        // and put your message in the "data" extra
                        // You can do e.messenger.write("Test", JSONMap("data", "It works!"))
                        // but it is longer

                        // Let's save this messenger as Bob in order to send messages later
                        bob = e.messenger
                    }

                    // You can also send complex objects such as infos about a player
                    // It will only work if Bob has already been saved
                    fun sendPlayerInfo(player: ProxiedPlayer){
                        bob.write("PlayerInfo", JSONMap(
                            "displayName", player.displayName, // You'll need to do getExtra<String>("displayName")
                            "uuid", player.uniqueId.toString(), // You'll need to do getExtra<String>("uuid").let{UUID.fromString(it)}
                            "ping", player.ping // You'll need to do getExtra<Int>("ping")
                        ))
                    }
                })
            }

            // If the default socket is a client
            is SocketClient -> {

                // Here, we are in the context of Bob sending messages to Alice
                // Bob is our default socket
                val bob = default

                proxy.pluginManager.registerListener(this, object:BungeeListener{
                    // Here, we will wait for the connection to be successfull in order to start the conversation
                    // Handshaked means "ready"
                    @BungeeEventHandler
                    fun onHandshaked(e: SocketEvent.Bungee.Client.Handshake){

                        // A client is itself a messenger, you don't have to use .server
                        if(e.client != bob) return
                        // Only start the conversation if OUR socket has handshaked

                        // Here, we start the conversation with a "Hello world!" message on the Test channel
                        e.client.write("Test", "Hello world!")
                    }

                    // Incoming messages from Alice
                    @BungeeEventHandler
                    fun onMessage(e: SocketEvent.Bungee.Client.Message){
                        if(e.client != bob) return
                        if(e.channel != "Test") return

                        val name = e.message.getExtra<String>("name")
                        // Get the name of the server

                        if(name != "Alice") return;
                        // We only want to process messages sent by Bob
                        // Other names will be ignored

                        val data = e.message.getExtra<String>("data")
                        if(data == "It works!")
                            e.client.write("AnotherChannel", "How are you?")
                            // This message will be sent over another channel
                    }
                })
            }
        }

    }
}