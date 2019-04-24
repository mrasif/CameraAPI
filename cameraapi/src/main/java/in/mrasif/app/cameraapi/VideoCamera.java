package in.mrasif.app.cameraapi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import in.mrasif.app.cameraapi.helper.VideoPreviewHelper;
import in.mrasif.app.cameraapi.utils.Utils;

public class VideoCamera extends AppCompatActivity {
    private static final String TAG = "VideoCamera";
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    public static final int VIDEO_REQUEST_CODE=3502;
    public static final String VIDEO_DURARION="video_duration";
    public static final String URL="url";
    public static final String WORKING_DIR = "working_directory";

    private VideoPreviewHelper videoPreviewHelper;
    private Button btnStart, btnStop;
    private TextView tvTitle;
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
        videoPreviewHelper = new VideoPreviewHelper(this);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(videoPreviewHelper);

        btnStart=findViewById(R.id.btnStart);
        btnStop=findViewById(R.id.btnStop);
        tvTitle=findViewById(R.id.tvTitle);

        Intent intent=getIntent();
        String working_dir=intent.getStringExtra(VideoCamera.WORKING_DIR);
        if (null!=working_dir){
            video_path= Utils.prepareCompleteFilePath(working_dir,"mp4");
        }
        else {
            video_path= Utils.prepareCompleteFilePath("mp4");
        }

        int duration=intent.getIntExtra(VideoCamera.VIDEO_DURARION,0);

        btnStart.setOnClickListener(v -> {
            if (duration>0){
                reverseTimer(duration,tvTitle);
            }
            videoPreviewHelper.startRecording(video_path);
            btnStart.setVisibility(View.GONE);
            btnStop.setVisibility(View.VISIBLE);
        });
        btnStop.setOnClickListener(v -> {
            videoPreviewHelper.stopCapturingVideo();
            btnStart.setVisibility(View.VISIBLE);
            btnStop.setVisibility(View.GONE);
            intent.putExtra(VideoCamera.URL,video_path);
            setResult(RESULT_OK,intent);
            finish();
        });
    }

    public void reverseTimer(int Seconds,final TextView tv){

        new CountDownTimer(Seconds* 1000+1000, 1000) {

            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                tv.setText("TIME : " + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
            }

            public void onFinish() {
                tv.setText("Completed");
                btnStop.callOnClick();
            }
        }.start();
    }

}
