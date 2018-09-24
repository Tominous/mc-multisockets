<h3 align=center>
    <img src="https://i.imgur.com/FwZRaEn.png"/><br>
</h3>
<br>

[![](https://i.imgur.com/3bVmcOF.png)](https://www.spigotmc.org/resources/sockets4mc-no-more-plugin-messaging-channels.15938/)

[![](https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=M7ZT66G6N56SS)

---

#### Tired of Plugin Messaging Channels?
This API simply allows developers to send any data from a server to another, including BungeeCords

This plugin uses [RHazSockets](https://github.com/RHazDev/RHazSockets)

### Short example

    // Server-side
    socket.onHandshake(plugin){
        write("MyChannel", "What is the answer to life?")

        onMessage(plugin, "MyChannel"){
            val answer = it.getExtra<String>("data")
            logger.info("The answer to life is $answer")
        }
    }

    // Client-side
    socket.onMessage(plugin, "MyChannel"){
        val data = it.getExtra<String>("data")
        if(data == "What is the answer to life?")
            write("MyChannel", "42")
    }


### Use it

- [Example in Kotlin](https://github.com/RHazDev/Sockets4MC/blob/master/test/KotlinTest.kt)

- [Example in Java](https://github.com/RHazDev/Sockets4MC/blob/master/test/JavaTest.java)

### Implement it

- Kotlin DSL: add this to your build.gradle.kts

      repositories {
          maven { url = URI("https://mymavenrepo.com/repo/NIp3fBk55f5oF6VI1Wso/")}
      }

      dependencies {
          compileOnly("fr.rhaz.minecraft:sockets4mc:4.0.3")
      }

- Gradle: add this to your build.gradle

      repositories {
          maven { url 'https://mymavenrepo.com/repo/NIp3fBk55f5oF6VI1Wso/' }
      }

      dependencies {
          compileOnly 'fr.rhaz.minecraft:sockets4mc:4.0.3'
      }


- Maven: add this to your pom.xml

      <repositories>
        <repository>
            <id>rhazdev</id>
            <url>https://mymavenrepo.com/repo/NIp3fBk55f5oF6VI1Wso/</url>
        </repository>
      </repositories>

      <dependencies>
        <dependency>
            <groupId>fr.rhaz.minecraft</groupId>
            <artifactId>sockets4mc</artifactId>
            <version>4.0.3</version>
            <scope>provided</scope>
        </dependency>
      </dependencies>
