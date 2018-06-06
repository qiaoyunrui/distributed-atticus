package distributed.client

import distributed.bean.Element

class ControlClientConfig(var port: Int = 12138,
                          var host: String = "127.0.0.1",
                          var callback: ((Element) -> Unit)? = null) {
}
