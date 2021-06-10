package practice2;

import practice2.Main;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Sender {
    public Sender() { }


   public void sendMessage(byte[] bytes) {
       ByteBuffer bb= ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN);

       if (bb.get()!=0x13){
           throw new IllegalArgumentException("Magic byte");
       }
       byte client = bb.get();
       long packet = bb.getLong();
       int messageLength = bb.getInt();
       short crc16Head = bb.getShort();
       System.out.println("practice3.Client:" + client);
       System.out.println("Package:" + packet);
       System.out.println("Length of message:" + messageLength);
       System.out.println("CRC16 of head:" + crc16Head);

       byte[] messageArray = Arrays.copyOfRange(bytes,16,16+messageLength);
       byte[] textArray = Arrays.copyOfRange(messageArray,8,messageLength);
       System.out.println("Message:" + new String(Main.deencode(textArray),StandardCharsets.UTF_8));
    }

}
