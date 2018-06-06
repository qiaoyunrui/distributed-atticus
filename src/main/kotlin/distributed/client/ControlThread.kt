package distributed.client

import distributed.bean.createControlUnit
import distributed.bean.createStartUnit
import distributed.distanceTime
import distributed.dprintln
import io.netty.bootstrap.Bootstrap
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel

/**
 * 与服务端的连接线程
 */
class ControlThread : Thread() {

    val group: EventLoopGroup = NioEventLoopGroup()
    val config = ControlClientConfig()
    val controlClientHandler = ControlClientHandler()

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
            controlClientHandler.callback = config.callback
            val bootStrap = Bootstrap()
            bootStrap.group(group)
                    .channel(NioSocketChannel::class.java)
                    .handler(ClientInitializer(controlClientHandler))

            val channel = bootStrap
                    .connect(config.host, config.port)
                    .sync()
                    .channel()
            // 向服务端发送身份标识
            channel.writeAndFlush("${createControlUnit()}\n")
            while (running) {
                val startTime = System.currentTimeMillis()
                if (!content.isEmpty()) {    //content 是空，则不进行通信
                    channel.writeAndFlush("${createStartUnit(content)}\n")     //向服务端发送信息
                    content = ""    // 消息设置为空
                }
                val time = System.currentTimeMillis() - startTime
                if (time < distanceTime) {
                    Thread.sleep(distanceTime - time)
                }
            }
            dprintln("停止运行")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            group.shutdownGracefully()
        }
    }

}