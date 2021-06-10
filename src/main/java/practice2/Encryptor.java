package practice2;


import practice1.Message;
import practice1.Packet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

import static practice1.CRC16.crc16;


public class Encryptor implements Runnable {

    private BlockingQueue<Packet> processedQueue;
    private BlockingQueue<byte[]> encryptedQueue;
    private Sender sender = new Sender();


    public Encryptor(BlockingQueue<Packet> processedQueue, BlockingQueue<byte[]> encryptedQueue) {
        this.processedQueue = processedQueue;
        this.encryptedQueue = encryptedQueue;
    }


    public void encrypt(Packet packet) throws InterruptedException {
        Message message = packet.getMessage();

        byte[] head = ByteBuffer.allocate(14).order(ByteOrder.BIG_ENDIAN).
                put((byte) 0x13).
                put(packet.getClient()). //number of application
                putLong(packet.getPacket()).//number of packet
                putInt(message.getText().length + 8).array();

        //System.out.println(new String(message.getText(), StandardCharsets.UTF_8));
        //System.out.println(crc16(message.getText()));

        byte[] encryptedBytes = ByteBuffer.allocate(16 + (message.getText().length + 8) * 2).order(ByteOrder.BIG_ENDIAN).
                put(head).
                putShort(crc16(head)).
                putInt(message.getCommand()). //number of command
                putInt(message.getUser()). //number of user
                put(Main.encode(message.getText())).
                putShort(crc16(message.getText())).
                array();

        //encryptedQueue.put(encryptedBytes);
        sender.sendMessage(encryptedBytes);
    }

    public static final Packet POISON_PACKET = new Packet((byte) 0, 0, new Message(0, 0, new byte[]{0}));

    @Override
    public void run() {
        try {
            while (true) {
                Packet receivedPacket = processedQueue.take();
                if (receivedPacket.equals(POISON_PACKET)) {
                    return;
                }
                encrypt(processedQueue.take());
                //System.out.println("c");
                System.out.println("DECRYPTOR: " + Main.DECRYPTOR_WORKING.get());
                System.out.println("PROCESSOR: " + Main.PROCESSOR_WORKING.get());
                System.out.println("ENCRYPTOR: " + Main.ENCRYPTOR_WORKING.get());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
