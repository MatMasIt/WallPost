import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WallPostServer {

    static final int PORT = 5555;
    public static void main(String args[]) {
        ServerSocket serverSocket = null;
        Socket socket = null;
        UserList UList= new UserList();
        PostList PList= new PostList();
       /* PermanentSorage ps=new PermanentSorage(UList,PList);
        ps.loadFromDisk();
        UList=ps.getUList();
        PList=ps.getPList();
        ps.start();*/
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();

        }
        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            // new thread for a client
            new ServerThread(socket,UList,PList).start();
        }
    }
}