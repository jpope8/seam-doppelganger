
import java.awt.Color;

/**
 * Patches seams created by a seam carver.
 * @author James Pope
 */
public class SeamPatcher
{
    public static void main(String[] args)
    {
        if (args.length != 1)
        {
            System.out.println("Usage:\njava SeamPatcher <image filename>");
            return;
        }

        Picture A = new Picture( args[0] );

        System.out.printf("Original image is %d columns by %d rows\n", A.width(), A.height());
        
        //--------------------------------------------------------------------//
        // Undo Seam Doppelganger
        //--------------------------------------------------------------------//
        // Make copy - per instructions not to mutate
        Picture B = new Picture(A.width(), A.height());
        for (int y = A.height()-1; y >= 0 ; y--)
        {
            for (int x = A.width()-1; x >= 0 ; x--)
            {
                Color c = A.get(x,y);

                //------------------------------------------------------------//
                // Check north-south coding
                //------------------------------------------------------------//
                int rn = (y > 0)            ? A.get(x,y-1).getRed()   : 0;
                int gn = (y > 0)            ? A.get(x,y-1).getGreen() : 0;
                int bn = (y > 0)            ? A.get(x,y-1).getBlue()  : 0;

                int rs = (y < A.height()-1) ? A.get(x,y+1).getRed()   : 0;
                int gs = (y < A.height()-1) ? A.get(x,y+1).getGreen() : 0;
                int bs = (y < A.height()-1) ? A.get(x,y+1).getBlue()  : 0;

                int rns = ( bn + gs + x ) & SeamDoppelganger.SEAM_HEX;
                int gns = ( rn + bs + x ) & SeamDoppelganger.SEAM_HEX;
                int bns = ( gn + rs + x ) & SeamDoppelganger.SEAM_HEX;
                
                boolean codedNS = (rns == c.getRed()) && (gns == c.getGreen()) && (bns == c.getBlue());
                
                //------------------------------------------------------------//
                // Check west-east coding
                //------------------------------------------------------------//
                int rw = (x > 0)           ? A.get(x-1,y).getRed()   : 0;
                int gw = (x > 0)           ? A.get(x-1,y).getGreen() : 0;
                int bw = (x > 0)           ? A.get(x-1,y).getBlue()  : 0;

                int re = (x < A.width()-1) ? A.get(x+1,y).getRed()   : 0;
                int ge = (x < A.width()-1) ? A.get(x+1,y).getGreen() : 0;
                int be = (x < A.width()-1) ? A.get(x+1,y).getBlue()  : 0;

                int rwe = ( bw + ge + y ) & SeamDoppelganger.SEAM_HEX;
                int gwe = ( rw + be + y ) & SeamDoppelganger.SEAM_HEX;
                int bwe = ( gw + re + y ) & SeamDoppelganger.SEAM_HEX;

                boolean codedWE = (rwe == c.getRed()) && (gwe == c.getGreen()) && (bwe == c.getBlue());


                if( codedNS )
                {
                    //System.out.printf("Undoing code x=%d y=%d\n", x, y);
                    // Believe this was coded, take average
                    int r = (rn + rs) / 2;
                    int g = (gn + gs) / 2;
                    int b = (bn + bs) / 2;
                    B.set(x, y, new Color(r,g,b) );
                }
                else if( codedWE )
                {
                    //System.out.printf("Undoing code x=%d y=%d\n", x, y);
                    // Believe this was coded, take average
                    int r = (rw + re) / 2;
                    int g = (gw + ge) / 2;
                    int b = (bw + be) / 2;
                    B.set(x, y, new Color(r,g,b) );
                }
                else
                {
                    // Just copy over from A, we do not believe this was coded
                    B.set(x, y, c );
                }
            }
        }
 
        A.show();
        B.show();
    }


}
