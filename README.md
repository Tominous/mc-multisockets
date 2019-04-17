# mc-multisockets

**WebSockets multiplexing for Minecraft (Spigot & BungeeCord)**

[**Download latest stable version**](https://jitpack.io/com/github/hazae41/mc-multisockets/master-SNAPSHOT/mc-multisockets-master-SNAPSHOT-bundle.jar)

[**All versions**](https://github.com/hazae41/mc-multisockets/releases)

[**Go to JitPack**](https://jitpack.io/#hazae41/mc-multisockets/master-SNAPSHOT)

By default, all routes are public and unencrypted

But you can use AES to encrypt them and a password verification to restrict them

**You must define routes in onSocketEnable{}**

#### Configuration

```yaml
sockets:
  bungee: # socket name
    port: 25590 # port used to receive requests
    peers: # connections
    - localhost:25590
    # secret key, must be the same for all your servers
    key: I/lP67yWicgL1d7K7AHb4w==
```

#### Usage

```kotlin
// When any socket is started
onSocketEnable { 
    println("Enabled socket $name on port $port")
    
    // You must define all routes here

    // Create route to /MyPlugin/test
    onConversation("/MyPlugin/test"){
        // Send unencrypted message
        send("it works!")
        // Wait until a message is received
        println(readMessage())
        // Do it as many times as you want
        send("it still works!")
        println(readMessage())
    }
    
    // When this socket connects to another
    onConnection { name ->
        println("Connected to $name!")
        // Start a conversation to /MyPlugin/hello
        conversation("/MyPlugin/hello") {
            // Use AES encryption
            val (encrypt, decrypt) = aes()
            // Send encrypted message
            send("hello world!".encrypt())
            // Wait until a message is received and decrypt it
            println(readMessage().decrypt())
        }
    }
    
    // When this socket connects to another named "factions"
    onConnection(filter = "factions") {
        // Short function equivalent to conversation { readMessage() }
        request("/Factions/factions"){
            // Just print the first message received and close the conversation
            result -> println(result)
        }
    }
}
```