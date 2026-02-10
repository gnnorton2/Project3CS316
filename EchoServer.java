import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {
    public static void main(String[] args) throws Exception {
        //server port
        int port = 3000;

        try(ServerSocket mySocket = new ServerSocket(3000)) {
            while(true) {
                System.out.println("Server is waiting on port... " + port);

                // line 14 unique to TCP because it's connection-oriented
                //establish the connection first
                Socket clientSocket = mySocket.accept();

                System.out.println("Client connected: " + clientSocket.getInetAddress()); //
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                String command;

                while ((command = in.readLine()) != null) {
                    if (command.toUpperCase().equals("L")) {
                        File folder = new File("serverFiles");
                        File [] files = folder.listFiles();

                        if (files != null) {
                            for (File f : files) {
                                out.println(f.getName());
                            }
                        }
                        out.println("...End...");
                    }
                    // add delete, rename, download, upload, and quit here
                }
            }
        } //end try
    } // end main
} //end class
