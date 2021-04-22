package FileUtils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Objects;

public class DeletingPathVisitor extends CountingPathVisitor {


    private final String[] skip;
    private final boolean overrideReadOnly;
    private final LinkOption[] linkOptions;


    public DeletingPathVisitor(final Counters.PathCounters pathCounter, final LinkOption[] linkOptions,
                               final DeleteOption[] deleteOption, final String... skip) {
        super(pathCounter);
        final String[] temp = skip != null ? skip.clone() : EMPTY_STRING_ARRAY;
        Arrays.sort(temp);
        this.skip = temp;
        this.overrideReadOnly = StandardDeleteOption.overrideReadOnly(deleteOption);
        this.linkOptions = linkOptions == null ? PathUtils.NOFOLLOW_LINK_OPTION_ARRAY : linkOptions.clone();
    }


    private boolean accept(final Path path) {
        return Arrays.binarySearch(skip, Objects.toString(path.getFileName(), null)) < 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DeletingPathVisitor other = (DeletingPathVisitor) obj;
        return overrideReadOnly == other.overrideReadOnly && Arrays.equals(skip, other.skip);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Arrays.hashCode(skip);
        result = prime * result + Objects.hash(overrideReadOnly);
        return result;
    }

    @Override
    public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
        if (PathUtils.isEmptyDirectory(dir)) {
            Files.deleteIfExists(dir);
        }
        return super.postVisitDirectory(dir, exc);
    }

    @Override
    public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
        super.preVisitDirectory(dir, attrs);
        return accept(dir) ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
    }

    @Override
    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
        if (accept(file)) {
            if (Files.exists(file, linkOptions)) {
                if (overrideReadOnly) {
                    PathUtils.setReadOnly(file, false, linkOptions);
                }
                Files.deleteIfExists(file);
            }
            if (Files.isSymbolicLink(file)) {
                try {
                    Files.delete(file);
                } catch (final NoSuchFileException e) {
                    // ignore
                }
            }
        }
        updateFileCounters(attrs);
        return FileVisitResult.CONTINUE;
    }
}
