package gmutils;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.util.Pair;

public class AudioVolumeController {

    private AudioManager audioManager = null;
    private Pair<Integer, Integer> audioLevels = null;

    public AudioVolumeController(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        int maxAudioLevel = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int minAudioLevel = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            minAudioLevel = audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC);
        }
        audioLevels = new Pair<>(minAudioLevel, maxAudioLevel);

        //val currentAudioLevel = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)
        //Logger.print { "maxAudioLevel: $maxAudioLevel, minAudioLevel: $minAudioLevel, currentAudioLevel: $currentAudioLevel" }
    }

    //----------------------------------------------------------------------------------------------

    int minVolumeValue = 2;

    public void setMinVolumeValue(int minVolumeValue) {
        this.minVolumeValue = minVolumeValue;
    }

    //----------------------------------------------------------------------------------------------

    public void setSpeakerVolume(int levelPercentage) {
        Pair<Integer, Integer> audioRange = audioLevels;

        float p = levelPercentage / 100f;
        float v = p * (audioRange.second - audioRange.first);

        int offset = audioRange.first + minVolumeValue;
        int targetAudioLevel = offset + (int) Math.ceil(v);

        if (targetAudioLevel > audioRange.second) targetAudioLevel = audioRange.second;
        else if (targetAudioLevel < audioRange.first) targetAudioLevel = audioRange.first;

        int x = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        //Logger.print { "current volume value: $x, target volume level: $targetAudioLevel" }

        if (x < targetAudioLevel) {
            while (x < targetAudioLevel) {
                audioManager.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE,
                        AudioManager.FLAG_PLAY_SOUND
                );

                x++;
            }
        } else if (x > targetAudioLevel) {
            while (x > targetAudioLevel) {
                audioManager.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER,
                        AudioManager.FLAG_PLAY_SOUND
                );

                x--;
            }
        }

        //val y = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)
        //Logger.print { "volume value after change: $y" }

    }

}
