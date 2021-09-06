package imageProcess;

import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.*;
import javax.swing.*;

// based on https://rosettacode.org/wiki/Hough_transform
public class HoughTransform {
    public static int[][] houghTransform(int[][] inputData, int thetaAxisSize, int rAxisSize) {
        int width = inputData.length;
        int height = inputData[0].length;
        int maxRadius = (int) Math.ceil(Math.hypot(width, height));
        int halfRAxisSize = rAxisSize >>> 1;
        int[][] outputData = new int[thetaAxisSize][rAxisSize];
        // x output ranges from 0 to pi
        // y output ranges from -maxRadius to maxRadius
        double[] sinTable = new double[thetaAxisSize];
        double[] cosTable = new double[thetaAxisSize];
        for (int theta = thetaAxisSize - 1; theta >= 0; theta--) {
            double thetaRadians = theta * Math.PI / thetaAxisSize;
            sinTable[theta] = Math.sin(thetaRadians);
            cosTable[theta] = Math.cos(thetaRadians);
        }

        for (int x = width - 1; x >= 0; x--) {
            for (int y = height - 1; y >= 0; y--) {
                if (threshold(inputData, x, y)) {
                    for (int theta = thetaAxisSize - 1; theta >= 0; theta--) {
                        double r = cosTable[theta] * x + sinTable[theta] * y;
                        // scale radius to fit the output image better
                        int rScaled = (int) Math.round(r * halfRAxisSize / maxRadius) + halfRAxisSize;
                        outputData[theta][rScaled]++;
                    }
                }
            }
        }
        return outputData;
    }

    public static boolean threshold(int[][] inputData, int x, int y) {
        int centerValue = inputData[x][y];
        for (int i = 1; i >= -1; i--) {
            for (int j = 1; j >= -1; j--) {
                // 3x3 kernel to check if the difference of current point vs other points in kernel is bigger than threshold
                int newx = x + i;
                int newy = y + j;
                if ((newx < 0) || (newx >= inputData.length) || (newy < 0) || (newy >= inputData[0].length))
                    continue;
                if (Math.abs(inputData[newx][newy] - centerValue) >= 80) // detect if difference is bigger than threshold
                    return true;
            }
        }
        return false;
    }

    public static void writeOutputImage(String filename, int[][] outputData) throws IOException {
        // find max, and we know min is 0
        int max = 0;
        for (int[] row : outputData){
            for (int i : row){
                max = Math.max(max, i);
            }
        }
        BufferedImage outputImage = new BufferedImage(outputData[0].length, outputData.length, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < outputData[0].length; x++) {
            for (int y = 0; y < outputData.length; y++) {
                // linear scaling to 0 - 255 for display
                int n = (int) (outputData[x][y] * 255.0 / max);
                // flip y back for output
                outputImage.setRGB(x, outputData.length - 1 - y, (n << 16) | (n << 8) | n | 0xFF000000);
                // draw red line at 180 degree for reference
                if (x == 180) outputImage.setRGB(x, outputData.length - 1 - y, Color.red.getRGB()); 
            }
        }
        ImageIO.write(outputImage, "PNG", new File(filename));
        return;
    }

    public static void main(String[] args) throws IOException {
        // load the image
        Image3D noisyWall = new Image3D();
        noisyWall.loadImage("C:\\Users\\blake\\Desktop\\test1.png");

        // use median filter to clean up some noise
        Filter filter = new Filter();
        filter.setBorder(2);
        int[][][] newImg = new int[3][noisyWall.getHeight()][noisyWall.getWidth()];
        newImg[0] = filter.medianFilter(noisyWall.getComponent(ImageObjectAbstract.COMPONENTS.RED), 3, 3);
        newImg[1] = filter.medianFilter(noisyWall.getComponent(ImageObjectAbstract.COMPONENTS.GREEN), 3, 3);
        newImg[2] = filter.medianFilter(noisyWall.getComponent(ImageObjectAbstract.COMPONENTS.BLUE), 3, 3);
        noisyWall.loadImage(newImg);
        noisyWall.saveImage("C:\\Users\\blake\\Desktop\\cleanerWall", "png");

        // canny edge detection
        BufferedImage bi = noisyWall.toBI();
        System.out.println(bi.getHeight() + "x" + bi.getWidth() + " : " + bi.getType());

        // -- BufferedImage -> int[][][]
        int RGB[][][] = new int[3][bi.getHeight()][bi.getWidth()];
        for (int y = 0; y < bi.getHeight(); ++y) {
            for (int x = 0; x < bi.getWidth(); ++x) {
                int argb = bi.getRGB(x, y);
                RGB[0][y][x] = (argb >> 16) & 0xFF; // -- RED
                RGB[1][y][x] = (argb >> 8) & 0xFF; // -- GREEN
                RGB[2][y][x] = (argb >> 0) & 0xFF; // -- BLUE
            }
        }
        int lines = RGB[0].length;
        int samples = RGB[0][0].length;

        Canny canny = new Canny(RGB[0]);

        // -- set very low thresholds just to see what happens
        int t0 = Integer.parseInt("0");
        int t1 = Integer.parseInt("1");
        canny.SetThresholds(t0, t1);

        int[][] edges = canny.Apply();

        for (int i = 0; i < lines; ++i) {
            for (int j = 0; j < samples; ++j) {
                RGB[0][i][j] = (edges[i][j] == 1) ? 255 : 0;
            }
        }

        for (int i = 0; i < lines; ++i) {
            for (int j = 0; j < samples; ++j) {
                RGB[1][i][j] = RGB[2][i][j] = RGB[0][i][j];
            }
        }
        // -- int[][][] -> BufferedImage
        BufferedImage biout = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);
        int[][] data = new int[bi.getWidth()][bi.getHeight()];
        for (int y = 0; y < bi.getHeight(); ++y) {
            for (int x = 0; x < bi.getWidth(); ++x) {
                int rgb =
                        (RGB[0][y][x] << 16) |
                                (RGB[1][y][x] << 8) |
                                (RGB[2][y][x] << 0);
                biout.setRGB(x, y, rgb);
                // flip y axis to do calculation
                data[x][bi.getHeight() - 1 - y] = RGB[1][y][x];
            }
        }
        noisyWall.loadImage(biout);
        noisyWall.saveImage("C:\\Users\\blake\\Desktop\\edgeOfWall", "png");

//        noisyWall.loadImage("C:\\Users\\blake\\Desktop\\greytest2.png");
//        //data = noisyWall.getComponent(ImageObjectAbstract.COMPONENTS.GREEN);
//        for (int y = 0; y < bi.getHeight(); ++y) {
//            for (int x = 0; x < bi.getWidth(); ++x) {
//                data[x][bi.getHeight() - 1 - y] = noisyWall.getComponent(ImageObjectAbstract.COMPONENTS.GREEN)[y][x];
//            }
//        }
        int[][] outputData = houghTransform(data, 360, 360);
        writeOutputImage("C:\\Users\\blake\\Desktop\\houghSpace.png", outputData);
    }

}