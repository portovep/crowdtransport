package com.coctelmental.android.project1886.tts;

import java.util.HashMap;
import java.util.Locale;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

public class TextToSpeechService extends Service implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener{

	public static final String EXTRA_TTS_MESSAGE = "tts_message";
	
	private static final String UTTERANCE_ID = "serviceRequestUtterance";
	private TextToSpeech tts;
	private String ttsMessage = "";
	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	// get message
    	Bundle extras = intent.getExtras();
    	if (extras != null) {
    		String message = extras.getString(EXTRA_TTS_MESSAGE);
    		if (message != null && !message.equals("")) {
    			// save msg
    			ttsMessage = message;
    			// start TTS engine
    	    	tts = new TextToSpeech(this, this);
    	    	return START_REDELIVER_INTENT;
    		}
    	}
    	// log error with received data
    	Log.w("TTS", "Invalid tts message");
    	// stop service
    	stopSelf();
    	return START_NOT_STICKY;
    }
	
	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			// try setup the default language as tts language
			int result = tts.setLanguage(Locale.getDefault());
			// check if it's supported
			if (result != TextToSpeech.LANG_MISSING_DATA &&
					result != TextToSpeech.LANG_NOT_SUPPORTED) {
				// set listener to stop service when work is done
				tts.setOnUtteranceCompletedListener(this);
				HashMap<String, String> ttsParams = new HashMap<String, String>();
				ttsParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, UTTERANCE_ID );
				tts.speak(ttsMessage, TextToSpeech.QUEUE_FLUSH, ttsParams);
			}
			else {
				Log.w("TTS", "Target language package is not available");
				stopSelf();
			}
		}
		else {
			Log.w("TTS", "TTS engine could not be started");
			stopSelf();
		}		
	}

	@Override
	public void onUtteranceCompleted(String utteranceId) {
		if (utteranceId.equals(UTTERANCE_ID)){ 
			// stop service
			stopSelf();
		}
	}
	
	@Override
	public void onDestroy() {
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
