package io.xnzr.maped;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.tools.Tool;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Currency;
import java.util.prefs.Preferences;

public class Maped {
    private static final String RES_ICON_IBEACON_96_PNG = "res/icons8-ibeacon-96.png";
    private JPanel panel;
    private JPanel clientSpace;
    private JToolBar toolBox;
    private JLabel label;
    private JButton bOpen;
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

    private enum State {
        Common,
        SetSource,
        DragSource
    }

    private State state = State.Common;

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

        createClientSpace();

        createImageLoader();
        createToolbox();

        loadImage("D:\\Programming\\Outsourcing\\xnzr\\maps\\8_0.jpg");

        try {
            radioSourceImage = ImageIO.read(new File(RES_ICON_IBEACON_96_PNG));
        } catch (IOException e) {
            e.printStackTrace();
        }

//        for (int i = 0; i < 5; i++) {
//            addRadioSource(i * 70, i * 70);
//        }
    }

    private void createClientSpace() {
        clientSpace = new JPanel();
        clientSpace.setLayout(null);
        listenMouse();
        frame.add(clientSpace, BorderLayout.CENTER);
    }

    private void listenMouse() {
        clientSpace.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                label.setText("click");
                switch (state) {
                    case Common:
                        break;
                    case SetSource:
                        label.setText("Radio source successfully set.");
                        state = State.Common;
                        Point p = clientSpace.getMousePosition();
                        addRadioSource(p.x, p.y);
                        clientSpace.setCursor(Cursor.getDefaultCursor());
                        break;
                    case DragSource:
                        break;
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                label.setText("press");

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    private void createToolbox() {
        toolBox = new JToolBar();
        toolBox.setLayout(new BoxLayout(toolBox, BoxLayout.X_AXIS));
        bOpen = new JButton("Open");
        bOpen.addActionListener(e -> {
            label.setText("loading image...");
            toolBox.add(imageLoader);
        });

        mapPicker = new JFilePicker("Map", "Browse");
        mapPicker.setAcceptAllFileFilterUsed(false);
        mapPicker.addFileTypeFilter(MAP_EXTENSION, "xnzr indoor map");


        JButton bSave = new JButton("Save");
        bSave.addActionListener(e -> {
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

        JButton bSetSource = new JButton("Source");
        bSetSource.addActionListener(e -> {
            state = State.SetSource;
            Point hotspot = new Point(0,0);
            Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(radioSourceImage, hotspot, "radio_cursor");
            clientSpace.setCursor(cursor);
            label.setText("Setting source...");
        });

        toolBox.add(bOpen);
        toolBox.add(bSave);
        toolBox.addSeparator();
        toolBox.add(bSetSource);


        frame.add(toolBox, BorderLayout.NORTH);
    }

    private void createImageLoader() {
        imageLoader = new JFilePicker("Pick a file", "Browse...");
        imageLoader.setAcceptAllFileFilterUsed(false);
        imageLoader.setMode(JFilePicker.MODE_OPEN);
        imageLoader.addFileTypeFilter(MAP_EXTENSION, "XNZR map");
        imageLoader.addFileTypeFilter(".jpg", "JPEG Images");
        imageLoader.addFileTypeFilter(".png", "PNG Images");
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

//        RadioSource data = new RadioSource((double)x/clientSpace.getWidth(), (double)y/clientSpace.getHeight());
//        curMap.addRadioSource(data);
    }

    private void createRadioSourceView(int x, int y) {
        JRadioSourceView pic = new JRadioSourceView(radioSourceImage);
        pic.setSize((int)(SOURCE_WIDTH*scale), (int)(SOURCE_HEIGHT*scale));
        double scaledX = x/scale;
        double scaledY = y/scale;
        pic.setLocation((int) (scaledX), (int) (scaledY));
        clientSpace.add(pic);
        frame.revalidate();
        sourceComponents.add(pic);

        RadioSource data = new RadioSource((double)x/mapImage.getWidth(), (double)y/mapImage.getHeight());
        curMap.addRadioSource(data);
    }

    private void showRadioSources() {
        for (RadioSource r: curMap.getRadioSources()) {
            int x = (int)(r.getX()*mapImage.getWidth());
            int y = (int)(r.getY()*mapImage.getHeight());

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
        listenMouse();
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
