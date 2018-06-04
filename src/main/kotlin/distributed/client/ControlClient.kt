package distributed.client

import distributed.KEY_CONTROL
import distributed.KEY_START
import distributed.distanceTime
import io.netty.bootstrap.Bootstrap
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel

/**
 * 控制客户端
 */

fun main(args: Array<String>) {
    val thread = ConnectThread()
    thread.start()
    Thread.sleep(1000)
    thread.send(KEY_START)
    Thread.sleep(1000)
//    thread.exit()
}

/**
 * 与服务端的连接线程
 */
class ConnectThread : Thread() {

    val group: EventLoopGroup = NioEventLoopGroup()
    val config = ClientConfig()

    private var content: String = ""    // 向服务端发送的内容
    private var running: Boolean = true // 是否正在运行

    fun exit() {
        running = false
    }

    fun send(data: String) {
        content = data
    }

    override fun run() {
        try {
            val bootStrap = Bootstrap()

            bootStrap.group(group)
                    .channel(NioSocketChannel::class.java)
                    .handler(ClientInitializer())

            val channel = bootStrap
                    .connect(config.host, config.port)
                    .sync()
                    .channel()
            // 向服务端发送身份标识
            channel.writeAndFlush("$KEY_CONTROL\n")
            while (running) {
                val startTime = System.currentTimeMillis()
                if (!content.isEmpty()) {    //content 是空，则不进行通信
                    channel.writeAndFlush("$content\n")     //向服务端发送信息
                    content = ""    // 消息设置为空
                }
                val time = System.currentTimeMillis() - startTime
                if (time < distanceTime) {
                    Thread.sleep(distanceTime - time)
                }
            }
            println("停止运行")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            group.shutdownGracefully()
        }
    }

}