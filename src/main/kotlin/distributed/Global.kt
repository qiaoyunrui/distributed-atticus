package distributed

import distributed.server.ChannelList
import io.netty.channel.Channel

val channelList = ChannelList.instance

var controlChannel: Channel? = null

val distanceTime: Long = 200

val KEY_CONTROL = "[Control]"
val KEY_START = "[Start]"