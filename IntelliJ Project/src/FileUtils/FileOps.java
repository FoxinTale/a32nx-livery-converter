package FileUtils;

import FileUtils.FileFilters.FileFileFilter;
import FileUtils.FileFilters.IOFileFilter;
import FileUtils.FileFilters.SuffixFileFilter;

import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// File based operations. Mostly for copying the livery.
public class FileOps {
    public static void copyDirectory(final File srcDir, final File destDir) throws IOException {
        copyDirectory(srcDir, destDir, true);
    }


    public static void copyDirectory(final File srcDir, final File destDir,
                                     final boolean preserveFileDate) throws IOException {
        copyDirectory(srcDir, destDir, null, preserveFileDate);
    }


    public static void copyDirectory(final File srcDir, final File destDir, final FileFilter filter,
                                     final boolean preserveFileDate) throws IOException {
        copyDirectory(srcDir, destDir, filter, preserveFileDate, StandardCopyOption.REPLACE_EXISTING);
    }


    public static void copyDirectory(final File srcDir, final File destDir, final FileFilter filter,
                                     final boolean preserveFileDate, final CopyOption... copyOptions) throws IOException {
        checkFileRequirements(srcDir, destDir);
        if (!srcDir.isDirectory()) {
            throw new IOException("Source '" + srcDir + "' exists but is not a directory");
        }
        if (srcDir.getCanonicalPath().equals(destDir.getCanonicalPath())) {
            throw new IOException("Source '" + srcDir + "' and destination '" + destDir + "' are the same");
        }

        List<String> exclusionList = null;
        if (destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath())) {
            final File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
            if (srcFiles != null && srcFiles.length > 0) {
                exclusionList = new ArrayList<>(srcFiles.length);
                for (final File srcFile : srcFiles) {
                    final File copiedFile = new File(destDir, srcFile.getName());
                    exclusionList.add(copiedFile.getCanonicalPath());
                }
            }
        }
        doCopyDirectory(srcDir, destDir, filter, preserveFileDate, exclusionList, copyOptions);
    }


    private static void checkFileRequirements(final File source, final File destination) throws FileNotFoundException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(destination, "target");
        if (!source.exists()) {
            throw new FileNotFoundException("Source '" + source + "' does not exist");
        }
    }


    private static void doCopyDirectory(final File srcDir, final File destDir, final FileFilter filter,
                                        final boolean preserveFileDate, final List<String> exclusionList, final CopyOption... copyOptions)
            throws IOException {
        final File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
        if (srcFiles == null) {
            throw new IOException("Failed to list contents of " + srcDir);
        }
        if (destDir.exists()) {
            if (!destDir.isDirectory()) {
                throw new IOException("Destination '" + destDir + "' exists but is not a directory");
            }
        } else {
            if (!destDir.mkdirs() && !destDir.isDirectory()) {
                throw new IOException("Destination '" + destDir + "' directory cannot be created");
            }
        }
        if (!destDir.canWrite()) {
            throw new IOException("Destination '" + destDir + "' cannot be written to");
        }
        for (final File srcFile : srcFiles) {
            final File dstFile = new File(destDir, srcFile.getName());
            if (exclusionList == null || !exclusionList.contains(srcFile.getCanonicalPath())) {
                if (srcFile.isDirectory()) {
                    doCopyDirectory(srcFile, dstFile, filter, preserveFileDate, exclusionList, copyOptions);
                } else {
                    doCopyFile(srcFile, dstFile, preserveFileDate, copyOptions);
                }
            }
        }

        if (preserveFileDate) {
            setLastModified(srcDir, destDir);
        }
    }



    private static void doCopyFile(final File srcFile, final File destFile, final boolean preserveFileDate, final CopyOption... copyOptions)
            throws IOException {
        if (destFile.exists() && destFile.isDirectory()) {
            throw new IOException("Destination '" + destFile + "' exists but is a directory");
        }

        final Path srcPath = srcFile.toPath();
        final Path destPath = destFile.toPath();
        Files.copy(srcPath, destPath, copyOptions);

        checkEqualSizes(srcFile, destFile, Files.size(srcPath), Files.size(destPath));
        checkEqualSizes(srcFile, destFile, srcFile.length(), destFile.length());

        if (preserveFileDate) {
            setLastModified(srcFile, destFile);
        }
    }


    private static void setLastModified(final File sourceFile, final File targetFile) throws IOException {
        if (!targetFile.setLastModified(sourceFile.lastModified())) {
            throw new IOException("Failed setLastModified on " + sourceFile);
        }
    }


    private static void checkEqualSizes(final File srcFile, final File destFile, final long srcLen, final long dstLen)
            throws IOException {
        if (srcLen != dstLen) {
            throw new IOException("Failed to copy full contents from '" + srcFile + "' to '" + destFile
                    + "' Expected length: " + srcLen + " Actual: " + dstLen);
        }
    }

