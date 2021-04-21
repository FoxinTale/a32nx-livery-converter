package FileUtils.FileFilters;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public interface IOFileFilter extends FileFilter, FilenameFilter, PathFilter {

    String[] EMPTY_STRING_ARRAY = new String[0];


    @Override
    boolean accept(File file);

    @Override
    boolean accept(File dir, String name);

    @Override
    default FileVisitResult accept(final Path path, final BasicFileAttributes attributes) {
        return AbstractFileFilter.toFileVisitResult(accept(path.toFile()), path);
    }

    default IOFileFilter and(final IOFileFilter fileFilter) {
        return new AndFileFilter(this, fileFilter);
    }


    default IOFileFilter negate() {
        return new NotFileFilter(this);
    }


    default IOFileFilter or(final IOFileFilter fileFilter) {
        return new OrFileFilter(this, fileFilter);
    }
}
