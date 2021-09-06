package imageProcess;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Image3D extends ImageObjectAbstract{

    private int lumi[][];

    public Image3D(){
        image = new int[3][1100][1020];
        BufferedImage bi = ImageProcess.randomRGB(1100,1020);
        for(int y = 0; y < bi.getHeight(); y++) {
            for (int x = 0; x < bi.getWidth(); x++) {
                int rgb = bi.getRGB(x, y);
                image[0][y][x] = (rgb >> 16) & 0xFF; //RED
                image[1][y][x] = (rgb >> 8) & 0xFF; //GREEN
                image[2][y][x] = (rgb >> 0) & 0xFF; //BLUE
            }
        }
        setLuminance();
    }
    public Image3D(int height, int width){
        image = new int[3][height][width];
    }

    @Override
    public int[][][] getImage() {
        return image;
    }

    @Override
    public int getHeight() {
        return image[0].length;
    }

    @Override
    public int getWidth() {
        return image[0][0].length;
    }

    @Override
    public int[] getPixel(int y, int x) throws IndexOutOfBoundsException {
        int[] rgb = new int[3];
        for(int i = 0; i < rgb.length; i++){
            rgb[i] = image[i][y][x];
        }
        return rgb;
    }

    @Override
    public BufferedImage toBI() {
        BufferedImage bi = new BufferedImage(image[0][0].length, image[0].length, BufferedImage.TYPE_INT_RGB);
        for(int y = 0; y < bi.getHeight(); y++){
            for(int x = 0; x < bi.getWidth(); x++){
                int rgb =
                        (image[0][y][x] << 16) |
                        (image[1][y][x] << 8) |
                        (image[2][y][x] << 0);
                bi.setRGB(x,y,rgb);
            }
        }
        return bi;
    }

    public void loadImage(int[][][] img){
        image = new int[3][img[0].length][img[0][0].length];
        for(int y = 0; y < img[0].length; y++) {
            for (int x = 0; x < img[0][0].length; x++) {
                for(int c = 0; c < 3; c++)
                image[c][y][x] = img[c][y][x];
            }
        }
        setLuminance();
    }

    @Override
    public void loadImage(String filename) throws IOException {
        loadImage(ImageIO.read(new File(filename)));
    }

    public void loadImage(BufferedImage bi){
        image = new int[3][bi.getHeight()][bi.getWidth()];
        for(int y = 0; y < bi.getHeight(); y++) {
            for (int x = 0; x < bi.getWidth(); x++) {
                int rgb = bi.getRGB(x, y);
                image[0][y][x] = (rgb >> 16) & 0xFF; //RED
                image[1][y][x] = (rgb >> 8) & 0xFF; //GREEN
                image[2][y][x] = (rgb >> 0) & 0xFF; //BLUE
            }
        }
        setLuminance();
    }

    @Override
    public void saveImage(String filename, String format) throws IOException {
        try {
            BufferedImage bi = this.toBI();
            File f = new File(filename + "." + format);
            ImageIO.write(bi, format, f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int[][] getComponent(COMPONENTS color) {
        return image[color.ordinal()];
    }

    // Point Operations

    @Override
    public int nonLineClamping(int n) {
        return Math.min(255, Math.max(0, n));
    }

    @Override
    public void applyTable(int[][] rgbTable){
        for(int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                for(int i = 0; i < 3; i++){
                    image[i][y][x] = rgbTable[i][image[i][y][x]];
                }
            }
        }
    }

    @Override
    public void add(int r, int g, int b) {
        int[][] rgbTable = new int[3][256];
        for(int i = 0; i < 256; i++){
            rgbTable[0][i] = nonLineClamping(i+r);
            rgbTable[1][i] = nonLineClamping(i+g);
            rgbTable[2][i] = nonLineClamping(i+b);
        }
        applyTable(rgbTable);
    }

    @Override
    public void subtract(int r, int g, int b) {
        add(-r, -g, -b);
    }

    @Override
    public void mutiply(double r, double g, double b) {
        int[][] rgbTable = new int[3][256];
        for(int i = 0; i < 256; i++){
            rgbTable[0][i] = nonLineClamping((int) (i*r));
            rgbTable[1][i] = nonLineClamping((int) (i*g));
            rgbTable[2][i] = nonLineClamping((int) (i*b));
        }
        applyTable(rgbTable);
    }

    @Override
    public void divide(double r, double g, double b) {
        if(r == 0 || g == 0 || b == 0){
            throw new ArithmeticException("Invalid Input, divide failed");
        }
        mutiply(1/r, 1/g, 1/b);
    }

    @Override
    public void contrastEnhancement(int low, int high) {
        if(low < 0 || high > 255 || low >= high){
            System.out.println("low "+low+" high "+high);
            throw new ArithmeticException("Invalid Input, contrastEnhancement failed");
        }
        int[] newP = new int[256];
        for(int i = 0; i < 256; i++){
            newP[i] = nonLineClamping((i - low) * 255 / (high-low));
        }
        applyTable(new int[][]{newP, newP, newP}); // same for rgb
    }

    public void autoContrastEnhancement(Histogram hist) {
        // find 5% low, 1% high on green
        int total = hist.getPixelCount();
        double[] greenCumHist = hist.getCumHisto()[1];
        int low = 0, high = 255;
        for(int i = 0; i < 256; i++){
            if(greenCumHist[i] >= 0.05){
                low = i;
                break;
            }
        }
        for(int i = 255; i >= 0; i--){
            if(greenCumHist[i] <= 0.99){
                high = i;
                break;
            }
        }
        System.out.println(low+" "+high);
        contrastEnhancement(low, high);
    }

    @Override
    public void gammaCorrection(double r, double g, double b) {
        if(r < 0 || g < 0 || b < 0){
            throw new ArithmeticException("Invalid gamma, gammaCorrection failed");
        }
        int[][] rgbTable = new int[3][256];
        for(int i = 0; i < 256; i++){
            double res =
                    rgbTable[0][i] = nonLineClamping((int) (255 * Math.pow((double)i / 255, r)));
            rgbTable[1][i] = nonLineClamping((int) (255 * Math.pow((double)i / 255, g)));
            rgbTable[2][i] = nonLineClamping((int) (255 * Math.pow((double)i / 255, b)));
        }
        applyTable(rgbTable);
    }

    @Override
    public void add(Image3D image2) {
        if(image2.getHeight() != getHeight() || image2.getWidth() != getWidth()){
            throw new ArithmeticException("Invalid Image, add failed");
        }
        for(int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                int[] img2Pixel = image2.getPixel(y,x);
                for(int i = 0; i < 3; i++){
                    image[i][y][x] = nonLineClamping((int) (image[i][y][x]+img2Pixel[i]));
                }
            }
        }
    }

    @Override
    public void subtract(Image3D image2) {
        if(image2.getHeight() != getHeight() || image2.getWidth() != getWidth()){
            throw new ArithmeticException("Invalid Image, subtract failed");
        }
        for(int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                int[] img2Pixel = image2.getPixel(y,x);
                for(int i = 0; i < 3; i++){
                    image[i][y][x] = nonLineClamping((int) (image[i][y][x]-img2Pixel[i]));
                }
            }
        }
    }

    @Override
    public void alphaBlending(double alpha, Image3D image2) {
        if(alpha < 0 || alpha > 1 || image2.getHeight() != getHeight() || image2.getWidth() != getWidth()){
            throw new ArithmeticException("Invalid Image, alphaBlending failed");
        }
        for(int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                int[] img2Pixel = image2.getPixel(y,x);
                for(int i = 0; i < 3; i++){
                    image[i][y][x] = nonLineClamping((int) (image[i][y][x]*alpha+img2Pixel[i]*(1-alpha)));
                }
            }
        }
    }

    @Override
    public void histogramEqualization(Histogram hist) {
        double[][] cumHisto = hist.getCumHisto();
        int[][] rgbTable = new int[3][256];
        for(int c = 0; c < 3; c++) {
            for (int i = 0; i < 256; i++) {
                rgbTable[c][i] = nonLineClamping((int)(cumHisto[c][i]*255));
            }
        }
        applyTable(rgbTable);
    }

    @Override
    public void histogramMatching(Histogram hist, Image3D image2) {
        Histogram hist2 = new Histogram(image2);
        hist2.setVisible(false);
        double[][] PA = hist.getCumHisto();
        double[][] PR = hist2.getCumHisto();
        int[][] rgbTable = new int[3][hist.getPixelCount()];
        for(int c = 0; c < 3; c++) {
            for (int a = 0; a < 256; a++) {
                int j = 256 - 1;
                do {
                    rgbTable[c][a] = j--;
                } while (j >= 0 && PA[c][a] <= PR[c][j]);
            }
        }
        hist2.dispose();
        applyTable(rgbTable);
    }

    @Override
    public void binarization(int threshold) {
        if(threshold < 0 || threshold > 255){
            throw new ArithmeticException("Invalid threshold, binarization failed");
        }
        int[][] rgbTable = new int[3][256];
        for(int c = 0; c < 3; c++){
            for(int i = 0; i < 256; i++){
                rgbTable[c][i] = i >= threshold ? 255 : 0;
            }
        }
        applyTable(rgbTable);
    }

    @Override
    public void autoBinarization(Histogram hist) {
        int threshold = getThresholdOtsu(hist);
        binarization(threshold);
    }

    public int getThresholdOtsu(Histogram hist) {
        // changed from http://www.labbookpages.co.uk/software/imgProc/otsuThreshold.html
        double sum = 0;
        int[] lumiHisto = new int[256];
        for(int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                lumiHisto[lumi[y][x]]++;
            }
        }
        System.out.println(Arrays.toString(lumiHisto));
        for (int t=0 ; t<256 ; t++) sum += t * lumiHisto[t];

        double sumB = 0;
        int weightB = 0, weightF;
        double max = 0;
        int threshold = 0;
        for (int i = 0 ; i < 256 ; i++) {
            weightB += lumiHisto[i];
            if (weightB == 0) continue;
            weightF = hist.getPixelCount() - weightB;
            if (weightF == 0) break;
            sumB += i * lumiHisto[i];
            double meanB = sumB / weightB;
            double meanF = (sum - sumB) / weightF;
            double varBetween = (double)weightB * weightF * (meanB - meanF) * (meanB - meanF);
            if (varBetween > max) {
                max = varBetween;
                threshold = i;
            }
        }
        System.out.println("Threshold: " + threshold);
        return threshold;
    }

    @Override
    public void luminance() {
        for(int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                for(int i = 0; i < 3; i++){
                    image[i][y][x] = nonLineClamping((int) lumi[y][x]);
                }
            }
        }
    }

    public void setLuminance() {
        double wR = 0.299, wG = 0.587, wB = 0.114;
        lumi = new int[getHeight()][getWidth()];
        for(int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                lumi[y][x] = (int)(image[0][y][x] * wR + image[1][y][x] * wG + image[2][y][x] * wB);
            }
        }
    }

    @Override
    public void desaturation(double s) {
        if(s < 0 || s > 1){
            throw new ArithmeticException("Invalid desat, desaturation failed");
        }
        for(int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                for(int i = 0; i < 3; i++){
                    image[i][y][x] = nonLineClamping((int)(lumi[y][x] + s * (image[i][y][x]-lumi[y][x])));
                }
            }
        }
    }


}
