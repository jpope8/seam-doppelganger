/*************************************************************************
 *  Compilation:  javac Blur.java
 *  Execution:    java Blur input.png 5 1.0
 *  Dependencies: Picture.java Matrix.java
 *
 *  Reads image and applies Gaussian blur, saves blurred image to file.
 *
 *************************************************************************/

public class Blur
{
    public static void main(String[] args)
    {
        if (args.length != 3)
        {
            System.out.println("Usage:\njava Blur <image filename> <kernel size> <stdev>");
            return;
        }

        Picture origPicture = new Picture(        args[0] );
        int n               = Integer.parseInt(   args[1] );
        double s            = Double.parseDouble( args[2] );
        Matrix kernel = KernelFactory.gaussianBlur(n,s);


        System.out.printf("Original image is %d columns by %d rows\n", origPicture.width(), origPicture.height());
        
        //--------------------------------------------------------------------//
        // Seam Carving
        //--------------------------------------------------------------------//
        Picture blurredPicture = origPicture.applyFilter( kernel );

        //origPicture.show();
        //blurredPicture.show();

        //--------------------------------------------------------------------//
        // Save blurred picture with a different name
        //--------------------------------------------------------------------//
        String filename = args[0];
        if( filename.endsWith(".png") ) filename = filename.replace(".png","_blur.png");
        else                            filename = filename.replace(".jpg","_blur.jpg");
        blurredPicture.save( filename );
    }
}
