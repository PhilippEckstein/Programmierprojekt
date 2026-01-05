import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HgtTile {
    public static final int SIZE = 3601; // 3601x3601 samples
    private final short[] heights;
    public int baseLat;
    public int baseLon;

    public HgtTile(Path path, String tileName, int baseLat, int baseLon) throws IOException {
        this.baseLat = baseLat;
        this.baseLon = baseLon;
        Path file = path.resolve(tileName + ".hgt");

        byte[] raw = Files.readAllBytes(file);
        int expected = 2 * SIZE * SIZE;
        if (raw.length != expected) {
            throw new IOException("Unexpected .hgt size: " + raw.length + " bytes, expected " + expected);
        }
        this.heights = new short[SIZE * SIZE];
        int p = 0;
        for (int i = 0; i < heights.length; i++) {
            int high = raw[p++] & 0xFF;
            int low = raw[p++] & 0xFF;
            heights[i] = (short) ((high <<8) | low);
        }
    }
    public short heightMeters(int row, int col) {
        return heights[row * SIZE + col];
    }

}
