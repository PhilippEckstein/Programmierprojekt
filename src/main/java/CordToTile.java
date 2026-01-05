import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class CordToTile {
    private final Path path;
    private final Map<String,HgtTile> tiles;

    public CordToTile(Path path) {
        this.path = path;
        this.tiles = new HashMap<>();
    }

    private static String tileName(double lat, double lon){
        int baseLat = (int) Math.floor(lat);
        int baseLon = (int) Math.floor(lon);
        return String.format("N%02dE%03d",baseLat,baseLon);
    }
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
            double wb = dx;
            double wc = dx + dy;
            hMeters = wA * hA + wb * hB + wc * hC;
        } else {
            double wD = dx + dy - 1.0;
            double wc = 1.0 - dx;
            double wb = 1.0 - dy;
            hMeters = wD * hD + wc * hC + wb * hB;
        }

        return (int) Math.round(hMeters * 100.0);
    }

}
