package practice3.TCP;

import practice1.Message;
import practice1.Packet;
import practice2.Main;
import practice3.Client;


import java.io.IOException;
import java.net.*;

public class ClientTCP implements Client {

    private final Socket socket;
    private final byte clientId;

    public ClientTCP(byte clientId) throws SocketException {
        socket = new Socket();
        this.clientId=clientId;
    }

    @Override
    public void send(byte[] bytes) throws UnknownHostException {
        byte[] encodePackage= Main.encodePackage(new Packet(clientId,10L, new Message(1,1,bytes)));
        try {
            if(!socket.isConnected()) {
                socket.connect(new InetSocketAddress(InetAddress.getByName(null), ServerTCP.PORT));
            }

            socket.getOutputStream().write(encodePackage);
            socket.getOutputStream().flush();
            //socket.shutdownOutput();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public Packet receive() {
        byte[] bytes = new byte[1000];
        try {
            socket.getInputStream().read(bytes);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return Main.decodePackage(bytes);
    }

    @Override
    public boolean isConnectionAvailable() {
        return false;
    }


}