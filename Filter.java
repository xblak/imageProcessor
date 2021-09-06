package imageProcess;

import java.io.*;
import java.util.*;

public class Filter {

    public enum Border {
        IGNORE,
        COPY,
        MIRROR
    }
    Border border = Border.COPY; // default ignore
    private double[][] kernel;
    private int[] origin;

    public void setBorder(int n){
        border = Border.values()[n];
    }

    public int[][] medianFilter(int[][] in, int shapeX, int shapeY){
        if(shapeX % 2 == 0 || shapeY % 2 == 0) throw new ArithmeticException("Invalid shape, Median Filter failed");
        int row = in.length, col = in[0].length;
        int shapeSize = shapeX*shapeY;
        int arrIndex = shapeSize/2;
        shapeX /= 2;
        shapeY /= 2;
        int[][] out = new int[row][col];
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                int[] arr = new int[shapeSize];
                int index = 0;
                for(int k = -shapeX; k <= shapeX; k++){
                    for(int l = -shapeY; l <= shapeY; l++){
                        arr[index++] = getPixel(in, i+k, j+l);
                    }
                }
                Arrays.sort(arr);
                out[i][j] = arr[arrIndex];
            }
        }
        return out;
    }

    public int[][] outlierFilter(int[][] in, int shapeX, int shapeY, int threshold){
        if(shapeX % 2 == 0 && shapeY % 2 == 0) throw new ArithmeticException("Invalid shape, Outlier Filter failed");
        int row = in.length, col = in[0].length;
        int shapeSize = shapeX*shapeY;
        shapeX /= 2;
        shapeY /= 2;
        int[][] out = new int[row][col];
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                double sum = 0;
                for(int k = -shapeX; k <= shapeX; k++){
                    for(int l = -shapeY; l <= shapeY; l++){
                        sum += getPixel(in, i+k, j+l);
                    }
                }
                double mean = (sum / shapeSize);
                // diff > threshold ? mean : cur
                out[i][j] = Math.abs(in[i][j]-mean) > threshold ? (int) mean : in[i][j];
            }
        }
        return out;
    }

    public int[][] GaussianFilter(int[][] in, int shapeX, int shapeY, double sigmax, double sigmay){
        if(shapeX % 2 == 0 || shapeY % 2 == 0) throw new ArithmeticException("Invalid shape, Gaussian Filter failed");
        kernel = new double[shapeX][shapeY];
        origin = new int[]{shapeX / 2, shapeY / 2};
        for(int x = -shapeX/ 2; x <= shapeX / 2; ++x) {
            for(int y = -shapeY/ 2; y<= shapeY/ 2; ++y) {
                kernel[x + shapeX / 2][y + shapeY / 2] = Math.exp(-(((x * x) / (2 * sigmax* sigmax)) + ((y * y) / (2 * sigmay* sigmay))));
            }
        }
        return convolution(in);
    }

    public int[][] convolution(int[][] in){
        int row = in.length, col = in[0].length;
        double[][] out = new double[row][col];
        int kRow = kernel.length-1, kCol = kernel[0].length-1;
        int down = kRow-origin[0], right = kCol-origin[1], up = down-kRow, left = right-kCol;
        // origin
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                // kernel
                double sum = 0;
                for(int k = up; k <= down; k++){
                    for(int l = left; l <= right; l++){
                        sum += getPixel(in, i+k, j+l) * kernel[k-up][l-left];
                    }
                }
                out[i][j] = sum;
            }
        }
        return linearScale(out);
    }

    public int getPixel(int[][] in, int row, int col){
        //System.out.println("Start"+row+":"+col);
        boolean rIn = 0 <= row && row < in.length;
        boolean cIn = 0 <= col && col < in[0].length;
        if(rIn && cIn) {
            // pixel in border
        }else if(border == Border.IGNORE && (!rIn || !cIn)){
            return 0;
        }else if(border == Border.COPY){
            if(!rIn){
                if(row < 0) {
                    row = 0;
                } else {
                    row = in.length-1;
                }
            }
            if(!cIn){
                if(col < 0) {
                    col = 0;
                } else {
                    col = in[0].length-1;
                }
            }
        }else if(border == Border.MIRROR){
            if(!rIn){
                if(row < 0) {
                    row = Math.abs(row+1);
                } else {
                    row = 2*in.length-row-1;
                }
            }
            if(!cIn){
                if(col < 0) {
                    col = Math.abs(col+1);
                } else {
                    col = 2*in[0].length-col-1;
                }
            }
        }
        //System.out.println("getting"+row+":"+col);
        return in[row][col];
    }

    public int[][] linearScale(double[][] in){
        double min = in[0][0], max = in[0][0];
        for(double[] row : in){
            for(double val : row){
                min = Math.min(min, val);
                max = Math.max(max, val);
            }
        }
        int[][] out = new int[in.length][in[0].length];
        for(int i = 0; i < in.length; i++){
            for(int j = 0; j < in[0].length; j++){
                out[i][j] = (int) ((in[i][j]-min)*255.0/(max-min));
            }
        }
        return out;
    }

    public void readKernel(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String s = reader.readLine();
        // read dim
        if(s != null){
            String[] dim = s.split(" ");
            kernel = new double[Integer.parseInt(dim[0])][Integer.parseInt(dim[1])];
        }
        // read kernel
        for(int i = 0; i < kernel.length; i++){
            s = reader.readLine();
            String[] row = s.split(" ");
            for(int j = 0; j < row.length; j++){
                kernel[i][j] = Double.parseDouble(row[j]);
            }
        }
        // read origin
        s = reader.readLine();
        if(s != null){
            String[] o = s.split(" ");
            origin = new int[]{Integer.parseInt(o[0]),Integer.parseInt(o[1])};
        }
        for(double[] kLine : kernel){
            System.out.println(Arrays.toString(kLine));
        }
        System.out.println(Arrays.toString(origin));
    }

}
