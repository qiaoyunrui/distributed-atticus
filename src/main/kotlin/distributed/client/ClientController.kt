package distributed.client

import io.netty.bootstrap.Bootstrap
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import java.io.BufferedReader
import java.io.InputStreamReader

class ClientController {

    companion object {
        private val group: EventLoopGroup = NioEventLoopGroup()
        fun start(config: ClientConfig = ClientConfig()) {
            try {
                val bootStrap = Bootstrap()

                bootStrap.group(group)
                        .channel(NioSocketChannel::class.java)
                        .handler(ClientInitializer())

                /**
                 * 连接服务器
                 */
                val channel = bootStrap
                        .connect(config.host, config.port)
                        .sync()
                        .channel()

                val input = BufferedReader(InputStreamReader(System.`in`))
                while (true) {
                    val line = input.readLine() ?: continue
                    channel.writeAndFlush("$line\r\n")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                group.shutdownGracefully()
            }
        }
    }

}