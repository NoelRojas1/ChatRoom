import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class is responsible for listening to clients that wish to connect ot the chat
 */

public class Server {

    private ServerSocket socketServer = null;

    //Global variable for the port in the Server class
    private static int port = 4500;

    private Server(ServerSocket serverSocket) {
        this.socketServer = serverSocket;
    }

    public void startServer() {
        /**
         * The server will loop/run until the connection is terminated
         */

        try {
            while(!socketServer.isClosed()) {
                // System.out.println("Server listening on port " + port);

                // Create a Socket that accepts all incoming connections.
                //.accept() method is blocking -> the thread will halt here until there is a connection.
                Socket socket = socketServer.accept();

                //After a connection is established, do some processing.
                System.out.println("A connection was established!");

                //Chat Manager will create a new thread for every connection
                ChatManager cm = new ChatManager(socket);

                Thread thread = new Thread(cm);
                thread.start();

            }

        } catch (IOException e) {
            closeServerSocket();
        }
    }


    public void closeServerSocket() {
        if(socketServer != null) {
            try {
                socketServer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getPort() {
        return port;
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
