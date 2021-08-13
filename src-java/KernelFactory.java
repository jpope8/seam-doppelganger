
/**
 * Factory to create Gaussian Blur kernels
 * @author James Pope
 */
public class KernelFactory
{
    /**
     * JPOPE: The below implementation is mine.
     * The results are slightly different because I use the gaussian normalizer
     * of 1 / (2 pi s^2) and the previous approach divided by the sum of
     * the entries.   The two approaches end up surprisingly close. 
     * My approach  is based on
     * https://en.wikipedia.org/wiki/Gaussian_blur
     *
     * @param n
     * @param sigma - sigma value in Gaussian function
     * @return matrix containing the spatial filter
     */
    public static Matrix gaussianBlur(int n, double sigma)
    {
        // Determine gaussian blur size
        // Return matrix zeroing
        Matrix gmat = new Matrix(n, n, 0.0);

        // The kernel must be odd length
        if( n % 2 == 0 || n < 3 )
        {
            String e = "The kernel length "+n+" must be odd and greater than 1";
            throw new IllegalArgumentException(e);
        }
        
        // Assume mean at 0 center pixel
        double coeff = 1.0 / ( 2.0*Math.PI*sigma*sigma );
        double twoSigma2 = 2.0*sigma*sigma;
        for( int x = 0; x < n; x++ )
        {
            for( int y = 0; y < n; y++ )
            {
                int a = x - n / 2;
                int b = y - n / 2;
                
                double exp = ((a*a) + (b*b)) / twoSigma2;
                double value = coeff * Math.pow( Math.E, -exp );
                
                gmat.set(x, y, value);
            }
        }
        
        return gmat;
    }
    
    public static Matrix gaussianBlur(double sigma)
    {
        // Based in part on ideas from Prof Sohaib Khan at:
        // http://suraj.lums.edu.pk/~cs436a02/CannyImplementation.htm
        // Determine gaussian blur size
        int n = 2 * Math.round((float) Math.sqrt(-Math.log(0.1) * 2.0 * (sigma * sigma))) + 1;
        return gaussianBlur(n, sigma);
    }
    
    public static Matrix verticalLineFilter( int n, double sigma )
    {
        //double[][] lineRemovalFilter1 =
        //{
        //    { 1.0,  1.0,  0.0, 0.0, 0.0,  1.0,  1.0 },
        //    { 1.0,  1.0,  0.0, 0.0, 0.0,  1.0,  1.0 },
        //    { 1.0,  1.0,  0.0, 0.0, 0.0,  1.0,  1.0 },
        //    { 1.0,  1.0,  0.0, 0.0, 0.0,  1.0,  1.0 },
        //    { 1.0,  1.0,  0.0, 0.0, 0.0,  1.0,  1.0 },
        //    { 1.0,  1.0,  0.0, 0.0, 0.0,  1.0,  1.0 },
        //    { 1.0,  1.0,  0.0, 0.0, 0.0,  1.0,  1.0 },
        //};
        
        // Sanity check, make sure n is odd
        if( n % 2 == 0 )
        {
            String e = "Kernel width has to be an odd number: " + n;
            throw new IllegalArgumentException(e);
        }
        
        
        // Assume mean at 0 center pixel
        double coeff = 1.0 / ( 2.0*Math.PI*sigma*sigma );
        double twoSigma2 = 2.0*sigma*sigma;
        
        double[][] kernel = new double[n][n];
        for (int y = 0; y < n; y++) // rows
        {
            for (int x = 0; x < kernel.length; x++) // cols
            {
                /*
                 * This implementation does not move out in both x,y direction
                 * We only consider the x direction.  Accordingly, we only
                 * iterate one dimension in the kernel - could have gotten
                 * away with only one dimension instead of two.
                 *
                 * The works much better for vertical lines as it does not
                 * distort the y direction.
                 */
                
                int a = x - n / 2;
                
                double exp = (a*a) / twoSigma2;
                double value = coeff * Math.pow( Math.E, -exp );
                
                // We invert, lowest near 0 and greater at the edges
                value = 1.0 - (5.0*value);
                kernel[y][x] = value;
                
                //// Simple weighted average approach
                //if( x > lineStart && x <= lineStart + lineWidth )
                //{
                //    kernel[y][x] = 0.0;
                //}
                //else
                //{
                //    kernel[y][x] = 1.0;
                //}
            }
        }
        
        // DEBUG
        //print( kernel );
        
        return new Matrix(kernel);
    }
    
    
    /**
     * Sharpen Filter (always 3x3).
     * @see https://en.wikipedia.org/wiki/Kernel_(image_processing)
     * @see https://en.wikipedia.org/wiki/Unsharp_masking
     *
     * @param sharpenForce
     * @return matrix containing the spatial filter
     */
    public static Matrix sharpen(double sharpenForce)
    {
        
        double[][] kernel =  {
            { 0,                   -1*sharpenForce,               0}, 
            { -1*sharpenForce, (4*sharpenForce) +1,-1 *sharpenForce}, 
            { 0,                   -1*sharpenForce,               0}
        };
        
        Matrix filter = new Matrix( kernel );
        
        return filter;
    }
    
    
    public static void print( double[][] kernel )
    {
        for( int x = 0; x < kernel.length; x++ )
        {
            for( int y = 0; y < kernel[0].length; y++ )
            {
                System.out.printf( " %10.8f", kernel[x][y] );
            }
            System.out.printf("\n");
        }
    }
}

