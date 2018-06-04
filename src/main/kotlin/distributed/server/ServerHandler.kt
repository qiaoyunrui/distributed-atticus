package distributed.server

import distributed.KEY_CONTROL
import distributed.KEY_START
import distributed.channelList
import distributed.controlChannel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import java.net.InetAddress

class ServerHandler : SimpleChannelInboundHandler<String>() {

    /**
     * 客户端想服务端发送消息时调用
     */
    override fun channelRead0(ctx: ChannelHandlerContext?, msg: String?) {
        // 打印接收的信息
        println("${ctx?.channel()?.remoteAddress()} say $msg")
        if (ctx == null) return
        when (msg) {
            "size" -> ctx.writeAndFlush("size is ${channelList.size} \n")
            KEY_CONTROL -> {
                channelList.remove(ctx.channel())
                // 标志该客户端为控制客户端
                controlChannel = ctx.channel()
            }
            KEY_START -> {
                channelList.forEachIndexed { index, channel ->
                    channel.writeAndFlush("标号: $index \n")
                }
            }
            else -> ctx.writeAndFlush("Received your message! \n")
        }
    }

    /**
     * 在 channel 被启动的时候调用（在建立连接的时候）
     */
    override fun channelActive(ctx: ChannelHandlerContext?) {
        println("RemoteAddress: ${ctx?.channel()?.remoteAddress()} active!")
        ctx?.writeAndFlush("Welcome to ${InetAddress.getLocalHost().hostName} service! \n")
        if (ctx != null) {
            // 新增 channel
            channelList.add(ctx.channel())
        }
        super.channelActive(ctx)
    }

    /**
     * 客户端断开连接的时候进行调用
     */
    override fun channelInactive(ctx: ChannelHandlerContext?) {
        println("${ctx?.channel()?.remoteAddress()} 断开连接")
        if (ctx != null) {
            // 移除 channel
            channelList.remove(ctx.channel())
        }
        super.channelInactive(ctx)
    }


}