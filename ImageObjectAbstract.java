package imageProcess;

import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class ImageObjectAbstract {
	
	public static enum COMPONENTS {RED, GREEN, BLUE};
	
	protected int image[][][];
	
	// -- include constructors as you see fit

	// -- returns a reference to the 3D image
	public abstract int[][][] getImage();
	public abstract int getHeight();
	public abstract int getWidth();
	
	// -- returns an int[3] containing the R, G, and B values at location img[][y][x]
	//    throws IndexOutOfBoundsException if y or x are out of bounds
	public abstract int[] getPixel(int y, int x) throws IndexOutOfBoundsException;
	
	// -- converts the image[][][] to a BufferedImage and returns the BufferedImage
	public abstract BufferedImage toBI(); 

	// -- reads an image from a file into image[][][]
	//    throws IOException if the file fails for any reason
	public abstract void loadImage (String filename) throws IOException;

	// -- saves image[][][] to a file
	//    throws IOException if the file fails for any reason
	public abstract void saveImage (String filename, String format) throws IOException;

	// -- returns individual color component, use enum to specify which
	//    color component is to be returned.
	public abstract int[][] getComponent(COMPONENTS color);

	// Point Operations
	public abstract int nonLineClamping(int n);
	public abstract void applyTable(int[][] rgbTable);

	public abstract void add(int r, int g, int b);
	public abstract void subtract(int r, int g, int b);
	public abstract void mutiply(double r, double g, double b);
	public abstract void divide(double r, double g, double b);
	public abstract void contrastEnhancement(int low, int high);
	public abstract void autoContrastEnhancement(Histogram hist);
	public abstract void add(Image3D image2);
	public abstract void subtract(Image3D image2);
	public abstract void gammaCorrection(double r, double g, double b);
	public abstract void alphaBlending(double alpha, Image3D image2);

	// Color Operations
	public abstract void histogramEqualization(Histogram hist);
	public abstract void histogramMatching(Histogram hist, Image3D image2);
	public abstract void binarization(int threshold);
	public abstract void autoBinarization(Histogram hist);
	public abstract void luminance();
	public abstract void desaturation(double s);
}
