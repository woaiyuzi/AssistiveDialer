package love.yuzi.dialer.utils

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.provider.Settings
import android.telecom.TelecomManager
import android.telecom.VideoProfile
import androidx.core.net.toUri
import timber.log.Timber

/**
 * 打开应用设置界面
 */
fun Context.openAppSettings(
) {
    startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
    })
}

fun Context.voiceCall(
    phone: String,
    autoOpenSpeaker: Boolean
) {
    val intent = Intent(Intent.ACTION_CALL).apply {
        data = "tel:${phone.replace(" ", "")}".toUri()
        putExtra(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, autoOpenSpeaker)
    }
    Timber.d("Voice call to $phone")
    startActivity(intent)
}

fun Context.videoCall(
    phone: String
) {
    val intent = Intent(Intent.ACTION_CALL).apply {
        data = "tel:${phone.replace(" ", "")}".toUri()
        putExtra(TelecomManager.EXTRA_START_CALL_WITH_VIDEO_STATE, VideoProfile.STATE_BIDIRECTIONAL)
        putExtra(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, true)
    }
    Timber.d("Video call to $phone")
    startActivity(intent)
}

fun Context.setCallVolume(
    volume: Int,
) {
    val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL)
    if (currentVolume == volume) return

    val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)
    val min = audioManager.getStreamMinVolume(AudioManager.STREAM_VOICE_CALL)
    val targetVolume = if (volume > max) max else if (volume < min) min else volume

    try {
        audioManager.setStreamVolume(
            AudioManager.STREAM_VOICE_CALL,
            targetVolume,
            AudioManager.FLAG_SHOW_UI
        )
        Timber.d("Set call volume to $targetVolume")
    } catch (e: SecurityException) {
        Timber.d(e, "Failed to set call volume to $targetVolume, permission denied")
    } catch (e: Exception) {
        Timber.e(e, "Failed to set call volume to $targetVolume")
    }
}

fun Context.getMaxVoiceVolume(): Int {
    val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)
    return max
}

fun Context.getMinVoiceVolume(): Int {
    val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val min = audioManager.getStreamMinVolume(AudioManager.STREAM_VOICE_CALL)
    return min
}