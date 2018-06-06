package distributed.client

/**
 * 客户端配置
 */
class ClientConfig(var port: Int = 12138,
                   var host: String = "127.0.0.1",
                   var chromosome_size: Int = 25,   //染色体长度，
                   var calculation: ((String) -> String)? = null)