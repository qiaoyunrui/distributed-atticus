package distributed;

import distributed.server.ServerConfig;
import distributed.server.ServerController;

public class Main {

    public static void main(String[] args) {
        ServerConfig config = new ServerConfig();
        System.out.println(config);
        ServerController.Companion.start(config);
    }

}
