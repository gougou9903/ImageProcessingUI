import java.awt.image.BufferedImage;

/**
 * Created by Zhihao on 12/13/14.
 */
public class Enhance implements Runnable{
    private BufferedImage inputImage;
    public Enhance(BufferedImage inputImage){
        this.inputImage = inputImage;
    }
    public void run(){

        final int width = this.inputImage.getWidth();
        final int height = this.inputImage.getHeight();
        for( int y = 0 ; y < height; y++) {
            for( int x = 0 ; x < width ; x++) {
                int rgb = this.inputImage.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = (rgb & 0xFF);
//                System.out.println("here"+r+","+g+","+b);
                if(r<100){
                    r=(int)(r*0.8);
                }else if(r>155){
                    r=255-(int)((255-r)*0.8);
                }else{
                    r=(int)((1.727*r)-92.7);
                }
                if(g<100){
                    g=(int)(g*0.8);
                }else if(g>155){
                    g=255-(int)((255-g)*0.8);
                }else{
                    g=(int)((1.727*g)-92.7);
                }
                if(b<100){
                    b=(int)(b*0.8);
                }else if(b>155){
                    b=255-(int)((255-b)*0.8);
                }else{
                    b=(int)((1.727*b)-92.7);
                }

//                System.out.println("here"+r+","+g+","+b);
                int RGB = (r << 16) + (g << 8) + b;
                this.inputImage.setRGB(x, y, RGB);
            }

        }
    }
}
