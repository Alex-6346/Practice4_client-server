package practice2;

import java.util.concurrent.BlockingQueue;

public class Receiver {

    private BlockingQueue<byte[]> enteringQueue;
    private BlockingQueue<byte[]> receivedQueue;
    private byte[] bytes;

    public Receiver(BlockingQueue<byte[]> enteringQueue, BlockingQueue<byte[]> receivedQueue) {
        this.enteringQueue = enteringQueue;
        this.receivedQueue = receivedQueue;
    }

    public void receivePacket() throws InterruptedException {
        bytes = enteringQueue.take();
        receivedQueue.put(bytes);
    }
}
