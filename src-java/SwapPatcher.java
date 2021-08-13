import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.TreeMap;
import java.io.IOException;
import java.awt.Color;

/**
 * Patches seams created by a seam carver.
 * @author James Pope
 */
public class SwapPatcher
{
    public static void main(String[] args) throws IOException
    {
        if (args.length != 2)
        {
            System.out.println("Usage:\njava SwapPatcher <orig image> <swap image>");
            return;
        }

        Picture seam = new Picture( args[0] );
        String swapFilename =       args[1];


        // Resize swap to be same size as original
        BufferedImage swapOrigImage = ImageIO.read( new File(swapFilename) );
        swapOrigImage = ImageTest.resizeImageWithHint(seam.width(), seam.height(), swapOrigImage, BufferedImage.TYPE_INT_ARGB);

        Picture swap = new Picture(swapOrigImage);


        System.out.printf("Original image is %d columns by %d rows\n", seam.width(), seam.height());
        
        //--------------------------------------------------------------------//
        // Undo Seam Doppelganger
        //--------------------------------------------------------------------//
        // Make copy - per instructions not to mutate
        int wi = seam.width();
        int hi = seam.height();
        Picture unseam = new Picture(wi, hi);
        for (int y = 0; y < hi; y++)
        {
            for (int x = 0; x < wi; x++)
            {
                Color cSeam = seam.get(x,y);
                Color cSwap = swap.get(x,y);
                // The pixel is from the swap, so need to replace
                if( cSeam.equals(cSwap) )
                {
                    // Figure out should we do horizontal or vertical?
                    Color n = (y==0)    ? null : seam.get( x, y-1 );
                    Color s = (y>=hi-1) ? null : seam.get( x, y+1 );
                    Color w = (x==0)    ? null : seam.get( x-1, y );
                    Color e = (x>=wi-1) ? null : seam.get( x+1, y );
                    double nsDiff = diff(n,s);
                    double ewDiff = diff(e,w);
                    if( ewDiff < nsDiff )
                    {
                        unseam.set( x, y, avg(e, w) );
                    }
                    else
                    {
                        unseam.set( x, y, avg(n, s) );
                    }
                }
                else
                {
                    unseam.set( x, y, cSeam );
                }
            }
        }
 
        seam.show();
        unseam.show();
    }


    public static Color avg( Color c1, Color c2 )
    {
        int r1 = 0;
        int g1 = 0;
        int b1 = 0;
        int r2 = 0;
        int g2 = 0;
        int b2 = 0;
        
        if( c1 != null )
        {
            r1 = c1.getRed();
            g1 = c1.getGreen();
            b1 = c1.getBlue();
        }

        if( c2 != null )
        {
            r2 = c2.getRed();
            g2 = c2.getGreen();
            b2 = c2.getBlue();
        }

        int r = (r1 + r2) / 2;
        int g = (g1 + g2) / 2;
        int b = (b1 + b2) / 2;
        return new Color(r,g,b);
    }

    public static double diff( Color c1, Color c2 )
    {
        if(      c1 == null ) return Luminance.intensity(c2);
        else if( c2 == null ) return Luminance.intensity(c1);
        else  return Math.abs( Luminance.intensity(c1) - Luminance.intensity(c2) );
    }


}
