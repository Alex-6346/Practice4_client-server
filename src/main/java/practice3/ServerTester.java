package practice3;

import practice3.TCP.ServerTCP;

import java.io.IOException;

public class ServerTester {

    public static void main(String[] args) throws IOException {
     //   ServerUDP server = new ServerUDP();
     //      server.start();

        ServerTCP serverTCP = new ServerTCP();
        serverTCP.start();

    }
}
