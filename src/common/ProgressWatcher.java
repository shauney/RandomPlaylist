package common;

import java.io.File;

public interface ProgressWatcher {

    public void percentComplete(int percentComplete);

    public void log(String message);

    public void updateSongsFound(File directory, int songsFound);

}
