package in.mrasif.app.cameraapi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;

import in.mrasif.app.cameraapi.helper.CameraPreview;
import in.mrasif.app.cameraapi.utils.Utils;

public class StillCamera extends AppCompatActivity {

    private static final String TAG = "StillCamera";
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    public static final int PICTURE_REQUEST_CODE=3501;
    public static final String URL="url";
    public static final String WORKING_DIR = "working_directory";

    private CameraPreview mPreview;
    private Button btnCapture, btnSave, btnCancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_still_camera);
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
                if (grantResult==PackageManager.PERMISSION_DENIED){
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
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this);
        FrameLayout preview = findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        btnCapture=findViewById(R.id.button_capture);
        btnSave=findViewById(R.id.button_save);
        btnCancel=findViewById(R.id.button_cancel);

        btnCapture.setOnClickListener(v -> {
            mPreview.takePicture();
            btnSave.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
            btnCapture.setVisibility(View.GONE);
        });

        btnSave.setOnClickListener(v -> {
            Intent intent=getIntent();
            String working_dir=intent.getStringExtra(StillCamera.WORKING_DIR);
            File photo= new File(Utils.prepareCompleteFilePath("jpg"));
            if (null!=working_dir){
                photo= new File(Utils.prepareCompleteFilePath(working_dir,"jpg"));
            }
            mPreview.savePicture(photo);
            intent.putExtra(StillCamera.URL,photo.getPath());
            setResult(RESULT_OK,intent);
            finish();
        });

        btnCancel.setOnClickListener(v -> {
            btnSave.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            btnCapture.setVisibility(View.VISIBLE);
            mPreview.startPreview();
        });
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPreview.destroy();
    }
}
