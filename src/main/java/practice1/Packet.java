package practice1;

import java.util.Arrays;

public class Packet {

    private final byte client;
    private final long packet;
    private final Message message;

    public Packet(final byte client, final long packet, final Message message){
        this.client=client;
        this.packet=packet;
        this.message=message;

    }

    public byte getClient() {
        return client;
    }
    public long getPacket() {
        return packet;
    }
    public Message getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "client=" + client +
                ", packet=" + packet +
                ", message=" + Arrays.toString(message.getText()) +
                '}';
    }
}
