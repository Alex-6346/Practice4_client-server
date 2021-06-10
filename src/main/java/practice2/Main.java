package practice2;


import practice1.Message;
import practice1.Packet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static practice1.CRC16.crc16;


public class Main {
    private static final byte MAGIC_BYTE = 0x13;

    public static AtomicBoolean DECRYPTOR_WORKING = new AtomicBoolean();
    public static AtomicBoolean PROCESSOR_WORKING = new AtomicBoolean();
    public static AtomicBoolean ENCRYPTOR_WORKING = new AtomicBoolean();

    public static void main(String[] args) throws InterruptedException {
        DECRYPTOR_WORKING.set(false);
        PROCESSOR_WORKING.set(false);
        ENCRYPTOR_WORKING.set(false);

        //=======================================\\
        BlockingQueue<byte[]> enteringQueue = new LinkedBlockingQueue<>();
        BlockingQueue<byte[]> receiverQueue = new LinkedBlockingQueue<>();
        BlockingQueue<Packet> decryptorQueue = new LinkedBlockingQueue<>();
        BlockingQueue<Packet> processorQueue = new LinkedBlockingDeque<>();
        BlockingQueue<byte[]> encryptorQueue = new LinkedBlockingQueue<>();

        Receiver receiver = new Receiver(enteringQueue, receiverQueue);

        Decryptor decryptor1 = new Decryptor(receiverQueue, decryptorQueue);
        Decryptor decryptor2 = new Decryptor(receiverQueue, decryptorQueue);

        Processor processor1 = new Processor(decryptorQueue, processorQueue);
        Processor processor2 = new Processor(decryptorQueue, processorQueue);

        Encryptor encryptor1 = new Encryptor(processorQueue, encryptorQueue);
        Encryptor encryptor2 = new Encryptor(processorQueue, encryptorQueue);

        //=========================================\\
        tenRandomMessages(enteringQueue);
        while (!enteringQueue.isEmpty()) {
            receiver.receivePacket();
        }

        Thread dec1 = new Thread(decryptor1);
        Thread dec2 = new Thread(decryptor2);

        Thread encr1 = new Thread(encryptor1);
        Thread encr2 = new Thread(encryptor2);

        Thread proc1 = new Thread(processor1);
        Thread proc2 = new Thread(processor2);


        dec1.start();
        proc1.start();
        encr1.start();
        dec2.start();
        proc2.start();
        encr2.start();


        Thread.sleep(2000);
        // Stopping decryptors:
        receiverQueue.put(Decryptor.POISON_BYTES);
        receiverQueue.put(Decryptor.POISON_BYTES);
        // Stopping processor:
        decryptorQueue.put(Processor.POISON_PACKET);
        decryptorQueue.put(Processor.POISON_PACKET);
        // Stopping encryptor:
        processorQueue.put(Encryptor.POISON_PACKET);
        processorQueue.put(Encryptor.POISON_PACKET);

        dec1.join();
        proc1.join();
        encr1.join();
        dec2.join();
        proc2.join();
        encr2.join();
    }

    public static void tenRandomMessages(BlockingQueue<byte[]> enteringQueue) throws InterruptedException {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            Message message = new Message(random.nextInt(6) + 1, random.nextInt(100) + 1, "Hello woooorld".getBytes(StandardCharsets.UTF_8));
            byte[] packet = encodePackage(new Packet((byte) (random.nextInt(100) + 1), (long) (random.nextInt(200) + 1), message));
            enteringQueue.put(packet);
        }
    }

    public static byte[] encodePackage(Packet packet) {
        Message message = packet.getMessage();

        byte[] head = ByteBuffer.allocate(14).order(ByteOrder.BIG_ENDIAN).
                put(MAGIC_BYTE).
                put(packet.getClient()). //number of application
                putLong(packet.getPacket()).//number of packet
                putInt(message.getText().length + 8).array();

        //System.out.println(new String(message.getText(), StandardCharsets.UTF_8));

        return ByteBuffer.allocate(16 + (message.getText().length + 8) * 2).order(ByteOrder.BIG_ENDIAN).
                put(head).
                putShort(crc16(head)).
                putInt(message.getCommand()). //number of command
                putInt(message.getUser()). //number of user
                put(encode(message.getText())).
                putShort(crc16(message.getText())).
                array();
    }

    public static Packet decodePackage(byte[] bytes) {
        ByteBuffer bb= ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN);

        if (bb.get()!=MAGIC_BYTE){
            throw new IllegalArgumentException("Magic byte");
        }
        byte client = bb.get();
        long packet = bb.getLong();
        int messageLength = bb.getInt();
        short crc16Head = bb.getShort();
       // System.out.println("Client:" + client);
        //System.out.println("Package:" + packet);
        //System.out.println("Length of message:" + messageLength);
        //System.out.println("CRC16 of head:" + crc16Head);

        byte[] head = ByteBuffer.allocate(14).order(ByteOrder.BIG_ENDIAN).
                put(MAGIC_BYTE).
                put(client). //number of application
                putLong(packet).//number of packet
                putInt(messageLength).array();
        if (crc16Head!=crc16(head)){
            throw new IllegalArgumentException("CRC 16 head");
        }


        byte[] messageArray = Arrays.copyOfRange(bytes,16,16+messageLength);
        ByteBuffer messagebb= ByteBuffer.wrap(messageArray).order(ByteOrder.BIG_ENDIAN);

        byte[] textArray = Arrays.copyOfRange(messageArray,8,messageLength);
        Message message= new Message(messagebb.getInt(),messagebb.getInt(), deencode(textArray));

        short crc16Message = bb.getShort(messageLength+16);

        if (crc16Message!=crc16(message.getText())){
            throw new IllegalArgumentException("CRC 16 message");
        }

        return new Packet(client,packet,message);
    }


    public static byte[] deencode(byte[] bytes) {
        char XorKey = 'p';
        String output = "";
        String bytesString = new String(bytes, StandardCharsets.UTF_8);
        int len = bytesString.length();

        for (int i = 0; i < len; i++) {
            output = output + (char) (bytesString.charAt(i) ^ XorKey);
        }

        return output.getBytes();
    }

    public static byte[] encode(byte[] bytes) {
        return deencode(bytes);
    }

}
