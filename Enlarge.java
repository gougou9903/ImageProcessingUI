import java.awt.image.BufferedImage;
import java.awt.Color;
/**
 * Created by Zhihao on 11/12/14.
 */
public class Enlarge implements Runnable{
    final static int a_NearestN = 0;
    final static int a_Bilinear = 1;
    final static int a_Bicubic = 2;

    private BufferedImage inputImage;
    private BufferedImage outputImage;
    private float ratio;
    private int inWidth;
    private int inHeight;
    private int outWidth;
    private int outHeight;
    private int algorithm;

    public Enlarge(BufferedImage inputImage, BufferedImage outputImage, float ratio, int algorithm){
        this.inputImage = inputImage;
        this.outputImage = outputImage;
        this.ratio = ratio;
        this.algorithm = algorithm;
    }
    public void run(){
        this.inWidth = this.inputImage.getWidth();
        this.inHeight = this.inputImage.getHeight();
        this.outWidth = this.outputImage.getWidth();
        this.outHeight = this.outputImage.getHeight();
//        System.out.println("inWidth" + this.inWidth);
//        System.out.println("inHeight" + this.inHeight);
//        System.out.println("outWidth" + this.outWidth);
//        System.out.println("outHeight" + this.outHeight);
        switch(this.algorithm){
            case a_NearestN:
                this.NearestN();
                break;
            case a_Bilinear:
                this.Bilinear();
                break;
            case a_Bicubic:
                bicubic temp = new bicubic();
                temp.filter(this.inputImage, this.outputImage);
        }
    }

    private void NearestN(){
        for( int y = 0 ; y < this.outHeight; y++) {
            for( int x = 0 ; x < this.outWidth ; x++) {
                int rgb = this.inputImage.getRGB((int)(x/this.ratio), (int)(y/this.ratio));
                this.outputImage.setRGB(x, y, rgb);
            }
        }
    }

    private void Bilinear(){
        for( int y = 0 ; y < this.outHeight-1; y++) {
            for( int x = 0 ; x < this.outWidth-1; x++) {
                float oriX = x/this.ratio;
                float oriY = y/this.ratio;
                int intOriX = (int) oriX;
                int intOriY = (int) oriY;
                float restOriX = oriX - intOriX;
//                System.out.println("restOriX = " + restOriX);
                float restOriY = oriY - intOriY;
//                System.out.println("restOriY = " + restOriY);
                Color rgbLU = new Color(this.inputImage.getRGB(intOriX, intOriY));
                int rLU = rgbLU.getRed();
                int gLU = rgbLU.getGreen();
                int bLU = rgbLU.getBlue();
                Color rgbLD = new Color(this.inputImage.getRGB(intOriX, intOriY+1));
                int rLD = rgbLD.getRed();
                int gLD = rgbLD.getGreen();
                int bLD = rgbLD.getBlue();
                Color rgbRU = new Color(this.inputImage.getRGB(intOriX+1, intOriY));
                int rRU = rgbRU.getRed();
                int gRU = rgbRU.getGreen();
                int bRU = rgbRU.getBlue();
                Color rgbRD = new Color(this.inputImage.getRGB(intOriX+1, intOriY+1));
                int rRD = rgbRD.getRed();
                int gRD = rgbRD.getGreen();
                int bRD = rgbRD.getBlue();
                int rU = (int)((rRU-rLU)*restOriX) +rLU;
                int rD = (int)((rRD-rLD)*restOriX) +rLD;
                int gU = (int)((gRU-gLU)*restOriX) +gLU;
                int gD = (int)((gRD-gLD)*restOriX) +gLD;
                int bU = (int)((bRU-bLU)*restOriX) +bLU;
                int bD = (int)((bRD-bLD)*restOriX) +bLD;
                int r = (int)((rD-rU)*restOriY) +rU;
//                System.out.println("rLU = " + rLU);
//                System.out.println("rRU = " + rRU);
//                System.out.println("rLD = " + rLD);
//                System.out.println("rRD = " + rRD);
//                System.out.println("rU = " + rU);
//                System.out.println("rD = " + rD);
//                System.out.println("r = " + r);
                int g = (int)((gD-gU)*restOriY) +gU;
//                System.out.println("g = " + g);
                int b = (int)((bD-bU)*restOriY) +bU;
//                System.out.println("b = " + b);
                Color rgb = new Color (r, g, b);
                this.outputImage.setRGB(x, y, rgb.getRGB());
            }
        }
        for( int y = 0 ; y < this.outHeight-1; y++) {
            float oriY = y/this.ratio;
            int intOriY = (int) oriY;
            float restOriY = oriY - intOriY;
//            System.out.println("oriY = " + oriY);
//            System.out.println("intOriY = " + intOriY);
//            System.out.println("restOriY = " + restOriY);
//            System.out.println("this.inWidth-1 = " + (this.inWidth-1));
            Color rgbU = new Color(this.inputImage.getRGB(this.inWidth-1, intOriY));
//            System.out.println("pixel read successfully");
            int rU = rgbU.getRed();
            int gU = rgbU.getGreen();
            int bU = rgbU.getBlue();
            Color rgbD = new Color(this.inputImage.getRGB(this.inWidth-1, intOriY+1));
//            System.out.println("pixel read successfully");
            int rD = rgbD.getRed();
            int gD = rgbD.getGreen();
            int bD = rgbD.getBlue();
            int r = (int)((rD-rU)*restOriY) +rU;
            int g = (int)((gD-gU)*restOriY) +gU;
            int b = (int)((bD-bU)*restOriY) +bU;
            Color rgb = new Color (r, g, b);
            this.outputImage.setRGB(this.outWidth-1, y, rgb.getRGB());
            try{

            }catch(java.lang.Exception e) {
                e.printStackTrace();
            }
        }
        for( int x = 0 ; x < this.outWidth-1; x++) {
            try{
                float oriX = x/this.ratio;
                int intOriX = (int) oriX;
                float restOriX = oriX - intOriX;
                Color rgbL = new Color(this.inputImage.getRGB(intOriX, this.inHeight-1));
                int rL = rgbL.getRed();
                int gL = rgbL.getGreen();
                int bL = rgbL.getBlue();
                Color rgbR = new Color(this.inputImage.getRGB(intOriX+1, this.inHeight-1));
                int rR = rgbR.getRed();
                int gR = rgbR.getGreen();
                int bR = rgbR.getBlue();
                int r = (int)((rR-rL)*restOriX) +rL;
                int g = (int)((gR-gL)*restOriX) +gL;
                int b = (int)((bR-bL)*restOriX) +bL;
                Color rgb = new Color (r, g, b);
                this.outputImage.setRGB(x, this.outHeight-1, rgb.getRGB());
            }catch(java.lang.Exception e) {
            e.printStackTrace();
            }
        }
        this.outputImage.setRGB(this.outWidth-1, this.outHeight-1, this.inputImage.getRGB(this.inWidth-1, this.inHeight-1));
    }
}


