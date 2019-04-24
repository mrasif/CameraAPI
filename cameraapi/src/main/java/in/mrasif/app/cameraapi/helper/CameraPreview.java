package in.mrasif.app.cameraapi.helper;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.ContentValues.TAG;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private byte[] image;

    public CameraPreview(Context context) {
        super(context);
        mCamera = getCameraInstance();

        Camera.Parameters parameters = mCamera.getParameters();
        if (this.getResources().getConfiguration().orientation !=
                Configuration.ORIENTATION_LANDSCAPE)
        {
            parameters.set("orientation", "portrait"); //<----THis gets the job done!!!
                // For Android Version 2.2 and above
                mCamera.setDisplayOrientation(90);
            // For Android Version 2.0 and above
            parameters.setRotation(90);
        }
        mCamera.setParameters(parameters);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.setFixedSize(1080,1080);
        mHolder.setKeepScreenOn(true);
        mHolder.addCallback(this);

        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    private Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    public void takePicture(){
        mCamera.takePicture(null, null, (byte[] data, Camera camera) -> {
            image=data;
        });
    }

    public void savePicture(File path){
        saveImage(path);
    }

    public void startPreview(){
        mCamera.startPreview();
    }

    public void destroy(){
        mCamera.release();
    }

    private boolean saveImage(File photo){
        boolean isSaved=false;
        if (photo.exists()) {
            photo.delete();
        }
        try {
            FileOutputStream fos=new FileOutputStream(photo.getPath());
            fos.write(image);
            fos.close();
            isSaved=true;
        }
        catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }
        return isSaved;
    }
}