package distributed.server

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import java.net.InetAddress

class ServerHandler : SimpleChannelInboundHandler<String>() {

    override fun channelRead0(ctx: ChannelHandlerContext?, msg: String?) {
        // 打印接收的信息
        println("${ctx?.channel()?.remoteAddress()} say $msg")
        ctx?.writeAndFlush("Received your message! \n")
    }

    /**
     * 在 channel 被启动的时候调用（在建立连接的时候）
     */
    override fun channelActive(ctx: ChannelHandlerContext?) {
        println("RemoteAddress: ${ctx?.channel()?.remoteAddress()} active!")
        ctx?.writeAndFlush("Welcome to ${InetAddress.getLocalHost().hostName} service!\n")
        super.channelActive(ctx)
    }

}