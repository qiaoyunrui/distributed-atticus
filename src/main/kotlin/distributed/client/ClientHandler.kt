package distributed.client

import distributed.KEY_CALCULATE
import distributed.bean.Element
import distributed.bean.createResultUnit
import distributed.dprintln
import distributed.gson
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

class ClientHandler : SimpleChannelInboundHandler<String>() {

    internal var calculation: ((String) -> String)? = null  //计算函数

    override fun channelRead0(ctx: ChannelHandlerContext?, msg: String?) {
        dprintln("服务端返回的消息为 $msg")
        val element = gson.fromJson<Element>(msg, Element::class.java)
        if (element.operation == KEY_CALCULATE) {   // 如果是计算指令的话
            val result = calculation?.invoke(element.data)
            ctx?.channel()?.writeAndFlush("${createResultUnit(result ?: "")}\n")   // 向服务器返回处理之后的数据
        }
    }

    override fun channelActive(ctx: ChannelHandlerContext?) {
        dprintln("客户端开启")
        super.channelActive(ctx)
    }

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        dprintln("客户端关闭")
        super.channelInactive(ctx)
    }

}