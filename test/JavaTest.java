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

        JavaPlugin plugin = this;

        // A socket may be a server or a client, depending on the configuration
        // You need to implement both cases

        // If it is a server
        onServerEnable(plugin, /*key:*/ "default", listener(
            // default socket in the S4MC configuration
            (alice) -> { // So, Alice is the server

                // And Bob is our target (we interact with him with a messenger)
                // We use an atomic reference because it may be in another thread
                AtomicReference<SocketMessenger> bob = null;

                // We listen for incoming connections from Bob
                onHandshake(/*socket:*/ alice, plugin, /*name:*/ "Bob", listener(

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
            }
        ));

        // If it is a client
        onClientEnable(plugin, "default", listener(

            (bob) -> { // Now we are in the Bob's context

                // A client is connected to one server,
                // So it acts as a client and as a messenger to its server (Alice)

                // We listen for incoming messages over the channel "Test"
                onMessage(/*socket:*/ bob, /*plugin:*/ this, /*channel:*/ "Test", listener(

                        (/*still Bob:*/ socket2, /*message:*/ msg) -> {

                            String data = msg.getExtra("data");
                            if (data == null) return;

                            if (data.equals("Hello world!"))
                                bob.write("Test", "How are you?");
                            // When using bob.write(), we are not writing to bob,
                            // but we use the Bob's messenger to write to Alice
                            // It may be confusing, remember that a client only has
                            // one connection (to the server), so the client acts like
                            // a messenger

                            if (data.equals("It works!"))
                                getLogger().info("Yay! It works!");
                        }
                ));

                // Register for ALL incoming messages to Bob (no matter what channel it is)
                // and log them
                onMessage(bob, plugin, listener(
                    (socket, channel, msg) -> {
                        String data = msg.getExtra("data");
                        getLogger().info(channel + ": " + data);
                    }
                ));

                // Example function for sending multiple data
                Consumer<Player> sendPlayerInfo = (player) -> {

                    // Ensure the connection is ready
                    if (!bob.getHandshaked()) return;
                    if (!bob.getReady()) return;

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
            }
        ));
    }
}
