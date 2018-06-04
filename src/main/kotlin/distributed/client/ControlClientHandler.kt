package distributed.client

import distributed.KEY_CALCULATE
import distributed.bean.Element
import distributed.dprintln
import distributed.gson
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

class ControlClientHandler : SimpleChannelInboundHandler<String>() {

    internal var callback: ((String) -> Unit)? = null

    override fun channelRead0(ctx: ChannelHandlerContext?, msg: String?) {
        dprintln("服务器发给控制客户端的信息为 $msg")
        val element = gson.fromJson<Element>(msg, Element::class.java)
        if (element.operation == KEY_CALCULATE) {
            callback?.invoke(element.data)
        }
    }

    override fun channelActive(ctx: ChannelHandlerContext?) {
        dprintln("控制客户端开启")
        super.channelActive(ctx)
    }

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        dprintln("控制客户端关闭")
        super.channelInactive(ctx)
    }

}