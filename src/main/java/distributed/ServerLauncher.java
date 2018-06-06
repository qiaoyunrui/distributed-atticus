package distributed;

import distributed.server.ServerConfig;
import distributed.server.ServerController;

/**
 * 启动服务器
 */
public class ServerLauncher {

    public static void main(String[] args) {
        // TODO: 2018/6/4 这里应该加载config.json 中的配置选项
        ServerConfig serverConfig = new ServerConfig();
        ServerController.Companion.start(serverConfig);
    }

}
