package FileUtils;

public class IOUtils {

    public static int length(final byte[] array) {
        return array == null ? 0 : array.length;
    }

    public static int length(final char[] array) {
        return array == null ? 0 : array.length;
    }

    public static int length(final CharSequence csq) {
        return csq == null ? 0 : csq.length();
    }

    public static int length(final Object[] array) {
        return array == null ? 0 : array.length;
    }
}
