
import java.awt.Color;

/**
 * SeamCarver is a Picture that provides operations to find seams within the picture
 * based on converted energy values (derived from color deltas).
 * @author James Pope
 */
public class SeamCarver
{
    private Picture p;
    
    /**
     * Creates new SeamCarver with specified parameters.
     * @param p 
     */
    public SeamCarver(Picture p)
    {
        // Make copy - per instructions not to mutate
        this.p = new Picture(p.width(), p.height());
        for (int i = 0; i < p.width(); i++)
        {
            for (int j = 0; j < p.height(); j++)
            {
                this.p.set(i, j, p.get(i, j));
            }
        }
    }

    /**
     * Get carver's picture
     * @return 
     */
    public Picture picture()
    {
        return this.p;
    }

    /**
     * Get picture width
     * @return 
     */
    public int width() 
    {
        return this.p.width();
    }

    /**
     * Get picture height
     * @return 
     */
    public int height()
    {
        return this.p.height();
    }

    /**
     * Computes the distance between the two colors.
     * @param c1
     * @param c2
     * @return euclidean distance between [c1.r,c1.g,c1.b] and [c2.r,c2.g,c2.b]
     */
    private double delta( Color c1, Color c2 )
    {
        double rx = c1.getRed()   - c2.getRed();
        double gx = c1.getGreen() - c2.getGreen();
        double bx = c1.getBlue()  - c2.getBlue();
        return (rx*rx) + (gx*gx) + (bx*bx);
    }
    
    /**
     * Energy of pixel at column x and row y
     * @param x
     * @param y
     * @return 
     */
    public double energy(int x, int y)
    {
        // If along edge, by definition return R^2 + G^2 + B^2
        if(x == 0 || y == 0 || x == this.p.width()-1 || y == this.p.height()-1)
        {
            return (255*255)+(255*255)+(255*255); // 195075
        }
        
        double dx2 = delta( p.get(x-1,   y), p.get(x+1,   y) );
        double dy2 = delta( p.get(  x, y-1), p.get(  x, y+1) );
        
        return dx2+dy2;
    }

    /**
     * Finds and returns the horizontal seam as a sequence of indices.
     * @return 
     */
    public int[] findHorizontalSeam()            
    {
        int w = p.width();
        int h = p.height();
        
        //System.out.println("Normal:");
        //print(makeEnergy(this));
        double[][] e = makeEnergyTranspose(this);
        //System.out.println("\nTransposed:");
         //print(e);
        double minEnergy = Double.MAX_VALUE;
        int[]  minPath   = null;
        for (int x = 0; x < h; x++)
        {
            int[] xPath = sp( e, x );
            double xEnergy = pathEnergy(e, xPath);
            if( xEnergy < minEnergy )
            {
                minEnergy = xEnergy;
                minPath   = xPath;
            }
        }
        
        return minPath;
    }

    /**
     * Finds and returns the vertical seam as a sequence of indices.
     * @return 
     */
    public int[] findVerticalSeam()
    {
        int w = p.width();
        int h = p.height();
        
        double[][] e = makeEnergy(this);
        double minEnergy = Double.MAX_VALUE;
        int[]  minPath   = null;
        for (int x = 0; x < w; x++)
        {
            int[] xPath = sp( e, x );
            double xEnergy = pathEnergy(e, xPath);
            if( xEnergy < minEnergy )
            {
                minEnergy = xEnergy;
                minPath   = xPath;
            }
        }
        return minPath;
    }

    /**
     * Removes the specified horizontal seam from the carver's picture.
     * @param a 
     */
    public void removeHorizontalSeam(int[] a)
    {
        if( this.height() <= 1 )
        {
            throw new IllegalArgumentException("Height too small "+this.height());
        }
        if( a.length != this.width() )
        {
            throw new IllegalArgumentException("Seam not compatible "+a.length);
        }
        
        int h = this.height()-1;
        int w = this.width();
        Picture newPicture = new Picture(w, h);
        for (int x = 0; x < w; x++)
        {
            for (int y = 0; y < h; y++)
            {
                int fromY = y;
                if( fromY >= a[x] )
                {
                    fromY = y+1;
                }
                Color c = this.picture().get(x, fromY);
                newPicture.set(x, y, c);
            }
        }
        this.p = newPicture;
    }

    /**
     * Removes the specified vertical seam from the carver's picture.
     * @param a 
     */
    public void removeVerticalSeam(int[] a)
    {
        if( this.width() <= 1 )
        {
            throw new IllegalArgumentException("Width too small "+this.width());
        }
        if( a.length != this.height() )
        {
            throw new IllegalArgumentException("Seam not compatible "+a.length);
        }
        
        int h = this.height();
        int w = this.width()-1;
        Picture newPicture = new Picture(w, h);
        for (int x = 0; x < w; x++)
        {
            for (int y = 0; y < h; y++)
            {
                int fromX = x;
                if( fromX >= a[y] )
                {
                    fromX = x+1;
                }
                Color c = this.picture().get(fromX, y);
                newPicture.set(x, y, c);
            }
        }
        this.p = newPicture;
    }
    
    /**
     * Converts the carver's picture into a two dimensional energy matrix.
     * @param sc
     * @return 
     */
    private static double[][] makeEnergy(SeamCarver sc)
    {
        Picture p = sc.picture();
        double[][] e = new double[p.height()][p.width()];
        
        for (int y = 0; y < p.height(); y++)
        {
            for (int x = 0; x < p.width(); x++)
            {
                e[y][x] = sc.energy(x, y);
            }
        }
        
        return e;
    }
    
    private static void print(double[][] e)
    {
        for (int y = 0; y < e.length; y++)
        {
            System.out.print("row="+y+": ");
            for (int x = 0; x < e[0].length; x++)
            {
                System.out.print(e[y][x]+", ");
            }
            System.out.println("");
        }
    }
    
    /**
     * Converts carver picture to a two dimensional energy matrix and then transposes.
     * @param sc
     * @return 
     */
    private static double[][] makeEnergyTranspose(SeamCarver sc)
    {
        Picture p = sc.picture();
        double[][] e = new double[p.width()][p.height()];

        for (int y = 0; y < p.height(); y++)
        {
            for (int x = 0; x < p.width(); x++)
            {
                e[x][y] = sc.energy(x, y);
            }
        }

        return e;
    }
    
    /**
     * Gets the energy values along the specified path.
     * @param e
     * @param a
     * @return 
     */
    private static double pathEnergy(double[][] e, int[] a)
    {
        double s = 0.0;
        for (int y = 0; y < a.length; y++)
        {
            s += e[y][a[y]];
        }
        return s;
    }
    
    /**
     * Find the path of least energy from top to bottom.
     * @param e
     * @param s
     * @return path of e.length
     */
    private static int[] sp(double[][] e, int s)
    {
        int w = e[0].length;
        int h = e.length;
        
        int[] sp = new int[h];
        
        int x = s;
        int y;
        for (y = 0; y < h-1; y++)
        {
            sp[y] = x;
            
            // Southwest edge
            double sw = (x > 0) ?   e[y+1][x-1] : Double.MAX_VALUE;
            double ss = e[y+1][x+0];
            double se = (x < w-1) ? e[y+1][x+1] : Double.MAX_VALUE;
            
            if( sw < ss && sw < se )
            {
                x = x-1;
            }
            else if( ss < sw && ss < se )
            {
                // default
            }
            else if( se < sw && se < ss )
            {
                x = x+1;
            }
        }
        // Pick up last decision
        sp[y] = x;
        
        return sp;
    }

}
