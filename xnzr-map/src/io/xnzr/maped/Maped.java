package io.xnzr.maped;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.prefs.Preferences;

public class Maped {
    public static final String RES_ICON_IBEACON_96_PNG = "res/icons8-ibeacon-96.png";
    private JPanel panel;
    private JPanel clientSpace;
    private JPanel toolBox;
    private JLabel label;
    private JButton bOpenImage;
    private final JFrame frame;
    private BorderLayout borderLayout;
    private JFilePicker imageLoader;
    private JFilePicker mapPicker;
    private ArrayList<JRadioSourceView> sourceComponents;
    private BufferedImage radioSourceImage;
    private MapInfo curMap;
    private Preferences userPrefs;
    private BufferedImage mapImage;
    private double scale = 1;

    private final int SOURCE_WIDTH = 50;
    private final int SOURCE_HEIGHT = 50;

    private final String MAP_EXTENSION = ".mxn";

    private final String PREFS_KEY_LAST_LOAD_PATH = "prefs_last_load_path";
    private final String PREFS_KEY_LAST_SAVE_PATH = "prefs_last_save_path";

    private Maped() {
        sourceComponents = new ArrayList<>();
        userPrefs = Preferences.userNodeForPackage(this.getClass());
        frame = new JFrame("Indoor map");
//        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setSize(800, 800);
        frame.setVisible(true);

        setWindowContent();
    }

    private void setWindowContent() {
        borderLayout = new BorderLayout();
        frame.setLayout(borderLayout);

        label = new JLabel("Image!");
        frame.add(label, BorderLayout.SOUTH);

        clientSpace = new JPanel();
        clientSpace.setLayout(null);
        frame.add(clientSpace, BorderLayout.CENTER);

        createImageLoader();
        createToolbox();

        loadImage("D:\\Programming\\Outsourcing\\xnzr\\maps\\8_0.jpg");

        try {
            radioSourceImage = ImageIO.read(new File(RES_ICON_IBEACON_96_PNG));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 5; i++) {
            addRadioSource(i * 70, i * 70);
        }
    }

    private void createToolbox() {
        toolBox = new JPanel();
        toolBox.setLayout(new BoxLayout(toolBox, BoxLayout.X_AXIS));
        bOpenImage = new JButton("Open");
        bOpenImage.addActionListener(e -> {
            label.setText("loading image...");
            toolBox.add(imageLoader);
        });
        toolBox.add(bOpenImage);
        JButton bSetSource = new JButton("Source");
        bSetSource.addActionListener(e -> {
            label.setText("Setting source...");
        });
        toolBox.add(bSetSource);

        mapPicker = new JFilePicker("Map", "Browse");
        mapPicker.setAcceptAllFileFilterUsed(false);
        mapPicker.addFileTypeFilter(MAP_EXTENSION, "xnzr indoor map");


        JButton bSaveMap = new JButton("Save map");
        bSaveMap.addActionListener(e -> {
            label.setText("Saving...");
            toolBox.add(mapPicker);
            String p = userPrefs.get(PREFS_KEY_LAST_SAVE_PATH, null);
            if (p != null) {
                mapPicker.setCurrentDirectory(p);
            }
            mapPicker.setMode(JFilePicker.MODE_SAVE);
            mapPicker.addCallback(path -> {
                try {
                    MapInfo.saveToXml(path, curMap);
                    label.setText("File " + path + " saved");
                    userPrefs.put(PREFS_KEY_LAST_SAVE_PATH, path);
                } catch (IOException e1) {
                    e1.printStackTrace();
                    label.setText("Failed to save " + path);
                }
            });
        });
        toolBox.add(bSaveMap);

        frame.add(toolBox, BorderLayout.NORTH);
    }

