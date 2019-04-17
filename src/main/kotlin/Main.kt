package hazae41.minecraft.sockets

import hazae41.sockets.Socket

object Sockets {
    val sockets = mutableMapOf<String, Socket>()
    val socketsNotifiers = mutableListOf<Socket.() -> Unit>()

    fun onSocketEnable(block: Socket.() -> Unit){
        socketsNotifiers += block
    }
}