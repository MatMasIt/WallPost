import javax.swing.*;
import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.rmi.ServerError;
import java.util.ArrayList;

public class ServerConnection {
    private Socket socket;
    private InputStreamReader i;
    private BufferedReader in;
    private OutputStreamWriter osw;
    private BufferedWriter bw;
    private PrintWriter out;
    private ArrayList<String> headers=new ArrayList<String>();
    private  ArrayList<String> values=new ArrayList<String>();
    private JFrame window;
    public ServerConnection(JFrame window) throws IOException {
        this.window=window;
        socket= new Socket("mascmt.ddns.net",70);
        i= new InputStreamReader(socket.getInputStream());
        in = new BufferedReader(i);
        osw= new OutputStreamWriter(socket.getOutputStream());
        bw =new BufferedWriter(osw);
        out= new PrintWriter(bw, true);
        out.println("HELLO");
    }
    public ArrayList<ArrayList<String>>  sendData(String action) throws IOException, ServerErrorException {//lenght of Arralist[] is 2 as in headers, values
        ArrayList<ArrayList<String>> hv=new ArrayList<ArrayList<String>>();
        hv.add(new ArrayList<String>());
        hv.add(new ArrayList<String>());
        out.println("HELLO AGAIN");
        out.println("ACTION " + action);
        for (int c = 0; c < headers.size(); c++) {
            out.println("FOLLOWS " + headers.get(c).replaceAll("FOLLOWS","follows").replaceAll("END","end"));
            out.println(values.get(c).replaceAll("FOLLOWS","follows").replaceAll("END","end"));
        }
        out.println("END");
        String line="",temp="";
        if(in.readLine().equals("ERROR")){
            while(true){
                line=in.readLine();
                if(line.equals("END")){
                    String title=in.readLine();
                    String type=in.readLine();

                    if(in.readLine().equals("QUIT")){
                        throw new ServerErrorException(title,temp,in.readLine(),true);
                    }
                    else{
                        throw new ServerErrorException(title,temp,in.readLine(),false);
                    }
                }
                else{

                    temp+=line;
                }
            }
        }
        int flNum=0;
        do{
            line=in.readLine();
            if(line.startsWith("FOLLOWS")){
                hv.get(0).add(line.split(" ",2)[1]);
                if(flNum!=0){
                    hv.get(1).add(temp);
                }
                temp="";
                flNum++;
            }
            else if(line.equals("END")){
                hv.get(1).add(temp);
            }
            else{
                temp+=line;
            }

        }while (!line.equals("END"));
        return hv;
    }
    public void init(){
        headers.clear();
        values.clear();
    }
    public void bindParam(String h,String v){
        headers.add(h);
        values.add(v);
    }

    public ArrayList<ArrayList<String>> handledGetData(String action){
        ArrayList<ArrayList<String>> s=new ArrayList<ArrayList<String>>(2);
        try {
            s=this.sendData(action);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(window,
                    "Connection error",
                    "Could not connect to the server",
                    JOptionPane.ERROR_MESSAGE);
        } catch (ServerErrorException e) {
            handleServerErrorInGUI(e.title,e.message,e.errorType,e.mustQuit);
        }
        System.out.println(s.toString());
        return s;
    }

    public void handleServerErrorInGUI(String title, String text, String type, boolean mustQuit){
        if(type.equals("INFO")){
            JOptionPane.showMessageDialog(this.window, title, text,JOptionPane.INFORMATION_MESSAGE);

        }
        else if(type.equals("WARNING")){
            JOptionPane.showMessageDialog(this.window,
                    title,
                    text,
                    JOptionPane.WARNING_MESSAGE);


        }
        else if(type.equals("PLAIN")){
            JOptionPane.showMessageDialog(this.window,
                    title,
                    text,
                    JOptionPane.PLAIN_MESSAGE);


        }
        else{//ERROR
            JOptionPane.showMessageDialog(this.window,
                    title,
                    text,
                    JOptionPane.ERROR_MESSAGE);

        }
        if(mustQuit) {
            System.exit(1);
        }
    }
    public String valueByHeader(ArrayList<ArrayList<String>> resp,String header) throws HeaderNotFoundException {
        for(int i=0;i<resp.get(0).size();i++){
            if(resp.get(0).get(i).equals(header)){
                return resp.get(1).get(i);
            }
        }
        throw new HeaderNotFoundException();
    }
    public boolean signIn(String username, String password){
        this.init();
        this.bindParam("username",username);
        this.bindParam("password",password);
        ArrayList<ArrayList<String>>  li= this.handledGetData("signIn");
        try {
            return this.valueByHeader(li,"status").trim().equals("OK");
        } catch (HeaderNotFoundException e) {
            return false;
        }
    }
    public boolean signUp(String username, String password){
        this.init();
        this.bindParam("username",username);
        this.bindParam("password",password);
        ArrayList<ArrayList<String>> li= this.handledGetData("signUp");

        try {
            return this.valueByHeader(li,"status").trim().equals("OK");
        } catch (HeaderNotFoundException e) {
            return false;
        }
    }
    public PostList fetchPostsFromServer(){
        this.init();
        ArrayList<ArrayList<String>> li =this.handledGetData("listPosts");
        PostList p= new PostList();
        int count=0;
        for(int i=0; i<li.get(0).size();i++){
            System.out.println(li.get(0).get(i));
            if(li.get(0).get(i).startsWith("POST-Id")){//POST-Id-0
                try {
                    Post tp= new Post(this.valueByHeader(li,"POST-Title-"+String.valueOf(count)),"empty",this.valueByHeader(li,"POST-Author-"+String.valueOf(count)),this.valueByHeader(li,"POST-Date-"+String.valueOf(count)),Integer.parseInt(this.valueByHeader(li,"POST-Id-"+String.valueOf(count))));

                    p.add(tp);
                } catch (HeaderNotFoundException e) {
                        e.printStackTrace();
                }
                count++;
            }
        }
        return p;

    }
    public Post fetchPostBodyIfEmpty(PostList list, int index){
        if(index<0){
            return null;
        }
        Post a=list.get(index);
        System.out.println(a.toString());
        if(a.getBody().equals("empty")){
            this.init();
            this.bindParam("POST-Id", String.valueOf(a.getId()));
            ArrayList<ArrayList<String>> li=this.handledGetData("getPostBody");
            String body="";
            try {
                body=this.valueByHeader(li,"body");
            } catch (HeaderNotFoundException e) {
                body="An error occurred while loading the post";
            }
            a.setBody(body);
            list.set(index,a);
        }
        return a;
    }
    public void publishPost(String title,String body){
        this.init();
        this.bindParam("POST-Title",title);
        this.bindParam("POST-Body",body);
        this.handledGetData("sendPost");
    }
    /*
    NOT SUPPORTED BY SERVER
    @Deprecated
    public Post getPost(int Id) throws HeaderNotFoundException {
        this.init();
        this.bindParam("POST-Id",String.valueOf(Id));
        ArrayList<ArrayList<String>> li=this.handledGetData("getPost");
        Post p= new Post(this.valueByHeader(li,"POST-Title"),this.valueByHeader(li,"POST-Body"),this.valueByHeader(li,"POST-Author"),this.valueByHeader(li,"POST-Date"),Integer.parseInt(this.valueByHeader(li,"POST-Id")));
        return p;
    }

     */

}
