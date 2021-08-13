import java.awt.Color;
import java.lang.reflect.Method;

/**
 * <P>
 * Basic image processing class for floating-point grayscale images.</P>
 *
 * <P>
 * Very little validation is performed, but it is sufficient for experimenting
 * along with MATLAB's Image Processing Toolbox. 
 * </P>
 *
 * <P>
 * License: this code is donated to the public domain. No warranty is made,
 * either express or implied about the suitability of this code for any purpose.
 * No liability for damage can extend to the author of this library. Use at your
 * own risk.</P>
 *
 * @author Tennessee Carmel-Veilleux (http://www.tentech.ca)
 */
public class Matrix
{

    /**
     * Contents of Matrix
     */
    private double[][] data;
    /**
     * Number of rows
     */
    private int nRows;
    /**
     * Number of columns
     */
    private int nCols;

    /**
     * Constructor from dimensions with no contents initialization
     * @param nRows - number of rows
     * @param nCols - number of columns
     */
    public Matrix(int nRows, int nCols)
    {
        this.data = new double[nRows][nCols];
        this.nRows = nRows;
        this.nCols = nCols;
    }
    
    /**
     * Constructor from dimensions with no contents initialization
     * @param data
     */
    public Matrix(double[][] data)
    {
        // CONSIDER MAKING DEFENSIVE COPY?
        this.data = data;
        this.nRows = data.length;
        this.nCols = data[0].length;
    }

    /**
     * Constructor from dimensions and initial value to fill
     * @param nRows - number of rows
     * @param nCols - number of columns
     * @param initValue - Initial value to fill in matrix
     */
    public Matrix(int nRows, int nCols, double initValue)
    {
        this(nRows, nCols);
        fill(initValue);
    }

    /**
     * Constructor to copy size and contents of another matrix
     * @param copy - Matrix to copy
     */
    public Matrix(Matrix copy)
    {
        this.nRows = copy.getRowSize();
        this.nCols = copy.getColumnSize();
        this.data = new double[nRows][nCols];
        for (int i = 0; i < this.data.length; i++)
        {
            System.arraycopy(copy.data[i], 0, this.data[i], 0, this.data[0].length);
        }
    }

    /**
     * Constructor to create Matrix from MATLAB file. Use the following MATLAB
     * command to save a file with the proper format from an existing matrix in
     * the workspace:
     * <pre>save filename VariableName -ASCII -TABS</pre>
     *
     * @param filename - filename to load
     */
    public Matrix(String filename)
    {
        loadFromFile(filename);
    }

    /**
     * @return number of rows in matrix
     */
    public int getRowSize()
    {
        return nRows;
    }
    
    public int height()
    {
        return this.getRowSize();
    }

    /**
     * @return number of columns in matrix
     */
    public int getColumnSize()
    {
        return nCols;
    }
    
    public int width()
    {
        return this.getColumnSize();
    }

    /**
     * Gets values at row and col.
     *
     * @param row - row index
     * @param col - column index
     * @return the element value at the specified position
     */
    public double get(int row, int col)
    {
        return data[row][col];
    }

    /**
     * Matrix accessor with linear (flat vector) indexing. Elements are stored
     * line by line contiguously, as in MATLAB.
     *
     * @param index - item index (start from 1)
     * @return
     */
    public double get(int index)
    {
        int row = index / this.nCols;
        int col = index % this.nCols;
        return data[row][col];
    }

    /**
     * Matrix setter with row-column (algebraic) indexing
     *
     * @param row - Row index (start from 1)
     * @param col - Column index (start from 1)
     * @param value
     */
    public void set(int row, int col, double value)
    {
        this.data[row][col] = value;
    }

//    /**
//     * Matrix setter with linear (flat vector) indexing. Elements are stored
//     * line by line contiguously, as in MATLAB.
//     *
//     * @param index - item index (start from 1)
//     * @param value - value to put at specified position
//     */
//    public void set(int index, double value)
//    {
//        int row = index / this.nCols;
//        int col = index % this.nCols;
//        this.data[row][col] = value;
//    }

