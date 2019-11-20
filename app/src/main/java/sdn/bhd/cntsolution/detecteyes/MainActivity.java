package sdn.bhd.cntsolution.detecteyes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;

import sdn.bhd.cntsolution.detecteyes.common.CameraSource;
import sdn.bhd.cntsolution.detecteyes.common.CameraSourcePreview;
import sdn.bhd.cntsolution.detecteyes.common.GraphicOverlay;
import sdn.bhd.cntsolution.detecteyes.facedetection.FaceDetectionProcessor;

public class MainActivity extends AppCompatActivity {
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;
    private CameraSource cameraSource = null;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;

        preview = (CameraSourcePreview) findViewById(R.id.firePreview);
        graphicOverlay = (GraphicOverlay) findViewById(R.id.fireFaceOverlay);

        createCameraSource();
    }

    private void createCameraSource() {
        // If there's no existing cameraSource, create one.
        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
        }

        cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);

        cameraSource.setMachineLearningFrameProcessor(new FaceDetectionProcessor(getResources(), context));
    }

    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    //Log.d(TAG, "resume: Preview is null");
                }
                if (graphicOverlay == null) {
                   //Log.d(TAG, "resume: graphOverlay is null");
                }
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                //Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }
}
