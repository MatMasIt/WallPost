import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;

public class Window extends JFrame implements ActionListener {
    private JPanel auth, list, viewOne, mine;
    private JLabel labelUsername, labelPassword, postHeader;
    private JTextField username, titleField;
    private JPasswordField password;
    private JButton signIn, signUp, make, send, backList,backList2;
    private JScrollPane TablePane, textViewPane, textEditPane,postHeaderPane;
    private JTable table;
    private JTextArea view, write;
    private DefaultTableModel tb;
    private ServerConnection server;
    private PostList PL;
    private Post CP;
    public Window() throws IOException {
        initComponents();
        server= new ServerConnection(this);
        PL=null;
    }
    public void initComponents(){

        auth=new JPanel();
        labelUsername= new JLabel("Username");
        labelPassword=new JLabel("Password");
        username = new JTextField(15);
        password = new JPasswordField(15);
        signIn=new JButton("Sign In");
        signUp=new JButton("Sign Up");
        auth.setLayout(new GridLayout(3,2));


        auth.add(labelUsername);
        auth.add(username);

        auth.add(labelPassword);
        auth.add(password);

        auth.add(signIn);

        auth.add(signUp);


        list=new JPanel();
        Object[][] data = {};
        String[] columnNames = {"User","Title","Date"};
        tb=new DefaultTableModel(data,columnNames);
        table = new JTable(tb);
        TablePane= new JScrollPane(table);
        make=new JButton("Write");

        list.setLayout(new GridLayout(2,1));

        list.add(TablePane);
        list.add(make);

        send= new JButton("Send");

        this.setLayout(new FlowLayout());
        //this.add(list,new GridBagConstraints());
        viewOne= new JPanel();
        view= new JTextArea(15,50);
        backList2= new JButton("Back To list");
        textViewPane= new JScrollPane(view);
        postHeader=new JLabel("Title, details");
        postHeaderPane=new JScrollPane(postHeader, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        viewOne.setLayout(new GridLayout(3,1));

        viewOne.add(postHeaderPane);
        viewOne.add(textViewPane);
        viewOne.add(backList2);

        mine=new JPanel();
        titleField=new JTextField(15);
        titleField.setForeground(Color.GRAY);
        titleField.setText("Title");
        titleField.setForeground(Color.GRAY);
        titleField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (titleField.getText().equals("Title")) {
                    titleField.setText("");
                    titleField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (titleField.getText().isEmpty()) {
                    titleField.setForeground(Color.GRAY);
                    titleField.setText("Title");
                }
            }
        });
        write=new JTextArea(15,50);
        write.setForeground(Color.GRAY);
        write.setText("Body");
        write.setForeground(Color.GRAY);
        write.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (write.getText().equals("Body")) {
                    write.setText("");
                    write.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (write.getText().isEmpty()) {
                    write.setForeground(Color.GRAY);
                    write.setText("Body");
                }
            }
        });

        textEditPane= new JScrollPane(write);

        backList= new JButton("Back To list");
        mine.setLayout(new GridLayout(4,1));
        mine.add(titleField);
        mine.add(textEditPane);
        mine.add(send);
        mine.add(backList);


        signIn.addActionListener(this);
        signUp.addActionListener(this);
        make.addActionListener(this);
        backList.addActionListener(this);
        backList2.addActionListener(this);
        send.addActionListener(this);

        this.add(auth);
        this.add(list);
        this.add(viewOne);
        this.add(mine);
        list.setVisible(false);
        viewOne.setVisible(false);
        mine.setVisible(false);
        //this.add(auth,new GridBagConstraints());
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                // do some actions here, for example
                // print first column value from selected row
                    CP=server.fetchPostBodyIfEmpty(PL,table.getSelectedRow());
                if (CP!= null) {


                    postHeader.setText(CP.getTitle() + ", written by " + CP.getAuthor() + " at " + CP.getDate());
                    view.setText(CP.getBody());
                    list.setVisible(false);
                    viewOne.setVisible(true);
                }
            }
        });
    }
    public static void main(String args[]) throws IOException {
        Window window= new Window();
        window.setSize(600,1000);
        window.setTitle("WallPost");
        window.setVisible(true);
    }

    public void updateTable(PostList a) {
        tb.setRowCount(0);//Imposta il numero di righe a 0 eliminando tutte le righe presenti
        for (int i = 0; i < a.size(); i++) {
            Object[] row = {a.get(i).getAuthor(),a.get(i).getTitle(), a.get(i).getDate()};//Crea riga temporanea
            tb.addRow(row);//aggiungi riga
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(signIn)){
            if(server.signIn(username.getText(),new String(password.getPassword()))) {
                PL=server.fetchPostsFromServer();
                this.updateTable(PL);
                auth.setVisible(false);
                list.setVisible(true);
            }
            else{
                JOptionPane.showMessageDialog(this,
                        "Wrong credentials",
                        "Cannot sign in",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
        else if(e.getSource().equals(signUp)){
            if(server.signUp(username.getText(),new String(password.getPassword()))) {
                updt();
                auth.setVisible(false);
                list.setVisible(true);
            }
            else{
                JOptionPane.showMessageDialog(this,
                        "User Taken",
                        "Cannot sign up",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
        else if(e.getSource().equals(make)){
            list.setVisible(false);
            mine.setVisible(true);
        }
        else if(e.getSource().equals(backList)||e.getSource().equals(backList2)){
            updt();
            viewOne.setVisible(false);
            mine.setVisible(false);
            list.setVisible(true);
        }
        else if(e.getSource().equals(send)){
            server.publishPost(titleField.getText(),write.getText());
            updt();
            mine.setVisible(false);
            list.setVisible(true);
        }
    }

    public void updt(){
        PL=server.fetchPostsFromServer();
        this.updateTable(PL);
    }
}

