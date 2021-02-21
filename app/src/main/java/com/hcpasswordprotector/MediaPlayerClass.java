package com.hcpasswordprotector;

import android.content.Context;
import android.media.MediaPlayer;

public class MediaPlayerClass {

    MediaPlayer mediaPlayer;

    public MediaPlayerClass(Context context,String string)
    {
        try {
            if (string.toLowerCase().equals("save".toLowerCase())) {
                mediaPlayer = MediaPlayer.create(context, R.raw.savesound);
            } else if (string.toLowerCase().equals("delete".toLowerCase())) {
                mediaPlayer = MediaPlayer.create(context, R.raw.deletesound);
            }
            mediaPlayer.start();
        } catch (Exception e)
        {

        }
    }

}
