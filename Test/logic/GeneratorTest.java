package logic;

import common.ProgressWatcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

@RunWith(MockitoJUnitRunner.class)
public class GeneratorTest {

    @Mock File dirA;
    @Mock File dirB;
    @Mock File dirC;

    @Mock File fileA;
    @Mock File fileB;
    @Mock File fileC;
    @Mock File fileD;
    @Mock File fileE;
    @Mock File fileF;

    ArrayList<File> sourceList = new ArrayList<File>();

    @Test
    public void generatePlaylist() {
        initialiseMockSourceList();
        ProgressWatcher watcher = new ProgressWatcher() {
            @Override
            public void percentComplete(int percentComplete) {
                // Do Nothing
            }
            @Override
            public void log(String message) {
                // Do Nothing
            }
            @Override
            public void updateSongsFound(File directory, int songsFound) {
                // Do Nothing
            }
        };

        Generator generator = new Generator();
        generator.setSourceList(sourceList);
        generator.subscribeWatcher(watcher);

        generator.generatePlaylist(45);
        ArrayList<File> playlist = generator.getChosenFiles();
        assertThat(playlist.size(), is(equalTo(4)));
    }

    private void initialiseMockSourceList() {
        File[] dirAFiles = {fileA, fileB, fileC};
        when(dirA.listFiles(any(FilenameFilter.class))).thenReturn(dirAFiles);

        File[] dirBFiles = {fileD, fileE};
        when(dirB.listFiles(any(FilenameFilter.class))).thenReturn(dirBFiles);

        File[] dirCFiles = {fileF};
        when(dirC.listFiles(any(FilenameFilter.class))).thenReturn(dirCFiles);

        when(dirA.isDirectory()).thenReturn(true);
        when(dirB.isDirectory()).thenReturn(true);
        when(dirC.isDirectory()).thenReturn(true);
        when(fileA.isDirectory()).thenReturn(false);
        when(fileB.isDirectory()).thenReturn(false);
        when(fileC.isDirectory()).thenReturn(false);
        when(fileD.isDirectory()).thenReturn(false);
        when(fileE.isDirectory()).thenReturn(false);
        when(fileF.isDirectory()).thenReturn(false);

        when(fileA.length()).thenReturn(new Long(10));
        when(fileB.length()).thenReturn(new Long(10));
        when(fileC.length()).thenReturn(new Long(10));
        when(fileD.length()).thenReturn(new Long(10));
        when(fileE.length()).thenReturn(new Long(10));
        when(fileF.length()).thenReturn(new Long(10));

        when(fileA.getName()).thenReturn("fileA.mp3");
        when(fileB.getName()).thenReturn("fileB.mp3");
        when(fileC.getName()).thenReturn("fileC.mp3");
        when(fileD.getName()).thenReturn("fileD.mp3");
        when(fileE.getName()).thenReturn("fileE.mp3");
        when(fileF.getName()).thenReturn("fileF.mp3");

        sourceList.add(dirA);
        sourceList.add(dirB);
        sourceList.add(dirC);
    }

}
