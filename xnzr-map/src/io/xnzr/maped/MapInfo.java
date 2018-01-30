package io.xnzr.maped;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.*;


public class MapInfo {
    static void saveToXml(String dstPath, MapInfo mapInfo) throws IOException {
        XStream xstream = new XStream(new DomDriver());
        String xml = xstream.toXML(mapInfo);

        try (PrintWriter out = new PrintWriter(dstPath)) {
            out.println(xml);
        }
    }

    static MapInfo loadFromXml(String path) throws IOException {
        XStream xstream = new XStream(new DomDriver());
        return (MapInfo)xstream.fromXML(new File(path));
    }

    MapInfo() {
        radioSources = new CopyOnWriteArrayList<>();
    }


    private CopyOnWriteArrayList<RadioSource> radioSources;

    public CopyOnWriteArrayList<RadioSource> getRadioSources() {
        return radioSources;
    }

    public void setRadioSources(CopyOnWriteArrayList<RadioSource> radioSources) {
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
