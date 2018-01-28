package io.xnzr.maped;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;


public class MapInfo {
    static void saveToXml(String dstPath, MapInfo mapInfo) throws IOException {
        FileOutputStream fs = new FileOutputStream(dstPath);
        XMLEncoder encoder = new XMLEncoder(fs);
        encoder.setExceptionListener(e -> System.out.println("Exception! Details: " + e.toString()));
        encoder.writeObject(mapInfo);
        encoder.close();
        fs.close();
    }

    static MapInfo loadFromXml(String path) throws IOException {
        FileInputStream fs = new FileInputStream(path);
        XMLDecoder decoder = new XMLDecoder(fs);
        MapInfo res = (MapInfo) decoder.readObject();
        decoder.close();
        fs.close();
        return res;
    }

    public MapInfo() {
        radioSources = new ArrayList<>();
    }


    private ArrayList<RadioSource> radioSources;

    ArrayList<RadioSource> getRadioSources() {
        return radioSources;
    }

    void setRadioSources(ArrayList<RadioSource> radioSources) {
        this.radioSources = radioSources;
    }

    void addRadioSource(RadioSource source) {
        radioSources.add(source);
    }

    private String map64;

    byte[] getMap() {
        Base64.Decoder decoder = Base64.getDecoder();
        return decoder.decode(map64);
    }

    void setMap(byte[] map) {
        Base64.Encoder e = Base64.getEncoder();
        map64 = e.encodeToString(map);
    }

    public String getMap64() {
        return map64;
    }

    public void setMap64(String value) {
        map64 = value;
    }


    private int width;

    public int getWidth() {
        return width;
    }

    public void setWidth(int value) {
        width = value;
    }

    private int height;

    public int getHeight() {
        return height;
    }

    public void setHeight(int value) {
        height = value;
    }
}
