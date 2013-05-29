package logic;

import common.ProgressWatcher;
import common.Utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Generator {

    ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(1);
    private ArrayList<File> sourceList = new ArrayList<File>();
    private ArrayList<File> mp3List = new ArrayList<File>();
    private ArrayList<File> chosenFiles = new ArrayList<File>();
    private ArrayList<ProgressWatcher> watchers = new ArrayList<ProgressWatcher>();
    private long listFilesize = 0;
    private long maxListFilesize = 1;
    int songsToCopy = 0;
    int songsCopied = 0;
    private FileFilter filter = new FileFilter() {
        @Override
        public boolean accept(File dir) {
            return dir.getName().toLowerCase().endsWith(".mp3") || dir.isDirectory();
        }
    };

    public Generator() {
    }

    public void subscribeWatcher(ProgressWatcher watcher) {
        watchers.add(watcher);
    }

    private void notifyWatchersPercentComplete(int percentComplete) {
        for ( ProgressWatcher watcher : watchers ) {
            watcher.percentComplete(percentComplete);
        }
    }

    private void notifyWatchersSongsFound(int songsFound, File directory) {
        for ( ProgressWatcher watcher : watchers ) {
            watcher.updateSongsFound(directory, songsFound);
        }
    }

    private void sendLogMessageToWatchers(String message) {
        for ( ProgressWatcher watcher : watchers ) {
            watcher.log(message);
        }
    }

    public void generatePlaylist(long maxListFilesize) {
        populateMp3List();
        sendLogMessageToWatchers("Generating Playlist....");
        populateChosenList(maxListFilesize);
        sendLogMessageToWatchers("Playlist Generated!");
        threadPool.shutdown();
    }

    public void copyPlaylist(File destinationDir) {
        songsToCopy = chosenFiles.size();
        songsCopied = 0;

        sendLogMessageToWatchers("Copying Playlist to " + destinationDir.getAbsolutePath());
        notifyWatchersPercentComplete(0);
        checkCopiedPercentage();

        if (!destinationDir.exists()) {
            destinationDir.mkdir();
        }

        for (File song : chosenFiles) {
            File songCopy = new File(destinationDir.getAbsolutePath() + File.separator + song.getName());
            try {
                Utils.copyFile(song, songCopy);
                songsCopied++;
            }
            catch (IOException ex) {
                sendLogMessageToWatchers("Error copying file - " + song.getName());
                sendLogMessageToWatchers(ex.getMessage());
            }
        }

        notifyWatchersPercentComplete(100);
        sendLogMessageToWatchers("Copy Complete.");
    }

    private void checkCopiedPercentage() {
        threadPool = new ScheduledThreadPoolExecutor(1);
        threadPool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                int percentComplete = Utils.getPercentage(songsCopied, songsToCopy);
                    notifyWatchersPercentComplete(percentComplete);
                }
        }, 1, 1, TimeUnit.SECONDS);
    }

    private void populateChosenList(long maxListFilesize) {
        int numOfSongs = mp3List.size();
        this.maxListFilesize = maxListFilesize;
        checkListFilesize();

        while (listFilesize < maxListFilesize && mp3List.size() > 0) {
            int random = (int)(Math.random() * numOfSongs);
            File file = mp3List.remove(random);
            chosenFiles.add(file);
            listFilesize += file.length();
            numOfSongs--;
        }

        // Remove last file to meet size limit
        chosenFiles.remove(chosenFiles.size() - 1);
        sendLogMessageToWatchers("Playlist Size: " + chosenFiles.size() + " Songs, " + Utils.bytesToMegaBytes(listFilesize) + "Mb");
        notifyWatchersPercentComplete(100);
    }

    private void checkListFilesize() {
        threadPool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                int percentComplete = Utils.getPercentage(listFilesize, maxListFilesize);

                if (percentComplete % 10 == 0) {
                    notifyWatchersPercentComplete(percentComplete);
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    private float round(float d) {
        DecimalFormat df = new DecimalFormat("##");
        return Float.valueOf(df.format((d)));
    }

    private void populateMp3List() {
        for (File directory : sourceList) {
            int oldSize = mp3List.size();
            mp3List.addAll(getSongsRecursively(directory));
            int sizeGrown = mp3List.size() - oldSize;

            notifyWatchersSongsFound(sizeGrown, directory);
        }
    }

    private ArrayList<File> getSongsRecursively(File directory) {
        ArrayList<File> songs = new ArrayList<File>();

        if (directory.isDirectory() && directory != null) {
            File[] files = directory.listFiles(filter);

            if (files.length > 0) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        songs.addAll(getSongsRecursively(file));
                    }
                    else {
                        songs.add(file);
                    }
                }
            }
        }

        return songs;
    }

    public void setSourceList(ArrayList<File> sourceList) {
        this.sourceList = sourceList;
    }

    public ArrayList<File> getChosenFiles() {
        return chosenFiles;
    }
}
