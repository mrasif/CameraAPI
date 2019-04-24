package in.mrasif.app.cameraapi_demo.dialogs;


import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;

import in.mrasif.app.cameraapi.helper.CameraPreview;
import in.mrasif.app.cameraapi.utils.Utils;
import in.mrasif.app.cameraapi_demo.MainActivity;
import in.mrasif.app.cameraapi_demo.R;

public class CameraDialog extends Dialog {

    MainActivity activity;
    FrameLayout preview;
    Button btnCapture, btnSave, btnCancel;
    CameraPreview cameraPreview;

    public CameraDialog(Context context, MainActivity activity) {
        super(context);
        this.activity=activity;
        setContentView(R.layout.dialog_camera);

        preview=findViewById(R.id.camera_preview);
        btnCapture=findViewById(R.id.button_capture);
        btnSave=findViewById(R.id.button_save);
        btnCancel=findViewById(R.id.button_cancel);

        cameraPreview=new CameraPreview(activity);
        preview.addView(cameraPreview);


        btnCapture.setOnClickListener(v -> {
            cameraPreview.takePicture();
            btnSave.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
            btnCapture.setVisibility(View.GONE);
        });

        btnSave.setOnClickListener(v -> {
            File photo= new File(Utils.prepareCompleteFilePath("jpg"));
            cameraPreview.savePicture(photo);
            Toast.makeText(context, "Image saved!\n"+photo.getPath(), Toast.LENGTH_SHORT).show();
            dismiss();
        });

        btnCancel.setOnClickListener(v -> {
            btnSave.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            btnCapture.setVisibility(View.VISIBLE);
            cameraPreview.startPreview();
        });
    }

}
