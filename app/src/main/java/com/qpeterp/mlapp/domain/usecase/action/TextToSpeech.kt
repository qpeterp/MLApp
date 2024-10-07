package com.qpeterp.mlapp.domain.usecase.action

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class TextToSpeech {
    fun textToSpeech(context: Context, tts: TextToSpeech?): TextToSpeech {
        val textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.KOREA
            }
        }
        return textToSpeech
    }
}