import fr.rhaz.sockets.Connection;
import fr.rhaz.sockets.MultiSocket;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import static fr.rhaz.minecraft.sockets.Sockets4Bungee.onSocketEnable;
import static fr.rhaz.minecraft.sockets.Sockets4MC.getSocket;
import static fr.rhaz.sockets.JsonKt.jsonMap;

public class JavaBungeeTest extends Plugin{

    @Override public void onEnable() {

        onSocketEnable(/*plugin*/ this, /*id*/ "default", (socket) -> {

            getLogger().info("Socket #default is available");

            // When any connection is ready
            socket.onReady(connection -> {
                getLogger().info("Connection to "+connection.getTargetName()+" is available");
                // Send a message over the channel "Test"
                connection.msg(/*channel*/ "Test", /*data*/ "How are you?");
            });

            // When a message is received over the channel "Test"
            socket.onMessage(/*channel*/ "Test", (connection, msg) -> {
                if(msg.get(/*key*/ "data").equals("It works fine!"))
                getLogger().info(connection.getTargetName()+" works fine!");
            });

        });
    }

    public void sendInfo(ProxiedPlayer player) throws Exception {
        String serverName = player.getServer().getInfo().getName();

        MultiSocket socket = getSocket(/*id*/ "default");
        if(socket == null) throw new Exception("Socket #default is not available");

        Connection connection = socket.getConnection(/*target*/ serverName);
        if(connection == null) throw new Exception("Connection to "+serverName+" is not available");

        connection.msg(/*channel*/ "PlayerInfo", jsonMap(
            /*key*/ "uuid", /*value*/ player.getUniqueId(),
            /*key*/ "name", /*value*/ player.getName(),
            /*key*/ "displayname", /*value*/ player.getDisplayName()
        ));
    }
}
