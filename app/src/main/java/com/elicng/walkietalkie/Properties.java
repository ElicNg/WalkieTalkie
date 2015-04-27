package com.elicng.walkietalkie;

import android.media.AudioFormat;
import android.media.AudioRecord;

/**
 * Created by Elic on 15-04-26.
 */
public class Properties {

    public final static int SAMPLING_RATE = 44100;
    public final static int BUFFER_SIZE =
            AudioRecord.getMinBufferSize(Properties.SAMPLING_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT);
}
