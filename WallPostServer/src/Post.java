import java.io.Serializable;

public class Post implements Serializable {
    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    private String title, body,author, date;
    private int id;
    public void setBody(String body) {
        this.body = body;
    }

    public Post(String title, String body, String author, String date, int id){
        this.title=title;
        this.body=body;
        this.author=author;
        this.date=date;
        this.id=id;
    }
}
