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
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

            //new code for project, while loop on both sides
            System.out.println("Enter command: (l)ist, (d)elete, (r)ename, do(w)nload, (u)pload, (q)uit\n");
            String command = keyboard.readLine();

            out.println(command);
            //tell the server client is done sending
            //this is the special signal
            socket.shutdownOutput();

            String line;
            while ((line = in.readLine()) != null) {
                if (line.equals("...End...")) {
                    break;
                }
                System.out.println(line);
            }

            //file is hardcoded, later will use a scanner to choose the files
            File myFolder = new File("clientFiles");
            if(!myFolder.exists()) {
                myFolder.mkdirs();
            }

            File myFile = new File(myFolder, command);
            byte[] buffer = new byte[1024];
            int bytesRead;


        }
    }
}