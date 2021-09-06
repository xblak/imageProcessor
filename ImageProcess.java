package imageProcess;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageProcess extends JFrame {
    private Image3D img = new Image3D();
    private int WIDTH = 900;
    private int HEIGHT = 1100;
    private thumbnail tn = new thumbnail(100,100);
    protected mainView main = new mainView(800,800);
    private ImageComponent rgbFrame = new ImageComponent();
    private Histogram histogram = new Histogram(img);
    private Filter filter = new Filter();

    private JMenuBar mb = new JMenuBar();

    private JMenu menuFile = new JMenu("File");
    private JMenuItem fileOpen = new JMenuItem("Open");
    private JMenuItem fileSave = new JMenuItem("Save"); // empty for now
    private JMenuItem fileSaveAs = new JMenuItem("Save As");
    private JMenuItem fileExit = new JMenuItem("Exit");

    private JMenu menuTools = new JMenu("Tools");
    private JCheckBoxMenuItem image_component = new JCheckBoxMenuItem("Image Component");
    private JCheckBoxMenuItem histogram_button = new JCheckBoxMenuItem("Histogram");

    private JMenu menuPointOps = new JMenu("Point Ops");
    private JMenu pixelMath = new JMenu("Pixel Math");
    private JMenuItem pixelAdd = new JMenuItem("Pixel Add");
    private JMenuItem pixelSub = new JMenuItem("Pixel Subtract");
    private JMenuItem pixelMult = new JMenuItem("Pixel Multiply");
    private JMenuItem pixelDiv = new JMenuItem("Pixel Divide");
    private JMenuItem contrastEnhancement = new JMenuItem("Contrast Enhancement");
    private JMenuItem imageMath = new JMenuItem("Image Math");
    private JMenuItem gammaCorrection = new JMenuItem("Gamma Correction");
    private JMenuItem alphaBlending = new JMenuItem("Alpha Blending");

    private JMenu menuColorOps = new JMenu("Color Ops");
    private JMenuItem HistogramEqualization = new JMenuItem("Histogram Equalization");
    private JMenuItem HistogramMatching = new JMenuItem("Histogram Matching");
    private JMenuItem Binarization = new JMenuItem("Binarization");
    private JMenuItem Luminance = new JMenuItem("Luminance");
    private JMenuItem Desaturation = new JMenuItem("Desaturation");

    private JMenu menuFilter = new JMenu("Filter");
    private JMenuItem MedianFilter = new JMenuItem("Median Filter");
    private JMenuItem OutlierFilter = new JMenuItem("Outlier Filter");
    private JMenuItem Convolution = new JMenuItem("Convolution");
    private JMenuItem GaussianFilter = new JMenuItem("Gaussian Filter");

    public ImageProcess(){

        setTitle("Image Processing Application");

        // -- size of the frame: width, height
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        // -- center the frame on the screen
        setLocationRelativeTo(null);
        // -- shut down the entire application when the frame is closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // -- set the layout manager and add items
        setLayout(new BorderLayout());
        rgbFrame.setLocation(getX() + getWidth(), getY());
        rgbFrame.setVisible(true);
        histogram.setLocation(getX() - histogram.getWidth(), getY());
        histogram.pack();
        histogram.setVisible(true);
        histogram.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                histogram_button.setState(false);
            }
        });

        //menu ActionListener
        fileOpen.addActionListener(
                event -> {
                    String userDir = System.getProperty("user.home");
                    JFileChooser fc = new JFileChooser(userDir +"/Desktop");
                    fc.addChoosableFileFilter(new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes()));
                    int returnVal = fc.showOpenDialog(ImageProcess.this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        try {
                            img.loadImage(file.getAbsolutePath());
                            repaint();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Opening: " + file.getAbsolutePath());
                    }

                }
        );
        fileSaveAs.addActionListener(
                event -> {
                        JFileChooser fc = new JFileChooser();
                        fc.setDialogTitle("Specify a file to save");
                        fc.setApproveButtonText("Save");
                        fc.addChoosableFileFilter(new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes()));
                        int returnValue = fc.showOpenDialog(null);
                        if (returnValue == JFileChooser.APPROVE_OPTION) {
                            File file = fc.getSelectedFile();
                            try {
                                img.saveImage(file.getAbsolutePath(), "png");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            System.out.println("Saved to: " + file.getAbsolutePath() + ".png");
                        }
                }
        );
        fileExit.addActionListener(
                event -> {
                    System.exit(0);
                }
        );
        image_component.addActionListener(
                event -> {
                    rgbFrame.setVisible(!rgbFrame.isVisible());
                    rgbFrame.setLocation(getX() + getWidth(), getY());
                }
        );
        histogram_button.addActionListener(
                event -> {
                    histogram.setVisible(!histogram.isVisible());
                    histogram.setLocation(getX() - histogram.getWidth(), getY()); // set on the left side of main
                }
        );
        image_component.setState(true); // image comps starts visible by default
        histogram_button.setState(true); // histogram starts visible by default

        // Point Ops action listener
        pixelAdd.addActionListener(
                event -> {
                    InputFrame in = new InputFrame("Add");
                }
        );
        pixelSub.addActionListener(
                event -> {
                    InputFrame in = new InputFrame("Substract");
                }
        );
        pixelMult.addActionListener(
                event -> {
                    InputFrame in = new InputFrame("Multiply");
                }
        );
        pixelDiv.addActionListener(
                event -> {
                    InputFrame in = new InputFrame("Divide");
                }
        );
        gammaCorrection.addActionListener(
                event -> {
                    InputFrame in = new InputFrame("Gamma Correction");
                }
        );
        contrastEnhancement.addActionListener(
                event -> {
                    InputFrame in = new InputFrame("Contrast Enhancement");
                }
        );
        imageMath.addActionListener(
                event -> {
                    InputFrame in = new InputFrame("Image Math");
                }
        );
        alphaBlending.addActionListener(
                event -> {
                    InputFrame in = new InputFrame("Alpha Blending");
                }
        );
        HistogramEqualization.addActionListener(
                event -> {
                    // no need input frame
                    img.histogramEqualization(histogram);
                    repaint();
                }
        );
        HistogramMatching.addActionListener(
                event -> {
                    InputFrame in = new InputFrame("Histogram Matching");
                }
        );
        Binarization.addActionListener(
                event -> {
                    InputFrame in = new InputFrame("Binarization");
                }
        );
        Luminance.addActionListener(
                event -> {
                    // no need input frame
                    img.luminance();
                    repaint();
                }
        );
        Desaturation.addActionListener(
                event -> {
                    InputFrame in = new InputFrame("Desaturation");
                }
        );
        MedianFilter.addActionListener(
                event -> {
                    InputFrame in = new InputFrame("Median Filter");
                }
        );
        OutlierFilter.addActionListener(
            event -> {
                InputFrame in = new InputFrame("Outlier Filter");
            }
        );
        Convolution.addActionListener(
            event -> {
                InputFrame in = new InputFrame("Convolution");
            }
        );
        GaussianFilter.addActionListener(
                event -> {
                    InputFrame in = new InputFrame("Gaussian Filter");
                }
        );
        // add menu
        menuFile.add(fileOpen);
        menuFile.add(fileSave);
        menuFile.add(fileSaveAs);
        menuFile.add(fileExit);

        menuTools.add(image_component);
        menuTools.add(histogram_button);

        menuPointOps.add(pixelMath);
        pixelMath.add(pixelAdd);
        pixelMath.add(pixelSub);
        pixelMath.add(pixelMult);
        pixelMath.add(pixelDiv);
        menuPointOps.add(contrastEnhancement);
        menuPointOps.add(imageMath);
        menuPointOps.add(gammaCorrection);
        menuPointOps.add(alphaBlending);

        menuColorOps.add(HistogramEqualization);
        menuColorOps.add(HistogramMatching);
        menuColorOps.add(Binarization);
        menuColorOps.add(Luminance);
        menuColorOps.add(Desaturation);

        menuFilter.add(MedianFilter);
        menuFilter.add(OutlierFilter);
        menuFilter.add(Convolution);
        menuFilter.add(GaussianFilter);

        mb.add(menuFile);
        mb.add(menuTools);
        mb.add(menuPointOps);
        mb.add(menuColorOps);
        mb.add(menuFilter);
        this.setJMenuBar(mb);

        // add thumbnail and main panel
        add(tn, BorderLayout.WEST);
        add(main, BorderLayout.CENTER);
        setVisible(true);
    }

    public class thumbnail extends JPanel{
        public thumbnail(int height, int width) {
            setPreferredSize(new Dimension(width+5, height+5));
            setMinimumSize(new Dimension(width, height));
            setBackground(Color.DARK_GRAY);
        }

        @Override
        public void paint(Graphics g){
            Graphics2D g2d=(Graphics2D)g;
            super.paintComponent(g);
            //need to cast the graphics g up to a graphics2D
            super.paintBorder(g);
            double ratio = (double)img.getImage()[0].length / img.getImage()[0][0].length;
            g2d.drawImage(img.toBI(), 0, 0, 100, (int) (100 * ratio), null);
            rgbFrame.comps.refresh();
            histogram.loadImg(img);
            histogram.repaint();
        }
    }

    public class mainView extends JPanel{
        public mainView(int height, int width) {
            setPreferredSize(new Dimension(width, height));
            setMinimumSize(new Dimension(width, height));
            setBackground(Color.GRAY);
        }

        @Override
        public void paint(Graphics g){
            Graphics2D g2d=(Graphics2D)g;
            super.paintComponent(g);
            //need to cast the graphics g up to a graphics2D
            super.paintBorder(g);
            g2d.drawImage(img.toBI(), 0, 0, img.getImage()[0][0].length, img.getImage()[0].length, null);
        }
    }

    public static BufferedImage randomRGB(int height, int width){
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                int pixel =	((int)(Math.random() * 255) << 16) | ((int)(Math.random() * 255) << 8) | ((int)(Math.random() * 255) << 0);
                bi.setRGB(j, i, pixel);
            }
        }
        return bi;
    }

    // Image Component Window
    public class ImageComponent extends JFrame{
        // show R,G,B layers
        private int WIDTH = 350;
        private int HEIGHT = 1100;
        protected componentRGB comps = new componentRGB();

        public ImageComponent(){
            setTitle("Image Component");
            // -- size of the frame: width, height
            setSize(WIDTH, HEIGHT);
            //setMinimumSize(new Dimension(WIDTH, HEIGHT));

            // Add Scroll Pane to JFrame
            JScrollPane scrollPane = new JScrollPane(comps);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            add(scrollPane);

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    image_component.setState(false);
                }
            });
        }

    }

    // Separated RGB layers
    public class componentRGB extends JPanel{
        JLabel picRED = new JLabel(new ImageIcon(componentToBi(img.getComponent(ImageObjectAbstract.COMPONENTS.RED)).getScaledInstance(300, -1, Image.SCALE_SMOOTH)));
        JLabel picGREEN = new JLabel(new ImageIcon(componentToBi(img.getComponent(ImageObjectAbstract.COMPONENTS.GREEN)).getScaledInstance(300, -1, Image.SCALE_SMOOTH)));
        JLabel picBLUE = new JLabel(new ImageIcon(componentToBi(img.getComponent(ImageObjectAbstract.COMPONENTS.BLUE)).getScaledInstance(300, -1, Image.SCALE_SMOOTH)));
        public componentRGB() {
            setLayout(new BorderLayout());
            setBackground(Color.DARK_GRAY);
            setSize(getWidth(), getHeight());
            add(picRED, BorderLayout.NORTH);
            add(picGREEN, BorderLayout.CENTER);
            add(picBLUE, BorderLayout.SOUTH);
        }

        public void refresh(){
            picRED.setIcon(new ImageIcon(componentToBi(img.getComponent(ImageObjectAbstract.COMPONENTS.RED)).getScaledInstance(300, -1, Image.SCALE_SMOOTH)));
            picGREEN.setIcon(new ImageIcon(componentToBi(img.getComponent(ImageObjectAbstract.COMPONENTS.GREEN)).getScaledInstance(300, -1, Image.SCALE_SMOOTH)));
            picBLUE.setIcon(new ImageIcon(componentToBi(img.getComponent(ImageObjectAbstract.COMPONENTS.BLUE)).getScaledInstance(300, -1, Image.SCALE_SMOOTH)));
        }
    }

    public BufferedImage componentToBi(int[][] comp){
        BufferedImage bi = new BufferedImage(comp[0].length, comp.length, BufferedImage.TYPE_INT_RGB);
        for(int y = 0; y < bi.getHeight(); y++){
            for(int x = 0; x < bi.getWidth(); x++){
                int rgb =
                        (comp[y][x] << 16) |
                                (comp[y][x] << 8) |
                                (comp[y][x] << 0);
                bi.setRGB(x,y,rgb);
            }
        }
        return bi;
    }

    public class InputFrame{
        private JTextField RedInput, GreenInput, BlueInput, lowInput, highInput, alphaInput, desatInput, thresholdInput, sigmaX, sigmaY;
        private Image3D imgInput = new Image3D();
        private JButton submitButton;
        private String mode;
        private JFrame frame;
        private JComboBox<String> borderType;
        public InputFrame(String title){
            frame = new JFrame(title);
            frame.setSize(300, 250);
            frame.setAlwaysOnTop(true);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            mode = title;
            JPanel panel = new JPanel();
            frame.add(panel);
            placeComponents(panel);
            frame.setVisible(true);
        }

        public void placeComponents(JPanel panel){
            panel.setLayout(null);

            if(mode.equals("Contrast Enhancement")){
                JLabel LowLabel = new JLabel("Low");
                LowLabel.setBounds(10, 10, 80, 30);
                panel.add(LowLabel);

                lowInput = new JTextField(20);
                lowInput.setBounds(70, 10, 130, 30);
                panel.add(lowInput);

                JLabel HighLabel = new JLabel("High");
                HighLabel.setBounds(10, 60, 80, 30);
                panel.add(HighLabel);

                highInput = new JTextField(20);
                highInput.setBounds(70, 60, 130, 30);
                panel.add(highInput);

                JButton auto = new JButton("Auto");
                auto.addActionListener(
                        event -> {
                            mode = "Auto Contrast Enhancement";
                            submit();
                            mode = "Contrast Enhancement";
                        }
                );
                auto.setBounds(70, 110, 130, 30);
                panel.add(auto);

            }else if(mode.equals("Image Math") || mode.equals("Alpha Blending") || mode.equals("Histogram Matching")){

                JButton chooseImg = new JButton("Choose Image");
                chooseImg.setBounds(70, 10, 130, 30);
                JLabel choosedImgPath = new JLabel("     No image selected");
                choosedImgPath.setBounds(30, 60, 250, 30);
                chooseImg.addActionListener(
                        event -> {
                            String userDir = System.getProperty("user.home");
                            JFileChooser fc = new JFileChooser(userDir +"/Desktop");
                            fc.addChoosableFileFilter(new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes()));
                            int returnVal = fc.showOpenDialog(frame);
                            if (returnVal == JFileChooser.APPROVE_OPTION) {
                                File file = fc.getSelectedFile();
                                try {
                                    imgInput.loadImage(file.getAbsolutePath());
                                    choosedImgPath.setText(file.getAbsolutePath());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                System.out.println("Opening: " + file.getAbsolutePath());
                            }
                        }
                );
                panel.add(chooseImg);
                panel.add(choosedImgPath);

                if(mode.equals("Image Math")){
                    JToggleButton addOrSub = new JToggleButton("Add");
                    mode = "Image Add";
                    ItemListener itemListener = new ItemListener() {
                        public void itemStateChanged(ItemEvent itemEvent) {
                            int state = itemEvent.getStateChange();
                            if (state == ItemEvent.SELECTED) {
                                addOrSub.setText("Subtract");
                                mode = "Image Sub";
                            } else {
                                addOrSub.setText("Add");
                                mode = "Image Add";
                            }
                        }
                    };
                    addOrSub.addItemListener(itemListener);
                    addOrSub.setBounds(70, 110, 130, 30);
                    panel.add(addOrSub);
                }

                if(mode.equals("Alpha Blending")){
                    JLabel alphaLabel = new JLabel("Alpha");
                    alphaLabel.setBounds(10, 110, 80, 30);
                    panel.add(alphaLabel);

                    alphaInput = new JTextField(20);
                    alphaInput.setBounds(70, 110, 130, 30);
                    panel.add(alphaInput);
                }
            }else if(mode.equals("Desaturation")){
                JLabel desatLabel = new JLabel("Color Remain");
                desatLabel.setBounds(10, 60, 80, 30);
                panel.add(desatLabel);

                desatInput = new JTextField(20);
                desatInput.setBounds(90, 60, 130, 30);
                panel.add(desatInput);
            }else if(mode.equals("Median Filter") || mode.equals("Outlier Filter") || mode.equals("Gaussian Filter")){
                frame.setSize(300, 350);
                JLabel tLabel = new JLabel("Specify the shape (Odd Only)");
                tLabel.setBounds(10, 10, 200, 30);
                panel.add(tLabel);

                JLabel widthLabel = new JLabel("Width");
                widthLabel.setBounds(10, 60, 80, 30);
                panel.add(widthLabel);

                JLabel heightLabel = new JLabel("Height");
                heightLabel.setBounds(10, 110, 80, 30);
                panel.add(heightLabel);

                JLabel borderLabel = new JLabel("Border Type");
                borderLabel.setBounds(10, 160, 80, 30);
                panel.add(borderLabel);

                lowInput = new JTextField(20);
                lowInput.setBounds(90, 60, 130, 30);
                panel.add(lowInput);

                highInput = new JTextField(20);
                highInput.setBounds(90, 110, 130, 30);
                panel.add(highInput);

                borderType = new JComboBox<String>(new String[]{"Ignore", "Copy", "Mirror"});
                borderType.setBounds(90, 160, 130, 30);

                panel.add(borderType);

                if(mode.equals("Outlier Filter")) {
                    JLabel threshLabel = new JLabel("Threshold");
                    threshLabel.setBounds(10, 210, 80, 30);
                    panel.add(threshLabel);

                    thresholdInput = new JTextField(20);
                    thresholdInput.setBounds(90, 210, 130, 30);
                    panel.add(thresholdInput);
                }
                if(mode.equals("Gaussian Filter")) {
                    frame.setSize(300, 450);
                    JLabel sigX = new JLabel("Sigma X");
                    sigX.setBounds(10, 210, 80, 30);
                    panel.add(sigX);

                    JLabel sigY = new JLabel("Sigma Y");
                    sigY.setBounds(10, 260, 80, 30);
                    panel.add(sigY);

                    sigmaX = new JTextField(20);
                    sigmaX.setBounds(90, 210, 130, 30);
                    panel.add(sigmaX);

                    sigmaY = new JTextField(20);
                    sigmaY.setBounds(90, 260, 130, 30);
                    panel.add(sigmaY);
                }

            }else if(mode.equals("Convolution")){
                JButton chooseKernel = new JButton("Choose Kernel File");
                chooseKernel.setBounds(70, 10, 130, 30);
                JLabel choosedKernelPath = new JLabel("     No Kernel selected");
                choosedKernelPath.setBounds(30, 60, 250, 30);
                chooseKernel.addActionListener(
                        event -> {
                            String userDir = System.getProperty("user.home");
                            JFileChooser fc = new JFileChooser(userDir +"/Desktop");
                            FileNameExtensionFilter filefilter = new FileNameExtensionFilter("Text files", "txt", "text");
                            fc.setFileFilter(filefilter);
                            int returnVal = fc.showOpenDialog(frame);
                            if (returnVal == JFileChooser.APPROVE_OPTION) {
                                File file = fc.getSelectedFile();
                                try {
                                    filter.readKernel(file.getAbsolutePath());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                choosedKernelPath.setText(file.getAbsolutePath());
                            }
                        }
                );
                borderType = new JComboBox<String>(new String[]{"Ignore", "Copy", "Mirror"});
                borderType.setBounds(90, 110, 130, 30);

                panel.add(choosedKernelPath);
                panel.add(chooseKernel);
                panel.add(borderType);
            }


            else{
                JLabel RedLabel = new JLabel("Red");
                RedLabel.setBounds(10, 10, 80, 30);
                panel.add(RedLabel);

                RedInput = new JTextField(20);
                RedInput.setBounds(70, 10, 130, 30);
                panel.add(RedInput);

                if(mode.equals("Binarization")){
                    RedLabel.setText("Threshold");
                    JButton auto = new JButton("Auto");
                    auto.addActionListener(
                            event -> {
                                mode = "Auto Binarization";
                                submit();
                                mode = "Binarization";
                            }
                    );
                    auto.setBounds(180, 160, 80, 35);
                    panel.add(auto);
                    submitButton = new JButton("Submit");
                    submitButton.addActionListener(
                            event -> {
                                submit();
                            }
                    );
                    submitButton.setBounds(80, 160, 80, 35);
                    panel.add(submitButton);
                    return;
                }

                JLabel GreenLabel = new JLabel("Green");
                GreenLabel.setBounds(10, 60, 80, 30);
                panel.add(GreenLabel);

                GreenInput = new JTextField(20);
                GreenInput.setBounds(70, 60, 130, 30);
                panel.add(GreenInput);

                JLabel BlueLabel = new JLabel("Blue");
                BlueLabel.setBounds(10, 110, 80, 30);
                panel.add(BlueLabel);

                BlueInput = new JTextField(20);
                BlueInput.setBounds(70, 110, 130, 30);
                panel.add(BlueInput);

            }

            submitButton = new JButton("Submit");
            submitButton.addActionListener(
                    event -> {
                        submit();
                    }
            );
            submitButton.setBounds(80, frame.getHeight()-90, 80, 35);
            panel.add(submitButton);
        }


        public void submit(){
            // point ops
            if(mode.equals("Add")){
                img.add(Integer.parseInt(RedInput.getText()),Integer.parseInt(GreenInput.getText()),Integer.parseInt(BlueInput.getText()));
            }else if(mode.equals("Substract")){
                img.subtract(Integer.parseInt(RedInput.getText()),Integer.parseInt(GreenInput.getText()),Integer.parseInt(BlueInput.getText()));
            }else if(mode.equals("Multiply")){
                img.mutiply(Integer.parseInt(RedInput.getText()),Integer.parseInt(GreenInput.getText()),Integer.parseInt(BlueInput.getText()));
            }else if(mode.equals("Divide")){
                img.divide(Integer.parseInt(RedInput.getText()),Integer.parseInt(GreenInput.getText()),Integer.parseInt(BlueInput.getText()));
            }else if(mode.equals("Gamma Correction")){
                img.gammaCorrection(Double.parseDouble(RedInput.getText()),Double.parseDouble(GreenInput.getText()),Double.parseDouble(BlueInput.getText()));
            }else if(mode.equals("Contrast Enhancement")){
                img.contrastEnhancement(Integer.parseInt(lowInput.getText()),Integer.parseInt(highInput.getText()));
            }else if(mode.equals("Auto Contrast Enhancement")){
                img.autoContrastEnhancement(histogram);
            }else if(mode.equals("Image Add")){
                img.add(imgInput);
            }else if(mode.equals("Image Sub")){
                img.subtract(imgInput);
            }else if(mode.equals("Alpha Blending")) {
                img.alphaBlending(Double.parseDouble(alphaInput.getText()), imgInput);
            }
            // color ops histogramEqualization, luminance, no input
            else if(mode.equals("Histogram Matching")){
                img.histogramMatching(histogram, imgInput);
            }else if(mode.equals("Binarization")){
                img.binarization(Integer.parseInt(RedInput.getText()));
            }else if(mode.equals("Auto Binarization")){
                img.autoBinarization(histogram);
            }else if(mode.equals("Desaturation")){
                img.desaturation(Double.parseDouble(desatInput.getText()));
            }else if(mode.equals("Median Filter")){
                filter.setBorder(borderType.getSelectedIndex());
                int[][][] newImg = new int[3][img.getHeight()][img.getWidth()];
                newImg[0] = filter.medianFilter(img.getComponent(ImageObjectAbstract.COMPONENTS.RED),Integer.parseInt(lowInput.getText()), Integer.parseInt(highInput.getText()));
                newImg[1] = filter.medianFilter(img.getComponent(ImageObjectAbstract.COMPONENTS.GREEN),Integer.parseInt(lowInput.getText()), Integer.parseInt(highInput.getText()));
                newImg[2] = filter.medianFilter(img.getComponent(ImageObjectAbstract.COMPONENTS.BLUE),Integer.parseInt(lowInput.getText()), Integer.parseInt(highInput.getText()));
                img.loadImage(newImg);
            }else if(mode.equals("Outlier Filter")){
                filter.setBorder(borderType.getSelectedIndex());
                int[][][] newImg = new int[3][img.getHeight()][img.getWidth()];
                newImg[0] = filter.outlierFilter(img.getComponent(ImageObjectAbstract.COMPONENTS.RED),Integer.parseInt(lowInput.getText()), Integer.parseInt(highInput.getText()), Integer.parseInt(thresholdInput.getText()));
                newImg[1] = filter.outlierFilter(img.getComponent(ImageObjectAbstract.COMPONENTS.GREEN),Integer.parseInt(lowInput.getText()), Integer.parseInt(highInput.getText()), Integer.parseInt(thresholdInput.getText()));
                newImg[2] = filter.outlierFilter(img.getComponent(ImageObjectAbstract.COMPONENTS.BLUE),Integer.parseInt(lowInput.getText()), Integer.parseInt(highInput.getText()), Integer.parseInt(thresholdInput.getText()));
                img.loadImage(newImg);
            }else if(mode.equals("Convolution")){
                int[][][] newImg = new int[3][img.getHeight()][img.getWidth()];
                newImg[0] = filter.convolution(img.getComponent(ImageObjectAbstract.COMPONENTS.RED));
                newImg[1] = filter.convolution(img.getComponent(ImageObjectAbstract.COMPONENTS.GREEN));
                newImg[2] = filter.convolution(img.getComponent(ImageObjectAbstract.COMPONENTS.BLUE));
                img.loadImage(newImg);
            }else if(mode.equals("Gaussian Filter")){
                int[][][] newImg = new int[3][img.getHeight()][img.getWidth()];
                int width = Integer.parseInt(lowInput.getText()), height = Integer.parseInt(highInput.getText());
                double sigX = Double.parseDouble(sigmaX.getText()), sigY = Double.parseDouble(sigmaY.getText());
                newImg[0] = filter.GaussianFilter(img.getComponent(ImageObjectAbstract.COMPONENTS.RED), width, height, sigX, sigY);
                newImg[1] = filter.GaussianFilter(img.getComponent(ImageObjectAbstract.COMPONENTS.GREEN), width, height, sigX, sigY);
                newImg[2] = filter.GaussianFilter(img.getComponent(ImageObjectAbstract.COMPONENTS.BLUE), width, height, sigX, sigY);
                img.loadImage(newImg);
            }
            else{
                System.out.println("No mode selected");
            }
            repaint();
        }
    }
}


