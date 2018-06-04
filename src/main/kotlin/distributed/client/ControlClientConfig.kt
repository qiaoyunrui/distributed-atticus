package distributed.client

class ControlClientConfig(var port: Int = 12138,
                          var host: String = "127.0.0.1",
                          var callback: ((String) -> Unit)? = null)
