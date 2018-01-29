package io.xnzr.maped;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;


public class MapInfo {
    static void saveToXml(String dstPath, MapInfo mapInfo) throws IOException {
        XStream xstream = new XStream(new DomDriver());
        String xml = xstream.toXML(mapInfo);

//        MapInfo t = (MapInfo)xstream.fromXML(xml);
        try (PrintWriter out = new PrintWriter(dstPath)) {
            out.println(xml);
        }
    }

    static MapInfo loadFromXml(String path) throws IOException {
        XStream xstream = new XStream(new DomDriver());
        return (MapInfo)xstream.fromXML(new File(path));
    }

    public MapInfo() {
        radioSources = new ArrayList<>();
    }


    private ArrayList<RadioSource> radioSources;

    public ArrayList<RadioSource> getRadioSources() {
        return radioSources;
    }

    public void setRadioSources(ArrayList<RadioSource> radioSources) {
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
