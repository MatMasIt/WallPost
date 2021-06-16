import java.io.Serializable;
import java.util.ArrayList;

public class UserList extends ArrayList<User> implements Serializable{
    public boolean signUp(User a){
        for(int i=0;i<this.size();i++){
            if(this.get(i).getUsername().equals(a.getUsername())){
                return false;
            }
        }
        this.add(a);
        return true;
    }
    public boolean signIn(User a){

        for(int i=0;i<this.size();i++){
            if(this.get(i).getUsername().equals(a.getUsername()) && this.get(i).getPassword().equals(a.getPassword())){
                return true;
            }
        }
        return false;
    }

}
