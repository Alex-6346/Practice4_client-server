package practice3.UDP;

import practice1.Message;
import practice1.Packet;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientUDPProcessor extends Thread{

    private final byte clientId;
    private SocketAddress socketAddress;
    private final Queue<Packet> queue = new ConcurrentLinkedQueue<>();

    public ClientUDPProcessor(byte clientId)
    {
        super("Client UDP processor" + clientId);
        this.clientId = clientId;
        start();
    }

    public void acceptPacket(Packet requestPacket,SocketAddress socketAddress)
    {
        this.socketAddress=socketAddress;
        queue.add(requestPacket);
    }

    @Override
    public void run()
    {
        while (true){
            try {
                Packet packet = queue.poll();
                if (packet != null) {
                    System.out.println("Client " + clientId + " is processing packet " + new String(packet.getMessage().getText(),StandardCharsets.UTF_8));

                    Packet responsePacket=new Packet(clientId,20L,new Message(1,1,"OK".getBytes(StandardCharsets.UTF_8)));
                    ServerQueue.QUEUE.add(new AddressedPacket(responsePacket,socketAddress));
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }


}
