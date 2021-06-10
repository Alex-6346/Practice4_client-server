package practice3;

import practice3.TCP.ClientTCP;
import practice3.UDP.ClientUDP;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class ClientTester {

    public static void main(String[] args) throws SocketException, UnknownHostException {
       // for (byte i=1;i<=30;i++){
         //   clientUDP(i);
        //}
        for (byte i=1;i<=3;i++){
            clientTCP(i);
        }
    }

    private static void clientTCP(byte id)
    {
        new Thread(() -> {
            try
            {
                Thread.sleep(new Random().nextInt(100));
                ClientTCP client= new ClientTCP(id);
                client.send(("Hello world" + id).getBytes(StandardCharsets.UTF_8));
                System.out.println(client.receive());
            }
            catch (SocketException | UnknownHostException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }




    private static void clientUDP(byte id)
    {
        new Thread(() -> {
            try
            {
                Thread.sleep(new Random().nextInt(100));
                ClientUDP client= new ClientUDP(id);
                client.send(("Hello world" + id).getBytes(StandardCharsets.UTF_8));
                System.out.println(client.receive());
            }
        catch (SocketException | UnknownHostException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }


}
