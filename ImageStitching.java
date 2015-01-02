/**
 * Created by Zhihao on 12/12/14.
 */

import boofcv.abst.feature.associate.AssociateDescription;
import boofcv.abst.feature.associate.ScoreAssociation;
import boofcv.abst.feature.detdesc.DetectDescribePoint;
import boofcv.abst.feature.detect.interest.ConfigFastHessian;
import boofcv.alg.distort.ImageDistort;
import boofcv.alg.distort.PixelTransformHomography_F32;
import boofcv.alg.distort.impl.DistortSupport;
import boofcv.alg.feature.UtilFeature;
import boofcv.alg.interpolate.impl.ImplBilinearPixel_F32;
import boofcv.alg.sfm.robust.DistanceHomographySq;
import boofcv.alg.sfm.robust.GenerateHomographyLinear;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.factory.feature.associate.FactoryAssociation;
import boofcv.factory.feature.detdesc.FactoryDetectDescribe;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.feature.AssociatedIndex;
import boofcv.struct.feature.SurfFeature;
import boofcv.struct.feature.TupleDesc;
import boofcv.struct.geo.AssociatedPair;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSingleBand;
import boofcv.struct.image.MultiSpectral;
import georegression.fitting.homography.ModelManagerHomography2D_F64;
import georegression.struct.homography.Homography2D_F64;
import georegression.struct.point.Point2D_F64;
import georegression.struct.point.Point2D_I32;
import georegression.transform.homography.HomographyPointOps_F64;
import org.ddogleg.fitting.modelset.ModelManager;
import org.ddogleg.fitting.modelset.ModelMatcher;
import org.ddogleg.fitting.modelset.ransac.Ransac;
import org.ddogleg.struct.FastQueue;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageStitching implements Runnable{
    String filenamein1;
    String filenamein2;
    String filenameout;

    public ImageStitching(final String filenamein1, final String filenamein2, final String filenameout){
        this.filenamein1 = filenamein1;
        this.filenamein2 = filenamein2;
        this.filenameout = filenameout;
    }
    /**
     * Using abstracted code, find a transform which minimizes the difference between corresponding features
     * in both images.  This code is completely model independent and is the core algorithms.
     */
    public <T extends ImageSingleBand, FD extends TupleDesc> Homography2D_F64
    computeTransform( T imageA , T imageB ,
                      DetectDescribePoint<T,FD> detDesc ,
                      AssociateDescription<FD> associate ,
                      ModelMatcher<Homography2D_F64,AssociatedPair> modelMatcher )
    {
        // get the length of the description
        List<Point2D_F64> pointsA = new ArrayList<Point2D_F64>();
        FastQueue<FD> descA = UtilFeature.createQueue(detDesc,100);
        List<Point2D_F64> pointsB = new ArrayList<Point2D_F64>();
        FastQueue<FD> descB = UtilFeature.createQueue(detDesc,100);

        // extract feature locations and descriptions from each image
        describeImage(imageA, detDesc, pointsA, descA);
        describeImage(imageB, detDesc, pointsB, descB);

        // Associate features between the two images
        associate.setSource(descA);
        associate.setDestination(descB);
        associate.associate();

        // create a list of AssociatedPairs that tell the model matcher how a feature moved
        FastQueue<AssociatedIndex> matches = associate.getMatches();
        List<AssociatedPair> pairs = new ArrayList<AssociatedPair>();

        for( int i = 0; i < matches.size(); i++ ) {
            AssociatedIndex match = matches.get(i);

            Point2D_F64 a = pointsA.get(match.src);
            Point2D_F64 b = pointsB.get(match.dst);

            pairs.add( new AssociatedPair(a,b,false));
        }

        // find the best fit model to describe the change between these images
        if( !modelMatcher.process(pairs) )
            throw new RuntimeException("Model Matcher failed!");

        // return the found image transform
        return modelMatcher.getModelParameters().copy();
    }

    /**
     * Detects features inside the two images and computes descriptions at those points.
     */
    private <T extends ImageSingleBand, FD extends TupleDesc>
    void describeImage(T image,
                       DetectDescribePoint<T,FD> detDesc,
                       List<Point2D_F64> points,
                       FastQueue<FD> listDescs) {
        detDesc.detect(image);

        listDescs.reset();
        for( int i = 0; i < detDesc.getNumberOfFeatures(); i++ ) {
            points.add( detDesc.getLocation(i).copy() );
            listDescs.grow().setTo(detDesc.getDescription(i));
        }
    }

    /**
     * Given two input images create and display an image where the two have been overlayed on top of each other.
     */
    public <T extends ImageSingleBand>
    void stitch( BufferedImage imageA , BufferedImage imageB , Class<T> imageType )
    {
        T inputA = ConvertBufferedImage.convertFromSingle(imageA, null, imageType);
        T inputB = ConvertBufferedImage.convertFromSingle(imageB, null, imageType);

        // Detect using the standard SURF feature descriptor and describer
        DetectDescribePoint detDesc = FactoryDetectDescribe.surfStable(
                new ConfigFastHessian(1, 2, 200, 1, 9, 4, 4), null,null, imageType);
        ScoreAssociation<SurfFeature> scorer = FactoryAssociation.scoreEuclidean(SurfFeature.class,true);
        AssociateDescription<SurfFeature> associate = FactoryAssociation.greedy(scorer,2,true);

        // fit the images using a homography.  This works well for rotations and distant objects.
        ModelManager<Homography2D_F64> manager = new ModelManagerHomography2D_F64();
        GenerateHomographyLinear modelFitter = new GenerateHomographyLinear(true);
        DistanceHomographySq distance = new DistanceHomographySq();

        ModelMatcher<Homography2D_F64,AssociatedPair> modelMatcher =
                new Ransac<Homography2D_F64,AssociatedPair>(123,manager,modelFitter,distance,60,9);

        Homography2D_F64 H = computeTransform(inputA, inputB, detDesc, associate, modelMatcher);

        renderStitching(imageA,imageB,H);
    }

    /**
     * Renders and displays the stitched together images
     */
    public void renderStitching( BufferedImage imageA, BufferedImage imageB ,
                                        Homography2D_F64 fromAtoB )
    {
        // specify size of output image
        double scale = 0.5;
        int outputWidth = imageA.getWidth();
        int outputHeight = imageA.getHeight();

        // Convert into a BoofCV color format
        MultiSpectral<ImageFloat32> colorA =
                ConvertBufferedImage.convertFromMulti(imageA, null,true, ImageFloat32.class);
        MultiSpectral<ImageFloat32> colorB =
                ConvertBufferedImage.convertFromMulti(imageB, null,true, ImageFloat32.class);

        // Where the output images are rendered into
        MultiSpectral<ImageFloat32> work = new MultiSpectral<ImageFloat32>(ImageFloat32.class,outputWidth,outputHeight,3);

        // Adjust the transform so that the whole image can appear inside of it
        Homography2D_F64 fromAToWork = new Homography2D_F64(scale,0,colorA.width/4,0,scale,colorA.height/4,0,0,1);
        Homography2D_F64 fromWorkToA = fromAToWork.invert(null);

        // Used to render the results onto an image
        PixelTransformHomography_F32 model = new PixelTransformHomography_F32();
        ImageDistort<MultiSpectral<ImageFloat32>,MultiSpectral<ImageFloat32>> distort =
                DistortSupport.createDistortMS(ImageFloat32.class, model, new ImplBilinearPixel_F32(),false, null);

        // Render first image
        model.set(fromWorkToA);
        distort.apply(colorA,work);

        // Render second image
        Homography2D_F64 fromWorkToB = fromWorkToA.concat(fromAtoB,null);
        model.set(fromWorkToB);
        distort.apply(colorB,work);

        // Convert the rendered image into a BufferedImage
        BufferedImage output = new BufferedImage(work.width,work.height,imageA.getType());
        ConvertBufferedImage.convertTo(work,output,true);



        // draw lines around the distorted image to make it easier to see
		Homography2D_F64 fromBtoWork = fromWorkToB.invert(null);
		Point2D_I32 corners1[] = new Point2D_I32[4];
		corners1[0] = renderPoint(0,0,fromBtoWork);
		corners1[1] = renderPoint(colorB.width,0,fromBtoWork);
		corners1[2] = renderPoint(colorB.width,colorB.height,fromBtoWork);
		corners1[3] = renderPoint(0,colorB.height,fromBtoWork);



        Homography2D_F64 fromAtoWork = fromWorkToA.invert(null);
        Point2D_I32 corners2[] = new Point2D_I32[4];
        corners2[0] = renderPoint(0,0,fromAtoWork);
        corners2[1] = renderPoint(colorA.width,0,fromAtoWork);
        corners2[2] = renderPoint(colorA.width,colorA.height,fromAtoWork);
        corners2[3] = renderPoint(0,colorA.height,fromAtoWork);




        int up1 = Math.max(corners2[0].y, corners2[1].y);
        int up2 = Math.max(corners1[0].y, corners1[1].y);
        int up = Math.min(up1, up2);

        int down1 = Math.min(corners2[2].y, corners2[3].y);
        int down2 = Math.min(corners1[2].y, corners1[3].y);
        int down = Math.max(down1, down2);

        System.out.println(corners2[0].x+" "+corners2[3].x+" "+corners1[0].x+" "+corners1[3].x);
        int left1 = Math.max(corners2[0].x, corners2[3].x);
        int left2 = Math.max(corners1[0].x, corners1[3].x);
        int left = Math.min(left1, left2);

        int right1 = Math.min(corners2[1].x, corners2[2].x);
        int right2 = Math.min(corners1[1].x, corners2[1].x);
        int right = Math.max(right1, right2);

//        Graphics2D g2 = output.createGraphics();
//        g2.setColor(Color.ORANGE);
//        g2.setStroke(new BasicStroke(4));
//        g2.drawLine(left, up,right,up);
//        g2.drawLine(right,up,right,down);
//        g2.drawLine(right,down,left,down);
//        g2.drawLine(left,down,left,up);

        System.out.println("left"+left);
        System.out.println("right"+right);
        System.out.println("up"+up);
        System.out.println("down"+down);

        up = Math.max(0, up);
        left = Math.max(0,left);
        right = Math.min(output.getWidth(),right);
        down = Math.min(output.getHeight(),down);
        BufferedImage newout = output.getSubimage(left, up, right-left, down-up);

//		ShowImages.showWindow(output,"Stitched Images");
        try {
            ImageIO.write(newout, "jpg", new File(this.filenameout));
        } catch (Exception ex) {
        }

    }

    private Point2D_I32 renderPoint( int x0 , int y0 , Homography2D_F64 fromBtoWork )
    {
        Point2D_F64 result = new Point2D_F64();
        HomographyPointOps_F64.transform(fromBtoWork, new Point2D_F64(x0, y0), result);
        return new Point2D_I32((int)result.x,(int)result.y);
    }

    public void run() {
        BufferedImage imageA,imageB;
        imageA = UtilImageIO.loadImage(this.filenamein1);
        imageB = UtilImageIO.loadImage(this.filenamein2);
        stitch(imageA,imageB, ImageFloat32.class);

//		imageA = UtilImageIO.loadImage("../data/evaluation/stitch/mountain_rotate_01.jpg");
//		imageB = UtilImageIO.loadImage("../data/evaluation/stitch//mountain_rotate_03.jpg");
//		stitch(imageA,imageB, ImageFloat32.class);
//		imageA = UtilImageIO.loadImage("../data/evaluation/stitch/kayak_01.jpg");
//		imageB = UtilImageIO.loadImage("../data/evaluation/stitch/kayak_03.jpg");
//		stitch(imageA,imageB, ImageFloat32.class);
//		imageA = UtilImageIO.loadImage("../data/evaluation/scale/rainforest_01.jpg");
//		imageB = UtilImageIO.loadImage("../data/evaluation/scale/rainforest_02.jpg");
//		stitch(imageA,imageB, ImageFloat32.class);
    }

}