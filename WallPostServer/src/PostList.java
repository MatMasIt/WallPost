import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class PostList extends ArrayList<Post> implements Serializable {
    public int getNewId(){
        int u;
        Random r = new Random();
        u=r.nextInt();
        for(int i=0;i<this.size();i++){
            u=r.nextInt();
            if(this.get(i).getId()==u){
                return this.getNewId();
            }
        }
        return u;
    }
}
