import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ServerThread extends Thread {
    protected Socket socket;
    protected UserList UList;
    protected PostList PList;
    protected ArrayList<String> headers, values;
    public ServerThread(Socket clientSocket,UserList UList,PostList PList) {
        this.socket = clientSocket;
        this.UList=UList;
        this.PList=PList;
    }
    /*

          ERROR
          TITLE
          END
          TEXT
          END
          END
          END


            ERROR
            TITLE
            END
            TEXT
            END
            QUIT
            QUIT

           */
    private String sanitize(String a){
        return a.replaceAll("FOLLOWS","follows").replaceAll("END","END");
    }
    private synchronized UserList accessUserList(){
        return this.UList;
    }
    private synchronized PostList accessPostList(){
        return this.PList;
    }
    public String valueByHeader(String header) throws HeaderNotFoundException {
        for(int i=0;i<headers.size();i++){
            if(headers.get(i).equals(header)){
                return values.get(i);
            }
        }
        throw new HeaderNotFoundException();
    }
    public void run() {
        InputStream inp = null;
        BufferedReader in = null;
        DataOutputStream out = null;
        try {
            inp = socket.getInputStream();
            in = new BufferedReader(new InputStreamReader(inp));
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            return;
        }
        String line,action=null,name="USER";
        headers= new ArrayList<String>();
         values= new ArrayList<String>();
        while (true) {
            try {
                line = in.readLine();
                if ((line == null) || line.equalsIgnoreCase("QUIT")) {
                    socket.close();
                    return;
                } else {
                    if(line.startsWith("ACTION")) {
                        headers.clear();
                        values.clear();
                        action=line.split(" ", 2)[1];
                        while(!line.equals("END")){
                            if(line.startsWith("FOLLOWS")){
                                headers.add(line.split(" ", 2)[1]);
                            }
                            else{
                                if(!line.startsWith("ACTION")) {
                                    values.add(line);
                                }
                            }
                            line=in.readLine();
                        }
                    }
                    if(line.equals("END")){
                        if(action.equals("signIn")){
                            UserList u=accessUserList();
                            boolean access= u.signIn(new User(valueByHeader("username"),valueByHeader("password")));
                            out.writeBytes("OK\n");
                            out.writeBytes("FOLLOWS status\n");
                            if(access){
                                out.writeBytes("OK\n");
                                out.writeBytes("END\n");
                                name=valueByHeader("username");
                            }
                            else{
                                out.writeBytes("NO\n");
                                out.writeBytes("END\n");

                            }
                        }
                        else if(action.equals("signUp")){
                            UserList u=accessUserList();
                            boolean access= u.signUp(new User(valueByHeader("username"),valueByHeader("password")));
                            out.writeBytes("OK\n");
                            out.writeBytes("FOLLOWS status\n");
                            if(access){
                                out.writeBytes("OK\n");
                                out.writeBytes("END\n");
                                name=valueByHeader("username");
                            }
                            else{
                                out.writeBytes("NO\n");
                                out.writeBytes("END\n");

                            }
                        }
                        else if(name.equals("DEFAULT")){
                            out.writeBytes("ERROR\n" +
                                    "Authentication Error\n" +
                                    "END\n" +
                                    "You must log in" +
                                    "END\n" +
                                    "QUIT\n" +
                                    "QUIT\n");
                        }
                        else if(action.equals("listPosts")){

                            PostList p= accessPostList();
                            out.writeBytes("OK\n");
                            for(int i=0;i<p.size();i++){
                                //Post c= p.get(i);
                                out.writeBytes("FOLLOWS POST-Id-"+String.valueOf(i) +"\n"+
                                        sanitize(String.valueOf(p.get(i).getId()))+"\n" +
                                        "FOLLOWS POST-Title-"+String.valueOf(i) +"\n"+
                                        sanitize(p.get(i).getTitle())+"\n" +
                                        "FOLLOWS POST-Author-"+String.valueOf(i) +"\n"+
                                        sanitize(p.get(i).getAuthor())+"\n" +
                                        "FOLLOWS POST-Date-"+String.valueOf(i) +"\n"+
                                        sanitize(p.get(i).getDate())+"\n");
                            }
                            out.writeBytes("END\n");
                        }
                        else if(action.equals("getPostBody")){
                            PostList p= accessPostList();
                            Post a= new Post("Error","Post not found","Server","01/01/1970",-1);
                            for(int i=0;i<p.size();i++){
                                if(p.get(i).getId()==Integer.parseInt(this.valueByHeader("POST-Id"))){
                                    a=p.get(i);
                                    out.writeBytes("OK\n");
                                    out.writeBytes("FOLLOWS body\n");
                                    out.writeBytes(a.getBody()+"\n");
                                    out.writeBytes("END\n");
                                    break;
                                }
                            }
                        }
                        else if(action.equals("sendPost")){
                            int ID=this.accessPostList().getNewId();
                            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            Date date = new Date();
                            Post p= new Post(this.valueByHeader("POST-Title"),this.valueByHeader("POST-Body"),name,dateFormat.format(date),ID);
                            PostList pl= accessPostList();
                            pl.add(p);
                            out.writeBytes("OK\n");
                            out.writeBytes("FOLLOWS status\n");
                            out.writeBytes("OK\n");
                            out.writeBytes("END\n");
                        }

                    }
                    //out.writeBytes(line + "\n\r");
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            } catch (HeaderNotFoundException e) {
                e.printStackTrace();
                try {
                    out.writeBytes("ERROR\n" +
                            "Request Error\n" +
                            "END\n" +
                            "The server received a malformed request\n" +
                            "END\n" +
                            "END\n" +
                            "END\n");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }
}