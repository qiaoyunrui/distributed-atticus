package distributed.client

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.DelimiterBasedFrameDecoder
import io.netty.handler.codec.Delimiters
import io.netty.handler.codec.string.StringDecoder
import io.netty.handler.codec.string.StringEncoder

class ClientInitializer : ChannelInitializer<SocketChannel>() {
    override fun initChannel(ch: SocketChannel) {
        val pipeline = ch.pipeline()
        /**
         * 这个地方必须和服务器对应上，否则无法正常解码和编码
         */
        pipeline.addLast("framer", DelimiterBasedFrameDecoder(8192,
                *Delimiters.lineDelimiter()))

        // 字符串解码和编码
        pipeline.addLast("decoder", StringDecoder())
        pipeline.addLast("encoder", StringEncoder())

        // 自己的逻辑Handler
        pipeline.addLast("handler", ClientHandler())
    }
}