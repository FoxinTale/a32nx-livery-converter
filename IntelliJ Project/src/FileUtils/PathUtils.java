package FileUtils;

import FileUtils.FileFilters.PathFilter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class PathUtils {
    public static final DeleteOption[] EMPTY_DELETE_OPTION_ARRAY = new DeleteOption[0];
    public static final LinkOption[] EMPTY_LINK_OPTION_ARRAY = new LinkOption[0];
    public static final LinkOption[] NOFOLLOW_LINK_OPTION_ARRAY = new LinkOption[] {LinkOption.NOFOLLOW_LINKS};

    public static Counters.PathCounters delete(final Path path, final LinkOption[] linkOptions,
                                               final DeleteOption... deleteOptions) throws IOException {
        // File deletion through Files deletes links, not targets, so use LinkOption.NOFOLLOW_LINKS.
        return Files.isDirectory(path, linkOptions) ? deleteDirectory(path, linkOptions, deleteOptions)
                : deleteFile(path, linkOptions, deleteOptions);
    }


    public static Counters.PathCounters deleteDirectory(final Path directory, final LinkOption[] linkOptions,
                                                        final DeleteOption... deleteOptions) throws IOException {
        return visitFileTree(new DeletingPathVisitor(Counters.longPathCounters(), linkOptions, deleteOptions),
                directory).getPathCounters();
    }

    public static <T extends FileVisitor<? super Path>> T visitFileTree(final T visitor, final Path directory)
            throws IOException {
        Files.walkFileTree(directory, visitor);
        return visitor;
    }

    public static <T extends FileVisitor<? super Path>> T visitFileTree(final T visitor, final Path start,
      final Set<FileVisitOption> options, final int maxDepth) throws IOException {
        Files.walkFileTree(start, options, maxDepth, visitor);
        return visitor;
    }

    public static <T extends FileVisitor<? super Path>> T visitFileTree(final T visitor, final String first,
        final String... more) throws IOException {
        return visitFileTree(visitor, Paths.get(first, more));
    }

    public static <T extends FileVisitor<? super Path>> T visitFileTree(final T visitor, final URI uri)
            throws IOException {
        return visitFileTree(visitor, Paths.get(uri));
    }

    public static boolean isEmptyDirectory(final Path directory) throws IOException {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
            return !directoryStream.iterator().hasNext();
        }
    }


    public static Counters.PathCounters deleteFile(final Path file, final LinkOption[] linkOptions,
                                                   final DeleteOption... deleteOptions) throws NoSuchFileException, IOException {
        if (Files.isDirectory(file, linkOptions)) {
            throw new NoSuchFileException(file.toString());
        }
        final Counters.PathCounters pathCounts = Counters.longPathCounters();
        final boolean exists = Files.exists(file, linkOptions);
        final long size = exists && !Files.isSymbolicLink(file) ? Files.size(file) : 0;
        if (overrideReadOnly(deleteOptions) && exists) {
            setReadOnly(file, false, linkOptions);
        }
        if (Files.deleteIfExists(file)) {
            pathCounts.getFileCounter().increment();
            pathCounts.getByteCounter().add(size);
        }
        return pathCounts;
    }

    public static Path setReadOnly(final Path path, final boolean readOnly, final LinkOption... linkOptions)
            throws IOException {
        final List<Exception> causeList = new ArrayList<>(2);
        final DosFileAttributeView fileAttributeView = Files.getFileAttributeView(path, DosFileAttributeView.class,
                linkOptions);
        if (fileAttributeView != null) {
            try {
                fileAttributeView.setReadOnly(readOnly);
                return path;
            } catch (final IOException e) {
                // ignore for now, retry with PosixFileAttributeView
                causeList.add(e);
            }
        }
        final PosixFileAttributeView posixFileAttributeView = Files.getFileAttributeView(path,
                PosixFileAttributeView.class, linkOptions);
        if (posixFileAttributeView != null) {
            // Works on Windows but not on Ubuntu:
            // Files.setAttribute(path, "unix:readonly", readOnly, options);
            // java.lang.IllegalArgumentException: 'unix:readonly' not recognized
            final PosixFileAttributes readAttributes = posixFileAttributeView.readAttributes();
            final Set<PosixFilePermission> permissions = readAttributes.permissions();
            permissions.remove(PosixFilePermission.OWNER_WRITE);
            permissions.remove(PosixFilePermission.GROUP_WRITE);
            permissions.remove(PosixFilePermission.OTHERS_WRITE);
            try {
                return Files.setPosixFilePermissions(path, permissions);
            } catch (final IOException e) {
                causeList.add(e);
            }
        }
        if (!causeList.isEmpty()) {
            throw new IOExceptionList(path.toString(), causeList);
        }
        throw new IOException(
                String.format("No DosFileAttributeView or PosixFileAttributeView for '%s' (linkOptions=%s)", path,
                        Arrays.toString(linkOptions)));
    }

    private static boolean overrideReadOnly(final DeleteOption... deleteOptions) {
        if (deleteOptions == null) {
            return false;
        }
        for (final DeleteOption deleteOption : deleteOptions) {
            if (deleteOption == StandardDeleteOption.OVERRIDE_READ_ONLY) {
                return true;
            }
        }
        return false;
    }

    public static Stream<Path> walk(final Path start, final PathFilter pathFilter, final int maxDepth,
                                    final boolean readAttributes, final FileVisitOption... options) throws IOException {
        return Files.walk(start, maxDepth, options).filter(path -> pathFilter.accept(path,
                readAttributes ? readBasicFileAttributesUnchecked(path) : null) == FileVisitResult.CONTINUE);
    }

    public static BasicFileAttributes readBasicFileAttributes(final Path path) throws IOException {
        return Files.readAttributes(path, BasicFileAttributes.class);
    }

    public static BasicFileAttributes readBasicFileAttributesUnchecked(final Path path) {
        try {
            return readBasicFileAttributes(path);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
