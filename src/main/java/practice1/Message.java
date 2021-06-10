package practice1;

import java.util.Arrays;

public class Message {

    private final int command;
    private final int user;
    private final byte[] text;

    public Message(final int command, final int user, final byte[] text){
        this.command=command;
        this.user=user;
        this.text=text;

    }

    public int getCommand() {
        return command;
    }
    public int getUser() {
        return user;
    }
    public byte[] getText() { return text; }

    @Override
    public String toString() {
        return "Message{" +
                "command=" + command +
                ", user=" + user +
                ", text=" + Arrays.toString(text) +
                '}';
    }
}
