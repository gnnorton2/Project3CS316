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
                        File folder = new File("ServerFiles");
                        File [] files = folder.listFiles();

                        if (files != null) {
                            for (File f : files) {
                                out.writeUTF(f.getName());
                            }
                        }
                        out.writeUTF("...End...");
                    }
                    else if (command.equalsIgnoreCase("D")){
                        String fileName = in.readUTF();
                        File file = new File("ServerFiles", fileName);
                        if(file.exists() && file.delete()){
                            out.writeUTF("S"); //success
                        } else {
                            out.writeUTF("F"); //failure
                        }
                        out.flush();
                    }
                    else if (command.equalsIgnoreCase("R")){
                        String oldName = in.readUTF();
                        String newName = in.readUTF();
                        File oldFile = new File("ServerFiles", oldName);
                        File newFile = new File("ServerFiles", newName);
                        if(oldFile.exists() && oldFile.renameTo(newFile)){
                            out.writeUTF("S");
                        } else {
                            out.writeUTF("F");
                        }
                        out.flush();
                    }
                    else if (command.toUpperCase().equals("U")) {
                        String fileName = in.readUTF();
                        long fileSize = in.readLong();

                        FileOutputStream fos = new FileOutputStream("ServerFiles/" + fileName);
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
                        out.writeUTF("S");
                        out.flush();
                    }
                    else if (command.toUpperCase().equals("W")) {
                        String fileName = in.readUTF();
                        File file = new File("ServerFiles", fileName);
                        if (!file.exists()) {
                            out.writeLong(-1);
                            out.flush();
                            continue;
                        }
                        out.writeLong(file.length());
                        FileInputStream fis = new FileInputStream(file);
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) > 0) {
                            out.write(buffer, 0, bytesRead);
                        }
                        fis.close();
                        out.flush();
                        System.out.println("Download complete: " + fileName);
                    }
                }
            }
        } //end try
    } // end main
} //end class
