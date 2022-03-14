import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader br;
    private BufferedWriter bw;
    private String clientName;

    public Client(Socket socket, String clientName) {
        try {
            this.socket = socket;
            this.clientName = clientName;
            this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            closeConnections(socket, bw, br);
        }

    }

    private void closeConnections(Socket socket, BufferedWriter bw, BufferedReader br) {
        try {
            if(bw != null) bw.close();
            if(br != null) br.close();
            if(socket != null) socket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage() {
        try {
            bw.write(clientName);
            //Write a new line
            bw.newLine();
            //Flush the buffer (buffer will likely not be full and thus not flushed automatically)
            bw.flush();

            //Get in user input (from console)
            Scanner in = new Scanner(System.in);

            //Clients are able to send messages while they are connected.
            while(socket.isConnected()) {
                String msg = in.nextLine();
                bw.write("[" + clientName +"]: " + msg);
                bw.newLine();
                bw.flush();
            }
        } catch (IOException e) {
            closeConnections(socket, bw, br);
        }
    }

    //Listen for messages that have been broadcasted
    public void messageListener() {

        //Listening for message is blocking. It needs to run in its own thread.
        new Thread(() -> {
            String chatMsg;

            //Listen while there is a connections
            while(socket.isConnected()) {
                try {
                    chatMsg = br.readLine();
                    System.out.println(chatMsg);
                } catch (IOException e) {
                    closeConnections(socket, bw, br);
                }
            }
        }).start();
    }
}
