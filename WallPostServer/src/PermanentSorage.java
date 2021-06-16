import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class PermanentSorage extends Thread{
    public UserList getUList() {
        return UList;
    }

    public PostList getPList() {
        return PList;
    }

    private UserList UList;
    private PostList PList;
    private FileInputStream FIn=null;
    private ObjectInputStream ObjIn=null;
    private boolean firstTime;
    public PermanentSorage(UserList UList, PostList PList) {
        this.UList=UList;
        this.PList=PList;
        try {
            File f = new File("SAVE.DAT");
            if(!f.exists()) {
                this.firstTime= f.createNewFile();
            }
            else {
                FIn = new FileInputStream(f);
                ObjIn = new ObjectInputStream(FIn);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadFromDisk(){
        try {
            if(!firstTime) {
                SaveContainer c = (SaveContainer) ObjIn.readObject();
                this.UList = c.getUl();
                this.PList = c.getPl();
                ObjIn.close();
                FIn.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        while(true) {
            try {
                System.out.println("STARTING SAVE");
                FileOutputStream FOut= new FileOutputStream(new File("SAVE.DAT"));
                ObjectOutputStream ObjOut= new ObjectOutputStream(FOut);
                ObjOut.writeObject(new SaveContainer(UList,PList));
                FOut.close();
                Thread.sleep(60000);//10 sec
                System.out.println("END SAVE");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
