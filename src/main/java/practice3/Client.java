package practice3;

import practice1.Packet;

import java.net.UnknownHostException;

public interface Client
{
    void send(byte[] bytes) throws UnknownHostException;
    Packet receive();

    boolean isConnectionAvailable();
}
