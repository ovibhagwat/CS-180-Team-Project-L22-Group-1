import java.util.HashMap;
import java.util.Map;
/**
 * A program that implements TestMultipleClient class
 *
 * <p>Purdue University -- CS18000 -- Spring 2024</p>
 *
 * @author Yixin Hu
 * @version April 15, 2024
 */

public class TestMultipleClients {
    public static void main(String[] args) {
        Runnable clientTask = () -> {
            Client client = new Client("localhost", 4242);
            try {
                for (int i = 0; i < 5; i++) {
                    Map<String, Object> parameters = new HashMap<>();
                    parameters.put("information", "test " + i);
                    RequestResponseProtocol.Request request = new RequestResponseProtocol.Request(RequestResponseProtocol.RequestType.TEST, parameters);
                    client.sendRequest(request);
                    RequestResponseProtocol.Response response = client.receiveResponse();
                    System.out.println(response.getData());
                }
            } finally {
                client.close();
            }
        };

        // Start multiple clients
        for (int i = 0; i < 5; i++) {
            new Thread(clientTask).start();
        }
    }

}
