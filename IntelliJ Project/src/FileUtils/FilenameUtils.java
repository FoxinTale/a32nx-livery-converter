package FileUtils;

import java.io.File;

public class FilenameUtils {

    private static final char WINDOWS_SEPARATOR = '\\';

    private static final char SYSTEM_SEPARATOR = File.separatorChar;
    static boolean isSystemWindows() {
        return SYSTEM_SEPARATOR == WINDOWS_SEPARATOR;
    }
}
