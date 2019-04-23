package in.mrasif.app.cameraapi.helper;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import static android.content.ContentValues.TAG;

public class VideoPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private MediaRecorder recorder;

    public VideoPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;

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

    public void startRecording(String path) {
        try {
            mCamera.unlock();
            recorder = new MediaRecorder();
            recorder.setCamera(mCamera);
            recorder.setOrientationHint(90);
//            recorder.setVideoSize(1080,1080);
            recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
            recorder.setOutputFile(path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            recorder.setPreviewDisplay(mHolder.getSurface());
            recorder.prepare();
            recorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopCapturingVideo() {
        try {
            recorder.stop();
            mCamera.lock();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}