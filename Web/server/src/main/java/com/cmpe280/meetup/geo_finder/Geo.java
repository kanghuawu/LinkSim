package com.cmpe280.meetup.geo_finder;

import com.esri.core.geometry.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Service
public class Geo {

    @Resource
    private Environment env;

    private static Geo instance;
    private FeatureCollection worldCollection;
    private FeatureCollection stateCollection;
    private String WORLD_DIR = "/Users/khwu/Projects/Meetup-Analysis/data/countries_feature_collections.geo.json.txt";
    private String US_DIR = "/Users/khwu/Projects/Meetup-Analysis/data/us-states_feature_collection.geo.json.txt";
    public static final String USA = "USA";
    private final static String DEBUG_FILE = "../config/debug.txt";
    private final static String PRODUCTION_FILE = "../config/production.txt";

    private Geo() throws IOException {
        InputStream worldStream = new FileInputStream(WORLD_DIR);
        worldCollection =
                new ObjectMapper().readValue(worldStream, FeatureCollection.class);
        InputStream usStream = new FileInputStream(US_DIR);
        stateCollection =
                new ObjectMapper().readValue(usStream, FeatureCollection.class);
    }

    public static Geo getInstance() throws IOException {
        if (instance == null) {
            instance = new Geo();
        }
        return instance;
    }

    public String findLocation(String region, double lon, double lat) throws JsonProcessingException {
        if (region.equals(USA)) return findLocation(stateCollection, lon ,lat);
        else return findLocation(worldCollection, lon ,lat);

    }

    private String findLocation(FeatureCollection featureCollection, double lon, double lat) throws JsonProcessingException {
        Point point = new Point((float) lon, (float) lat);
        for (Feature f : featureCollection.getFeatures()){
            String json= new ObjectMapper().writeValueAsString(f.getGeometry());
            Polygon neighborhood = (Polygon) OperatorImportFromGeoJson
                    .local()
                    .execute(GeoJsonImportFlags.geoJsonImportDefaults, Geometry.Type.Polygon, json, null)
                    .getGeometry();
            if (OperatorContains.local().execute(neighborhood, point,null, null)) {
                return f.getId();
            }
        }
        return null;
    }
}