    /**
     * Fill all matrix elements with the same value
     * @param value - value to store in every matrix location
     */
    public void fill(double value)
    {
        for (int i = 0; i < this.data.length; i++)
        {
            for (int j = 0; j < this.data[0].length; j++)
            {
                this.data[i][j] = value;
            }
        }
    }

//    /**
//     * Package-wide helper accessor for internal array. <B>Do not use unless you
//     * understand the code of the entire class.</B>
//     * @return the flat Java array containing all the items
//     */
//    public double[] getArray()
//    {
//        double[] temp = new double[nRows*nCols];
//        for (int i = 0; i < nRows; i++)
//        {
//            System.arraycopy(data[i], 0, temp, i*nCols, nCols);
//        }
//        return temp;
//    }
    
    /**
     * Gets the minimum value in the matrix.
     * @return 
     */
    public double min()
    {
        double min = this.data[0][0];
        for (int i = 0; i < data.length; i++)
        {
            for (int j = 0; j < data[0].length; j++)
            {
                double value = data[i][j];
                if( value < min ) min = value;
            }
        }
        return min;
    }
    
    /**
     * Gets the minimum value in the matrix.
     * @return 
     */
    public double max()
    {
        double max = this.data[0][0];
        for (int i = 0; i < data.length; i++)
        {
            for (int j = 0; j < data[0].length; j++)
            {
                double value = data[i][j];
                if( value > max ) max = value;
            }
        }
        return max;
    }
    
    private void compatible( Matrix other )
    {
        // Validate parameters
        if (other.nCols != this.nCols || other.nRows != this.nRows )
        {
            throw new IllegalArgumentException("Matrices are not compatible");
        }
    }

    /**
     * Add a constant to the matrix.
     * @param value - value to add
     * @return a new matrix with the result
     */
    public Matrix add(double value)
    {
        Matrix m = new Matrix(this);
        // Add constant to every item
        for (int i = 0; i < data.length; i++)
        {
            for (int j = 0; j < data[0].length; j++)
            {
                m.data[i][j] += value;
            }
        }
        return m;
    }

    /**
     * Add a matrix element-wise to this one.
     * @param matrix - matrix to add
     * @return a new matrix with the result
     */
    public Matrix add(Matrix matrix)
    {
        // Validate parameters
        this.compatible(matrix);
        
        Matrix m = new Matrix(this);

        // Add items elementwise
        for (int i = 0; i < data.length; i++)
        {
            for (int j = 0; j < data[0].length; j++)
            {
                m.data[i][j] += matrix.data[i][j];
            }
        }

        return m;
    }

    /**
     * Substract a constant from the matrix.
     * @param value - value to substract
     * @return a new matrix with the result
     */
    public Matrix substract(double value)
    {
        Matrix m = new Matrix(this);
        // Add constant to every item
        for (int i = 0; i < data.length; i++)
        {
            for (int j = 0; j < data[0].length; j++)
            {
                m.data[i][j] -= value;
            }
        }
        return m;
    }

    /**
     * Substract all elements from a constant (ie: A = 1.0 - B), element-wise.
     *
     * @param value - value from which to substract
     * @return a new matrix with the result
     */
    public Matrix substractThisFrom(double value)
    {
        Matrix m = new Matrix(nRows, nCols, 0.0);

        // Substract constant from every item
        for (int i = 0; i < data.length; i++)
        {
            for (int j = 0; j < data[0].length; j++)
            {
                m.data[i][j] = value - this.data[i][j];
            }
        }

        return m;
    }

    /**
     * Substract a matrix element-wise from this one
     *
     * @param matrix - matrix to substract
     * @return a new matrix with the result
     */
    public Matrix substract(Matrix matrix)
    {
        Matrix m = new Matrix(this);

        // Validate parameters
        if (matrix.nCols != this.nCols || matrix.nRows != this.nRows )
        {
            throw new IllegalArgumentException("Matrix dimensions must match");
        }

        // Add items elementwise
        for (int i = 0; i < data.length; i++)
        {
            for (int j = 0; j < data[0].length; j++)
            {
                m.data[i][j] -= matrix.data[i][j];
            }
        }

        return m;
    }

