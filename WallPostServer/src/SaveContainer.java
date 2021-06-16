import java.io.Serializable;

public class SaveContainer implements Serializable {
    public UserList getUl() {
        return ul;
    }

    public void setUl(UserList ul) {
        this.ul = ul;
    }

    public PostList getPl() {
        return pl;
    }

    public void setPl(PostList pl) {
        this.pl = pl;
    }

    private UserList ul;
    private PostList pl;
    public SaveContainer(UserList ul, PostList pl){
        this.ul=ul;
        this.pl=pl;
    }

}
