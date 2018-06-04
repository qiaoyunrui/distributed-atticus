package distributed.client

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

class ClientHandler : SimpleChannelInboundHandler<String>() {
    override fun channelRead0(ctx: ChannelHandlerContext?, msg: String?) {
        println("Server say : $msg")
    }

    override fun channelActive(ctx: ChannelHandlerContext?) {
        println("Client active")
        super.channelActive(ctx)
    }

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        println("Client close")
        super.channelInactive(ctx)
    }

}