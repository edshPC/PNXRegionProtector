package Sergey_Dertan.SRegionProtector.Utils;

import cn.nukkit.plugin.Library;
import cn.nukkit.plugin.LibraryLoadException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.logging.Logger;

public class LibraryLoader {

    private static final File BASE_FOLDER = new File("./libs");
    private static final Logger LOGGER = Logger.getLogger("LibraryLoader");
    private static final String SUFFIX = ".jar";

    static {
        if (BASE_FOLDER.mkdir()) {
            LOGGER.info("Created libraries folder.");
        }
    }

    public static boolean load(String library) throws LibraryLoadException {
        String[] split = library.split(":");
        if (split.length != 3) {
            throw new IllegalArgumentException(library);
        }
        return load(new Library() {
            @Override
            public String getGroupId() {
                return split[0];
            }

            @Override
            public String getArtifactId() {
                return split[1];
            }

            @Override
            public String getVersion() {
                return split[2];
            }
        });
    }

    public static boolean load(Library library) throws LibraryLoadException {
        String filePath = library.getGroupId().replace('.', '/') + '/' + library.getArtifactId() + '/' + library.getVersion();
        String fileName = library.getArtifactId() + '-' + library.getVersion() + SUFFIX;

        File folder = BASE_FOLDER;
        if (folder.mkdirs()) {
            LOGGER.info("Created " + folder.getPath() + '.');
        }

        File file = new File(folder, fileName);
        if(file.isFile()) return false;

        try {
            URL url = new URL("https://repo1.maven.org/maven2/" + filePath + '/' + fileName);
            LOGGER.info("Get library from " + url + '.');
            Files.copy(url.openStream(), file.toPath());
            LOGGER.info("Get library " + fileName + " done!");
            return true;
        } catch (IOException e) {
            throw new LibraryLoadException(library);
        }

    }

    public static File getBaseFolder() {
        return BASE_FOLDER;
    }

}