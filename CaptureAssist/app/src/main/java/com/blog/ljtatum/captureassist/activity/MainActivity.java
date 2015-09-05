package com.blog.ljtatum.captureassist.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.blog.ljtatum.captureassist.R;
import com.blog.ljtatum.captureassist.constants.Constants;
import com.blog.ljtatum.captureassist.database.SpeakDatabase;
import com.blog.ljtatum.captureassist.enums.TextToSpeechResult;
import com.blog.ljtatum.captureassist.enums.VoiceEngineResult;
import com.blog.ljtatum.captureassist.helper.VoiceEngine;
import com.blog.ljtatum.captureassist.interfaces.OnTextToSpeechListener;
import com.blog.ljtatum.captureassist.interfaces.OnVoiceEngineListener;
import com.blog.ljtatum.captureassist.logger.Logger;
import com.blog.ljtatum.captureassist.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Tatum on 8/8/2015.
 */
public class MainActivity extends BaseActivity implements SurfaceHolder.Callback{
    private final static String TAG = MainActivity.class.getSimpleName();

    private Context mContext;
    private VoiceEngine voiceEngine;

    private static Camera cameraBack; // back camera object
    private static Camera cameraFront; // front camera object
    private Camera.Parameters paramCamBack;
    private Camera.Parameters paramCamFront;

    private TextView tvDesc;
    private SurfaceView svPreview;
    private SurfaceHolder holder;

    private OrientationEventListener sensorListener;
    private Camera.Size size;
    private Vibrator v;

    private boolean isBalanced, isPreviewActive;
    private static final String CAMERA_ID = "camera-id";
    private int cameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        getIds();

        // camera notes on orientation
        //http://developer.android.com/reference/android/hardware/Camera.html

        // separate old camera implemenation with new camera implemenation
//        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            //use old camera API
//        }else{
//            //use new camera API
//        }
    }

    private void getIds() {
        mContext = MainActivity.this;

        // initialize TTS at start of app
        initTTS(mContext);

        tvDesc = (TextView) findViewById(R.id.tv_desc);
        svPreview = (SurfaceView) findViewById(R.id.sv_preview);
        holder = svPreview.getHolder();
        holder.addCallback(this);

        // OnClickListener for capturing photos
        svPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.i(TAG, "surface screen pressed");


            }
        });

        // set custom text to speech initialized listener
        setTextToSpeechInitListener(new OnTextToSpeechListener() {

            @Override
            public void onInitTextToSpeech(TextToSpeechResult result) {
                if (result == TextToSpeechResult.INITIALIZED) {
                    // speak welcome message
                    speakText(SpeakDatabase.Welcome.message(0));
                }
            }
        });

        // initialize voice engine and set custom speech recognized listener
        voiceEngine = new VoiceEngine(mContext, "Say something", new OnVoiceEngineListener() {

            @Override
            public void onSpeechRecognized(VoiceEngineResult result, ArrayList<String> data) {
                if (!Utils.checkIfNull(data) && result == VoiceEngineResult.SUCCESS) {
                    if (Constants.DEBUG_HIGH_VERBOSITY) {
                        Logger.i(TAG, "------RECOGNIZED WORDS------");
                        for (int i = 0; i < data.size(); i++) {
                            Logger.i(TAG, data.get(i).toString());
                        }
                        Logger.i(TAG, "----------------------------");
                    }

                } else if (result == VoiceEngineResult.NO_SPEECH_INPUT) {
                    Logger.i(TAG, "no speech input");
                    // prompt user to say something
                }
            }
        });
        //voiceEngine.startVoiceEngine();


        sensorListener = new OrientationEventListener(mContext, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {

                // holding the phone right side up
                if ((orientation >= 0) && (orientation <= 10)
                        || ((orientation >= 350) && (orientation <= 359))
                        || ((orientation >= 170) && (orientation <= 179))
                        || ((orientation >= 180) && (orientation <= 190))) {

                    Logger.i(TAG, "phone is ready to take picture");
                    isBalanced = true; // phone is held upright and ready to take a picture
                } else {
                    Logger.e(TAG, "phone is not ready to take picture");
                    isBalanced = false; // phone is not held upright and is not ready to take a picture
                }

            }
        };
    }

    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                result = size;
            } else {
                int resultArea = result.width * result.height;
                int sizeArea = size.width * size.height;
                if (sizeArea > resultArea) {
                    result = size;
                }
            }
        }

        return result;
    }


    private void backCamera() {
        // release front camera if instantiated
        if (!Utils.checkIfNull(cameraFront)) {
            cameraFront.stopPreview();
            cameraFront.release();
            cameraFront = null;
        }

        cameraId = 0; // set cameraId to 0 for back camera, otherwise 1 for front camera
        cameraBack = Camera.open(cameraId);
        paramCamBack = cameraBack.getParameters();

        Camera.Size size = getBestPreviewSize(svPreview.getWidth(), svPreview.getHeight(), paramCamBack);
        paramCamBack.setPreviewSize(size.width, size.height);
        svPreview.getTop();

        // add auto detection for flash mode
        // Note: front camera does not support flash mode
        if ((!Utils.checkIfNull(paramCamBack.getSupportedFlashModes()) &&
            paramCamBack.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_AUTO))) {
            paramCamBack.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        }

        // add auto continuous focus mode intended for taking pictures
        if (!Utils.checkIfNull(paramCamBack.getSupportedFocusModes()) &&
            paramCamBack.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            paramCamBack.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        // add auto white balance; balancing light/shadows from daylight, shade, twilight, ect
        if (!Utils.checkIfNull(paramCamBack.getSupportedWhiteBalance()) &&
            paramCamBack.getSupportedWhiteBalance().contains(Camera.Parameters.WHITE_BALANCE_AUTO)) {
            paramCamBack.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
        }

        // add auto scene adjustments; adjusting fast moving objects, pictures on the beach,
        // scenes lit by candles, ect
        if (!Utils.checkIfNull(paramCamBack.getSupportedSceneModes()) &&
            paramCamBack.getSupportedSceneModes().contains(Camera.Parameters.SCENE_MODE_AUTO)) {
            paramCamBack.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
        }

        // set quality of photos to 100
        paramCamBack.setExposureCompensation(0);
        paramCamBack.setJpegQuality(100);
        paramCamBack.setJpegThumbnailQuality(100);

        // add camera id to our camera
        paramCamBack.set(CAMERA_ID, cameraId);

        try {
            cameraBack.setPreviewDisplay(holder);
            cameraBack.setDisplayOrientation(90);
        } catch (Throwable ignored) {
            Logger.e(TAG, ignored.getMessage());
            ignored.printStackTrace();
        }

        cameraBack.setParameters(paramCamBack);
        cameraBack.startPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Logger.i(TAG, "surfaceCreated()");
        backCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Logger.i(TAG, "surfaceChanged()");

        if (isPreviewActive) {

        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Logger.i(TAG, "surfaceDestroyed()");
    }

    /**
     * Method is used to display exit app dialog
     */
    private void dialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Exit");
        dialog.setMessage("Are you sure you want to exit");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {


            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                destroyTTS();
                finish();
            }


        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                // do nothing
            }


        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        dialog();
    }

    @Override
    protected void onDestroy() {
        voiceEngine.destroyVoiceEngine();
        super.onDestroy();
    }

}
