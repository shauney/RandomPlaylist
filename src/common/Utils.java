package common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;

public class Utils {

    public static long bytesToMegaBytes(long bytes) {
        return bytes / 1000000;
    }

    public static long megaBytesToBytes(long megaBytes) {
        return megaBytes * 1000000;
    }

    public static int getPercentage(int completed, int total) {
        float firstNumber = (completed/(float)total) * 100;
        return (int)round(firstNumber);
    }

    public static int getPercentage(long completed, long total) {
        float firstNumber = (completed/(float)total) * 100;
        return (int)round(firstNumber);
    }

    private static float round(float d) {
        DecimalFormat df = new DecimalFormat("##");
        return Float.valueOf(df.format((d)));
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if(!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        finally {
            if(source != null) {
                source.close();
            }
            if(destination != null) {
                destination.close();
            }
        }
    }
}
