package GUI;

import common.ProgressWatcher;
import common.Utils;
import logic.Generator;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GeneratorController implements ProgressWatcher {

    private JProgressBar progressBar;
    private JTextArea log;
    private CustomTableModel tableData;
    private Generator generator = new Generator();

    public GeneratorController(JProgressBar progressBar, JTextArea log, CustomTableModel tableData) {
        this.progressBar = progressBar;
        this.log = log;
        this.tableData = tableData;
        generator.subscribeWatcher(this);
    }

    public void GeneratePlaylist(final ArrayList<File> sourceList, final File destinationDir, final long maxSize) {
        log.setText("");
        log("Discovering Songs....");
        ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(1);
        threadPool.schedule(new Runnable() {
            @Override
            public void run() {
                generator.setSourceList(sourceList);
                generator.generatePlaylist(Utils.megaBytesToBytes(maxSize));
                generator.copyPlaylist(destinationDir);
            }
        }, 0, TimeUnit.MILLISECONDS);
    }

    @Override
    public void percentComplete(int percentComplete) {
        progressBar.setValue(percentComplete);
    }

    @Override
    public void log(String message) {
        log.append(message + "\n");
    }

    @Override
    public void updateSongsFound(File directory, int songsFound) {
        for (int i = 0; i < tableData.getRowCount(); i++) {
            String rowDirectory = (String)tableData.getValueAt(i, 0);
            if (directory.getAbsolutePath().equals(rowDirectory)) {
                tableData.setRowSongs(i, Integer.toString(songsFound));
            }
        }
    }
}
