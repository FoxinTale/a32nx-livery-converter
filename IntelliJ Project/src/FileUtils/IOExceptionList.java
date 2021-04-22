package FileUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class IOExceptionList extends IOException {

    private static final long serialVersionUID = 1L;
    private final List<? extends Throwable> causeList;


    public IOExceptionList(final String message, final List<? extends Throwable> causeList) {
        super(message, causeList == null ? null : causeList.get(0));
        this.causeList = causeList == null ? Collections.emptyList() : causeList;
    }
}