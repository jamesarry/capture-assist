package com.blog.ljtatum.captureassist.interfaces;

import com.blog.ljtatum.captureassist.enums.VoiceEngineResult;

import java.util.ArrayList;

/**
 * Created by Tatum on 8/15/2015.
 */
public interface OnVoiceEngineListener {
    void onSpeechRecognized(VoiceEngineResult result, ArrayList<String> data);
}
