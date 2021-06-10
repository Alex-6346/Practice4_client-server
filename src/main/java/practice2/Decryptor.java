package practice2;


import practice1.Message;
import practice1.Packet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

import static practice1.CRC16.crc16;


public class Decryptor implements Runnable {

    private BlockingQueue<byte[]> receiverQueue;
    private BlockingQueue<Packet> decryptedQueue;
    private boolean isWorking;

    public Decryptor(BlockingQueue<byte[]> receiverQueue, BlockingQueue<Packet> decryptedQueue) {
        this.receiverQueue = receiverQueue;
        this.decryptedQueue = decryptedQueue;
        isWorking = true;
    }


    public void decrypt(byte[] bytes) throws InterruptedException {
        ByteBuffer bb = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN);

        if (bb.get() != 0x13) {
            throw new IllegalArgumentException("Magic byte");
        }
        byte client = bb.get();
        long packet = bb.getLong();
        int messageLength = bb.getInt();
        short crc16Head = bb.getShort();
        //System.out.println("practice3.Client:" + client);
        //System.out.println("Package:" + packet);
        //System.out.println("Length of message:" + messageLength);
        //System.out.println("CRC16 of head:" + crc16Head);

        byte[] head = ByteBuffer.allocate(14).order(ByteOrder.BIG_ENDIAN).
                put((byte) 0x13).
                put(client). //number of application
                putLong(packet).//number of packet
                putInt(messageLength).array();
        if (crc16Head != crc16(head)) {
            throw new IllegalArgumentException("CRC 16 head");
        }

        byte[] messageArray = Arrays.copyOfRange(bytes, 16, 16 + messageLength);
        ByteBuffer messagebb = ByteBuffer.wrap(messageArray).order(ByteOrder.BIG_ENDIAN);

        byte[] textArray = Arrays.copyOfRange(messageArray, 8, messageLength);
        Message message = new Message(messagebb.getInt(), messagebb.getInt(), Main.deencode(textArray));

        short crc16Message = bb.getShort(16 + messageLength);

        //System.out.println(new String(message.getText(),StandardCharsets.UTF_8));
        System.out.println(crc16Message);

        if (crc16Message != crc16(message.getText())) {
            throw new IllegalArgumentException("CRC 16 message");
        }

        Packet decryptedPacket = new Packet(client, packet, message);
        //return new Packet(client,packet,message);

        decryptedQueue.put(decryptedPacket);
    }


    public static final byte[] POISON_BYTES = {0};

    @Override
    public void run() {
        try {
            while (true) {
                byte[] receivedBytes = receiverQueue.take();
                if (Arrays.compare(receivedBytes, POISON_BYTES) == 0) {
                    return;
                }
                decrypt(receivedBytes);
                //System.out.println("a");
                System.out.println("DECRYPTOR: " + Main.DECRYPTOR_WORKING.get());
                System.out.println("PROCESSOR: " + Main.PROCESSOR_WORKING.get());
                System.out.println("ENCRYPTOR: " + Main.ENCRYPTOR_WORKING.get());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
