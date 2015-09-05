package com.blog.ljtatum.captureassist.helper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import com.blog.ljtatum.captureassist.enums.VoiceEngineResult;
import com.blog.ljtatum.captureassist.interfaces.OnVoiceEngineListener;
import com.blog.ljtatum.captureassist.logger.Logger;

import java.util.ArrayList;

/**
 * Created by Tatum on 8/15/2015.
 */
public class VoiceEngine {
    private static final String TAG = VoiceEngine.class.getSimpleName();

    private Context mContext;
    private static SpeechRecognizer speechRecognizer;
    private final int MAX_RESULTS = 5;
    private final int SPEECH_INPUT_MINIMUM_LENGTH = 7000;
    private final int SPEECH_INPUT_SILENCE_LENGTH = 3000;
    private String mVoiceEnginePrompt;
    private OnVoiceEngineListener mVoiceEngineListener;

    public VoiceEngine(Context context, String voiceEnginePrompt, OnVoiceEngineListener voiceEngineListener) {
        mContext = context;
        mVoiceEnginePrompt = voiceEnginePrompt;
        mVoiceEngineListener = voiceEngineListener;
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(mContext);
    }

    /**
     * Method is used to initialize voice engine properties and start listening
     */
    public void startVoiceEngine() {
        // set voice engine listener
        speechRecognizer.setRecognitionListener(new VoiceEngineListener(mVoiceEngineListener));

        // set voice engine properties
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, mContext.getApplicationInfo().getClass().getName());
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, MAX_RESULTS);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, mVoiceEnginePrompt);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, SPEECH_INPUT_MINIMUM_LENGTH);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, SPEECH_INPUT_SILENCE_LENGTH);
        speechRecognizer.startListening(intent);
    }

    /**
     * Method is used to stop voice engine
     */
    public void stopVoiceEngine() {
        speechRecognizer.stopListening();
    }

    /**
     * Method is used to destroy voice engine
     */
    public void destroyVoiceEngine() {
        speechRecognizer.destroy();
    }


    public class VoiceEngineListener implements RecognitionListener {

        private OnVoiceEngineListener mVoiceEngineListener;

        public VoiceEngineListener(OnVoiceEngineListener voiceEngineListener) {
            mVoiceEngineListener = voiceEngineListener;
        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            Logger.v(TAG, "onReadyForSpeech");
        }

        @Override
        public void onBeginningOfSpeech() {
            Logger.v(TAG, "onBeginningOfSpeech");
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            // do nothing
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            Logger.v(TAG, "onBufferReceived");
        }

        @Override
        public void onEndOfSpeech() {
            Logger.v(TAG, "onEndOfSpeech");
        }

        @Override
        public void onError(int error) {
            Logger.v(TAG, "onError: " + error);
            /* Legend: Error Code
             * @1 network operation timed out
             * @2 other network related errors
             * @3 audio recording error
             * @4 server sends error stats
             * @5 other client side errors
             * @6 no speech input
             * @7 no recognition result matched
             * @8 RecognitionService busy
             * @9 insufficient permissions
             */

            if (error == 6) {
                // pass array list of retrieved results to custom speech recognized listener
                mVoiceEngineListener.onSpeechRecognized(VoiceEngineResult.NO_SPEECH_INPUT, null);
            }

        }

        @Override
        public void onResults(Bundle results) {
            Logger.v(TAG, "onResults");

            // populate array list of retrieved results recognized by the voice engine
            ArrayList<String> alData = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            // pass array list of retrieved results to custom speech recognized listener
            mVoiceEngineListener.onSpeechRecognized(VoiceEngineResult.SUCCESS, alData);
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            Logger.v(TAG, "onPartialResults");
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            Logger.v(TAG, "onEvent");
        }
    }

}
