package act.sds.samsung.angelman.presentation.util;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileUtil {

    private static final int BUFFER_SIZE = 8192;

    public static void removeFile(String path){
        try {
            File file = new File(path);
            file.delete();
        }catch (NullPointerException e){
            Log.e("Exception", "Not found file " + path);
        }
    }

    public static void removeFilesIn(String path) {
        File directory = new File(path);
        if(directory.isDirectory()) {
            File[] files = directory.listFiles();
            for(File file : files) {
                file.delete();
            }
        }
    }

    public static void copyFile(File in, File out) throws IOException {
        copyFile(new FileInputStream(in), new FileOutputStream(out));
    }

    public static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    public static boolean isFileExist(String filePath){
        return new File(filePath).exists();
    }

    public static void zip(String[] files, String zipFile) throws IOException {
        BufferedInputStream origin;
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
        try {
            byte data[] = new byte[BUFFER_SIZE];

            for (int i = 0; i < files.length; i++) {
                FileInputStream fi = new FileInputStream(files[i]);
                origin = new BufferedInputStream(fi, BUFFER_SIZE);
                try {
                    ZipEntry entry = new ZipEntry(files[i].substring(files[i].lastIndexOf(File.separator) + 1));
                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
                        out.write(data, 0, count);
                    }
                } finally {
                    origin.close();
                }
            }
        } finally {
            out.close();
        }
    }

    public static void unzip(String zipFile, String location) throws IOException {
        try {
            Log.d("#", zipFile);
            Log.d("#", location);

            File f = new File(location);
            if (!f.isDirectory()) {
                f.mkdirs();
            }
            ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile));
            try {
                ZipEntry ze;
                while ((ze = zin.getNextEntry()) != null) {
                    String path = location + File.separator + ze.getName();
                    if (ze.isDirectory()) {
                        File unzipFile = new File(path);
                        if (!unzipFile.isDirectory()) {
                            unzipFile.mkdirs();
                        }
                    } else {
                        byte data[] = new byte[BUFFER_SIZE];
                        BufferedOutputStream fout = new BufferedOutputStream( new FileOutputStream(path, false), BUFFER_SIZE);
                        try {
                            int count;
                            while ((count = zin.read(data, 0, BUFFER_SIZE)) != -1) {
                                fout.write(data, 0, count);
                            }
                            zin.closeEntry();
                        } finally {
                            fout.close();
                        }
                    }
                }
            } finally {
                zin.close();
            }
        } catch (Exception e) {
            Log.e("Error", "Unzip exception", e);
        }
    }
}
