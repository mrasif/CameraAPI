# CameraAPI
[![](https://jitpack.io/v/mrasif/CameraAPI.svg)](https://github.com/mrasif/CameraAPI)

Camera with Still Image and Video recording Library for Android Development purpose.

## For Gradle:
Step 1. Add it in your root build.gradle at the end of repositories:
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
Step 2. Add the dependency:
```
dependencies {
        implementation 'com.github.mrasif:CameraAPI:1.0.0-beta'
}
```
### For Maven:
Step 1. Add the JitPack repository to your build file:
```
<repositories>
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
</repositories>
```
Step 2. Add the dependency:
```
<dependency>
    <groupId>com.github.mrasif</groupId>
    <artifactId>CameraAPI</artifactId>
    <version>1.0.0-beta</version>
</dependency>
```
### For SBT:
Step 1. Add the JitPack repository to your build.sbt file:
```
resolvers += "jitpack" at "https://jitpack.io"
```
Step 2. Add the dependency:
```
libraryDependencies += "com.github.mrasif" % "CameraAPI" % "1.0.0-beta"
```
### For Leiningen:
Step 1. Add it in your project.clj at the end of repositories:
```
:repositories [["jitpack" "https://jitpack.io"]]
```
Step 2. Add the dependency:
```
:dependencies [[com.github.mrasif/CameraAPI "1.0.0-beta"]]
```

### Add this in your layout xml file:
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:context=".MainActivity">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center"
        android:orientation="horizontal">
        <Button
            android:id="@+id/btnCaptureImage"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:text="Capture Image"/>
        <Button
            android:id="@+id/btnRecordVideo"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:text="Video Record"/>
    </LinearLayout>

    <RelativeLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        <ImageView
            android:id="@+id/ivPreview"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />
        <VideoView
            android:id="@+id/vvPreview"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />
    </RelativeLayout>

</LinearLayout>
```

### Add this in your activity java files:
```
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
            intent.putExtra(VideoCamera.VIDEO_DURARION,10);  // If you want to set duration
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
                        mediaController.setAnchorView(vvPreview);
                        Uri uri = Uri.parse(videoPath);
                        vvPreview.setMediaController(mediaController);
                        vvPreview.setVideoURI(uri);
                        vvPreview.requestFocus();
                        vvPreview.start();

                    }
                    Toast.makeText(this, "Video Recorded!\n"+videoPath, Toast.LENGTH_SHORT).show();
                }
            } break;
        }
    }
}

```

You are done.