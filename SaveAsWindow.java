import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

public class SaveAsWindow extends JFrame implements ActionListener{
	JLabel Save_Label = new JLabel("Save as: ");
	JTextField text = new JTextField(20);
	JButton OK_Button = new JButton("OK");
	JButton Close_Button = new JButton("Close");
	String name;
	public SaveAsWindow(String newName){
		super("Save As");
		setBounds(400,200,200,200);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(null);
		setVisible(true);
		name = newName;
		
		Save_Label.setBounds(20,30,50,20);
		Save_Label.setFont(new Font("Serif", Font.PLAIN, 14));
		
		text.setBounds(100,30,80,20);
		text.setFont(new Font("Serif", Font.PLAIN, 14));
		
		OK_Button.setBounds(30,150,50,20);
		Close_Button.setBounds(120,150,50,20);
		OK_Button.setFont(new Font("Serif", Font.PLAIN, 14));
		Close_Button.setFont(new Font("Serif", Font.PLAIN, 14));
		
		add(Save_Label);
		add(text);
		add(OK_Button);
		add(Close_Button);
		
		OK_Button.addActionListener(this);
		Close_Button.addActionListener(this);
	}
	
	
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == OK_Button ){
			name = text.getText();
			if(name.length()>0) System.exit(1);
			else JOptionPane.showMessageDialog(this,"Please specify a new name");
		}else if(e.getSource() == Close_Button){
			System.exit(1);
			
		}
		
	}
}