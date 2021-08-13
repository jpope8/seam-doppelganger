import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.TreeMap;
import java.io.IOException;

/*************************************************************************
 *  Compilation:  javac ReplaceDemo.java
 *  Execution:    java ReplaceDemo input.png columnsToReplace rowsToReplace
 *  Dependencies: SeamDoppelganger.java SCUtility.java Picture.java
 *                Stopwatch.java StdDraw.java
 *
 *  Read image from file specified as command line argument. Use SeamDoppelganger
 *  to replace number of rows and columns specified as command line arguments.
 *  Show the images in StdDraw and print time elapsed to screen.
 *
 *************************************************************************/

public class SwapDemo
{
    public static void main(String[] args) throws IOException
    {
        if (args.length != 4)
        {
            System.out.println("Usage:\njava ReplaceDemo <orig image> <swap image> <num columns to replace> <num rows to replace>");
            return;
        }

        Picture origImg = new Picture(      args[0]);
        String swapFilename =               args[1];

        int removeColumns = Integer.parseInt(args[2]);
        int removeRows    = Integer.parseInt(args[3]);

        // Resize swap to be same size as original
        BufferedImage swapOrigImage = ImageIO.read( new File(swapFilename) );
        swapOrigImage = ImageTest.resizeImageWithHint(origImg.width(), origImg.height(), swapOrigImage, BufferedImage.TYPE_INT_ARGB);

        Picture swapImg = new Picture(swapOrigImage);

        System.out.printf("image is %d columns by %d rows\n", origImg.width(), origImg.height());

        
        
        
        //--------------------------------------------------------------------//
        // Seam Carving
        //--------------------------------------------------------------------//
        Picture seamPicture = processRecursive( origImg, swapImg, removeColumns, removeRows );
        // Determine roughly the number of pixels that will be modified
        System.out.printf("new image size is %d columns by %d rows\n", seamPicture.width(), seamPicture.height());

        String filename = args[0];
        if( filename.endsWith(".png") ) filename = filename.replace(".png","_seam.png");
        else                            filename = filename.replace(".jpg","_seam.png"); // NB: Save as PNG
        seamPicture.save( filename );

        int seamPixels = (removeColumns*seamPicture.width()) + (removeRows*seamPicture.height())
                         - ( removeColumns + removeRows );
        System.out.printf("Seam Random pixels %d\n", seamPixels );

        //inputImg.show();
        //seamPicture.show();

        //--------------------------------------------------------------------//
        // Compare to random
        //--------------------------------------------------------------------//
        //int numPixels = (int)( 0.10 * inputImg.height() * inputImg.width() );
        //int numPixels = seamPixels;
        //System.out.printf("Orig Random pixels %d\n", numPixels );
        //Picture randPicture = origImg.replaceRandom( numPixels );
        //filename = args[0];
        //if( filename.endsWith(".png") ) filename = filename.replace(".png","_rand.png");
        //else                            filename = filename.replace(".jpg","_rand.png"); // NB: PNG
        //randPicture.save( filename );
    }

    public static Picture process( Picture origImg, Picture swapImg, int removeCols, int removeRows )
    {
        SeamDoppelganger sc = new SeamDoppelganger(origImg);
        
        TreeMap<Integer,int[]> hseams = sc.findSmallestHorizontalSeams();
        int hcount = 0;
        while( hcount < removeRows && !hseams.isEmpty() )
        {
            int[] horizontalSeam = hseams.remove( hseams.firstKey() );
            sc.swapHorizontalSeamRandom(horizontalSeam, swapImg);
            hcount++;
        }

        TreeMap<Integer,int[]> vseams = sc.findSmallestVerticalSeams();
        int vcount = 0 ;
        while( vcount < removeCols && !vseams.isEmpty() )
        {
            int[] verticalSeam = sc.findSmallestVerticalSeam();
            sc.swapVerticalSeamRandom(verticalSeam, swapImg);
            vcount++;
        }
        
        return sc.picture();
    }


    public static Picture processRecursive( Picture origImg, Picture swapImg, int removeCols, int removeRows )
    {
        SeamDoppelganger sc = new SeamDoppelganger(origImg);
        
        for (int i = 0; i < removeRows; i++)
        {
            int[] horizontalSeam = sc.findSmallestHorizontalSeam();
            sc.swapHorizontalSeamRandom(horizontalSeam, swapImg);
        }

        for (int i = 0; i < removeCols; i++)
        {
            int[] verticalSeam = sc.findSmallestVerticalSeam();
            sc.swapVerticalSeamRandom(verticalSeam, swapImg);
        }
        
        return sc.picture();
    }


}
