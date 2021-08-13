import java.io.File;

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

public class PaperDemo
{
    public static void main(String[] args)
    {
        if (args.length != 3)
        {
            System.out.println("Usage:\nPaperDemo <imagedir> <outputdir> <percentage>");
            System.out.println("Example:\nPaperDemo ./birds ./birds_10 0.10");
            return;
        }

        File inputdir  = new File( args[0] );
        File outputdir = new File( args[1] );
        Float p       = Float.parseFloat( args[2] );
        if( p < 0.0f  || p > 1.0f) throw new IllegalArgumentException("Invalid p " + p);


        File[] inputfiles = inputdir.listFiles();
        for( File inputfile : inputfiles )
        {
            String filename = inputfile.getName();
            if( !inputfile.isFile() || !filename.endsWith(".jpg") ) continue;

            Picture inputImg = new Picture(inputfile);

            int removeCols = Math.round( p * inputImg.width()  );
            int removeRows = Math.round( p * inputImg.height() );

            //--------------------------------------------------------------------//
            // Seam Carving
            //--------------------------------------------------------------------//
            Picture seamImg = ReplaceDemo.process( inputImg, removeCols, removeRows );
            // Determine roughly the number of pixels that will be modified
            System.out.printf("new image size is %d columns by %d rows\n", seamImg.width(), seamImg.height());

            //--------------------------------------------------------------------//
            // NB: Save as PNG
            //--------------------------------------------------------------------//
            String outputname = filename.replace(".jpg","_seam.png");
            File outputFile = new File( outputdir, outputname );
            seamImg.save( outputFile );

            //--------------------------------------------------------------------//
            // Compare to random
            //--------------------------------------------------------------------//


            // Estimate number of pixels changed in seam doppelanger
            int seamPixels = (removeCols*inputImg.width()) + (removeRows*inputImg.height())
                         - ( removeCols + removeRows );


            // Debugging
            int imagepositions = inputImg.width() * inputImg.height();
            if( seamPixels > imagepositions )
            {
                System.out.println("There is a problem: " + filename);
                System.out.println("             width: " + inputImg.width());
                System.out.println("            height: " + inputImg.height());
                System.out.println("        seamPixels: " + seamPixels);
                System.out.println("    imagepositions: " + imagepositions);
            }
 

            int numPixels = seamPixels;
            System.out.printf("Orig Random pixels %d\n", numPixels );
            Picture randPicture = inputImg.replaceRandom( numPixels );
            
            outputname = filename.replace(".jpg","_rand.png");
            outputFile = new File( outputdir, outputname );
            
            randPicture.save( outputFile );
        }
    }

    
}
