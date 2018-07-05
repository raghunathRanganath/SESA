package com.trio.sesa;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.security.Policy;
import java.util.HashMap;
import java.util.List;

public class ScanLiveActivity extends AppCompatActivity {

    private SurfaceView surfaceViewer;
    private TextView textViewer;
    private BarcodeDetector barcodeDetector;
    private CameraSource camSrc;
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_live);

        surfaceViewer = (SurfaceView)findViewById(R.id.surfaceViewBox);
        textViewer = (TextView)findViewById(R.id.textViewBox);

        barcodeDetector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        camSrc = new CameraSource.Builder(getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600,1024)
                .setAutoFocusEnabled(true)
                .build();

        surfaceViewer.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(ScanLiveActivity.this, new String[] {android.Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
                    } else {
                        try {
                            surfaceViewer.setFocusable(true);
                            camSrc.start(surfaceViewer.getHolder());
                        } catch (SecurityException sc) {
                            Log.e("Permission Denied", sc.getMessage());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (SecurityException sc) {
                    Log.e("Permission denied", sc.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                camSrc.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray barcodes = detections.getDetectedItems();
                if(barcodes.size()!=0) {
                    textViewer.post(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < barcodes.size(); i++) {
                                Barcode bc = (Barcode) barcodes.get(barcodes.keyAt(i));
                                textViewer.setText(bc.displayValue);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_CAMERA_REQUEST_CODE) {
            if(grantResults[0] ==PackageManager.PERMISSION_GRANTED) {
                try {
                    camSrc.start(surfaceViewer.getHolder());
                } catch (SecurityException sc) {
                    Log.e("Permission Denied", sc.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(ScanLiveActivity.this, "Camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
}
