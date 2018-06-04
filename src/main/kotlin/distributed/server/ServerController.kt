package distributed.server

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel

class ServerController {

    companion object {
        private val bossGroup: EventLoopGroup = NioEventLoopGroup()
        private val workerGroup: EventLoopGroup = NioEventLoopGroup()
        fun start(config: ServerConfig = ServerConfig()) {
            val bootStrap = ServerBootstrap()
            bootStrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel::class.java)
                    .childHandler(ServerInitializer())

            try {
                bootStrap.bind(config.port)
                        .sync()
                        .channel()
                        .closeFuture()
                        .sync()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                bossGroup.shutdownGracefully()
                workerGroup.shutdownGracefully()
            }
        }
    }

}