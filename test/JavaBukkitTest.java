import fr.rhaz.sockets.Connection;
import fr.rhaz.sockets.MultiSocket;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import static fr.rhaz.minecraft.sockets.Sockets4Bukkit.onSocketEnable;
import static fr.rhaz.minecraft.sockets.Sockets4MC.getSocket;
import static fr.rhaz.sockets.JsonKt.jsonMap;

public class JavaBukkitTest extends JavaPlugin {

    @Override public void onEnable(){
        onSocketEnable(/*plugin*/ this, /*id*/ "default", socket -> {
            getLogger().info("Socket #default is available");

            socket.onReady(connection -> {
                getLogger().info("Connection to "+connection.getTargetName()+" is available");
            });

            socket.onMessage(/*channel*/ "Test", (connection, msg) -> {
                if(msg.get(/*key*/ "data").equals("How are you?"))
                    connection.msg(/*channel*/ "Test", /*data*/ "It works fine!");
            });
        });
    }

    public void sendInfo(Player player) throws Exception {
        String proxyName = "MyProxy";

        MultiSocket socket = getSocket(/*id*/ "default");
        if(socket == null) throw new Exception("Socket #default is not available");

        Connection connection = socket.getConnection(/*target*/ proxyName);
        if(connection == null) throw new Exception("Connection to "+proxyName+" is not available");

        connection.msg(/*channel*/ "PlayerInfo", jsonMap(
                /*key*/ "uuid", /*value*/ player.getUniqueId(),
                /*key*/ "name", /*value*/ player.getName(),
                /*key*/ "displayname", /*value*/ player.getDisplayName(),
                /*key*/ "exp", /*value*/ player.getExp()
        ));
    }
}