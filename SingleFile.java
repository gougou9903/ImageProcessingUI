/**
 * Created by Zhihao on 11/8/14.
 * Split one large image file to multiple parts, and process concurrently.
 */

import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Graphics;

public class SingleFile {
    final static int f_ENLARGE = 0;
    final static int f_RGB2GRAY = 1;
    final static int f_ENHANCE = 3;
    private int partnum;
    private BufferedImage inputImage;
    private BufferedImage outputImage;
    private int width;
    private int height;



    public SingleFile(final String filenamein, final String filenameout, int function, int partnum, float ratio, int algorithm) throws IOException {
        this.partnum = partnum;
        this.inputImage = ImageIO.read(new File ( filenamein ) );
        this.width  = this.inputImage.getWidth();
        this.height = this.inputImage.getHeight();
        int subWidth = (int)((float)this.width/(float)this.partnum);

        BufferedImage[] subImage = new BufferedImage[this.partnum];
        BufferedImage[] subOutImage = new BufferedImage[this.partnum];

        Thread[] subThreads = new Thread[this.partnum];


        for(int i=0; i<this.partnum; i++) {
            if(i<this.partnum-1){
                subImage[i] = this.inputImage.getSubimage(i * subWidth, 0, subWidth, height);
            }
            else{
                subImage[i] = this.inputImage.getSubimage(i * subWidth, 0, this.width-i*subWidth, height);
            }
            switch(function){
                case f_ENLARGE:
                    int outHeight = (int) (this.height * ratio)-1;
                    subOutImage[i] = new BufferedImage((int)(subImage[i].getWidth() * ratio)-1, outHeight, BufferedImage.TYPE_INT_RGB);
                    subThreads[i] = new Thread(new Enlarge(subImage[i], subOutImage[i], ratio, algorithm));
                    break;
                case f_RGB2GRAY:
                    subThreads[i] = new Thread(new RGB2Gray(subImage[i]));
                    break;
                case f_ENHANCE:
                    subThreads[i] = new Thread(new Enhance(subImage[i]));
                    break;
            }

            subThreads[i].start();
        }

        for (int i = 0; i < this.partnum; i++) {
            try {
                subThreads[i].join();
            } catch (Exception ex) {
            }
        }


        int newWidth = 0;
        int newSubWidth = 0;

        if(function == f_RGB2GRAY||function == f_ENHANCE){
            newSubWidth = subImage[0].getWidth();
            newWidth = newSubWidth * (partnum-1) + subImage[partnum-1].getWidth();

            this.outputImage = new BufferedImage(newWidth, subImage[0].getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics g = this.outputImage.getGraphics();
            for (int i = 0; i < this.partnum; i++){
                g.drawImage(subImage[i], newSubWidth*i, 0, null);
            }
            g.dispose();
        }
        else if(function == f_ENLARGE){
            newSubWidth = subOutImage[0].getWidth();
            newWidth = newSubWidth * (partnum-1) + subOutImage[partnum-1].getWidth();

            this.outputImage = new BufferedImage(newWidth, subOutImage[0].getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics g = this.outputImage.getGraphics();
            for (int i = 0; i < this.partnum; i++){
                g.drawImage(subOutImage[i], newSubWidth*i, 0, null);
            }
            g.dispose();
        }


        ImageIO.write(this.outputImage, "jpeg", new File(filenameout));

        return;
    }

}