    /**
     * Multiply the matrix with a constant.
     * @param value - value to multiply
     * @return a new matrix with the result
     */
    public Matrix multiply(double value)
    {
        Matrix m = new Matrix(this);
        // Multiply every item by constant
        for (int i = 0; i < data.length; i++)
        {
            for (int j = 0; j < data[0].length; j++)
            {
                m.data[i][j] = data[i][j] * value;
            }
        }
        return m;
    }

    /**
     * Multiply a matrix element-wise with this one (ie: result = this .* matrix)
     * @param matrix - matrix to multiply
     * @return a new matrix with the result
     */
    public Matrix multiplyElements(Matrix matrix)
    {
        // Validate parameters
        this.compatible(matrix);
        
        Matrix m = new Matrix(nRows, nCols, 0.0);
        // Multiply items elementwise
        for (int i = 0; i < data.length; i++)
        {
            for (int j = 0; j < data[0].length; j++)
            {
                m.data[i][j] = data[i][j] * matrix.data[i][j];
            }
        }
        return m;
    }

    /**
     * Divide the matrix by a constant.
     *
     * @param divisor - value by which to divide
     * @return a new matrix with the result
     */
    public Matrix divide(double divisor)
    {
        Matrix m = new Matrix(this);
        // Divide every item by constant
        for (int i = 0; i < data.length; i++)
        {
            for (int j = 0; j < data[0].length; j++)
            {
                m.data[i][j] /= divisor;
            }
        }
        return m;
    }

