import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter username to join the chat: ");
        String username = in.nextLine();
        Socket socket = new Socket("localhost", Server.getPort());
        Client client = new Client(socket, username);
        client.messageListener(); //Running in its own thread.
        client.sendMessage(); //Running in its own thread.
    }
}
