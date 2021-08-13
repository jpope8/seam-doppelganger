
import java.awt.Color;
import java.util.TreeMap;

/**
 * SeamDoppelganger is a SeamCarving approach that replaces found seams with
 * similar but noisy facsimilies.   Ideally this replacement should confuse
 * machine learning algorithms.
 * @author James Pope
 */
public class SeamDoppelganger
{
    public static final int SEAM_HEX = 0x000000C5; // 1100 0101 = 197
    public static final int SEAM_MULTIPLIER = 4;

    private Picture p;
    
    /**
     * Creates new SeamDoppelganger with specified parameters.
     * @param p 
     */
    public SeamDoppelganger(Picture p)
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
    private int delta( Color c1, Color c2 )
    {
        int rx = c1.getRed()   - c2.getRed();
        int gx = c1.getGreen() - c2.getGreen();
        int bx = c1.getBlue()  - c2.getBlue();
        return (rx*rx) + (gx*gx) + (bx*bx);
    }
    
    /**
     * Energy of pixel at column x and row y
     * @param x
     * @param y
     * @return 
     */
    public int energy(int x, int y)
    {
        // If along edge, by definition return R^2 + G^2 + B^2
        if(x == 0 || y == 0 || x == this.p.width()-1 || y == this.p.height()-1)
        {
            return (255*255)+(255*255)+(255*255); // 195075
        }
        
        int dx2 = delta( p.get(x-1,   y), p.get(x+1,   y) );
        int dy2 = delta( p.get(  x, y-1), p.get(  x, y+1) );
        
        return dx2+dy2;
    }

    /**
     * Finds and returns the horizontal seam as a sequence of indices.
     * @return 
     */
    public int[] findSmallestHorizontalSeam()            
    {
        int w = p.width();
        int h = p.height();
        
        //System.out.println("Normal:");
        //print(makeEnergy(this));
        int[][] e = makeEnergyTranspose(this);
        //System.out.println("\nTransposed:");
         //print(e);
        int minEnergy = Integer.MAX_VALUE;
        int[]  minPath   = null;
        for (int x = 0; x < h; x++)
        {
            int[] xPath = leastShortestPath( e, x );
            int xEnergy = pathEnergy(e, xPath);
            if( xEnergy < minEnergy )
            {
                minEnergy = xEnergy;
                minPath   = xPath;
            }
        }
        
        return minPath;
    }


    /**
     * Finds and returns the horizontal seam as a sequence of indices.
     * @return 
     */
    public TreeMap<Integer, int[]> findSmallestHorizontalSeams()            
    {
        int w = p.width();
        int h = p.height();
        
        TreeMap<Integer, int[]> seams = new TreeMap();
        //System.out.println("Normal:");
        //print(makeEnergy(this));
        int[][] e = makeEnergyTranspose(this);
        //System.out.println("\nTransposed:");
         //print(e);
        int minEnergy = Integer.MAX_VALUE;
        int[]  minPath   = null;
        for (int x = 0; x < h; x++)
        {
            int[] xPath = leastShortestPath( e, x );
            int xEnergy = pathEnergy(e, xPath);
            seams.put( xEnergy, xPath );
        }
        
        return seams;
    }



