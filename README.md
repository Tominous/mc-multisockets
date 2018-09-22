<h3 align=center>
    <img src="https://i.imgur.com/FwZRaEn.png"/><br>
</h3>
<br>

[![](https://i.imgur.com/3bVmcOF.png)](https://www.spigotmc.org/resources/sockets4mc-no-more-plugin-messaging-channels.15938/)

[![](https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=M7ZT66G6N56SS)
---

#### Tired of Plugin Messaging Channels?
This API simply allows developers to send any data from a server to another

### Short example

    // Server-side
    mess.write("MyChannel", "What is the answer to life?")

    // Client-side
    client.write("MyChannel", "42")

### Use it

- Example in Kotlin

- Example in Java

### Implement it

- Kotlin DSL: add this to your build.gradle.kts

      repositories {
          maven { url = URI("https://mymavenrepo.com/repo/NIp3fBk55f5oF6VI1Wso/")}
      }

      dependencies {
          compileOnly("fr.rhaz.minecraft:sockets4mc:4.0.1")
      }

- Gradle: add this to your build.gradle

      repositories {
          maven { url 'https://mymavenrepo.com/repo/NIp3fBk55f5oF6VI1Wso/' }
      }

      dependencies {
          compileOnly 'fr.rhaz.minecraft:sockets4mc:4.0.1'
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
            <version>4.0.1</version>
            <scope>provided</scope>
        </dependency>
      </dependencies>
