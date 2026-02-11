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
                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                String command;

                while (true) {
                    command = in.readUTF();
                    if (command.toUpperCase().equals("L")) {
                        File folder = new File("serverFiles");
                        File [] files = folder.listFiles();

                        if (files != null) {
                            for (File f : files) {
                                out.writeUTF(f.getName());
                            }
                        }
                        out.writeUTF("...End...");
                    }
                    // add delete, rename, download, upload, and quit here
                    if (command.toUpperCase().equals("U")) {
                        String fileName = in.readUTF();
                        long fileSize = in.readLong();

                        FileOutputStream fos = new FileOutputStream("serverFiles/" + fileName);
                        //InputStream socketIn = clientSocket.getInputStream();

                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        long totalRead = 0;

                        while (totalRead < fileSize) {
                            bytesRead = in.read(buffer);
                            fos.write(buffer, 0, bytesRead);
                            totalRead += bytesRead;
                        }
                        fos.close();
                        System.out.println("Upload complete: " + fileName);

                    }
                    else if (command.toUpperCase().equals("W")) {
                        String fileName = in.readUTF();
                        long fileSize = in.readLong();

                        FileOutputStream fos = new FileOutputStream("clientFiles/" + fileName);
                        //InputStream socketIn = clientSocket.getInputStream();

                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        long totalRead = 0;

                        while (totalRead < fileSize) {
                            bytesRead = in.read(buffer);
                            fos.write(buffer, 0, bytesRead);
                            totalRead += bytesRead;
                        }
                        fos.close();
                        System.out.println("Download complete: " + fileName);
                    }
                }
            }
        } //end try
    } // end main
} //end class
