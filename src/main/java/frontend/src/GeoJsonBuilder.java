package frontend.src;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import backend.Graph;
import backend.Dijkstra;
import java.util.List;

public class GeoJsonBuilder {
    public static JsonObject buildRouteGeoJson(
            Graph graph,
            Dijkstra.DijkstraPath path,
            int markerAId, double markerALat, double markerALon,
            int markerBId, double markerBLat, double markerBLon,
            double sliderWeight
    ) {
        JsonObject featureCollection = new JsonObject();
        featureCollection.addProperty("type", "FeatureCollection");

        JsonArray features = new JsonArray();

        features.add(pointFeature(markerALon, markerALat, props("id", markerAId, "role", "A")));

        features.add(pointFeature(markerBLon, markerBLat,props("id", markerBId, "role", "B")));

        JsonArray coordinates = new JsonArray();

        List<Integer> nodeIds = path.getPathFromAToB();
        double[] latList = graph.getLat();
        double[] lonList = graph.getLon();

        for (int nodeId : nodeIds) {
            double lat = latList[nodeId];
            double lon = lonList[nodeId];
            if (nodeId < 0 || nodeId >= graph.getNumberOfNodes()) {
                throw new IllegalArgumentException("Invalid nodeId in path: " + nodeId);
            }
            JsonArray coord = new JsonArray();
            coord.add(lon);
            coord.add(lat);
            coordinates.add(coord);
        }

        JsonObject routeProps = new JsonObject();
        routeProps.addProperty("aId", markerAId);
        routeProps.addProperty("bId", markerBId);
        routeProps.addProperty("sliderWeight", sliderWeight);

        routeProps.addProperty("distance_cm", path.getTotalDistanceCm());
        routeProps.addProperty("elevation_cm", path.getTotalElevationCm());
        routeProps.addProperty("cost", path.getTotalCost());

        features.add(lineStringFeature(coordinates, routeProps));

        featureCollection.add("features", features);
        return featureCollection;
    }

    private static JsonObject props(String k1, int v1, String k2, String v2) {
        JsonObject p = new JsonObject();
        p.addProperty(k1, v1);
        p.addProperty(k2, v2);
        return p;
    }

    private static JsonObject pointFeature(double lon, double lat, JsonObject properties) {
        JsonObject feature = new JsonObject();
        feature.addProperty("type", "Feature");

        JsonObject geometry = new JsonObject();
        geometry.addProperty("type", "Point");

        JsonArray coords = new JsonArray();
        coords.add(lon);
        coords.add(lat);

        geometry.add("coordinates", coords);
        feature.add("geometry", geometry);

        feature.add("properties", properties != null ? properties : new JsonObject());
        return feature;
    }

    private static JsonObject lineStringFeature(JsonArray coordinates, JsonObject properties) {
        JsonObject feature = new JsonObject();
        feature.addProperty("type", "Feature");

        JsonObject geometry = new JsonObject();
        geometry.addProperty("type", "LineString");
        geometry.add("coordinates", coordinates);

        feature.add("geometry", geometry);
        feature.add("properties", properties != null ? properties : new JsonObject());
        return feature;
    }
}
