package com.nate.aesexo;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.FrameworkSampleSource;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.SampleSource;
import com.google.android.exoplayer.TrackRenderer;


public class MainActivity extends AppCompatActivity {

    ExoPlayer exoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        exoPlayer = ExoPlayer.Factory.newInstance(1);

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.music_sample);

        // todo something like Aes128DataSource
        SampleSource sampleSource = new FrameworkSampleSource(this, videoUri, null);

        TrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource, null, true);
        exoPlayer.prepare(audioRenderer);

        exoPlayer.setPlayWhenReady(true);
    }

    @Override
    public void onDestroy() {
        exoPlayer.release();
        super.onDestroy();
    }
}

