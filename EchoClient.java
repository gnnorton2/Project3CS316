import java.io.*;
import java.net.Socket;

public class EchoClient {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Please specify server IP & port...");
            return;
        }
        String serverIP = args[0];
        int serverPort = Integer.parseInt(args[1]);
        //tries to establish connection to this Ip and this port
        // the other side uses accept to accept this port
        try(Socket socket = new Socket(serverIP, serverPort)){
            System.out.println("Connected to the server...");
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataInputStream keyboard = new DataInputStream(System.in);
            while (true) {
                //new code for project, while loop on both sides
                System.out.println("Enter command: (l)ist, (d)elete, (r)ename, do(w)nload, (u)pload, (q)uit\n");
                String command = keyboard.readLine();
                String[] parts = command.split(" ");
                String commandTwo = parts[0];
                if (command.toUpperCase().equals("Q")) {
                    out.writeUTF("Q");
                    out.flush();
                    System.out.println("...Quitting...");
                    break;
                }
                else if(command.equalsIgnoreCase("D")){
                    System.out.println("Enter file name to delete: ");
                    String fileName = keyboard.readLine();
                    out.writeUTF("D");
                    out.writeUTF(fileName);
                    out.flush();
                    String status = in.readUTF();
                    if(status.equals("S")){
                        System.out.println("File deleted successfully.");
                    } else {
                        System.out.println("Delete failed.");
                    }
                }
                else if (command.equalsIgnoreCase("R")){
                    System.out.println("Enter current file name: ");
                    String oldName = keyboard.readLine();
                    System.out.println("Enter new file name: ");
                    String newName = keyboard.readLine();
                    out.writeUTF("R");
                    out.writeUTF(oldName);
                    out.writeUTF(newName);
                    out.flush();
                    String status = in.readUTF();
                    if(status.equals("S")){
                        System.out.println("Rename successful.");
                    } else {
                        System.out.println("Rename failed.");
                    }
                }
                else if (command.toUpperCase().equals("L")) {
                    out.writeUTF("L");
                    String line;
                    while ((line = in.readUTF()) != null && !line.equals("...End...")) {
                        System.out.println(line);
                    }
                }
                else if (command.toUpperCase().equals("U")) {
                    System.out.println("Enter the file name to upload:\n");
                    String fileName = keyboard.readLine();
                    //System.out.println("Looking for file at: " + new File("clientFiles", fileName).getAbsolutePath());
                    File file = new File("ClientFiles", fileName);
                    if (!file.exists()) {
                        System.out.println("File upload failed (does not exist)\n");
                        return;
                    }
                    out.writeUTF("U");
                    out.writeUTF(file.getName());
                    out.writeLong(file.length());

                    FileInputStream fis = new FileInputStream(file);

                    byte[] buffer = new byte[1024];
                    int bytesRead;

                    while ((bytesRead = fis.read(buffer)) > 0) {
                        out.write(buffer, 0, bytesRead);
                    }
                    String status = in.readUTF();
                    if (status.equals("S")) {
                        System.out.printf("%s has been uploaded\n", fileName);
                    }
                    else {
                        System.out.println("File check has failed: no upload");
                    }

                    out.flush();
                    fis.close();
                    }
                else if (command.toUpperCase().equals("W")) {
                    System.out.println("Enter the file name to download:\n");
                    String fileName = keyboard.readLine();
                    out.writeUTF("W");
                    out.writeUTF(fileName);
                    out.flush();
                    long fileSize = in.readLong();
                    if (fileSize == -1) {
                        System.out.println("File does not exist on server.");
                        continue;
                    }
                    FileOutputStream fos = new FileOutputStream("ClientFiles/" + fileName);
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
            String line;
            while ((line = in.readUTF()) != null) {
                if (line.equals("...End...")) {
                    break;
                }
                System.out.println(line);
            }
        }
    }
}