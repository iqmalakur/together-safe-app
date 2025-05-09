package com.togethersafe.app.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.togethersafe.app.R

class IncidentSoundPlayer(context: Context) {
    private val soundPool: SoundPool
    private val lowIncidentSoundId: Int
    private val mediumIncidentSoundId: Int
    private val highIncidentSoundId: Int
    private val warningIncidentSoundId: Int

    init {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(attributes)
            .build()

        lowIncidentSoundId = soundPool.load(context, R.raw.incident_area_low, 1)
        mediumIncidentSoundId = soundPool.load(context, R.raw.incident_area_medium, 1)
        highIncidentSoundId = soundPool.load(context, R.raw.incident_area_high, 1)
        warningIncidentSoundId = soundPool.load(context, R.raw.incident_area_warning, 1)
    }

    fun playWarning(riskLevel: String) {
        when (riskLevel) {
            "high" ->
                soundPool.play(highIncidentSoundId, 1f, 1f, 1, 0, 1f)
            "medium" ->
                soundPool.play(mediumIncidentSoundId, 1f, 1f, 1, 0, 1f)
            "low" ->
                soundPool.play(lowIncidentSoundId, 1f, 1f, 1, 0, 1f)
            else ->
                soundPool.play(warningIncidentSoundId, 1f, 1f, 1, 0, 1f)
        }
    }

    fun release() {
        soundPool.release()
    }
}
