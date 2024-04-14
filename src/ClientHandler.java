import java.net.Socket;
import java.util.Scanner;
import java.io.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private int threadNum;

    public ClientHandler(Socket socket, int threadNum) {
        this.socket = socket;
        this.threadNum = threadNum;
    }

    public void run() {
        System.out.printf("Thread %d\n", threadNum);

    }

}
