import fr.rhaz.minecraft.SocketEvent;
import fr.rhaz.sockets.JSONMap;
import fr.rhaz.sockets.SocketClient;
import fr.rhaz.sockets.SocketMessenger;
import fr.rhaz.sockets.SocketServer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import static fr.rhaz.minecraft.Sockets4MCKt.getSockets;

// --- Java example of Bukkit plugin ---
// Let's call our server Alice and our client Bob
//
public class JavaTest extends JavaPlugin {
    @Override
    public void onEnable() {
        Object def = getSockets().getSockets().get("default");
        // Get the default socket in the S4MC configuration

        // A socket may be a server or a client, depending on the configuration
        // You need to implement both cases

        // If it is a server
        if(def instanceof SocketServer){

            // We will register some events
            getServer().getPluginManager().registerEvents(new Listener() {

                // So, Alice is the server
                SocketServer alice = (SocketServer) def;

                // And Bob is our target (we interact with him with a messenger)
                SocketMessenger bob = null;

                // When ANY connection is ready, check if that connection is made
                // between our server (Alice) and our target (Bob)
                // If so, register the messenger of Bob for later use
                // and send him a "Hello world!" message
                @EventHandler
                public void onHandshake(SocketEvent.Bukkit.Server.Handshake e){

                    // The server may not be Alice, it is important to check it
                    // because events are fired for ALL sockets in the configuration
                    if(e.messenger.getServer() != alice) return;

                    // The client may not be Bob, it could be Martin for example
                    if(!e.messenger.getTarget().name.equals("Bob")) return;

                    bob = e.messenger;
                    // We saved Bob's messenger into a variable in order to use it later
                    // Of course, you can save as many messengers as you want,
                    // we use only one messenger for the example

                    bob.write("Test", "Hello world!");
                    // We send him a message
                    // We could use e.messenger.write()
                    // But since we know that e.messenger = bob
                    // It is simpler to use bob directly
                }

                // When ANY message is received, check if it is from Bob
                // And check if it is over the channel we used before
                @EventHandler
                public void onMessage(SocketEvent.Bukkit.Server.Message e){
                    // No need to check if the server is Alice
                    // because if the messenger is Bob's messenger of the Bob-Alice connection,
                    // the server is necessarily Alice
                    if(e.messenger != bob) return;

                    // We need to check if the channel used is the channel we used before
                    if(!e.message.getChannel().equals("Test")) return;

                    // Get the message Bob sent us
                    String data = e.message.getExtra("data");
                    if(data.equals("How are you?"))
                        bob.write("Test", "It works!");
                    // We answer him :)
                    // We could use e.messenger.write()
                    // But since we know that e.messenger = bob
                    // It is simpler to use bob directly
                }
            }, this);
        }

        // If it is a client
        if(def instanceof SocketClient){

            // Now we are in the Bob's context
            SocketClient bob = (SocketClient) def;

            // A client is connected to one server,
            // So it acts as a client and as a messenger to its server

            getServer().getPluginManager().registerEvents(new Listener() {

                // We'll receive some "Hello world!" messages
                // from Alice, and we'll respond her
                @EventHandler
                public void onMessage(SocketEvent.Bukkit.Client.Message e){
                    // Is the message to Bob?
                    if(e.client != bob) return;

                    // Is the message from Alice?
                    if(!e.client.target.name.equals("Alice")) return;

                    // Is the channel the one we use?
                    if(!e.message.getChannel().equals("Test")) return;

                    String data = e.message.getExtra("data");

                    if(data.equals("Hello world!"))
                        e.client.write("Test", "How are you?");

                    if(data.equals("It works!"))
                        getLogger().info("Yay! It works!");
                }

                // Example method for sending multiple data
                public void sendPlayerInfo(Player player){
                    // JSONMap is an easy and beautiful way to write maps
                    // using only commas to separate keys and value
                    // Usage: new JSONMap(key, value, key, value, key, value, ...)
                    bob.write("PlayerInfo", new JSONMap(
                        "username", player.getName(),
                        "displayname", player.getDisplayName(),
                        "health", player.getHealth()
                        // "world", "gamemode", "experience", ...
                    ));
                    // When using bob.write(), we are not writing to bob,
                    // but we use the Bob's messenger to write to Alice
                    // It may be confusing, remember that a client only has
                    // one connection (to the server), so the client acts like
                    // a messenger
                }
            }, this);
        }
    }
}
