/**
 * Created by Zhihao on 11/9/14.
 */
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;


class UI extends JFrame implements ActionListener, ItemListener{
    final int THREADNUM = 6;
    final static int f_ENLARGE = 0;
    final static int f_RGB2GRAY = 1;
	final static int f_PANORAMA = 2;
	final static int f_ENHANCE = 3;
    final static int a_NearestN = 0;
    final static int a_Bilinear = 1;
	final static int a_Bicubic = 2;
    
    String method = "Enlarge";
    String algorithm = "NearestN";
    String newFileName;

    JButton chooseFileButton = new JButton("Choose File");
	JButton saveAsButton = new JButton("Save As");
	JButton processButton = new JButton("process");
	JButton closeButton = new JButton("Close");
	
	JLabel chosenFileLabel = new JLabel("Chosen File: ");
	JLabel chosenFileLabelC = new JLabel();
	JLabel saveAsLabel = new JLabel("Save As: ");
	JLabel saveAsLabelC = new JLabel();
	JLabel pMethodLabel = new JLabel("Processing Method: ");
	JLabel ratioLabel = new JLabel("Ratio: ");
	JLabel algorithmLabel = new JLabel("Algorithm: ");
	
	JComboBox PM_comboBox = new JComboBox();
	JComboBox Alm_comboBox = new JComboBox();
	
	JTextField ratio = new JTextField(10);
	File[] sf;
    public UI() {
    	super("File Chooser");
    	setBounds(500,200,400,400);
    	setDefaultCloseOperation(EXIT_ON_CLOSE);
    	setLayout(null);
    	
    	chooseFileButton.setBounds(20, 340, 75,20);   	
    	saveAsButton.setBounds(115,340,75,20);
    	processButton.setBounds(210,340,75,20);
    	closeButton.setBounds(305,340,75,20);
    	
    	saveAsLabel.setBounds(20,250,100,20);
    	saveAsLabel.setFont(new Font("Serif", Font.PLAIN, 12));
    	saveAsLabelC.setBounds(140,250,200,50);
    	saveAsLabelC.setFont(new Font("Serif", Font.PLAIN, 12));
    	chosenFileLabel.setBounds(10,100,100,20);
    	chosenFileLabel.setFont(new Font("Serif", Font.PLAIN, 12));
    	chosenFileLabelC.setBounds(140,100,200,50);
    	chosenFileLabelC.setFont(new Font("Serif", Font.PLAIN, 12));
    	pMethodLabel.setBounds(10, 20, 100, 20);
    	pMethodLabel.setFont(new Font("Serif", Font.PLAIN, 12));
    	ratioLabel.setBounds(220,20,50,20);
    	ratioLabel.setFont(new Font("Serif", Font.PLAIN, 12));
    	algorithmLabel.setBounds(220,40,70,20);
    	algorithmLabel.setFont(new Font("Serif", Font.PLAIN, 12));
    	ratio.setBounds(250,20,80,20);
    	ratio.setFont(new Font("Serif", Font.PLAIN, 12));
    	
    	PM_comboBox.setBounds(110, 20, 100, 20);
    	PM_comboBox.setFont(new Font("Serif", Font.PLAIN, 12));
    	PM_comboBox.addItem("Enlarge");
    	PM_comboBox.addItem("Gray");
		PM_comboBox.addItem("Panorama");
		PM_comboBox.addItem("Enhance");
    	Alm_comboBox.setBounds(290,40,80,20);
    	Alm_comboBox.setFont(new Font("Serif", Font.PLAIN, 10));
    	Alm_comboBox.addItem("NearestN");
    	Alm_comboBox.addItem("Bilinear");
		Alm_comboBox.addItem("Bicubic");

		add(chooseFileButton);
//    	add(saveAsButton);
    	add(processButton);
    	add(closeButton);
    	add(chosenFileLabel);
    	add(chosenFileLabelC);
    	add(saveAsLabel);
    	add(saveAsLabelC);
    	add(pMethodLabel);
    	add(PM_comboBox);
    	add(Alm_comboBox);
    	add(ratioLabel);
    	add(algorithmLabel);
    	add(ratio);
    	ratio.setText("1");
    	chooseFileButton.addActionListener(this);
    	saveAsButton.addActionListener(this);
    	closeButton.addActionListener(this);
    	processButton.addActionListener(this);
    	ratio.addActionListener(this);
    	PM_comboBox.addItemListener(this);
    	Alm_comboBox.addItemListener(this);
    	
    	validate();
    }

