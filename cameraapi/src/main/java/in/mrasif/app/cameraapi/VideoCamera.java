package in.mrasif.app.cameraapi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;

import in.mrasif.app.cameraapi.helper.VideoPreview;
import in.mrasif.app.cameraapi.utils.Utils;

public class VideoCamera extends AppCompatActivity {
    private static final String TAG = "VideoCamera";
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    public static final int VIDEO_REQUEST_CODE=3502;
    public static final String URL="url";
    public static final String WORKING_DIR = "working_directory";

    private VideoPreview videoPreview;
    private Camera mCamera;
    private Button btnStart, btnStop;
    private String video_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_camera);
        askForPermission();
    }

    private void askForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    || (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    || (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)) {

                requestPermissions(new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                }, MY_CAMERA_REQUEST_CODE);
            }
            else {
                init();
            }
        }
        else {
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            boolean granted=true;
            Log.d(TAG, "onRequestPermissionsResult: "+grantResults.length);
            for (int grantResult:grantResults){
                if (grantResult== PackageManager.PERMISSION_DENIED){
                    granted=false;
                }
            }
            if (!granted) {
                askForPermission();
            }
            else {
                init();
            }
        }
    }

    private void init() {
        mCamera=getCameraInstance();

        videoPreview = new VideoPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(videoPreview);

        btnStart=findViewById(R.id.btnStart);

        btnStop=findViewById(R.id.btnStop);

        Intent intent=getIntent();
        String working_dir=intent.getStringExtra(StillCamera.WORKING_DIR);
        if (null!=working_dir){
            video_path= Utils.prepareCompleteFilePath(working_dir,"mp4");
        }
        else {
            video_path= Utils.prepareCompleteFilePath("mp4");
        }

        btnStart.setOnClickListener(v -> {
            videoPreview.startRecording(video_path);
        });
        btnStop.setOnClickListener(v -> {
            intent.putExtra(StillCamera.URL,video_path);
            setResult(RESULT_OK,intent);
            finish();
        });
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
}
