<h3 align=center>
    <img src="https://i.imgur.com/FwZRaEn.png"/><br>
</h3>
<br>

[![](https://i.imgur.com/3bVmcOF.png)](https://www.spigotmc.org/resources/sockets4mc-no-more-plugin-messaging-channels.15938/)

[![](https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=M7ZT66G6N56SS)

---

#### Tired of Plugin Messaging Channels?
This API simply allows developers to send any data from a server to another, including BungeeCords

This plugin uses [Sockets.kt](https://github.com/RHazDev/Sockets.kt)

### Short examples

<h4 align=center>
    Kotlin
</h4>

```kotlin
// On one side
onSocketEnable(id = "default"){
    onReady{
        msg(channel = "MyChannel", data = "What is the answer to life?")
    }
   onMessage(channel = "MyChannel"){ msg ->
        val answer = msg["data"] as? String
        logger.info("The answer to life is $answer")
    }
}

// On other side
onSocketEnable(id = "default"){
    onMessage(channel = "MyChannel"){ msg ->
        if(msg["data"] == "What is the answer to life?")
            msg(channel = "MyChannel", data = "42")
    }
}
```

<h4 align=center>
    Java
</h4>

```java
// On one side
onSocketEnable(/*plugin*/ this, /*id*/ "default", (socket) -> {
    onReady(socket, (connection) -> {
        connection.msg(/*channel*/ "MyChannel", /*data*/ "What is the answer to life?");
    });
    onMessage(socket, /*channel*/ "MyChannel", (connection, msg) -> {
        String answer = (String) msg.get("data");
        getLogger().info("The answer to life is "+answer);
     });
});

// On other side
onSocketEnable(/*plugin*/ this, /*id*/ "default", (socket) -> {
    onMessage(socket, /*channel*/ "MyChannel", (connection, msg) -> {
        if(msg.get("data").equals("What is the answer to life?"))
        connection.msg(/*channel*/ "MyChannel", /*data*/ "42");
    });
});
```

### Advanced examples

- [Example in Kotlin](https://github.com/RHazDev/Sockets4MC/blob/master/test/KotlinTest.kt)

- [Example in Java](https://github.com/RHazDev/Sockets4MC/blob/master/test/JavaTest.java)

### Implement it

- Kotlin DSL: add this to your build.gradle.kts

      repositories {
          maven { url = URI("https://mymavenrepo.com/repo/NIp3fBk55f5oF6VI1Wso/")}
      }

      dependencies {
          compileOnly("fr.rhaz.minecraft:sockets4mc:5.0.4")
      }

- Gradle: add this to your build.gradle

      repositories {
          maven { url 'https://mymavenrepo.com/repo/NIp3fBk55f5oF6VI1Wso/' }
      }

      dependencies {
          compileOnly 'fr.rhaz.minecraft:sockets4mc:5.0.4'
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
            <version>5.0.4/version>
            <scope>provided</scope>
        </dependency>
      </dependencies>
