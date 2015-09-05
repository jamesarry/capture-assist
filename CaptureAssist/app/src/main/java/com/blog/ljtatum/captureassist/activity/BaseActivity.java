package com.blog.ljtatum.captureassist.activity;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;

import com.blog.ljtatum.captureassist.enums.TextToSpeechResult;
import com.blog.ljtatum.captureassist.interfaces.OnTextToSpeechListener;
import com.blog.ljtatum.captureassist.logger.Logger;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Tatum on 8/8/2015.
 */
public class BaseActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private final static String TAG = BaseActivity.class.getSimpleName();
    protected static TextToSpeech textToSpeech;
    private static HashMap<String, String> map = new HashMap<String, String>();
    private Context mContext;
    private OnTextToSpeechListener mTextToSpeechListener;

    /**
     * Method is used to speak the String using the specified queuing strategy and speech parameters
     *
     * @param text
     */
    @SuppressWarnings("deprecation")
    protected static void speakText(String text) {
        if (textToSpeech.isSpeaking()) {
            return;
        }
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    protected void goToActivity(Context context, Class<?> activity) {
        Intent intent = new Intent(context, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    /**
     * Initialize Text-To-Speech engine
     *
     * @param context
     */
    protected void initTTS(Context context) {
        mContext = context;
        textToSpeech = new TextToSpeech(context, (TextToSpeech.OnInitListener) context);
        textToSpeech.setLanguage(Locale.US);
        textToSpeech.setPitch(10 / 10);
        textToSpeech.setSpeechRate(16 / 12);
    }

    /**
     * Method is used to stop the TTS Engine
     */
    protected void stopTTS() {
        while (textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
    }

    /**
     * Method is used to destroy the TTS Engine
     */
    protected void destroyTTS() {
        textToSpeech.stop();
        textToSpeech.shutdown();
    }

    @Override
    public void onInit(int status) {
        // TODO Auto-generated method stub
        if (status == TextToSpeech.SUCCESS) {
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");

            // pass array list of retrieved results to custom speech recognized listener
            mTextToSpeechListener.onInitTextToSpeech(TextToSpeechResult.INITIALIZED);
        } else if (status == TextToSpeech.ERROR) {
            // initialization of TTS failed so reinitialize new TTS Engine
            Logger.e(TAG, "Initialization of TTS Engine fail");
            initTTS(mContext);
        }
    }

    protected void setTextToSpeechInitListener(OnTextToSpeechListener textToSpeechListener) {
        mTextToSpeechListener = textToSpeechListener;
    }
}
