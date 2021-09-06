package imageProcess;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class Histogram extends JFrame {
    public enum COMPONENTS {RED, GREEN, BLUE};
    private HistogramInner hisIn;
    private StaticsInner StatIn;

    private int[][] COLOR = new int[3][256];
    private double[][] cumHisto = new double[3][256];
    private int height, width;

    public Histogram(Image3D img) {
        setTitle("Histogram");
        setSize(270, 200);
        setLayout(new BorderLayout());
        loadImg(img);
        hisIn = new HistogramInner(img);
        add(hisIn, BorderLayout.CENTER);
        StatIn = new StaticsInner();
        add(StatIn, BorderLayout.SOUTH);
        setVisible(true);
    }

    public class HistogramInner extends JPanel {
        public HistogramInner(Image3D img) {
            // -- size of the frame: width, height
            setPreferredSize(new Dimension(256, 600));
            setBackground(Color.BLACK);
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            super.paintComponent(g);
            super.paintBorder(g);

            double scaleHis = (double)getHeight()/getScaleMax()/3;
            double scaleCum = (double)getHeight()/3;
            double scaleH = 1;//(double)getWidth()/256;
            int transparency = 255;
            g2d.setXORMode(Color.BLACK);
            g2d.setColor(new Color(255, 0, 0, transparency));
            for(int i = 0; i < 256; i++){
                g2d.drawLine((int) (i*scaleH), getHeight() / 3 + 1, (int) (i*scaleH), getHeight() / 3 + 1 - (int) (COLOR[0][i]*scaleHis));
                g2d.drawLine((int) (i*scaleH), getHeight() / 3 + 1 - (int) (cumHisto[0][i]*scaleCum), (int) (i*scaleH), getHeight() / 3 + 1 - (int) (cumHisto[0][i]*scaleCum));
            }
            g2d.setColor(new Color(0, 255, 0, transparency));
            for(int i = 0; i < 256; i++){
                g2d.drawLine((int) (i*scaleH), getHeight() / 3 * 2 + 2, (int) (i*scaleH), getHeight()/ 3 * 2 + 2 - (int) (COLOR[1][i]*scaleHis));
                g2d.drawLine((int) (i*scaleH), getHeight()/ 3 * 2 + 2 - (int) (cumHisto[1][i]*scaleCum), (int) (i*scaleH), getHeight()/ 3 * 2 + 2 - (int) (cumHisto[1][i]*scaleCum));
            }
            g2d.setColor(new Color(0, 0, 255, transparency));
            for(int i = 0; i < 256; i++){
                g2d.drawLine((int) (i*scaleH), getHeight() + 3, (int) (i*scaleH), getHeight() + 3 - (int) (COLOR[2][i]*scaleHis));
                g2d.drawLine((int) (i*scaleH), getHeight() + 3 - (int) (cumHisto[2][i]*scaleCum), (int) (i*scaleH), getHeight() + 3 - (int) (cumHisto[2][i]*scaleCum));
            }

        }

    }

    public class StaticsInner extends JPanel {

        JLabel RMean = new JLabel();
        JLabel GMean = new JLabel();
        JLabel BMean = new JLabel();
        JLabel RSD = new JLabel();
        JLabel GSD = new JLabel();
        JLabel BSD = new JLabel();
        JLabel RMode = new JLabel();
        JLabel GMode = new JLabel();
        JLabel BMode = new JLabel();
        JLabel RMax = new JLabel();
        JLabel GMax = new JLabel();
        JLabel BMax = new JLabel();
        JLabel RMin = new JLabel();
        JLabel GMin = new JLabel();
        JLabel BMin = new JLabel();

        public StaticsInner(){
            setPreferredSize(new Dimension(256, 300));
            setBackground(Color.WHITE);
            setLayout(new GridLayout(4,6));
            add(new JLabel());
            add(new JLabel("Mean:"));
            add(new JLabel("Std Dev:"));
            add(new JLabel("Mode:"));
            add(new JLabel("Max:"));
            add(new JLabel("Min:"));

            add(new JLabel("    R:"));
            add(RMean);
            add(RSD);
            add(RMode);
            add(RMax);
            add(RMin);
            add(new JLabel("    G:"));
            add(GMean);
            add(GSD);
            add(GMode);
            add(GMax);
            add(GMin);
            add(new JLabel("    B:"));
            add(BMean);
            add(BSD);
            add(BMode);
            add(BMax);
            add(BMin);
            refresh();
        }

        public void refresh(){
             RMean.setText(String.format("%.2f", getMean(COMPONENTS.RED)));
             GMean.setText(String.format("%.2f", getMean(COMPONENTS.GREEN)));
             BMean.setText(String.format("%.2f", getMean(COMPONENTS.BLUE)));
             RSD.setText(String.format("%.2f", getSD(COMPONENTS.RED)));
             GSD.setText(String.format("%.2f", getSD(COMPONENTS.GREEN)));
             BSD.setText(String.format("%.2f", getSD(COMPONENTS.BLUE)));
             RMode.setText(String.valueOf(getMode(COMPONENTS.RED)));
             GMode.setText(String.valueOf(getMode(COMPONENTS.GREEN)));
             BMode.setText(String.valueOf(getMode(COMPONENTS.BLUE)));
             RMax.setText(String.valueOf(getMax(COMPONENTS.RED)));
             GMax.setText(String.valueOf(getMax(COMPONENTS.GREEN)));
             BMax.setText(String.valueOf(getMax(COMPONENTS.BLUE)));
             RMin.setText(String.valueOf(getMin(COMPONENTS.RED)));
             GMin.setText(String.valueOf(getMin(COMPONENTS.GREEN)));
             BMin.setText(String.valueOf(getMin(COMPONENTS.BLUE)));
        }
    }

    public void loadImg(Image3D img) {
        COLOR = new int[3][256];
        height = img.getHeight();
        width = img.getWidth();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int[] pixel = img.getPixel(y, x);
                COLOR[COMPONENTS.RED.ordinal()][pixel[0]]++;
                COLOR[COMPONENTS.GREEN.ordinal()][pixel[1]]++;
                COLOR[COMPONENTS.BLUE.ordinal()][pixel[2]]++;
            }
        }
        cumHisto();
        if(StatIn != null) StatIn.refresh();
    }

    public void test() {
        System.out.println("Red");
        System.out.println(getMean(COMPONENTS.RED));
        System.out.println(getSD(COMPONENTS.RED));
        System.out.println(getMode(COMPONENTS.RED));
        System.out.println(getMax(COMPONENTS.RED));
        System.out.println(getMin(COMPONENTS.RED));

        System.out.println("Green");
        System.out.println(getMean(COMPONENTS.GREEN));
        System.out.println(getSD(COMPONENTS.GREEN));
        System.out.println(getMode(COMPONENTS.GREEN));
        System.out.println(getMax(COMPONENTS.GREEN));
        System.out.println(getMin(COMPONENTS.GREEN));

        System.out.println("Blue");
        System.out.println(getMean(COMPONENTS.BLUE));
        System.out.println(getSD(COMPONENTS.BLUE));
        System.out.println(getMode(COMPONENTS.BLUE));
        System.out.println(getMax(COMPONENTS.BLUE));
        System.out.println(getMin(COMPONENTS.BLUE));
    }

    public double getMean(COMPONENTS Color) {
        double sum = 0;
        for (int i = 0; i < 256; i++) {
            sum += i * COLOR[Color.ordinal()][i];
        }
        return sum / (height * width);
    }

    public double getSD(COMPONENTS Color) {
        double sum = 0;
        double mean = getMean(Color);
        for (int i = 0; i < 256; i++) {
            sum += Math.pow(i - mean, 2) * COLOR[Color.ordinal()][i];
        }
        return Math.sqrt((double) sum / (height * width));
    }

    public int getMode(COMPONENTS Color) {
        int max = COLOR[Color.ordinal()][0];
        int mode = 0;
        for (int i = 1; i < 256; i++) {
            if (max < COLOR[Color.ordinal()][i]) {
                max = COLOR[Color.ordinal()][i];
                mode = i;
            }
        }
        return mode;
    }

    public int getScaleMax(){
        int redMax = COLOR[0][getMode(COMPONENTS.RED)];
        int greenMax = COLOR[1][getMode(COMPONENTS.GREEN)];
        int blueMax = COLOR[2][getMode(COMPONENTS.BLUE)];
        return Math.max(Math.max(redMax, greenMax), blueMax);
    }

    public int getMax(COMPONENTS Color) {
        for (int i = 255; i >= 0; i--) {
            if (COLOR[Color.ordinal()][i] != 0) {
                return i;
            }
        }
        return -1;
    }

    public int getMin(COMPONENTS Color) {
        for (int i = 0; i < 256; i++) {
            if (COLOR[Color.ordinal()][i] != 0) {
                return i;
            }
        }
        return -1;
    }


    public int getPixelCount() {
        return height*width;
    }

    public void cumHisto(){
        for(int c = 0; c < 3; c++) {
            int count = 0;
            for (int i = 0; i < 256; i++) {
                count += COLOR[c][i];
                cumHisto[c][i] = (double)count/getPixelCount();
            }
        }
    }

    public double[][] getCumHisto(){
        return cumHisto;
    }
}
