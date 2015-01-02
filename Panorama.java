import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Zhihao on 12/12/14.
 */
public class Panorama {

    public Panorama(File[] array, String outName) throws IOException {

        ArrayList<File> list = new ArrayList<File>(Arrays.asList(array));//在Arraylist中方便筛选图片文件

        for(int i = 0; i < list.size(); i++) {

            boolean a = array[i].getPath().endsWith(".JPG");
            boolean b = array[i].getPath().endsWith(".jpg");

            if(!(a || b)) {
                list.remove(i);
            }

        }
        System.out.println("Number of Files: "+list.size());
        int listSize = list.size();
        while(list.size()>=2){
            int size = list.size()/2;
            System.out.println("new loop:"+list.size()+"1/2"+size);
            Thread[] thread = new Thread[size];
            for(int i = 0; i<size; i++){
                System.out.println("thread"+i);
                String tempname = "temp"+i;
                thread[i] = new Thread(new ImageStitching(array[2*i].getPath(),array[2*i+1].getPath(),tempname));
                thread[i].start();
            }

            for(int i = 0; i<size; i++) {
                System.out.println("join"+i);
                try {
                    thread[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(list.size() == 2) break;

            for(int i = 0; i<size; i++){
                System.out.println("after"+i);
                list.remove(i+1);
                list.remove(i);
                File temp = new File("temp"+i);
                list.add(i, temp);
            }
            list.toArray(array);
        }
        for(int i = 1; i<listSize/2; i++){
            File temp = new File("temp"+i);
            temp.delete();
        }
        File temp = new File("temp0");
        File temp2 = new File(outName);
        temp.renameTo(temp2);



        return;
    }


}