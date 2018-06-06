package distributed.server

import clojure.lang.PersistentVector
import distributed.*
import distributed.bean.Element
import distributed.bean.createCalculateUnit
import distributed.bean.createErrorUnit
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import java.net.InetAddress

class ServerHandler : SimpleChannelInboundHandler<String>() {

    /**
     * 客户端想服务端发送消息时调用
     */
    override fun channelRead0(ctx: ChannelHandlerContext?, msg: String?) {
        dprintln("客户端发送的数据为： $msg")
        if (ctx == null) return
        val element: Element = gson.fromJson<Element>(msg, Element::class.java)
        when (element.operation) {
            KEY_CONTROL -> {
                channelList.remove(ctx.channel())
                // 标志该客户端为控制客户端
                controlChannel = ctx.channel()
            }
            KEY_START -> {
                // Clojure 把数据发送给服务器，然后服务器把数据发送给控制线程
                if (channelList.isEmpty()) {
                    controlChannel?.writeAndFlush("${createErrorUnit("没有计算线程")}\n")
                } else {
                    // 对数据进行编码
                    val list: Array<Array<Long>> = gson.fromJson<Array<Array<Long>>>(element.data, Array<Array<Long>>::class.java)
                    // 对计算任务进行分配
                    val channelSize = channelList.size
                    dprintln("计算线程个数为：$channelSize")
                    val subList: Array<MutableList<Array<Long>>> = Array(channelSize, {
                        ArrayList<Array<Long>>()
                    })
//                    println("$list")
                    /**
                     * 将一个列表分为多个列表
                     */
                    list.forEachIndexed { index, vector ->
                        subList[index % channelSize].add(vector)
                    }
                    channelList.forEachIndexed { index, channel ->
                        channel.writeAndFlush("${createCalculateUnit(gson.toJson(subList[index]))}\n")
                    }
                }
            }
            KEY_RESULT -> {
                // 当有一个计算线程 计算线程完成计算工作后，把计算结果发送给服务器
                // 把这个结果直接转发给控制线程，不需要进行额外处理
                controlChannel?.writeAndFlush("$msg\n")
            }
        }
    }

    /**
     * 在 channel 被启动的时候调用（在建立连接的时候）
     */
    override fun channelActive(ctx: ChannelHandlerContext?) {
        dprintln("客户端接入: ${ctx?.channel()?.remoteAddress()}")
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
        dprintln("客户端断开: ${ctx?.channel()?.remoteAddress()}")
        if (ctx != null) {
            // 移除 channel
            channelList.remove(ctx.channel())
        }
        super.channelInactive(ctx)
    }


}