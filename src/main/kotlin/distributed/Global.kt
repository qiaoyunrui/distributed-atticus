package distributed

import com.google.gson.Gson
import distributed.server.ChannelList
import io.netty.channel.Channel
import redis.clients.jedis.Jedis

val channelList = ChannelList.instance

var controlChannel: Channel? = null

val distanceTime: Long = 200

val KEY_CONTROL = "Control" // 控制客户端的控制标识
val KEY_START = "Start"     // 服务器把数据分配给多个客户端
val KEY_RESULT = "Result"   // 客户端计算结果
val KEY_CALCULATE = "Calculate" // 命令客户端进行计算
val KEY_ERROR = "Error" //发生错误


val gson = Gson()

//val jedis = Jedis()

var DEBUG = true   //是否打印日志

fun dprintln(message: String) {
    if (DEBUG) {
        println(message)
    }
}

val ATTICUS = "atticus"