    /**
     * Finds and returns the vertical seam as a sequence of indices.
     * @return 
     */
    public int[] findSmallestVerticalSeam()
    {
        int w = p.width();
        int h = p.height();
        
        int[][] e = makeEnergy(this);
        int minEnergy = Integer.MAX_VALUE;
        int[]  minPath   = null;
        for (int x = 0; x < w; x++)
        {
            int[] xPath = leastShortestPath( e, x );
            int xEnergy = pathEnergy(e, xPath);
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
    public TreeMap<Integer,int[]> findSmallestVerticalSeams()
    {
        int w = p.width();
        int h = p.height();

        TreeMap<Integer, int[]> seams = new TreeMap();
        
        int[][] e = makeEnergy(this);
        int minEnergy = Integer.MAX_VALUE;
        int[]  minPath   = null;
        for (int x = 0; x < w; x++)
        {
            int[] xPath = leastShortestPath( e, x );
            int xEnergy = pathEnergy(e, xPath);
            seams.put(xEnergy, xPath);
        }
        return seams;
    }




    /**
     * Finds and returns the horizontal seam as a sequence of indices.
     * @return 
     */
    public int[] findLargestHorizontalSeam()            
    {
        int[][] e = makeEnergyTranspose(this);

        int w = e[0].length;
        int h = e.length;

        //System.out.printf( "HorizontalSeam w=%d  h=%d \n", w, h);

        int minEnergy = Integer.MIN_VALUE;
        int[]  minPath   = null;
        for (int x = 0; x < w; x++)
        {
            int[] path = greatestShortestPath( e, x );
            int energy = pathEnergy(e, path);
            if( energy > minEnergy )
            {
                minEnergy = energy;
                minPath   = path;
            }
        }
        //System.out.printf( "    minPath.length=%d \n", minPath.length );
        return minPath;
    }

    /**
     * Finds and returns the vertical seam as a sequence of indices.
     * @return 
     */
    public int[] findLargestVerticalSeam()
    {   
        int[][] e = makeEnergy(this);

        int w = e[0].length;
        int h = e.length;

        //System.out.printf( "VerticalSeam w=%d  h=%d \n", w, h);

        int minEnergy = Integer.MIN_VALUE;
        int[]  minPath   = null;
        for (int x = 0; x < w; x++)
        {
            int[] path = greatestShortestPath( e, x );
            int energy = pathEnergy(e, path);
            if( energy > minEnergy )
            {
                minEnergy = energy;
                minPath   = path;
            }
        }
        //System.out.printf( "    minPath.length=%d \n", minPath.length );
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
     * Replaces the specified horizontal seam from the carver's picture.
     * @param a 
     */
    public void replaceHorizontalSeam(int[] a, Matrix kernel)
    {
        if( this.height() <= 1 )
        {
            throw new IllegalArgumentException("Height too small "+this.height());
        }
        if( a.length != this.width() )
        {
            throw new IllegalArgumentException("Seam not compatible "+a.length);
        }
        
        for (int x = 0; x < this.width(); x++)
        {
            int y = a[x];

            Color oldColor = this.picture().get(x, y);

            int r = StdRandom.uniform( 0, 256 );
            int g = StdRandom.uniform( 0, 256 );
            int b = StdRandom.uniform( 0, 256 );


            //Color newColor = new Color( r, g, b );
            Color newColor = new Color( 255, 0, 0 );

            this.p.set(x, y, newColor);
        }
    }

    /**
     * Replaces the specified horizontal seam from the carver's picture.
     * @param a 
     */
    public void replaceHorizontalSeamRandom(int[] a)
    {
        if( this.height() <= 1 )
        {
            throw new IllegalArgumentException("Height too small "+this.height());
        }
        if( a.length != this.width() )
        {
            throw new IllegalArgumentException("Seam not compatible "+a.length);
        }
        
        for (int x = 0; x < this.width(); x++)
        {
            int y = a[x];

            //Color oldColor = this.picture().get(x, y);
            //int r = StdRandom.uniform( 0, 256 );
            //int g = StdRandom.uniform( 0, 256 );
            //int b = StdRandom.uniform( 0, 256 );

            //Color newColor = new Color( r, g, b );

            Color c = this.p.get(x,y);

            // Function of north and south neighbors
            int rn = (y > 0)            ? this.p.get(x,y-1).getRed()   : 0;
            int gn = (y > 0)            ? this.p.get(x,y-1).getGreen() : 0;
            int bn = (y > 0)            ? this.p.get(x,y-1).getBlue()  : 0;

            int rs = (y < p.height()-1) ? this.p.get(x,y+1).getRed()   : 0;
            int gs = (y < p.height()-1) ? this.p.get(x,y+1).getGreen() : 0;
            int bs = (y < p.height()-1) ? this.p.get(x,y+1).getBlue()  : 0;

            int rns = ( bn + gs + x ) & SEAM_HEX;
            int gns = ( rn + bs + x ) & SEAM_HEX;
            int bns = ( gn + rs + x ) & SEAM_HEX;

            //this.p.set(x, y, newColor);
            //this.p.set(x, y, new Color(0,0,0) );
            this.p.set(x, y, new Color(rns,gns,bns) );
        }
    }

    /**
     * Replaces the specified horizontal seam from the carver's picture.
     * @param a 
     */
    public void swapHorizontalSeamRandom(int[] a, Picture swapImg)
    {
        if( this.height() <= 1 )
        {
            throw new IllegalArgumentException("Height too small "+this.height());
        }
        if( a.length != this.width() )
        {
            throw new IllegalArgumentException("Seam not compatible "+a.length);
        }
        
        for (int x = 0; x < this.width(); x++)
        {
            int y = a[x];
            this.p.set(x, y, swapImg.get(x,y) );
        }
    }

    /**
     * Replaces the specified horizontal seam from the carver's picture.
     * @param a 
     */
    public void convolveHorizontalSeam(int[] a, Matrix kernel)
    {
        if( this.height() <= 1 )
        {
            throw new IllegalArgumentException("Height too small "+this.height());
        }
        if( a.length != this.width() )
        {
            throw new IllegalArgumentException("Seam not compatible "+a.length);
        }

        int n = kernel.getRowSize();
        int mp = n / 2; // midpoint
        
        for (int x = 0; x < this.width(); x++)
        {
            int yc = a[x];

            int maxY = yc + mp;
            if( maxY > this.height() ) maxY = this.height();
            int minY = yc - mp;
            if( minY < 0 ) minY = 0;
            for( int y = minY; y < maxY; y++ )
            {
                //----------------------------------------------------------------//
                // Convolve around x and y
                //----------------------------------------------------------------//
                double kernelSumR = 0.0;
                double kernelSumG = 0.0;
                double kernelSumB = 0.0;


                // Apply the filter, careful to handle near edges
                for (int i = 0; i < n; i++)
                {
                    for (int j = 0; j < n; j++)
                    {
                        // Get color from original picture
                        int xi = x-mp+i;
                        int yj = y-mp+j;

                        // Outside picture too far north is negative y
                        // Outside picture too far west  is negative x
                        // Outside picture too far south is y > picture height
                        // Outside picture too far east  is x > picture width
                        boolean outofbounds = xi < 0 || yj < 0 || xi >= this.width() || yj >= this.height();

                        // Check if out of bounds, effectively adds zero padding
                        // because we do not add if outofbounds but still divide
                        // by kernel size.
                        if( outofbounds == false )
                        {
                            Color c = this.picture().get(xi, yj);

                            double r = c.getRed();
                            double g = c.getGreen();
                            double b = c.getBlue();

                            // Get Kernel value from filter
                            double kernelValue = kernel.get(i,j);
                            kernelSumR += (r*kernelValue);
                            kernelSumG += (g*kernelValue);
                            kernelSumB += (b*kernelValue);
                        }
                    }
                }

                int r = (int)kernelSumR;
                int g = (int)kernelSumG;
                int b = (int)kernelSumB;

                // Make sure within [0,255]
                if( r > 255 ) r = 255;
                if( g > 255 ) g = 255;
                if( b > 255 ) b = 255;

                //Color oldColor = this.picture().get(x, y);
                Color newColor = new Color( r, g, b );

                //System.out.printf( "(%d,%d) Old RGB: (%d,%d,%d) New RGB: (%d,%d,%d)\n", x, y,
                //       oldColor.getRed(), oldColor.getGreen(), oldColor.getBlue(),
                //       newColor.getRed(), newColor.getGreen(), newColor.getBlue()  );

                
                //Color newColor = new Color( 255, 0, 0 );
                //----------------------------------------------------------------//
                

                this.p.set(x, y, newColor);
            }
        }
    }

    /**
     * Replaces the specified vertical seam from the carver's picture.
     * @param a 
     */
    public void replaceVerticalSeam(int[] a, Matrix kernel)
    {
        if( this.width() <= 1 )
        {
            throw new IllegalArgumentException("Width too small "+this.width());
        }
        if( a.length != this.height() )
        {
            throw new IllegalArgumentException("Seam not compatible "+a.length);
        }
        
        for (int y = 0; y < this.height(); y++)
        {
            int x = a[y];
            Color oldColor = this.picture().get(x, y);

            int r = StdRandom.uniform( 0, 256 );
            int g = StdRandom.uniform( 0, 256 );
            int b = StdRandom.uniform( 0, 256 );

            //Color newColor = new Color( r, g, b );
            Color newColor = new Color( 0, 0, 255 );

            this.p.set(x, y, newColor);
        }
        
    }

    /**
     * Replaces the specified vertical seam from the carver's picture.
     * @param a 
     */
    public void replaceVerticalSeamRandom(int[] a)
    {
        if( this.width() <= 1 )
        {
            throw new IllegalArgumentException("Width too small "+this.width());
        }
        if( a.length != this.height() )
        {
            throw new IllegalArgumentException("Seam not compatible "+a.length);
        }
        
        for (int y = 0; y < this.height(); y++)
        {
            int x = a[y];
            //Color oldColor = this.picture().get(x, y);

            //int r = StdRandom.uniform( 0, 256 );
            //int g = StdRandom.uniform( 0, 256 );
            //int b = StdRandom.uniform( 0, 256 );

            //Color newColor = new Color( r, g, b );

            Color c = this.p.get(x,y);

            // Function of west and east neighbors
            int rw = (x > 0)           ? this.p.get(x-1,y).getRed()   : 0;
            int gw = (x > 0)           ? this.p.get(x-1,y).getGreen() : 0;
            int bw = (x > 0)           ? this.p.get(x-1,y).getBlue()  : 0;

            int re = (x < p.width()-1) ? this.p.get(x+1,y).getRed()   : 0;
            int ge = (x < p.width()-1) ? this.p.get(x+1,y).getGreen() : 0;
            int be = (x < p.width()-1) ? this.p.get(x+1,y).getBlue()  : 0;

            int rwe = ( bw + ge + y ) & SEAM_HEX;
            int gwe = ( rw + be + y ) & SEAM_HEX;
            int bwe = ( gw + re + y ) & SEAM_HEX;

            //this.p.set(x, y, newColor);
            //this.p.set(x, y, new Color(0,0,0) );
            this.p.set(x, y, new Color(rwe,gwe,bwe) );
        }
        
    }

    /**
     * Replaces the specified vertical seam from the carver's picture.
     * @param a 
     */
    public void swapVerticalSeamRandom(int[] a, Picture swapImg)
    {
        if( this.width() <= 1 )
        {
            throw new IllegalArgumentException("Width too small "+this.width());
        }
        if( a.length != this.height() )
        {
            throw new IllegalArgumentException("Seam not compatible "+a.length);
        }
        
        for (int y = 0; y < this.height(); y++)
        {
            int x = a[y];
            this.p.set(x, y, swapImg.get(x,y) );
        }
        
    }

    /**
     * Replaces the specified vertical seam from the carver's picture.
     * @param a 
     */
    public void convolveVerticalSeam(int[] a, Matrix kernel)
    {
        if( this.width() <= 1 )
        {
            throw new IllegalArgumentException("Width too small "+this.width());
        }
        if( a.length != this.height() )
        {
            throw new IllegalArgumentException("Seam not compatible "+a.length);
        }

        int n = kernel.getRowSize();
        int mp = n / 2; // midpoint
        
        for (int y = 0; y < this.height(); y++)
        {
            int xc = a[y];

            int maxX = xc + mp;
            if( maxX > this.width() ) maxX = this.width();
            int minX = xc - mp;
            if( minX < 0 ) minX = 0;
            for( int x = minX; x < maxX; x++ )
            {
                
                //----------------------------------------------------------------//
                // Convolve around x and y
                //----------------------------------------------------------------//
                double kernelSumR = 0.0;
                double kernelSumG = 0.0;
                double kernelSumB = 0.0;


                // Apply the filter, careful to handle near edges
                for (int i = 0; i < n; i++)
                {
                    for (int j = 0; j < n; j++)
                    {
                        // Get color from original picture
                        int xi = x-mp+i;
                        int yj = y-mp+j;

                        // Outside picture too far north is negative y
                        // Outside picture too far west  is negative x
                        // Outside picture too far south is y > picture height
                        // Outside picture too far east  is x > picture width
                        boolean outofbounds = xi < 0 || yj < 0 || xi >= this.width() || yj >= this.height();

                        // Check if out of bounds, effectively adds zero padding
                        // because we do not add if outofbounds but still divide
                        // by kernel size.
                        if( outofbounds == false )
                        {
                            Color c = this.picture().get(xi, yj);

                            double r = c.getRed();
                            double g = c.getGreen();
                            double b = c.getBlue();

                            // Get Kernel value from filter
                            double kernelValue = kernel.get(i,j);
                            kernelSumR += (r*kernelValue);
                            kernelSumG += (g*kernelValue);
                            kernelSumB += (b*kernelValue);
                        }
                    }
                }

                int r = (int)kernelSumR;
                int g = (int)kernelSumG;
                int b = (int)kernelSumB;

                // Make sure within [0,255]
                if( r > 255 ) r = 255;
                if( g > 255 ) g = 255;
                if( b > 255 ) b = 255;

                Color newColor = new Color( r, g, b );
                //----------------------------------------------------------------//

                this.p.set(x, y, newColor);
            }
        }
        
    }

    
    /**
     * Converts the carver's picture into a two dimensional energy matrix.
     * @param sc
     * @return 
     */
    private static int[][] makeEnergy(SeamDoppelganger sc)
    {
        /*
         * Switched from double[][] to int[][] because round off was a pain.
         * Can handle up to about 10,000 pixel image without overflow.
         * Note we add them for the path, each pixel can be at most (255*255) * 3 
         * 10000 * (255*255) * 3 < 2^31
         */
        Picture p = sc.picture();
        int[][] e = new int[p.height()][p.width()];
        
        for (int y = 0; y < p.height(); y++)
        {
            for (int x = 0; x < p.width(); x++)
            {
                e[y][x] = sc.energy(x, y);
            }
        }
        
        return e;
    }
    
    private static void print(int[][] e)
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
    private static int[][] makeEnergyTranspose(SeamDoppelganger sc)
    {
        Picture p = sc.picture();
        int[][] e = new int[p.width()][p.height()];

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
    private static int pathEnergy(int[][] e, int[] a)
    {
        int s = 0;
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
    private static int[] leastShortestPath(int[][] e, int s)
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
            int sw = (x > 0) ?   e[y+1][x-1] : Integer.MAX_VALUE;
            int ss = e[y+1][x+0];
            int se = (x < w-1) ? e[y+1][x+1] : Integer.MAX_VALUE;
            
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

    /**
     * Find the path of greatest energy from top to bottom.
     * @param e
     * @param s
     * @return path of e.length
     */
    private static int[] greatestShortestPath(int[][] e, int s)
    {
        //System.out.printf( "Calling greatestShortestPath with x=%d\n", s );

        int w = e[0].length;
        int h = e.length;

        if( s == w )
        {
            String err = String.format("Specified width s %d is width of energy %d", s, w);
            throw new IllegalArgumentException(err);
        }
        //System.out.println("greatestShortestPath Height is " + h);
        //System.out.println("greatestShortestPath Width  is " + w);
        
        int[] sp = new int[h];
        
        int y = 0;
        sp[y] = s;
        while ( y < h-1 )
        {
            int x = sp[y];

            // Southwest edge
            int sw = (x > 0) ?   e[y+1][x-1] : Integer.MIN_VALUE;
            int ss = e[y+1][x+0];
            int se = (x < w-1) ? e[y+1][x+1] : Integer.MIN_VALUE;
            

            // If edge, set to 0
            if( sw == 195075 ) sw = 0;
            if( ss == 195075 ) ss = 0;
            if( se == 195075 ) se = 0;


            if( sw > ss && sw > se )
            {
                x = x-1;
                //System.out.printf( "Case 1 y = %d  x=%d   sw=%.2f ss=%.2f se=%.2f\n", y, x, sw, ss, se );
            }
            else if( ss > sw && ss > se )
            {
                // default
                //System.out.printf( "Case 2 y = %d  x=%d   sw=%.2f ss=%.2f se=%.2f\n", y, x, sw, ss, se );
            }
            else if( se > sw && se > ss )
            {
                x = x+1;
                //System.out.printf( "Case 3 y = %d  x=%d   sw=%.28f ss=%.28f se=%.28f\n", y, x, sw, ss, se );
                //System.out.println( "Case 3" );            
                //System.out.println( "     sw=" + sw );
                //System.out.println( "     ss=" + ss );
                //System.out.println( "     se=" + se );
            }
            else
            {
                //System.out.printf( "Case 4 y = %d  x=%d   sw=%.2f ss=%.2f se=%.2f\n", y, x, sw, ss, se );
            }

            /*
             * Peculiar Case 3:  Sometimes se=0 < sw and ss when x = width!!!
             * This we add one and get exception in next loop AIOOBE.
             * floating point issue, changed to integer and works fine now.
             */
            //if( x >= w ) x = w-1;

            
            //System.out.printf( "y = %d  x=%d   sw=%.2f ss=%.2f se=%.2f\n", y, x, sw, ss, se );
            y++;
            sp[y] = x;
        }
        // Pick up last decision
        //sp[y] = x;
        
        return sp;
    }


}
