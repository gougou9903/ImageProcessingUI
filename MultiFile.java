import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import java.awt.Graphics;



import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import java.awt.Graphics;

public class MultiFile {

    final static int f_ENLARGE = 0;
    final static int f_RGB2GRAY = 1;
    final int TNUM = 6;
    //private BufferedImage inputImage;


    public MultiFile(File[] array, int function, float ratio, int algorithm) throws IOException, InterruptedException {
        //File multiFile = new File(path);
        //File[] array = multiFile.listFiles();
        ArrayList<File> list = new ArrayList<File>(Arrays.asList(array));//在Arraylist中方便筛选图片文件
        ArrayList<File> pack1 = new ArrayList<File>(list.size());
        ArrayList<File> pack2 = new ArrayList<File>(list.size());
        ArrayList<File> pack3 = new ArrayList<File>(list.size());
        ArrayList<File> pack4 = new ArrayList<File>(list.size());
        ArrayList<File> pack5 = new ArrayList<File>(list.size());
        ArrayList<File> pack6 = new ArrayList<File>(list.size());
        ArrayList<ArrayList<File>> pack = new ArrayList<ArrayList<File>>(list.size());

        pack.add(0, pack1);
        pack.add(1, pack2);
        pack.add(2, pack3);
        pack.add(3, pack4);
        pack.add(4, pack5);
        pack.add(5, pack6);
        for(int i = 0; i < list.size(); i++) {

            boolean a = array[i].getPath().endsWith(".JPG");
            boolean b = array[i].getPath().endsWith(".jpg");

            if(!(a || b)) {
                list.remove(i);
            }

        }
//        BufferedImage[] inputImage = new BufferedImage[list.size()];
//        BufferedImage[] outputImage = new BufferedImage[list.size()];
        Thread[] thread = new Thread[TNUM];
        System.out.println("Number of Files: "+list.size());



//        File[] array2 = new File[list.size()];//将筛选好的list转换成array，array2 stores file lists.
//        list.toArray(array2);




        System.out.println("list size: " + list.size());
        /*------put files into packages-----*/
        if(list.size() <= TNUM){

            for(int i = 0; i < list.size(); i++){
                pack.get(i).add(list.get(i));

            }

            for(int i = 0; i < list.size(); i++){


                thread[i] = new Thread(new MultiFileThread(pack.get(i), function, ratio, algorithm));

            }


        }else{
            int pack_NUM = list.size() / TNUM;
            int rest = list.size();
            int current = 0;
        	while(rest>6){
        		pack.get(0).add(list.get(current));
        		pack.get(1).add(list.get(current+1));
        		pack.get(2).add(list.get(current+2));
        		pack.get(3).add(list.get(current+3));
        		pack.get(4).add(list.get(current+4));
        		pack.get(5).add(list.get(current+5));
        		current+=6;
        		rest-=6;
        	}
        	for(int j = 0; j<rest; j++){
        		pack.get(j).add(list.get(current));
        		current++;
        	}
//            for(int i = 0; i < pack_NUM ; i++){
//                pack.get(0).add(list.get(i));
//            }
//
//            for(int i = pack_NUM; i < pack_NUM*2 ; i++){
//                pack.get(1).add(list.get(i));
//            }
//
//            for(int i = pack_NUM*2; i < pack_NUM*3 ; i++){
//                pack.get(2).add(list.get(i));
//            }
//
//            for(int i = pack_NUM*3; i < pack_NUM*4 ; i++){
//                pack.get(3).add(list.get(i));
//            }
//
//            for(int i = pack_NUM*4; i < pack_NUM*5 ; i++){
//                pack.get(4).add(list.get(i));
//            }
//
//            for(int i = pack_NUM*5; i < list.size() ; i++){
//                pack.get(5).add(list.get(i));
//            }

            for(int i =0; i< this.TNUM; i++){
//        		System.out.println(pack.get(i).size());
                thread[i] = new Thread(new MultiFileThread(pack.get(i), function, ratio, algorithm));
            }
        }
        System.out.println("thread length: " + thread.length);

        for(int i =0; i < TNUM; i ++){
            thread[i].start();
        }


//        for(int i = 0; i < array2.length; i++){   // read file and start threads
//            inputImage[i] = ImageIO.read(new File (array2[i].getPath()));
//            int width = inputImage[i].getWidth();
//            int height = inputImage[i].getHeight();
//
//            switch(function){
//                case f_ENLARGE:
//                    outputImage[i] = new BufferedImage((int)(width*ratio), (int)(height*ratio), BufferedImage.TYPE_INT_RGB);
//                    thread[i] = new Thread(new Enlarge(inputImage[i],outputImage[i],ratio, algorithm));
//                    break;
//                case f_RGB2GRAY:
//                    //thread[i] = new Thread(new RGB2Gray(inputImage[i]));
//                	thread[i] = new Thread(new MultiFileThread());
//                    break;
//            }
//            thread[i].start();
//        }
        System.out.println("00");
        for(int t =0; t < TNUM; t ++){    // wait for threads to end
            thread[t].join();
        }

//        /*---------------output images--------------*/
//        for(int t =0; t < array2.length; t ++){
//
//            String newName = "-" + array2[t].getName();
//            String newPath = array2[t].getParent();
//            switch(function){
//                case f_ENLARGE:
//                    boolean a = ImageIO.write(outputImage[t], "jpeg", new File(newPath, newName));
//                    if(a == true)
//                        System.out.println("Successfully enlarging " + array2[t].getPath());
//                    break;
//                case f_RGB2GRAY:
//                    boolean b = ImageIO.write(inputImage[t], "jpeg", new File(newPath, newName));
//                    if(b == true)
//                        System.out.println("Successfully graying " + array2[t].getPath());
//                    break;
//            }
//
//
//        }
        return;
    }


}










