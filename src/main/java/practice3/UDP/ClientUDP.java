package practice3.UDP;

import practice1.Message;
import practice1.Packet;
import practice2.Main;
import practice3.Client;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class ClientUDP implements Client {

    private final DatagramSocket socket;
    private final byte clientId;

    public ClientUDP(byte clientId) throws SocketException {
        socket = new DatagramSocket();
        this.clientId=clientId;
    }

    @Override
    public void send(byte[] bytes) throws UnknownHostException {
        byte[] encodePackage= Main.encodePackage(new Packet(clientId,10L, new Message(1,1,bytes)));

        DatagramPacket datagramPacket = new DatagramPacket(encodePackage,encodePackage.length, InetAddress.getByName(null), ServerUDP.PORT);
        try {
            socket.send(datagramPacket);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public Packet receive() {
        DatagramPacket packet = new DatagramPacket(new byte[1000],1000);
        try {
            socket.receive(packet);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return Main.decodePackage(Arrays.copyOfRange(packet.getData(),0,packet.getLength()));
    }

    @Override
    public boolean isConnectionAvailable() {
        return false;
    }


}
