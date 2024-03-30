import java.util.Date;

public class Message {
    private String filename;
    private String sender;
    private String receiver;
    private String content;
    private Date timestamp;

    public Message(String sender, String receiver, String content, Date timestamp) {

        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = timestamp;

        if(sender.compareTo(receiver)>0){
            filename = sender+receiver;
        } else if (sender.compareTo(receiver)<0) {
            filename = receiver+sender;
        } else {
            filename = null;
        }

    }
}
