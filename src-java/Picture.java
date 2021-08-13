/*************************************************************************
 *  Compilation:  javac Picture.java
 *  Execution:    java Picture imagename
 *
 *  Data type for manipulating individual pixels of an image. The original
 *  image can be read from a file in jpg, gif, or png format, or the
 *  user can create a blank image of a given size. Includes methods for
 *  displaying the image in a window on the screen or saving to a file.
 *
 *  % java Picture mandrill.jpg
 *
 *  Remarks
 *  -------
 *   - pixel (x, y) is column x and row y, where (0, 0) is upper left
 *
 *   - see also GrayPicture.java for a grayscale version
 *
 *************************************************************************/

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import java.util.TreeSet;


/**
 *  This class provides methods for manipulating individual pixels of
 *  an image. The original image can be read from a file in JPEG, GIF,
 *  or PNG format, or the user can create a blank image of a given size.
 *  This class includes methods for displaying the image in a window on
 *  the screen or saving to a file.
 *  <p>
 *  By default, pixel (x, y) is column x, row y, where (0, 0) is upper left.
 *  The method setOriginLowerLeft() change the origin to the lower left.
 *  <p>
 *  For additional documentation, see
 *  <a href="http://introcs.cs.princeton.edu/31datatype">Section 3.1</a> of
 *  <i>Introduction to Programming in Java: An Interdisciplinary Approach</i>
 *  by Robert Sedgewick and Kevin Wayne.
 */
public final class Picture implements ActionListener {
    private BufferedImage image;               // the rasterized image
    private JFrame frame;                      // on-screen view
    private String filename;                   // name of file
    private boolean isOriginUpperLeft = true;  // location of origin
    private final int width, height;           // width and height



   /**
     * Create a blank w-by-h picture, where each pixel is black.
     */
    public Picture(int w, int h) {
        width = w;
        height = h;
        image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        // set to TYPE_INT_ARGB to support transparency
        filename = w + "-by-" + h;
    }


    /**
     * Creates a picture by reading an image from a file or URL.
     *
     * @param  filename the name of the file (.png, .gif, or .jpg) or URL.
     * @param image
     * @throws IllegalArgumentException if cannot read image
     * @throws IllegalArgumentException if {@code filename} is {@code null}
     */
    public Picture(BufferedImage image) {
        if (image    == null) throw new IllegalArgumentException("constructor argument is null");
        
        this.image    = image;
        this.width    = image.getWidth(null);
        this.height   = image.getHeight(null);
    }


   /**
     * Copy constructor.
     */
    public Picture(Picture pic) {
        width = pic.width();
        height = pic.height();
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        filename = pic.filename;
        for (int i = 0; i < width(); i++)
            for (int j = 0; j < height(); j++)
                image.setRGB(i, j, pic.get(i, j).getRGB());
    }

   /**
     * Create a picture by reading in a .png, .gif, or .jpg from
     * the given filename or URL name.
     */
    public Picture(String filename) {
        this.filename = filename;
        try {
            // try to read from file in working directory
            File file = new File(filename);
            if (file.isFile()) {
                image = ImageIO.read(file);
            }

            // now try to read from file in same directory as this .class file
            else {
                URL url = getClass().getResource(filename);
                if (url == null) { url = new URL(filename); }
                image = ImageIO.read(url);
            }
            width  = image.getWidth(null);
            height = image.getHeight(null);
        }
        catch (IOException e) {
            // e.printStackTrace();
            throw new RuntimeException("Could not open file: " + filename);
        }
    }

