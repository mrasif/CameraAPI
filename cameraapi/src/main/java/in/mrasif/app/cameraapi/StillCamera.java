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
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

import in.mrasif.app.cameraapi.helper.CameraPreview;
import in.mrasif.app.cameraapi.utils.Utils;

public class StillCamera extends AppCompatActivity {

    private static final String TAG = "StillCamera";
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    public static final int PICTURE_REQUEST_CODE=3501;
    public static final String URL="url";
    public static final String WORKING_DIR = "working_directory";

    private Camera mCamera;
    private CameraPreview mPreview;

    private Button btnCapture, btnSave, btnCancel;
    private byte[] image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
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
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);


        btnCapture=findViewById(R.id.button_capture);
        btnSave=findViewById(R.id.button_save);
        btnCancel=findViewById(R.id.button_cancel);

        btnCapture.setOnClickListener(v -> {
            mCamera.takePicture(null, null, (byte[] data, Camera camera) -> {
                image=data;
                btnSave.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
                btnCapture.setVisibility(View.GONE);
            });
        });

        btnSave.setOnClickListener(v -> {
            Intent intent=getIntent();
            String working_dir=intent.getStringExtra(StillCamera.WORKING_DIR);
            Log.d(TAG, "onCreate: "+working_dir);
            File photo= new File(Utils.prepareCompleteFilePath("jpg"));
            if (null!=working_dir){
                photo= new File(Utils.prepareCompleteFilePath(working_dir,"jpg"));
            }
            saveImage(image,photo);
            intent.putExtra(StillCamera.URL,photo.getPath());
            setResult(RESULT_OK,intent);
            finish();
        });

        btnCancel.setOnClickListener(v -> {
            btnSave.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            btnCapture.setVisibility(View.VISIBLE);
            mCamera.startPreview();
        });
    }

    private boolean saveImage(byte[] image, File photo){
        boolean isSaved=false;
        if (photo.exists()) {
            photo.delete();
        }
        try {
            FileOutputStream fos=new FileOutputStream(photo.getPath());
            Toast.makeText(this, photo.getPath(), Toast.LENGTH_SHORT).show();
            fos.write(image);
            fos.close();
            isSaved=true;
        }
        catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }
        return isSaved;
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

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        mCamera.release();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mPreview.releasePointerCapture();
        }
        super.onDestroy();
    }
}
