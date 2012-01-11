package com.coctelmental.android.project1886.tts;

import android.content.Context;
import android.content.Intent;

public class TextToSpeechMain {

	public static void playMessage(Context context, String ttsMessage) {
		Intent ttsIntent = new Intent(context, TextToSpeechService.class);
		// attach TTS message
		ttsIntent.putExtra(TextToSpeechService.EXTRA_TTS_MESSAGE, ttsMessage);
		// call service to launch TTS (text to speech) task
		context.startService(ttsIntent);
	}
}
