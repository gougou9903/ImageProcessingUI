/**
 * Created by Zhihao on 11/9/14.
 * Transfer RGB image to Gray.
 */
import java.io.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class RGB2Gray implements Runnable {
    private BufferedImage inputImage;
    public RGB2Gray(BufferedImage inputImage){
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

                int grayLevel = (r + g + b) / 3;
                int gray = (grayLevel << 16) + (grayLevel << 8) + grayLevel;
                this.inputImage.setRGB(x, y, gray);
            }
        }
    }
}
