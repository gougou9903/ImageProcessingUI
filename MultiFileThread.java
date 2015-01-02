/**
 * Created by Zhihao on 12/13/14.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

public class MultiFileThread implements Runnable{
    final static int f_ENLARGE = 0;
    final static int f_RGB2GRAY = 1;
    final static int f_ENHANCE = 3;

//    private JProgressBar progressBar;
//    private JFrame frame;
    private int function;
    ArrayList<File> list;
    BufferedImage[] inputImage;
    BufferedImage[] outputImage;
    int[] width;
    int[] height;
    float ratio;
    int algorithm;

    public MultiFileThread(ArrayList<File> list, int function, float ratio, int algorithm) throws IOException, InterruptedException{
        this.list = list;
        this.function = function;
        this.ratio = ratio;
        this.algorithm = algorithm;

        inputImage = new BufferedImage[this.list.size()];
        outputImage = new BufferedImage[this.list.size()];
        width = new int[this.list.size()];
        height = new int[this.list.size()];

        for(int i = 0; i< this.list.size(); i ++){
            inputImage[i] = ImageIO.read(new File (list.get(i).getPath()));
            width[i] = inputImage[i].getWidth();
            height[i] = inputImage[i].getHeight();
        }
    }

//    private void GUI(){
//        frame = new JFrame("PRPCESS");
//        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//        frame.setBounds(0,0,100,100);
//        frame.setLayout(new BorderLayout());
//
////        JProgressBar progressBar;
//        progressBar = new JProgressBar(0,100);
//        progressBar.setStringPainted(true);
//        progressBar.setValue(0);
//        frame.add(progressBar, BorderLayout.NORTH);
//
//        frame.setVisible(true);
//        System.out.println("11");
//    }


    public void run(){
//        GUI();
        File[] array2 = new File[list.size()];//将筛选好的list转换成array，array2 stores file lists.
        list.toArray(array2);

        for(int i = 0; i< this.list.size(); i ++){
            switch(function){
                case f_ENLARGE:
                    outputImage[i] = new BufferedImage((int)((width[i]-1)*ratio), (int)((height[i]-1)*ratio), BufferedImage.TYPE_INT_RGB);
                    Thread enlarge = new Thread(new Enlarge(inputImage[i],outputImage[i],ratio, algorithm));
                    enlarge.start();;
                    try {
                        enlarge.join();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case f_RGB2GRAY:
                    Thread rgb = new Thread(new RGB2Gray(inputImage[i]));
                    rgb.start();
                    try {
                        rgb.join();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                case f_ENHANCE:
                    Thread enhance = new Thread(new Enhance(inputImage[i]));
                    enhance.start();
                    try {
                        enhance.join();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
            }
        }
        /*---------------output images--------------*/
        for(int t =0; t < array2.length; t ++){

            String newName = "-" + array2[t].getName();
            String newPath = array2[t].getParent();
            switch(function){
                case f_ENLARGE:
                    boolean a = false;
                    try {
                        a = ImageIO.write(outputImage[t], "jpeg", new File(newPath, newName));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(a == true)
                        System.out.println("Successfully enlarging " + array2[t].getPath());
                    break;
                case f_ENHANCE:  //fall through
                case f_RGB2GRAY:
                    boolean b = false;
                    try {
                        b = ImageIO.write(inputImage[t], "jpeg", new File(newPath, newName));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(b == true)
                        System.out.println("Successfully graying/enhanced " + array2[t].getPath());
                    break;
            }


        }

    }

}
