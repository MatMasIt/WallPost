import java.io.Serializable;

public class User implements Serializable {
    private String username, password;

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public User(String username, String password){
        this.username=username;
        this.password=password;
    }
}
