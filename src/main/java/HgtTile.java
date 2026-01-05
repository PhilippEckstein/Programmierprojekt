import java.io.BufferedInputStream;
import java.io.DataInputStream;
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
        long expectedBytes = 2L * SIZE * SIZE;
        long actualBytes = Files.size(file);
        if (actualBytes != expectedBytes) {
            throw new IOException("Unexpected .hgt size: " + actualBytes + " bytes, expected " + expectedBytes);
        }
        this.heights = new short[SIZE * SIZE];
        try (DataInputStream in = new DataInputStream(
                new BufferedInputStream(Files.newInputStream(file), 1 << 20))) {
            for (int i = 0; i < heights.length; i++) {
                heights[i] = in.readShort();
            }

        } catch (IOException e) {
            throw new IOException("Unexpected EOF while reading: " + file, e);
        }
    }
    public short heightMeters(int row, int col) {
        return heights[row * SIZE + col];
    }

}
