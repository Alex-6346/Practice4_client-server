package practice3.TCP;
import practice1.Packet;
import practice2.Main;
import practice3.TCP.ClientTCPProcessor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ServerTCP {

    public static final int PORT=4000;

    private final ServerSocket serverSocket;
    private final ConcurrentMap<Byte, ClientTCPProcessor> clientMap;


    public ServerTCP() throws IOException {
        serverSocket=new ServerSocket(PORT);
        clientMap=new ConcurrentHashMap<>();
    }

    public void start(){
        new Thread(this::receive, "ReceiverTCP").start();
    }

    private void receive() {
        while (true)
        {
            try
            {

                Socket clientSocket= serverSocket.accept();
                if(clientSocket.isConnected())
                {
                    byte[] bytes = new byte[1000];
                    clientSocket.getInputStream().read(bytes);
                    Packet packet= Main.decodePackage(bytes);
                    clientMap.computeIfAbsent(packet.getClient(), ClientTCPProcessor::new).acceptPacket(clientSocket,packet);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




}