    private void createImageLoader() {
        imageLoader = new JFilePicker("Pick a file", "Browse...");
        imageLoader.setAcceptAllFileFilterUsed(false);
        imageLoader.setMode(JFilePicker.MODE_OPEN);
        imageLoader.addFileTypeFilter(".jpg", "JPEG Images");
        imageLoader.addFileTypeFilter(".png", "PNG Images");
        imageLoader.addFileTypeFilter(MAP_EXTENSION, "XNZR map");
        String curLoadPath = userPrefs.get(PREFS_KEY_LAST_LOAD_PATH, "");
        if (!curLoadPath.equals("")) {
            imageLoader.setCurrentDirectory(curLoadPath);
        }
        imageLoader.addCallback(path -> {
            toolBox.remove(imageLoader);
            userPrefs.put(PREFS_KEY_LAST_LOAD_PATH, path);
            loadMap(path);
            frame.revalidate();
            label.setText("Image loaded");
            imageLoader.setVisible(false);
        });
    }

    private void addRadioSource(int x, int y) {
        createRadioSourceView(x, y);

        RadioSource data = new RadioSource((double)x/clientSpace.getWidth(), (double)y/clientSpace.getHeight());
        curMap.addRadioSource(data);
    }

    private void createRadioSourceView(int x, int y) {
        JRadioSourceView pic = new JRadioSourceView(radioSourceImage);
        pic.setSize(SOURCE_WIDTH, SOURCE_HEIGHT);
        pic.setLocation(x, y);
        clientSpace.add(pic);
        sourceComponents.add(pic);
    }

    private void showRadioSources() {
        for (RadioSource r: curMap.getRadioSources()) {
            int x = (int)(r.getX()*mapImage.getWidth()*scale);
            int y = (int)(r.getY()*mapImage.getHeight()*scale);
            createRadioSourceView(x, y);
//            createRadioSourceView(0, 0);
        }
    }


    private void loadMap(String path) {
        switch (getMapType(path)) {
            case Picture:
                loadMap(path);
                break;
            case MapInfo:
                loadMapInfo(path);
                break;
            case Unknown:
                label.setText("Unknown file type!");
                break;
        }
    }

    private enum MapType {
        Picture,
        MapInfo,
        Unknown
    }

    private MapType getMapType(String path) {
        int idx = path.lastIndexOf('.');
        if (idx != -1) {
            String ext = path.substring(idx);
            if (ext.equals(MAP_EXTENSION)) {
                return MapType.MapInfo;
            }
            if (ext.equals(".jpg") || ext.equals(".png")) {
                return MapType.Picture;
            }
        }
        return MapType.Unknown;
    }

    private void loadMapInfo(String path) {
        try {
            curMap = MapInfo.loadFromXml(path);
            ByteArrayInputStream bis = new ByteArrayInputStream(curMap.getMap());

            mapImage = ImageIO.read(bis);
            setupImage();
            showRadioSources();
        } catch (IOException e) {
            e.printStackTrace();
            label.setText("Could not open file " + path);
        }
    }

    private void loadImage(String path) {
        try {
            mapImage = ImageIO.read(new File(path));

            curMap = new MapInfo();
            byte [] data = Files.readAllBytes(Paths.get(path));
            curMap.setMap(data);
            curMap.setHeight(mapImage.getHeight());
            curMap.setWidth(mapImage.getWidth());

            setupImage();
        } catch (IOException e) {
            e.printStackTrace();
            label.setText("Image load failed");
        }
    }

    private void setupImage() {
        frame.remove(clientSpace);
        clientSpace = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                double scaleWidth = (double) getWidth() / mapImage.getWidth();
                double scaleHeight = (double) getHeight() / mapImage.getHeight();
                scale = Math.min(scaleWidth, scaleHeight);
                g2d.scale(scale, scale);
                g2d.drawImage(mapImage, 0, 0, null);
                super.paintComponents(g);
            }
        };
        clientSpace.setLayout(null);
        frame.add(clientSpace, BorderLayout.CENTER);
    }

//    public class LoadFinishedCallback implements IDelegade {
//        @Override
//        public void call() {
//
//        }
//    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Maped::new);
    }
}
