package FileUtils.FileFilters;

import java.io.File;
import java.io.Serializable;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class FalseFileFilter implements IOFileFilter, Serializable {

    private static final String TO_STRING = Boolean.FALSE.toString();
    public static final IOFileFilter FALSE = new FalseFileFilter();
    public static final IOFileFilter INSTANCE = FALSE;
    private static final long serialVersionUID = 6210271677940926200L;


    protected FalseFileFilter() {
    }

    @Override
    public boolean accept(final File file) {
        return false;
    }

    @Override
    public boolean accept(final File dir, final String name) {
        return false;
    }

    @Override
    public FileVisitResult accept(final Path file, final BasicFileAttributes attributes) {
        return FileVisitResult.TERMINATE;
    }

    @Override
    public IOFileFilter negate() {
        return TrueFileFilter.INSTANCE;
    }

    @Override
    public String toString() {
        return TO_STRING;
    }

    @Override
    public IOFileFilter and(final IOFileFilter fileFilter) {
        return INSTANCE;
    }

    @Override
    public IOFileFilter or(final IOFileFilter fileFilter) {
        return fileFilter;
    }
}