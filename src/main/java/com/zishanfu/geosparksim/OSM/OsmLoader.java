package com.zishanfu.geosparksim.OSM;

import com.vividsolutions.jts.geom.Coordinate;
import com.zishanfu.geosparksim.Tools.FileOps;
import org.openstreetmap.osmosis.xml.v0_6.XmlDownloader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Road network parser
 * Download road network data and reformat it
 */
public class OsmLoader {

    public void parquet(Coordinate geo1, Coordinate geo2, String path) {
        String osmUrl = "http://overpass-api.de/api";
        XmlDownloader xmlDownloader = new XmlDownloader(geo1.y, geo2.y, geo1.x, geo2.x, osmUrl);
        xmlDownloader.setSink(new OsmParquetSink(path));
        xmlDownloader.run();
    }

    public void osm(Coordinate geo1, Coordinate geo2) {
        String OSM_URL = "http://overpass-api.de/api/map?bbox=";
        URL url = null;

        double left = Math.min(geo1.y, geo2.y);
        double right = Math.max(geo1.y, geo2.y);
        double top = Math.max(geo1.x, geo2.x);
        double bottom = Math.min(geo1.x, geo2.x);

        OSM_URL += left + "," + bottom + "," + right + "," + top;

        try {
            url = new URL(OSM_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String resources = System.getProperty("user.dir") + "/src/test/resources";
        String path = resources + "/geosparksim";
        FileOps fileOps = new FileOps();
        fileOps.createDirectory(path);
        String newFileName = String.format("%s/%s.osm", path, "map");

        try {
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream(newFileName);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}