package practice3.TCP;

import practice1.Message;
import practice1.Packet;
import practice2.Main;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientTCPProcessor extends Thread{

    private final byte clientId;
    private final Queue<Packet> queue = new ConcurrentLinkedQueue<>();
    private Socket socket;

    public ClientTCPProcessor(byte clientId)
    {
        super("Client TCP processor" + clientId);
        this.clientId = clientId;
        start();
    }

    public void acceptPacket(Socket socket, Packet requestPacket)
    {
        this.socket=socket;
        queue.add(requestPacket);
    }

    private void send(Packet responsePacket) {
            try
            {
                byte[] bytes= Main.encodePackage(responsePacket);
                socket.getOutputStream().write(bytes);
                //socket.shutdownOutput();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
    }


    @Override
    public void run()
    {
        while (true){
            try {
                Packet packet = queue.poll();
                if (packet != null) {
                    queue.peek();
                    System.out.println("Client " + clientId + " is processing packet " + new String(packet.getMessage().getText(), StandardCharsets.UTF_8));
                    Packet responsePacket=new Packet(clientId,20L,new Message(1,1,"OK".getBytes(StandardCharsets.UTF_8)));
                    send(responsePacket);
                }
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }


}