import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatManager implements Runnable {

    //This list belongs to the class not to every instance of the class. This is why it is static
    private static List<ChatManager> chats = new ArrayList<>();

    private Socket socket;

    //Create buffer to read data/messages
    private BufferedReader br;

    //Create buffer to send data
    private BufferedWriter bw;

    //User name of the client
    private String clientUserName;

    public ChatManager(Socket socket) {
        try {
            this.socket = socket;
            this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //Get client username
            this.clientUserName = br.readLine();

            //Add client to list
            chats.add(this);

            //Send messages to connected clients
            broadcastMessage("[Server]: " + clientUserName + " has joined the chat!");

        }catch (IOException e) {
            closeConnections(socket, bw, br);
        }
    }

    private void closeConnections(Socket socket, BufferedWriter bw, BufferedReader br) {
        removeClient();

        try {
            if(bw != null) bw.close();
            if(br != null) br.close();
            if(socket != null) socket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcastMessage(String msg) {
        //Send message to every client in the chat except for who sent it.
        chats.stream().filter(client -> !client.clientUserName.equals(clientUserName))
                .forEach(client -> {
                    try {
                        client.bw.write(msg);
                        //Write a new line
                        client.bw.newLine();
                        //Flush the buffer (buffer will likely not be full and thus not flushed automatically)
                        client.bw.flush();
                    } catch (IOException e) {
                        closeConnections(socket, bw, br);
                    }
                });
    }

    //Listening for messages is blocking. Thus, it needs to run in its own thread
    @Override
    public void run() {
        String clientMsg;

        //Listen for messages
        while(socket.isConnected()) {
            try {
                clientMsg = br.readLine();
                broadcastMessage(clientMsg);
            } catch (IOException e) {
                closeConnections(socket, bw, br);

                //Break if all clients disconnect.
                break;
            }
        }
    }

    private void removeClient() {
        broadcastMessage(this.clientUserName + " has left the chat.");
        chats.remove(this);
    }
}
