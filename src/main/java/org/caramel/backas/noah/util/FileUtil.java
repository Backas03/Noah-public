package org.caramel.backas.noah.util;

import java.io.File;
import java.io.IOException;

public class FileUtil {

    public static void checkAndCreateFile(File file) throws IOException {
        checkAndCreateFolder(file.getParentFile());
        if (!file.exists()) file.createNewFile();
    }

    public static void checkAndCreateFolder(File folder) {
        if (!folder.exists()) folder.mkdirs();
    }

}
