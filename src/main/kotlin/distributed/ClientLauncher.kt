package distributed

import clojure.java.api.Clojure
import clojure.lang.*
import distributed.client.ClientConfig
import distributed.client.GeneralClient


fun main(args: Array<String>) {
    requireFn.invoke(Symbol.intern("me.juhezi.local-calculate"))
    val config = ClientConfig()
    config.calculation = {
        calculate(it, config.chromosome_size)
    }
    GeneralClient.start(config)
}

/**
 * 这里要注意，输出的格式为[适应度, x, y]
 */
fun calculate(data: String, chromosome_size: Int): String {
    // 把字符串转换为 List<List>
    val list: Array<Array<Long>> = gson.fromJson<Array<Array<Long>>>(data,
            Array<Array<Long>>::class.java)
    val input: List<ArraySeq> = list.map {
        ArraySeq.create(it[0], it[1])
    }
    val result: LazySeq = calculator.invoke(input, chromosome_size) as LazySeq
    val tempList = result.subList(0, result.size)
    // 将适应度放在数组的最后一位
    val newList: List<Array<Any>> = tempList.map {
        var result: Array<Any> = emptyArray()
        if (it is Cons) {
            val temp = it.toArray() as Array<Any>
            result = arrayOf(temp[1], temp[2], temp[0])
        }
        result
    }
    return gson.toJson(newList)
}