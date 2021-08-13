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

public class ReplaceDemo
{
    public static void main(String[] args)
    {
        if (args.length != 2)
        {
            System.out.println("Usage:\njava ReplaceDemo <image filename> <percent to replace>");
            return;
        }

        Picture inputImg = new Picture(   args[0]);
        Float p       = Float.parseFloat( args[1] );
        if( p < 0.0  || p > 1.0) throw new IllegalArgumentException("Invalid p " + p);

        //int n             = Integer.parseInt(args[3]);
        //double s       = Double.parseDouble( args[4] );
        //Matrix kernel = KernelFactory.gaussianBlur(n,s);

        int removeCols = Math.round( p * inputImg.width()  );
        int removeRows = Math.round( p * inputImg.height() );


        System.out.printf("Image is %d columns by %d rows\n", inputImg.width(), inputImg.height());
        System.out.printf("Removing %d columns\n", removeCols);
        System.out.printf("Removing %d rows\n", removeRows);
        
        //--------------------------------------------------------------------//
        // Seam Carving
        //--------------------------------------------------------------------//
        Picture seamPicture = process( inputImg, removeCols, removeRows );
        // Determine roughly the number of pixels that will be modified
        System.out.printf("new image size is %d columns by %d rows\n", seamPicture.width(), seamPicture.height());

        String filename = args[0];
        if( filename.endsWith(".png") ) filename = filename.replace(".png","_seam.png");
        else                            filename = filename.replace(".jpg","_seam.png"); // NB: Save as PNG
        seamPicture.save( filename );

        int seamPixels = (removeCols*seamPicture.width()) + (removeRows*seamPicture.height())
                         - ( removeCols + removeRows );
        System.out.printf("Seam Random pixels %d\n", seamPixels );

        //inputImg.show();
        //seamPicture.show();

        //--------------------------------------------------------------------//
        // Compare to random
        //--------------------------------------------------------------------//
        //int numPixels = (int)( 0.10 * inputImg.height() * inputImg.width() );
        int numPixels = seamPixels;
        System.out.printf("Orig Random pixels %d\n", numPixels );
        Picture randPicture = inputImg.replaceRandom( numPixels );
        filename = args[0];
        if( filename.endsWith(".png") ) filename = filename.replace(".png","_rand.png");
        else                            filename = filename.replace(".jpg","_rand.png"); // NB: PNG
        randPicture.save( filename );
    }

    public static Picture process( Picture inputImg, int removeCols, int removeRows )
    {
        SeamDoppelganger sc = new SeamDoppelganger(inputImg);

        /*
        for (int i = 0; i < removeRows; i++)
        {
            int[] horizontalSeam = sc.findSmallestHorizontalSeam();
            sc.removeHorizontalSeam(horizontalSeam);
        }

        for (int i = 0; i < removeColumns; i++)
        {
            int[] verticalSeam = sc.findSmallestVerticalSeam();
            sc.removeVerticalSeam(verticalSeam);
        }
        */

        
        for (int i = 0; i < removeRows; i++)
        {
            int[] horizontalSeam = sc.findSmallestHorizontalSeam();
            //int[] horizontalSeam = sc.findLargestHorizontalSeam();

            //sc.replaceHorizontalSeam(horizontalSeam, kernel);
            //sc.convolveHorizontalSeam(horizontalSeam, kernel);
            sc.replaceHorizontalSeamRandom(horizontalSeam);
            //sc.removeHorizontalSeam(horizontalSeam);
        }

        for (int i = 0; i < removeCols; i++)
        {
            int[] verticalSeam = sc.findSmallestVerticalSeam();
            //int[] verticalSeam = sc.findLargestVerticalSeam();

            //for ( int y = 0; y < 5; y++ ) System.out.printf("[%d]=%d\n", y, verticalSeam[y]); 

            //sc.replaceVerticalSeam(verticalSeam, kernel);
            //sc.convolveVerticalSeam(verticalSeam, kernel);
            sc.replaceVerticalSeamRandom(verticalSeam);
            //sc.removeVerticalSeam(verticalSeam);
        }
        
        return sc.picture();
    }
}