    /**
     * Divide this matrix element-wise with the argument (ie: result = this ./
     * matrix)
     *
     * @param divisor - matrix by which to divide
     * @return a new matrix with the result
     */
    public Matrix divideElements(Matrix divisor)
    {
        // Validate parameters
        this.compatible(divisor);
        
        Matrix m = new Matrix(nRows, nCols, 0.0);
        // Divide items elementwise
        for (int i = 0; i < data.length; i++)
        {
            for (int j = 0; j < data[0].length; j++)
            {
                m.data[i][j] = this.data[i][j] / divisor.data[i][j];
            }
        }
        return m;
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
    public Matrix applyFilter(Matrix kernel)
    {
        // We do not zero pad or properly filter near edges.
        // Instead, filtering starts at half kernel width.
        // Iterate over matrix
        
        Matrix m = new Matrix(nRows, nCols);
        
        if( kernel.nCols != kernel.nRows )
        {
            String e = "Kernel must be symetric";
            throw new IllegalArgumentException(e);
        }
        
        //int height = this.data.length;
        //int width  = this.data[0].length;
        
        int width = this.data.length;
        int height = this.data[0].length;
        
        int n = kernel.nRows;
        int n2= n*n;
        int mp = n / 2;
        for (int x = mp; x < width-mp; x++)
        {
            for (int y = mp; y < height-mp; y++)
            {
                double kernelSum = 0.0;
                // Apply the filter, careful to handle near edges
                for (int i = 0; i < n; i++)
                {
                    for (int j = 0; j < n; j++)
                    {
                        // Be careful near the edges???
                        // Not sure about this, looks like col,row access????
                        double intensity = this.data[x-mp+i][y-mp+j];
                        double kernelValue = kernel.data[i][j];
                        kernelSum += (intensity*kernelValue);
                    }
                }
                double kernelAverage = kernelSum / n2;
                m.data[x][y] = kernelAverage;
            }
        }

        return m;
    }

    /**
     * <B>Warning: this might not be numerically stable for very large matrices
     * since it does not ensure even separation in order-of-magnitude domains.
     * It does however use a higher resolution (double) accumulator.
     *
     * @return the elementwise sum of all the elements in this matrix
     */
    public double sum()
    {
        double theSum = 0.0;
        // Add items Together
        for (int i = 0; i < data.length; i++)
        {
            for (int j = 0; j < data[0].length; j++)
            {
                theSum += this.data[i][j];
            }
        }
        return theSum;
    }

    /**
     * Applies a method element-wise to this matrix. The method must have
     * a "methodName(double arg)" signature, which means a single double
     * return value and a single double argument. This is very useful to apply a
     * Java.Math operation. In case of an exception occuring, all elements are
     * filled with Double.NaN.
     *
     * <P>
     * Example:</P>
     * <pre>
     * // Apply a sine function to every element
     * MatrixF result = m.applyMethod(getMathOperation("sin"));
     *
     * // Apply the "double MyClassName.MyMethodName(double arg)" to every element
     * MatrixF result2 = m.applyMethod(getDoubleOperation(MyClassName, "MyMethodName");
     * </pre>
     *
     * @param method - Method to apply to every element
     * @return a new matrix with the result
     */
    public Matrix applyMethod(Method method)
    {
        Matrix m = new Matrix(this);
        
        Double[] input = new Double[1];

        try
        {
            // Apply method elementwise
            for (int i = 0; i < data.length; i++)
            {
                for (int j = 0; j < data[0].length; j++)
                {
                    input[0] = data[i][j];
                    m.data[i][j] = ((Double) (method.invoke(this, (Object[]) input)));
                }
            }
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
            m.fill(Double.NaN);
        }

        return m;
    }

    /**
     * Extracts a method from the Java.Math class by name for use with
     * applyMethod().
     *
     * @param name - name of the method to extract (ie: "sin", "exp", "floor",
     * etc)
     * @return the Method instance for the selected operation
     */
    static public Method getMathOperation(String name)
    {
        return getDoubleOperation(Math.class, name);
    }

    /**
     * Extracts a method by name from an arbitrary class for use with
     * applyMethod().
     *
     * @param name - name of the method to extract (ie: "myFloatMethod")
     * @param theClass - class to extract from
     * @return the Method instance for the selected operation
     */
    @SuppressWarnings("unchecked")
    static public Method getDoubleOperation(Class theClass, String name)
    {
        try
        {
            Class[] parameters = new Class[1];
            parameters[0] = Double.TYPE;

            Method m = theClass.getMethod(name, parameters);
            return m;
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
            return null;
        }
    }

    /**
     * Applies the atan2() function element-wise between two matrices.
     *
     * @param num - Numerator of the arctangent
     * @param denom - Denominator of the arctangent
     * @return a new matrix with the result
     */
    static public Matrix atan2(Matrix num, Matrix denom)
    {
        // Validate parameters
        if ( num.nCols != denom.nCols || num.nRows != denom.nRows )
        {
            throw new IllegalArgumentException("Matrix dimensions must match");
        }

        // Create output Matrix
        Matrix m = new Matrix( num.nRows, num.nCols );
        // Apply atan2 elementwise
        for (int i = 0; i < num.nRows; i++)
        {
            for (int j = 0; j < num.nCols; j++)
            {
                m.data[i][j] = Math.atan2( num.data[i][j], denom.data[i][j] );
            }
        }
        return m;
    }

    /**
     * <P>
     * Applies a logical operation elementwise between a matrix and a constant
     * value. In case of the unary not operator (!), the value is ignored.
     * Result matrix contains 1.0f when condition is true, 0.0f when false.
     * Order of operation is: result = source (OP) value. Be aware that to do:
     * result = value (OP) source, you will have to reverse the operation.</P>
     *
     * <P>
     * Operations can be one of: "<", "<=", "==", "!=", ">=", ">", "!"</P>
     *
     * <P>
     * If the operation is not recognized, the result matrix is filled with
     * Double.NaN.</P>
     * <P>
     * Example: MatrixF result = logicalOp(A, ">=", 3.3f); // result = (A >=
     * 3.3); in MATLAB</P>
     *
     * @param source - source matrix on which to operate (left-hand-side of
     * operator)
     * @param operation - string containing the Java-syntax logical operation
     * @param value - constant to use (right-hand-side of operator)
     * @return a new matrix with the result
     */
    static public Matrix logicalOp(Matrix source, String operation, double value)
    {
        // Initialize matrices
        Matrix out = new Matrix(source.getRowSize(), source.getColumnSize());
        int rows = source.data.length;
        int cols = source.data[0].length;
        double[][] sa = source.data; // alias for shortness
        double[][] oa = out.data;

        // Apply operations elementwise
        if (operation.equals("<"))
        {
            for (int i = 0; i < rows; i++)
            {
                for (int j = 0; j < cols; j++)
                {
                    oa[i][j] = (sa[i][j] < value) ? 1.0 : 0.0;
                }
            }
        }
        else if (operation.equals("<="))
        {
            for (int i = 0; i < rows; i++)
            {
                for (int j = 0; j < cols; j++)
                {
                    oa[i][j] = (sa[i][j] <= value) ? 1.0 : 0.0;
                }
            }
        }
        else if (operation.equals("=="))
        {
            for (int i = 0; i < rows; i++)
            {
                for (int j = 0; j < cols; j++)
                {
                    oa[i][j] = (sa[i][j] == value) ? 1.0 : 0.0;
                }
            }
        }
        else if (operation.equals("!="))
        {
            for (int i = 0; i < rows; i++)
            {
                for (int j = 0; j < cols; j++)
                {
                    oa[i][j] = (sa[i][j] != value) ? 1.0 : 0.0;
                }
            }
        }
        else if (operation.equals(">="))
        {
            for (int i = 0; i < rows; i++)
            {
                for (int j = 0; j < cols; j++)
                {
                    oa[i][j] = (sa[i][j] >= value) ? 1.0 : 0.0;
                }
            }
        }
        else if (operation.equals(">"))
        {
            for (int i = 0; i < rows; i++)
            {
                for (int j = 0; j < cols; j++)
                {
                    oa[i][j] = (sa[i][j] > value) ? 1.0 : 0.0;
                }
            }
        }
        else if (operation.equals("!"))
        {
            for (int i = 0; i < rows; i++)
            {
                for (int j = 0; j < cols; j++)
                {
                    oa[i][j] = (sa[i][j] == value) ? 1.0 : 0.0;
                }
            }
        }
        else
        {
            out.fill(Double.NaN);
        }

        return out;
    }

    /**
     * <P>
     * Applies a logical operation elementwise between this matrix (as operator
     * left-hand-side) and a constant value. In case of the unary not operator
     * (!), the value is ignored. Result matrix contains 1.0f when condition is
     * true, 0.0f when false. Order of operation is: result = source (OP) value.
     * Be aware that to do: result = value (OP) source, you will have to reverse
     * the operation.</P>
     *
     * <P>
     * Operations can be one of: "<", "<=", "==", "!=", ">=", ">", "!"</P>
     *
     * <P>
     * If the operation is not recognized, the result matrix is filled with
     * Double.NaN.</P>
     *
     * <P>
     * Example: MatrixF result = logicalOp(">=", 3.3f); // result = (this >=
     * 3.3); in MATLAB</P>
     *
     * @param operation - string containing the Java-syntax logical operation
     * @param value - constant to use (right-hand-side of operator)
     * @return a new matrix with the result
     */
    public Matrix logicalOp(String operation, double value)
    {
        return logicalOp(this, operation, value);
    }

    /**
     * <P>
     * Loads the contents of this matrix from a MATLAB-generated single-matrix
     * ASCII text file. Dimensions of this matrix are adjusted to those of the
     * loaded matrix.</P>
     * <P>
     * Use the following MATLAB command to save a file with the proper format
     * from an existing matrix in the workspace:</P>
     * <pre>save filename VariableName -ASCII -TABS</pre>
     * <P>
     * If there is a problem loading the matrix from file, the resulting matrix
     * is of size 0,0.</P>
     *
     * @param filename - Filename to load (MATLAB format)
     */
    private void loadFromFile(String filename)
    {
        Picture picture = new Picture(filename);
        
        this.nRows = picture.height();
        this.nCols = picture.width();

        // Validate file format
        if (this.nRows == 0)
        {
            throw new RuntimeException("No valid lines in file '" + filename + "'");
        }

        if (this.nCols == 0)
        {
            throw new RuntimeException("First line of file contains no columns in '" + filename + "'");
        }

        // Create array
        this.data = new double[nRows][nCols];
        for (int r = 0; r < nRows; r++)
        {
            for (int c = 0; c < nCols; c++)
            {
                int value = picture.getRGB(c, r);
                this.data[r][c] = value;
            }
        }
    }
    
    
    //------------------------------------------------------------------------//
    // Integrated Picture class for showing and saving.  Though do not like
    //------------------------------------------------------------------------//
    public Picture makePicture()
    {
        // We have to do some scaling so that the image shows something.
        double min = this.min();
        double max = this.max();
        
        double range = max - min;
        if( range == 0.0 ) range = 0.0;
        double scale = 255.0 / range;
        
        //System.out.println("scale=" + scale + " for " + filename);
        
        Picture picture = new Picture( this.nCols, this.nRows );
        for (int row = 0; row < nRows; row++)
        {
            for (int col = 0; col < nCols; col++)
            {
                double value = this.data[row][col];
                // MIGHT NEED TO SCALE
                
                // make in range (works for negative min as well)
                value = value - min;
                value = value * scale;
                
                // HACK!!! REMOVE
                //if( value > 1.0 ) value = 1.0;
                
                //int v = (int)(value*128.0);
                
                // Truncate top values
                int v = (int)value;
                if( v > 255 ) v = 255;
                if( v < 0   ) v = 0;
                
                Color c = new Color( v, v, v );
                picture.set(col, row, c);
            }
        }
        return picture;
    }
    

    /**
     * Saves the current matrix to a text file format loadable by MATLAB's load
     * command.
     *
     * @param filename - destination filename
     */
    public void saveToFile(String filename)
    {
        Picture picture = this.makePicture();
        picture.save(filename);
    }
    
    public void show()
    {
        Picture picture = this.makePicture();
        picture.show();
    }
    
    //------------------------------------------------------------------------//
    
    /**
     * Crops a portion of the matrix (more typically picture operation).
     * @param x (col)
     * @param y (row)
     * @param width
     * @param height
     * @return specified subset of this matrix
     */
    public Matrix crop( int x, int y, int width, int height )
    {
        Matrix cropped = new Matrix( height, width );
        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                double v = this.get(y+i, x+j);
                cropped.set(i, j, v);
            }
        }
        return cropped;
    }
    
    public String info()
    {
        return "cols=" + this.nCols + " rows=" + this.nRows +
                " min=" + this.min() + " max=" + this.max();
    }

    /**
     * (non-Javadoc)
     * @return 
     */
    @Override
    public String toString()
    {
        String result = "";

        double total = 0.0;
        // "Pretty-print" matrix. This is only pretty for small matrices.
        for (int i = 0; i < nRows; i++)
        {
            for (int j = 0; j < nCols; j++)
            {
                //result += String.format("%10.6f ", data[i * nCols + j]);
                result += String.format("%10.6f ", data[i][j]);
                total += data[i][j];
            }
            result += "\n";
        }
        //result += String.format("Total %.3f \n", total);
        return result;
    }

    /**
     * Basic test routine (does not check much, since I have not kept track of
     * all my tests).
     * @param args
     */
    public static void main(String[] args)
    {
        String filename = args[0];
        Matrix m = new Matrix(filename);
        Matrix m3 = new Matrix(2, 2, 0.0);
        m3.set(2, 2, 4.0f);
        m3.set(1, 1, -0.5f);
        m3.set(1, 2, 0.5f);
        m3.set(2, 1, -0.5f);
        m3.set(2, 2, 0.5f);

        Matrix m2 = m.applyFilter(m3);
        filename = filename.replace(".png", "_FILTERED.png");
        m2.saveToFile(filename);
    }
}