   /**
     * Create a picture by reading in a .png, .gif, or .jpg from a File.
     */
    public Picture(File file) {
        try { image = ImageIO.read(file); }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not open file: " + file);
        }
        if (image == null) {
            throw new RuntimeException("Invalid image file: " + file);
        }
        width  = image.getWidth(null);
        height = image.getHeight(null);
        filename = file.getName();
    }

   /**
     * Return a JLabel containing this Picture, for embedding in a JPanel,
     * JFrame or other GUI widget.
     */
    public JLabel getJLabel() {
        if (image == null) { return null; }         // no image available
        ImageIcon icon = new ImageIcon(image);
        return new JLabel(icon);
    }

   /**
     * Set the origin to be the upper left pixel.
     */
    public void setOriginUpperLeft() {
        isOriginUpperLeft = true;
    }

   /**
     * Set the origin to be the lower left pixel.
     */
    public void setOriginLowerLeft() {
        isOriginUpperLeft = false;
    }

   /**
     * Display the picture in a window on the screen.
     */
    public void show() {

        // create the GUI for viewing the image if needed
        if (frame == null) {
            frame = new JFrame();

            JMenuBar menuBar = new JMenuBar();
            JMenu menu = new JMenu("File");
            menuBar.add(menu);
            JMenuItem menuItem1 = new JMenuItem(" Save...   ");
            menuItem1.addActionListener(this);
            menuItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                                     Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            menu.add(menuItem1);
            frame.setJMenuBar(menuBar);



            frame.setContentPane(getJLabel());
            // f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setTitle(filename);
            frame.setResizable(false);
            frame.pack();
            frame.setVisible(true);
        }

        // draw
        frame.repaint();
    }

   /**
     * Return the height of the picture in pixels.
     */
    public int height() {
        return height;
    }

   /**
     * Return the width of the picture in pixels.
     */
    public int width() {
        return width;
    }

    private void validateRowIndex(int row) {
        if (row < 0 || row >= height())
            throw new IllegalArgumentException("row index must be between 0 and " + (height() - 1) + ": " + row);
    }

    private void validateColumnIndex(int col) {
        if (col < 0 || col >= width())
            throw new IllegalArgumentException("column index must be between 0 and " + (width() - 1) + ": " + col);
    }

   /**
     * Return the color of pixel (i, j).
     */
    public Color get(int i, int j) {
        if (isOriginUpperLeft) return new Color(image.getRGB(i, j));
        else                   return new Color(image.getRGB(i, height - j - 1));
    }

    /**
     * Returns the color of pixel ({@code col}, {@code row}) as an {@code int}.
     * Using this method can be more efficient than {@link #get(int, int)} because
     * it does not create a {@code Color} object.
     *
     * @param col the column index
     * @param row the row index
     * @return the integer representation of the color of pixel ({@code col}, {@code row})
     * @throws IllegalArgumentException unless both {@code 0 <= col < width} and {@code 0 <= row < height}
     */
    public int getRGB(int col, int row) {
        validateColumnIndex(col);
        validateRowIndex(row);
        if (isOriginUpperLeft) return image.getRGB(col, row);
        else                   return image.getRGB(col, height - row - 1);
    }

   /**
     * Set the color of pixel (i, j) to c.
     */
    public void set(int i, int j, Color c) {
        if (c == null) { throw new RuntimeException("can't set Color to null"); }
        if (isOriginUpperLeft) image.setRGB(i, j, c.getRGB());
        else                   image.setRGB(i, height - j - 1, c.getRGB());
    }

   /**
     * Is this Picture equal to obj?
     */
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;
        Picture that = (Picture) obj;
        if (this.width()  != that.width())  return false;
        if (this.height() != that.height()) return false;
        for (int x = 0; x < width(); x++)
            for (int y = 0; y < height(); y++)
                if (!this.get(x, y).equals(that.get(x, y))) return false;
        return true;
    }


   /**
     * Save the picture to a file in a standard image format.
     * The filetype must be .png or .jpg.
     */
    public void save(String name) {
        save(new File(name));
    }

   /**
     * Save the picture to a file in a standard image format.
     */
    public void save(File file) {
        this.filename = file.getName();
        if (frame != null) { frame.setTitle(filename); }
        String suffix = filename.substring(filename.lastIndexOf('.') + 1);
        suffix = suffix.toLowerCase();
        if (suffix.equals("jpg") || suffix.equals("png")) {
            try { ImageIO.write(image, suffix, file); }
            catch (IOException e) { e.printStackTrace(); }
        }
        else {
            System.out.println("Error: filename must end in .jpg or .png");
        }
    }

   /**
     * Opens a save dialog box when the user selects "Save As" from the menu.
     */
    public void actionPerformed(ActionEvent e) {
        FileDialog chooser = new FileDialog(frame,
                             "Use a .png or .jpg extension", FileDialog.SAVE);
        chooser.setVisible(true);
        if (chooser.getFile() != null) {
            save(chooser.getDirectory() + File.separator + chooser.getFile());
        }
    }

    /**
     * Gets transpose of this Picture, obviously a copy.
     */
    public Picture transpose()
    {
        Picture pT = new Picture(this.height(), this.width());
        for (int i = 0; i < this.width(); i++)
        {
            for (int j = 0; j < this.height(); j++)
            {
                pT.set(j, i, this.get(i, j));
            }
        }
        return pT;
    }


    /**
     * Apply a spatial FIR (linear, non-recursive) filter to the image, using
     * the correlation operator. Uses 0-padding for pixels around the image.
     * This is equivalent to MATLAB:
     * <pre>result = filter2(kernel, this);</pre>
     *
     * @param kernel - Filter kernel matrix
     * @return a new matrix with the result
     */
    public Picture applyFilter( Matrix kernel )
    {
        // We do not zero pad or properly filter near edges.
        // Instead, filtering starts at half kernel width.
        // Iterate over matrix
        
        Picture m = new Picture(this.width, this.height);
        
        if( kernel.getColumnSize() != kernel.getRowSize() )
        {
            String e = "Kernel must be symetric";
            throw new IllegalArgumentException(e);
        }
        
        //int height = this.data.length;
        //int width  = this.data[0].length;
        
        int n = kernel.getRowSize();
        //int n2= n*n;
        int mp = n / 2;
        for (int x = mp; x < m.width-mp; x++)
        {
            for (int y = mp; y < m.height-mp; y++)
            {
                double kernelSumR = 0.0;
                double kernelSumG = 0.0;
                double kernelSumB = 0.0;
                
                // Apply the filter, careful to handle near edges
                for (int i = 0; i < n; i++)
                {
                    for (int j = 0; j < n; j++)
                    {
                        // Be careful near the edges???
                        // Not sure about this, looks like col,row access????
                        //double intensity = this.data[x-mp+i][y-mp+j];
                        //Color c = this.get( x-mp+i, y-mp+j ) ;
                        //double intensity = Luminance.intensity(c);
                        
                        Color c = this.get( x-mp+i, y-mp+j );
                        
                        // Make so all are in [0.0, 1.0]
                        double r = c.getRed();
                        double g = c.getGreen();
                        double b = c.getBlue();
                        
                        //int intensity = this.getRGB(x-mp+i, y-mp+j);
                        
                        double kernelValue = kernel.get(i, j);
                        
                        // Kernel values are in [0.0,1.0] so 
                        // multiplying another number in [0.0,1.0] will
                        // always produce number in [0.0,1.0] 
                        kernelSumR += (r*kernelValue);
                        kernelSumG += (g*kernelValue);
                        kernelSumB += (b*kernelValue);
                    }
                }
                
                // We typically need to brighten after filter 
                //double bright = 16.0;
                //double bright = 1.0;
                
                /*
                 * kernel sums might be greater than 1.0 but not certain
                 * if division my proportional n^2 normalizes???
                 * We check if over 255 just in case.
                 */
                // Frankly not sure why I was dividing by n^2
                // Seems the weighted coefficients prevent exceeding 255.
                
//                double avgR = kernelSumR / n2;
//                double avgG = kernelSumG / n2;
//                double avgB = kernelSumB / n2;
                int sumRi = (int)kernelSumR;
                int sumGi = (int)kernelSumG;
                int sumBi = (int)kernelSumB;
                
                // Back in integer domain
                
                // Make sure within bounds to [0,255]
                if( sumRi > 255 ) sumRi = 255;
                if( sumGi > 255 ) sumGi = 255;
                if( sumBi > 255 ) sumBi = 255;
                
                //System.out.printf("(%d,%d) = [%d,%d,%d]\n", x, y, avgR, avgG, avgB );
                //Color c = new Color((int)kernelAverage, (int)kernelAverage, (int)kernelAverage);
                //m.set(x, y, c );
                //int bright = 16;
                
                //if( avgR > 0.0 ) System.out.printf("%.4f", avgR);
                // Keep track of the max intensity so we can scale after filter
                //if( avgRi > maxIntensity ) maxIntensity = avgRi;
                //if( avgGi > maxIntensity ) maxIntensity = avgGi;
                //if( avgBi > maxIntensity ) maxIntensity = avgBi;
                
                m.set(x, y, new Color(sumRi,sumGi,sumBi) );
                
                //m.data[x][y] = kernelAverage;
            }
        }

        // Set filename, not necedssary for this operation but useful
        // to remember the original filename for the produced picture
        //m.setFilename( this.filename );
        m.filename = this.filename;
        return m;
    }

    public Picture makeGray()
    {
        Picture g = new Picture(this.width, this.height);
        for (int x = 0; x < g.width; x++)
        {
            for (int y = 0; y < g.height; y++)
            {
                Color c = this.get( x, y );
                Color gray = Luminance.toGray(c);
                g.set(x, y, gray);
            }
        }
        return g;
    }

    public Picture replaceRandom( int numRandomPixels )
    {
        int[] pixels = new int[ this.width * this.height ];
        for( int i = 0 ; i < pixels.length; i++ ) pixels[i] = i;
        StdRandom.shuffle( pixels );
        TreeSet<Integer> randPixels = new TreeSet();
        for( int i = 0; i < numRandomPixels; i++ ) randPixels.add( pixels[i] );
        

        Picture p = new Picture(this.width, this.height);
        for (int x = 0; x < p.width; x++)
        {
            for (int y = 0; y < p.height; y++)
            {
                Color c = this.get( x, y );
                if( randPixels.contains(x*y) )
                {
                    int r = StdRandom.uniform( 0, 256 );
                    int g = StdRandom.uniform( 0, 256 );
                    int b = StdRandom.uniform( 0, 256 );
                    Color newColor = new Color( r, g, b );
                    p.set(x, y, newColor);
                }
                else
                {
                    p.set(x, y, c);
                }
            }
        }
        return p;
    }



   /**
     * Test client. Reads a picture specified by the command-line argument,
     * and shows it in a window on the screen.
     */
    public static void main(String[] args) {
        Picture pic = new Picture(args[0]);
        System.out.printf("%d-by-%d\n", pic.width(), pic.height());
        pic.show();
    }

}
