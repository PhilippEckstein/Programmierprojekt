import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        Path srtmDir = Paths.get("C:\\Users\\phili\\Documents\\Programmierprojekt\\data\\srtm");
        HgtTile tile = new HgtTile(
                srtmDir,
                "N47E005",
                47,
                5
        );
        System.out.println(tile.heightMeters(0, 0));
        System.out.println(tile.heightMeters(3600, 3600));
    }
}