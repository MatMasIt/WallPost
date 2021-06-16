import javax.swing.*;

public class ServerErrorException extends Exception{
    public String message,errorType, title;
    public boolean mustQuit;
    public ServerErrorException(String title, String message, String errorType, boolean mustQuit){
        super("The server issued an error");
        this.message=message;
        this.title=title;
        this.errorType = errorType;
        this.mustQuit=mustQuit;
    }
}
