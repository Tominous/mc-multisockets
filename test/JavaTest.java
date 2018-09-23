import fr.rhaz.minecraft.SocketEvent;
import fr.rhaz.sockets.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static fr.rhaz.minecraft.Sockets4MCKt.*;

// --- Java example of Bukkit plugin ---
// Let's call our server Alice and our client Bob
//
public class JavaTest extends JavaPlugin {
    @Override
    public void onEnable() {
        SocketHandler def = getMinecraftSockets().getSockets().get("default");
        // Get the default socket in the S4MC configuration

        // A socket may be a server or a client, depending on the configuration
        // You need to implement both cases

        // If it is a server
        if(def instanceof SocketServer){

            // So, Alice is the server
            SocketServer alice = (SocketServer) def;

            // And Bob is our target (we interact with him with a messenger)
            // We use an atomic reference because it may be in another thread
            AtomicReference<SocketMessenger> bob = null;

            // We listen for incoming connections from Bob
            onHandshake(/*socket:*/ alice, /*plugin:*/ this, /*expected target name:*/ "Bob", listener(

                (/*Bob's messenger:*/ messenger) -> {

                    messenger.write("Test", "Hello world!");
                    // We send him a message over the channel "Test" and the extra "data"

                    // We listen for incoming messages from Bob's messenger
                    onMessage(/*Bob's messenger:*/ messenger, this, "Test", listener(

                        (/*still Bob's messenger*/ messenger2, msg) -> {

                            String data = msg.getExtra("data");
                            // We retrieve the message from the "data" extra

                            if(data.equals("How are you?"))
                                messenger2.write("Test", "It works!");
                        }
                    ));

                    bob.set(messenger);
                    // We save Bob's messenger into a variable in order to use it later
                    // Of course, you can save as many messengers as you want,
                    // we use only one messenger for the example
                }
            ));





            // ----------- DEPRECATED -------------
            // You can also use the full event, but is more verbose
            /*getServer().getPluginManager().registerEvents(*/new Listener() {

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

                    bob.set(e.messenger);
                    // We saved Bob's messenger into a variable in order to use it later
                    // Of course, you can save as many messengers as you want,
                    // we use only one messenger for the example

                    e.messenger.write("Test", "Hello world!");
                    // We send him a message
                }

                // When ANY message is received, check if it is from Bob
                // And check if it is over the channel we used before
                @EventHandler
                public void onMessage(SocketEvent.Bukkit.Server.Message e){
                    // No need to check if the server is Alice
                    // because if the messenger is Bob's messenger of the Bob-Alice connection,
                    // the server is necessarily Alice
                    if(e.messenger != bob.get()) return;

                    // We need to check if the channel used is the channel we used before
                    if(!e.message.getChannel().equals("Test")) return;

                    // Get the message Bob sent us
                    String data = e.message.getExtra("data");
                    if(data.equals("How are you?"))
                        e.messenger.write("Test", "It works!");
                    // We answer him :)
                }
            }/*, this)*/;

        }

        // If it is a client
        if(def instanceof SocketClient){

            // A client is connected to one server,
            // So it acts as a client and as a messenger to its server (Alice)

            // Now we are in the Bob's context
            SocketClient bob = (SocketClient) def;

            // We listen for incoming messages over the channel "Test"
            onMessage(/*socket:*/ bob, /*plugin:*/ this, /*channel:*/ "Test", listener(

                (/*still Bob:*/ socket, /*message:*/ msg) -> {

                    String data = msg.getExtra("data");
                    if(data == null) return;

                    if(data.equals("Hello world!"))
                        bob.write("Test", "How are you?");
                    // When using bob.write(), we are not writing to bob,
                    // but we use the Bob's messenger to write to Alice
                    // It may be confusing, remember that a client only has
                    // one connection (to the server), so the client acts like
                    // a messenger

                    if(data.equals("It works!"))
                        getLogger().info("Yay! It works!");
                }
            ));

            // Register for ALL incoming messages to Bob (no matter what channel it is)
            // and log them
            onMessage(bob, this, listener(
                (socket, channel, msg) -> {
                    String data = msg.getExtra("data");
                    getLogger().info(channel + ": " + data);
                }
            ));

            // Example function for sending multiple data
            Consumer<Player> sendPlayerInfo = (player) -> {

                // Ensure the connection is ready
                if(!bob.getHandshaked()) return;
                if(!bob.getReady()) return;

                // JSONMap is an easy and beautiful way to write maps
                // using only commas to separate keys and value
                // Usage: new JSONMap(key, value, key, value, key, value, ...)
                bob.write("PlayerInfo", new JSONMap(
                        "username", player.getName(),
                        "displayname", player.getDisplayName(),
                        "health", player.getHealth()
                        // "world", "gamemode", "experience", ...
                ));
            };
            // You can call this function later with
            // sendPlayerInfo.accept(getServer().getPlayer("Hazae41"));







            // ----------- DEPRECATED -------------
            // You can also use the full event, but is more verbose
            /*getServer().getPluginManager().registerEvents(*/new Listener() {

                // We'll receive some "Hello world!" messages
                // from Alice, and we'll respond her
                @EventHandler
                public void onMessage(SocketEvent.Bukkit.Client.Message e){
                    // Is the message to Bob?
                    // No need to check if the message is from Alice because the client
                    // has one connection: the Bob-Alice connection
                    if(e.client != bob) return;

                    // Is the channel the one we use?
                    if(!e.message.getChannel().equals("Test")) return;

                    String data = e.message.getExtra("data");

                    if(data.equals("Hello world!"))
                        e.client.write("Test", "How are you?");

                    if(data.equals("It works!"))
                        getLogger().info("Yay! It works!");
                }
            }/*, this)*/;
        }
    }
}