//
//public class MultiFile {
//
//    final static int f_ENLARGE = 0;
//    final static int f_RGB2GRAY = 1;
//    final static int f_ENHANCE = 3;
//    //private BufferedImage inputImage;
//
//
//    public MultiFile(File[] array, int function, float ratio, int algorithm) throws IOException {
//        //File multiFile = new File(path);
//        //File[] array = multiFile.listFiles();
//        ArrayList<File> list = new ArrayList<File>(Arrays.asList(array));//在Arraylist中方便筛选图片文件
//        BufferedImage[] inputImage = new BufferedImage[list.size()];
//        BufferedImage[] outputImage = new BufferedImage[list.size()];
//        Thread[] thread = new Thread[list.size()];
//        System.out.println("Number of Files: "+list.size());
//
//        for(int i = 0; i < list.size(); i++) {
//
//            boolean a = array[i].getPath().endsWith(".JPG");
//            boolean b = array[i].getPath().endsWith(".jpg");
//
//            if(!(a || b)) {
//                list.remove(i);
//            }
//
//        }
//
//        File[] array2 = new File[list.size()];//将筛选好的list转换成array，array2 stores file lists.
//        list.toArray(array2);
//
//        for(int i = 0; i < array2.length; i++){   // read file and start threads
//            inputImage[i] = ImageIO.read(new File (array2[i].getPath()));
//            int width = inputImage[i].getWidth();
//            int height = inputImage[i].getHeight();
//
//            switch(function){
//                case f_ENLARGE:
//                    outputImage[i] = new BufferedImage((int)((width-1)*ratio), (int)((height-1)*ratio), BufferedImage.TYPE_INT_RGB);
//                    thread[i] = new Thread(new Enlarge(inputImage[i],outputImage[i],ratio, algorithm));
//                    break;
//                case f_RGB2GRAY:
//                    thread[i] = new Thread(new RGB2Gray(inputImage[i]));
//                    break;
//                case f_ENHANCE:
//                    thread[i] = new Thread(new Enhance(inputImage[i]));
//                    break;
//            }
//            thread[i].start();
//        }
//
//        for(int t =0; t < array2.length; t ++){    // wait for threads to end
//            try {
//                thread[t].join();
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//
//        }
//
//        /*---------------output images--------------*/
//        for(int t =0; t < array2.length; t ++){
//
//            String newName = "-" + array2[t].getName();
//            String newPath = array2[t].getParent();
//            switch(function){
//                case f_ENLARGE:
//                    boolean a = ImageIO.write(outputImage[t], "jpeg", new File(newPath, newName));
//                    if(a == true)
//                        System.out.println("Successfully enlarging " + array2[t].getPath());
//                    break;
//                case f_ENHANCE: //fall through
//                case f_RGB2GRAY:
//                    boolean b = ImageIO.write(inputImage[t], "jpeg", new File(newPath, newName));
//                    if(b == true)
//                        System.out.println("Successfully graying " + array2[t].getPath());
//                    break;
//            }
//
//
//        }
//        return;
//    }
//
//
//}