// Folder deletion


    public static void deleteDirectory(final File directory) throws IOException {
        Objects.requireNonNull(directory, "directory");
        if (!directory.exists()) {
            return;
        }
        if (!isSymlink(directory)) {
            cleanDirectory(directory);
        }
        delete(directory);
    }

    private static File[] listFiles(final File directory, final FileFilter fileFilter) throws IOException {
        requireDirectoryExists(directory, "directory");
        final File[] files = fileFilter == null ? directory.listFiles() : directory.listFiles(fileFilter);
        if (files == null) {
            throw new IOException("Unknown I/O error listing contents of directory: " + directory);
        }
        return files;
    }


    public static Collection<File> listFiles(final File directory, final String[] extensions, final boolean recursive) {
        try {
            return toList(streamFiles(directory, recursive, extensions));
        } catch (final IOException e) {
            throw new UncheckedIOException(directory.toString(), e);
        }
    }


    public static Stream<File> streamFiles(final File directory, final boolean recursive, final String... extensions)
            throws IOException {
        final IOFileFilter filter = extensions == null ? FileFileFilter.INSTANCE
                : FileFileFilter.INSTANCE.and(new SuffixFileFilter(toSuffixes(extensions)));
        return PathUtils.walk(directory.toPath(), filter, toMaxDepth(recursive), false).map(Path::toFile);
    }



    public static void cleanDirectory(final File directory) throws IOException {
        final File[] files = listFiles(directory, null);

        final List<Exception> causeList = new ArrayList<>();
        for (final File file : files) {
            try {
                forceDelete(file);
            } catch (final IOException ioe) {
                causeList.add(ioe);
            }
        }

        if (!causeList.isEmpty()) {
            throw new IOExceptionList(directory.toString(), causeList);
        }
    }


    public static boolean isSymlink(final File file) {
        return file != null ? Files.isSymbolicLink(file.toPath()) : false;
    }


    public static void delete(final File file) throws IOException {
        Objects.requireNonNull(file, "file");
        Files.delete(file.toPath());
    }



    public static void forceDelete(final File file) throws IOException {
        Objects.requireNonNull(file, "file");
        final Counters.PathCounters deleteCounters;
        try {
            deleteCounters = PathUtils.delete(file.toPath(), PathUtils.EMPTY_LINK_OPTION_ARRAY,
                    StandardDeleteOption.OVERRIDE_READ_ONLY);
        } catch (final IOException e) {
            throw new IOException("Cannot delete file: " + file, e);
        }

        if (deleteCounters.getFileCounter().get() < 1 && deleteCounters.getDirectoryCounter().get() < 1) {
            // didn't find a file to delete.
            throw new FileNotFoundException("File does not exist: " + file);
        }
    }


    private static List<File> toList(final Stream<File> stream) {
        return stream.collect(Collectors.toList());
    }


    private static int toMaxDepth(final boolean recursive) {
        return recursive ? Integer.MAX_VALUE : 1;
    }

    private static String[] toSuffixes(final String... extensions) {
        Objects.requireNonNull(extensions, "extensions");
        final String[] suffixes = new String[extensions.length];
        for (int i = 0; i < extensions.length; i++) {
            suffixes[i] = "." + extensions[i];
        }
        return suffixes;
    }

    private static void requireDirectoryExists(final File directory, final String name) {
        requireExists(directory, name);
        requireDirectory(directory, name);
    }

    private static void requireDirectory(final File directory, final String name) {
        Objects.requireNonNull(directory, name);
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Parameter '" + name + "' is not a directory: '" + directory + "'");
        }
    }

    private static void requireExists(final File file, final String fileParamName) {
        Objects.requireNonNull(file, fileParamName);
        if (!file.exists()) {
            throw new IllegalArgumentException(
                    "File system element for parameter '" + fileParamName + "' does not exist: '" + file + "'");
        }
    }
}
