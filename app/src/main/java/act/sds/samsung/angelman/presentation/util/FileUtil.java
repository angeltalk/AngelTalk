package act.sds.samsung.angelman.presentation.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
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

import static act.sds.samsung.angelman.presentation.util.ContentsUtil.getContentFolder;

public class FileUtil {

    public static final String ANGELMAN_FOLDER = "angelman";
    public static final String VOICE_FOLDER = "voice";

    public static final String IMAGE_FULL_PATH = ANGELMAN_FOLDER + File.separator + ContentsUtil.CONTENT_FOLDER;
    public static final String VOICE_FULL_PATH = ANGELMAN_FOLDER + File.separator + VOICE_FOLDER;
    public static final int BUFFER_SIZE = 8192;

    public static void initExternalStorageFolder() {
        File rootFolder = new File(Environment.getExternalStorageDirectory() + File.separator + ANGELMAN_FOLDER);

        if (!rootFolder.exists()) {
            rootFolder.mkdir();
        }

        File imageFolder = new File(ContentsUtil.getContentFolder());

        if (!imageFolder.exists()) {
            imageFolder.mkdir();
        }

        File voiceFolder = new File(ContentsUtil.getVoiceFolder());

        if (!voiceFolder.exists())
            voiceFolder.mkdir();
    }

    public static void copyDefaultAssetImagesToImageFolder(Context context) {
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("contents");
        } catch (IOException e) {
            Log.e("AngelmanApplication", "Failed to get asset file list.", e);
        }
        if (files != null) for (String fileName : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open("contents" + File.separator + fileName);
                File outFile = new File(getContentFolder(), fileName);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            } catch(IOException e) {
                Log.e("AngelmanApplication", "Failed to copy asset file: " + fileName, e);
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        Log.e("AngelmanApplication", "Failed to close image input file.", e);
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        Log.e("AngelmanApplication", "Failed to close image output file.", e);
                    }
                }
            }
        }
    }

    public static void removeFile(String path){
        try {
            File file = new File(path);
            file.delete();
        }catch (NullPointerException e){
            Log.e("Exception", "Not found file " + path);
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    public static boolean isContentExist(String contentPath){
        return  new File(contentPath).exists();
    }

    public static void zip(String[] files, String zipFile) throws IOException {
        BufferedInputStream origin = null;
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
        try {
            byte data[] = new byte[BUFFER_SIZE];

            for (int i = 0; i < files.length; i++) {
                FileInputStream fi = new FileInputStream(files[i]);
                origin = new BufferedInputStream(fi, BUFFER_SIZE);
                try {
                    ZipEntry entry = new ZipEntry(files[i].substring(files[i].lastIndexOf("/") + 1));
                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
                        out.write(data, 0, count);
                    }
                }
                finally {
                    origin.close();
                }
            }
        }
        finally {
            out.close();
        }
    }

    public static void unzip(String zipFile, String location) throws IOException {
        int size;
        byte[] buffer = new byte[BUFFER_SIZE];

        try {
            if ( !location.endsWith("/") ) {
                location += "/";
            }
            File f = new File(location);
            if(!f.isDirectory()) {
                f.mkdirs();
            }
            ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile), BUFFER_SIZE));
            try {
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {
                    String path = location + ze.getName();
                    File unzipFile = new File(path);

                    if (ze.isDirectory()) {
                        if(!unzipFile.isDirectory()) {
                            unzipFile.mkdirs();
                        }
                    } else {
                        // check for and create parent directories if they don't exist
                        File parentDir = unzipFile.getParentFile();
                        if ( null != parentDir ) {
                            if ( !parentDir.isDirectory() ) {
                                parentDir.mkdirs();
                            }
                        }

                        // unzip the file
                        FileOutputStream out = new FileOutputStream(unzipFile, false);
                        BufferedOutputStream fout = new BufferedOutputStream(out, BUFFER_SIZE);
                        try {
                            while ( (size = zin.read(buffer, 0,  BUFFER_SIZE)) != -1 ) {
                                fout.write(buffer, 0, size);
                            }

                            zin.closeEntry();
                        }
                        finally {
                            fout.flush();
                            fout.close();
                        }
                    }
                }
            }
            finally {
                zin.close();
            }
        }
        catch (Exception e) {
            Log.e("#", "Unzip exception", e);
        }
    }

}
