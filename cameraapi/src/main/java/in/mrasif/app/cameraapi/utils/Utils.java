package in.mrasif.app.cameraapi.utils;

import android.os.Environment;

import java.io.File;

public class Utils {

    private static final String TAG = "Utils";

    public static File getWorkingDirectory(){
        File rootDir = new File(Environment.getExternalStorageDirectory().getPath(),"CameraAPI");
        if (!rootDir.exists()){
            try{
                rootDir.mkdir();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return rootDir;
    }

    public static File getWorkingDirectory(String dir_name){
        File rootDir = new File(Environment.getExternalStorageDirectory().getPath(),dir_name);
        if (!rootDir.exists()){
            try{
                rootDir.mkdir();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return rootDir;
    }


    public static String getFileSize(String path){
        File file = new File(path);
        float length = file.length() / 1024f; // Size in KB
        String value;
        if(length >= 1024) {
            value = length / 1024f + " MB";
        }
        else {
            value = length + " KB";
        }
        return value;
    }

    public static float getFileSizeInKB(String path){
        File file = new File(path);
        float length = file.length() / 1024f; // Size in KB
        float value;
        if(length >= 1024) {
            value = length / 1024f;
        }
        else {
            value = length;
        }
        return value;
    }

    public static String prepareCompleteFilePath(String extension) {
        File file = Utils.getWorkingDirectory();
        return  (file.getAbsolutePath() + "/" + System.currentTimeMillis() + "."+extension);
    }

    public static String prepareCompleteFilePath(String working_dir,String extension) {
        File file = new File(working_dir);
        return  (file.getAbsolutePath() + "/" + System.currentTimeMillis() + "."+extension);
    }

}
