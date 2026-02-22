package backend;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class CordToTile {
    private static final int SIZE = 3601;
    private final Path path;
    private final Map<String, HgtTile> tiles;
    private HgtTile lastTile = null;
    private int lastTileBaseLat = Integer.MIN_VALUE;
    private int lastTileBaseLon = Integer.MIN_VALUE;

    /**
     * Creates a new CordToTile object.
     *
     * @param path The path to the hgt tile folder.
     */
    public CordToTile(Path path) {
        this.path = path;
        this.tiles = new HashMap<>();
    }

    /**
     * Determines the name of the tile based on the latitude and longitude.
     *
     * @param baseLat The base latitude of the tile in question.
     * @param baseLon The base longitude of the tile in question.
     * @return Returns a string that contains the latitude and longitude, for example "N47E009".
     */
    private static String tileName(int baseLat, int baseLon) {
        return String.format("N%02dE%03d", baseLat, baseLon);
    }

    /**
     * Returns the HgtTile that is relevant for the coordinate in question. If it does not exist yet,
     * the tile will be created and loaded.
     *
     * @param baseLat The base latitude of the coordinate in question.
     * @param baseLon The base longitude of the coordinate in question.
     * @return The HgtTile that belongs to the coordinate.
     * @throws IOException for I/O issues.
     */
    private HgtTile getTile(final int baseLat, final int baseLon) throws IOException {
        if (baseLat == lastTileBaseLat && baseLon == lastTileBaseLon) {
            return lastTile;
        }
        String name = tileName(baseLat, baseLon);
        HgtTile tile = tiles.get(name);
        if (tile == null) {
            tile = new HgtTile(path, name, baseLat, baseLon);
            tiles.put(name, tile);
        }
        lastTile = tile;
        lastTileBaseLat = baseLat;
        lastTileBaseLon = baseLon;
        return tile;
    }

    /**
     * Calculates and returns the height in cm at a given latitude and longitude
     * using barycentric coordinate interpolation.
     *
     * @param lat The latitude of the coordinate.
     * @param lon The longitude of the coordinate.
     * @return Returns an int containing the height at the coordinate specified in cm.
     * @throws IOException for I/O issues.
     */
    public int heightCmAt(double lat, double lon) throws IOException {
        // Integer tile coordinates
        final int baseLat = (int) lat;
        final int baseLon = (int) lon;

        // Get tile
        final HgtTile tile = getTile(baseLat, baseLon);

        // Convert to arc-seconds
        final double x = (lon - baseLon) * 3600;
        final double y = (baseLat + 1 - lat) * 3600;

        // Integer grid cell
        int col = (int) x;
        int row = (int) y;

        // Fast clamp (branch-minimized)
        if (col < 0) col = 0;
        else if (col > 3599) col = 3599;

        if (row < 0) row = 0;
        else if (row > 3599) row = 3599;

        // Fractional offsets
        final double dx = x - col;
        final double dy = y - row;

        final short[] h = tile.getHeights();

        final int hA = h[row * SIZE + col];
        final int hB = h[(row + 1) * SIZE + col];
        final int hC = h[row * SIZE + col + 1];
        final int hD = h[(row + 1) * SIZE + col + 1];

        // Barycentric interpolation
        final double hMeters;
        if (dx + dy <= 1.0) {
            hMeters =
                    hA +
                            dx * (hC - hA) +
                            dy * (hB - hA);
        } else {
            hMeters =
                    hD +
                            (1.0 - dx) * (hB - hD) +
                            (1.0 - dy) * (hC - hD);
        }

        // Convert meters to centimeters
        return (int) (hMeters * 100.0 + 0.5);
    }
}