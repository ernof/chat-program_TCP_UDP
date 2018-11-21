package client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientLogin {
    private JPanel panel1;
    private JTextField textField1;
    private JButton loginButton;
    private JButton exitButton;
    private JLabel Label1;
    public static JFrame jframe;


    public ClientLogin() {
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(textField1.getText().isEmpty()){
                    textField1.requestFocus();
                    return;
                }
                String username = textField1.getText();
                ChatView chatView = new ChatView(username);

                System.out.println(chatView.getUsername());
                jframe.setVisible(false);
            }
        });
    }

    public static void StartLogin() {
        jframe = new JFrame("Login");
        jframe.setContentPane(new ClientLogin().panel1);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.pack();
        jframe.setVisible(true);
    }
}

