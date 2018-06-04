package distributed.server

import io.netty.channel.Channel
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 与服务端连接的客户端列表
 * 使用代理模式
 */
class ChannelList private constructor() :
        MutableList<Channel>
        by CopyOnWriteArrayList<Channel>() {

    /**
     * 同步懒加载单例模式
     */
    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ChannelList()
        }
    }

}
