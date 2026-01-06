import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class CordToTile {
    private final Path path;
    private final Map<String,HgtTile> tiles;

    /**
     * Creates a new CordToTile object.
     * @param path The path to the hgt tile folder.
     */
    public CordToTile(Path path) {
        this.path = path;
        this.tiles = new HashMap<>();
    }

    /**
     * Determines the name of the tile based on the latitude and longitude.
     * @param lat The latitude that is part of the tile.
     * @param lon The longitude that is part of the tile.
     * @return Returns a string that contains the latitude and longitude, for example "N47E009".
     */
    private static String tileName(double lat, double lon){
        int baseLat = (int) Math.floor(lat);
        int baseLon = (int) Math.floor(lon);

        char ns = (baseLat >= 0) ? 'N' : 'S';
        char ew = (baseLon >= 0) ? 'E' : 'W';

        int absLat = Math.abs(baseLat);
        int absLon = Math.abs(baseLon);

        return String.format("%c%02d%c%03d", ns, absLat, ew, absLon);
    }

    /**
     * Returns the HgtTile that is relevant for the coordinate in question. If it does not exist yet,
     * the tile will be created and loaded.
     * @param lat The latitude of the coordinate in question.
     * @param lon The longitude of the coordinate in question.
     * @return The HgtTile that belongs to the coordinate.
     * @throws IOException for I/O issues.
     */
    private HgtTile getTile(double lat, double lon) throws IOException {
        int baseLat = (int) Math.floor(lat);
        int baseLon = (int) Math.floor(lon);
        String name = tileName(baseLat, baseLon);
        HgtTile tile = tiles.get(name);
        if (tile == null) {
            tile = new HgtTile(path, name, baseLat, baseLon);
            tiles.put(name, tile);
        }
        return tile;
    }

    /**
     * Calculates and returns the height in cm at a given latitude and longitude
     * using barycentric coordinate interpolation.
     * @param lat The latitude of the coordinate.
     * @param lon The longitude of the coordinate.
     * @return Returns an int containing the height at the coordinate specified in cm.
     * @throws IOException for I/O issues.
     */
    public int heightCmAt(double lat, double lon) throws IOException {
        HgtTile tile = getTile(lat, lon);
        int baseLat = (int) Math.floor(lat);
        int baseLon = (int) Math.floor(lon);
        double x =  (lon - baseLon)*3600.0;
        double y =  (baseLat + 1.0 - lat)*3600.0;
        int col = (int) Math.floor(x);
        int row = (int) Math.floor(y);
        //Clamp row and col
        if (col < 0) col = 0;
        if (row < 0) row = 0;
        if (col > 3599) col = 3599;
        if (row > 3599) row = 3599;

        double dx = x - col;
        double dy = y - row;

        // Create a rectangle where the point is located
        short hA = tile.heightMeters(row,col);
        short hB = tile.heightMeters(row+1,col);
        short hC = tile.heightMeters(row,col+1);
        short hD = tile.heightMeters(row+1,col+1);

        double hMeters;
        // Create a triangle from the rectangle and the chose the triangle where the point is located
        if (dx + dy <= 1.0){
            double wA = 1.0 - dx - dy;
            double wb = dy;
            double wc = dx;
            hMeters = wA * hA + wb * hB + wc * hC;
        } else {
            double wD = dx + dy - 1.0;
            double wb = 1.0 - dx;
            double wc = 1.0 - dy;
            hMeters = wD * hD + wc * hC + wb * hB;
        }

        return (int) Math.round(hMeters * 100.0);
    }
}