package practice2;

import practice1.Message;
import practice1.Packet;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;

public class Processor implements Runnable {

    private BlockingQueue<Packet> decryptedQueue;
    private BlockingQueue<Packet> processedQueue;


    public Processor(BlockingQueue<Packet> decryptedQueue, BlockingQueue<Packet> processedQueue) {
        this.decryptedQueue = decryptedQueue;
        this.processedQueue = processedQueue;
    }

    public void processMessage(Packet packet) throws InterruptedException {
        Message message = packet.getMessage();
        Message processedMessage = new Message(message.getCommand(), message.getUser(), "OK.".getBytes(StandardCharsets.UTF_8));
        Packet processedPacket = new Packet(packet.getClient(), packet.getPacket(), processedMessage);
        processedQueue.put(processedPacket);
    }

    public static final Packet POISON_PACKET = new Packet((byte) 0, 0, new Message(0, 0, new byte[]{0}));

    @Override
    public void run() {
        try {
            while (true) {
                Packet receivedPacket = decryptedQueue.take();
                if (receivedPacket.equals(POISON_PACKET)) {
                    return;
                }
                processMessage(receivedPacket);
                //System.out.println("b");
                System.out.println("DECRYPTOR: " + Main.DECRYPTOR_WORKING.get());
                System.out.println("PROCESSOR: " + Main.PROCESSOR_WORKING.get());
                System.out.println("ENCRYPTOR: " + Main.ENCRYPTOR_WORKING.get());

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
