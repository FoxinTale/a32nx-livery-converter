package FileUtils.FileFilters;

import java.util.List;

public interface ConditionalFileFilter {
    void addFileFilter(IOFileFilter ioFileFilter);
    List<IOFileFilter> getFileFilters();
    boolean removeFileFilter(IOFileFilter ioFileFilter);
    void setFileFilters(List<IOFileFilter> fileFilters);
}