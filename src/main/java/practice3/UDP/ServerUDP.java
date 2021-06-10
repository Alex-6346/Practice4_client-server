package practice3.UDP;

import practice1.Packet;
import practice2.Main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ServerUDP /*implements Server*/{

    public static final int PORT=4000;

    private final DatagramSocket socket;
    private final ConcurrentMap<Byte, ClientUDPProcessor> clientMap;


    public ServerUDP() throws SocketException {
        socket = new DatagramSocket(PORT);
        clientMap=new ConcurrentHashMap<>();


    }

    public void start(){
        new Thread(this::send,"SenderUDP").start();
        new Thread(this::receive, "ReceiverUDP").start();
    }

    //@Override
    private void send() {
        while (true){
            try
            {
                AddressedPacket addressedPacket = ServerQueue.QUEUE.poll();
                if(addressedPacket!=null){
                    byte[] packetBytes=Main.encodePackage(addressedPacket.getPacket());
                    DatagramPacket datagramPacket= new DatagramPacket(packetBytes,packetBytes.length,addressedPacket.getSocketAddress());
                    socket.send(datagramPacket);
                }
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }

        }
    }

    //@Override
    private void receive() {
        while (true) {
            try {
                DatagramPacket datagramPacket = new DatagramPacket(new byte[1000], 1000);
                try {
                    socket.receive(datagramPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Packet packet = Main.decodePackage(Arrays.copyOfRange(datagramPacket.getData(), 0, datagramPacket.getLength()));
                clientMap.computeIfAbsent(packet.getClient(), ClientUDPProcessor::new).acceptPacket(packet, datagramPacket.getSocketAddress());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
