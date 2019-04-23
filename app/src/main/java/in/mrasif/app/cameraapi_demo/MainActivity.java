package in.mrasif.app.cameraapi_demo;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

import in.mrasif.app.cameraapi.StillCamera;
import in.mrasif.app.cameraapi.VideoCamera;
import in.mrasif.app.cameraapi.utils.Utils;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    Button btnCaptureImage, btnRecordVideo;
    ImageView ivPreview;
    VideoView vvPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCaptureImage=findViewById(R.id.btnCaptureImage);
        btnRecordVideo=findViewById(R.id.btnRecordVideo);
        ivPreview=findViewById(R.id.ivPreview);
        vvPreview=findViewById(R.id.vvPreview);

        btnCaptureImage.setOnClickListener((v)->{
            Intent intent=new Intent(MainActivity.this, StillCamera.class);
            intent.putExtra(StillCamera.WORKING_DIR, Utils.getWorkingDirectory("CameraAPI_CUSTOM2").getPath());
            startActivityForResult(intent,StillCamera.PICTURE_REQUEST_CODE);
        });

        btnRecordVideo.setOnClickListener(v -> {
            Intent intent=new Intent(MainActivity.this, VideoCamera.class);
            intent.putExtra(VideoCamera.WORKING_DIR, Utils.getWorkingDirectory("CameraAPI_CUSTOM2").getPath());
            startActivityForResult(intent,VideoCamera.VIDEO_REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case StillCamera.PICTURE_REQUEST_CODE: {
                if (resultCode==RESULT_OK){
                    String photoPath=data.getStringExtra(StillCamera.URL);
                    if (null!=photoPath){
                        ivPreview.setVisibility(View.VISIBLE);
                        vvPreview.setVisibility(View.GONE);
                        if (vvPreview.isPlaying()){
                            vvPreview.stopPlayback();
                        }
                        Glide.with(getApplicationContext())
                                .applyDefaultRequestOptions(RequestOptions.skipMemoryCacheOf(true))
                                .load(photoPath)
//                                .apply(new RequestOptions().placeholder(resId).error(resId))
                                .into(ivPreview);

                    }
                    Toast.makeText(this, "Image Captured!\n"+photoPath, Toast.LENGTH_SHORT).show();
                }
            } break;

            case VideoCamera.VIDEO_REQUEST_CODE: {
                if (resultCode==RESULT_OK){
                    String videoPath=data.getStringExtra(VideoCamera.URL);
                    Log.d(TAG, "onActivityResult:VideoPath=> "+videoPath);
                    if (null!=videoPath){
                        ivPreview.setVisibility(View.GONE);
                        vvPreview.setVisibility(View.VISIBLE);
                        MediaController mediaController= new MediaController(this);
                        vvPreview.setMediaController(mediaController);
                        mediaController.setAnchorView(vvPreview);
                        vvPreview.setVideoPath(videoPath);
                        vvPreview.start();


                        /*Uri uri = Uri.parse(videoPath);
                        vvPreview.setVideoURI(uri);
                        vvPreview.requestFocus();
                        vvPreview.start();*/

                    }
                    Toast.makeText(this, "Video Recorded!\n"+videoPath, Toast.LENGTH_SHORT).show();
                }
            } break;
        }
    }
}
