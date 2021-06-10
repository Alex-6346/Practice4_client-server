package practice3;

import practice1.Packet;

import java.io.IOException;

public interface Server
{
    void send(Packet packet);
    Packet receive() throws IOException;


}