    public void actionPerformed(ActionEvent e)
    {
    	if(e.getSource() == chooseFileButton)
    	{
    		JFileChooser chooser = new JFileChooser();
    		chooser.setMultiSelectionEnabled(true);
    		int option = chooser.showOpenDialog(this);
    		 if (option == JFileChooser.APPROVE_OPTION) {
    			 sf = chooser.getSelectedFiles();
    			 String filelist = "nothing";
                 if (sf.length > 0) filelist = sf[0].getName();
                 for (int i = 1; i < sf.length; i++) {
                     filelist += ", " + sf[i].getName();
                 }
                 System.out.println("length: " + sf.length);
                 chosenFileLabelC.setText(filelist);
    		 }else chosenFileLabelC.setText("Nothing");
    		 
    	}else if(e.getSource() == saveAsButton)
    	{
    		
    		
    	}else if(e.getSource() == closeButton)
    	{
    		System.exit(1);
    	}else if(e.getSource() == ratio)
    	{
    		System.out.println(ratio.getText());
    	}else if(e.getSource() == processButton)
    	{
    		int method_int = this.checkFunction(method);
    		int algorithm_int = this.checkAlgorithm(algorithm);
    		String ratio_string = ratio.getText();
    		float ratio_int = Float.parseFloat(ratio_string);
    		System.out.println("Process!");
    		if(sf.length == 1){
    			//SaveAsWindow dialog = new SaveAsWindow(newFileName);
    			String newName = JOptionPane.showInputDialog(this, "Save as", sf[0].getPath());// specify a new name for one single image
    			
	    		try{
	    			if(method_int == f_RGB2GRAY){
	    				ratio_int = 1;
	    				SingleFile simple = new SingleFile(sf[0].getPath(), newName, method_int, this.THREADNUM, ratio_int, algorithm_int);
	    			}else if(method_int == f_ENLARGE){
	    				SingleFile simple = new SingleFile(sf[0].getPath(), newName, method_int, this.THREADNUM, ratio_int, algorithm_int);
	    			}else if(method_int == f_ENHANCE){
						SingleFile simple = new SingleFile(sf[0].getPath(), newName, method_int, this.THREADNUM, ratio_int, algorithm_int);
					}
	    			
	    			
	    		}catch(java.lang.Exception t)
	    		{
	    			t.printStackTrace();
	    		}
    		}else if(sf.length >= 2)
    		{
    			try{
	    			if(method_int == f_RGB2GRAY){
	    				ratio_int = 1;
	    				MultiFile sample = new MultiFile(sf,method_int,ratio_int,algorithm_int);
	    				
	    			}else if(method_int == f_ENLARGE){
	    				MultiFile sample = new MultiFile(sf,method_int,ratio_int,algorithm_int);
	    			}else if(method_int == f_PANORAMA){
						String newName = JOptionPane.showInputDialog(this, "Save as", sf[0].getPath());// specify a new name for one single image
						Panorama sample = new Panorama(sf, newName);
					}else if(method_int == f_ENHANCE){
						ratio_int = 1;
						MultiFile sample = new MultiFile(sf,method_int,ratio_int,algorithm_int);
					}
	    			
	    			
	    		}catch(java.lang.Exception t)
	    		{
	    			t.printStackTrace();
	    		}
    			
    		}
    	}
    }
    
    public void itemStateChanged(ItemEvent i)
    {
    	if(i.getSource() == PM_comboBox){
	    	int state = i.getStateChange();
	    	if(state == ItemEvent.SELECTED){
		    	ItemSelectable is = i.getItemSelectable();
		    	Object select[] = is.getSelectedObjects();
		    	this.method = (String)select[0];
		    	System.out.println(method);
	    	}
    	}else if(i.getSource() == Alm_comboBox){
    		int state = i.getStateChange();
    		if(state == ItemEvent.SELECTED){
    			ItemSelectable is = i.getItemSelectable();
    	    	Object select[] = is.getSelectedObjects();
    	    	this.algorithm = (String)select[0];
    	    	System.out.println(algorithm);
    		}
    	}
    }
    
    private int checkFunction(String function){
        if(function.equals("Enlarge")) return f_ENLARGE;
        else if (function.equals("Gray")) return f_RGB2GRAY;
		else if (function.equals("Panorama")) return f_PANORAMA;
		else if (function.equals("Enhance")) return f_ENHANCE;
        else return -1;
    }

    private int checkAlgorithm(String function){
        if(function.equals("NearestN")) return a_NearestN;
        else if (function.equals("Bilinear")) return a_Bilinear;
		else if (function.equals("Bicubic")) return a_Bicubic;
        else return -1;
    }

    public static void main ( String[] args ) throws IOException {
    	UI fileChooser = new UI();
    	fileChooser.setVisible(true);
    	
    }
}
