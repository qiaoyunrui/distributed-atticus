package distributed.server

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.DelimiterBasedFrameDecoder
import io.netty.handler.codec.Delimiters
import io.netty.handler.codec.string.StringDecoder
import io.netty.handler.codec.string.StringEncoder

class ServerInitializer : ChannelInitializer<SocketChannel>() {
    override fun initChannel(ch: SocketChannel) {
        val pipeline = ch.pipeline()

        // 以 \n 结尾
        pipeline.addLast("framer", DelimiterBasedFrameDecoder(8192,
                *Delimiters.lineDelimiter()))

        // 字符串解码和编码
        pipeline.addLast("decoder", StringDecoder())
        pipeline.addLast("encoder", StringEncoder())

        // 自己的逻辑Handler
        pipeline.addLast("handler", ServerHandler())
    }
}
