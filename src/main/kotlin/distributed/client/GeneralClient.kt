package distributed.client

import io.netty.bootstrap.Bootstrap
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel

/**
 * 客户端连接到服务器之后会一直待命，直到手动关闭
 */
class GeneralClient {

    companion object {
        private val group: EventLoopGroup = NioEventLoopGroup()
        fun start(config: ClientConfig = ClientConfig()) {
            try {
                val bootStrap = Bootstrap()
                val clientHandler = ClientHandler()
                clientHandler.calculation = config.calculation
                bootStrap.group(group)
                        .channel(NioSocketChannel::class.java)
                        .handler(ClientInitializer(clientHandler))

                /**
                 * 连接服务器
                 */
                bootStrap
                        .connect(config.host, config.port)
                        .sync()
                        .channel()

                while (true) {   //阻塞
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                group.shutdownGracefully()
            }
        }
    }

}