package distributed;

public class Main {

    public static void main(String[] args) {
        System.out.println("HelloWorld");
        LibKt.sayHello();
        Node node = new Node();
        node.x = 20;
        node.y = 30;
        System.out.println(node);
    }

}
