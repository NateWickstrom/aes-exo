package com.nate.aesexo;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.ExoPlayerLibraryInfo;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.SampleSource;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;


public class MainActivity extends AppCompatActivity {

    //encryption command (on mac): openssl enc -aes-128-cbc -nosalt -p -in music.aac -out music.enc
    private static final byte[] AES_KEY = hexStringToByteArray("098F6BCD4621D373CADE4E832627B4F6");
    private static final byte[] AES_IV = hexStringToByteArray("0A9172716AE6428409885B8B829CCB05");

    private static final Uri ENCRYPTED_MUSIC_URI = Uri.parse("asset:///music.enc");
    private static final Uri MUSIC_URI = Uri.parse("asset:///music.aac");

    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;

    private ExoPlayer player;

    // toggle setting used for debugging
    private static final boolean PLAY_ENCRYPTED = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        player = ExoPlayer.Factory.newInstance(1);

        setupPlayer();
    }

    @Override
    public void onDestroy() {
        player.release();
        super.onDestroy();
    }

    private void setupPlayer(){
        SampleSource sampleSource = PLAY_ENCRYPTED ? getEncryptedSampleSource() : getSampleSource();
        TrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource);

        player.prepare(audioRenderer);
        player.setPlayWhenReady(true);
    }

    private SampleSource getSampleSource(){
        Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
        DataSource dataSource = new DefaultUriDataSource(this, getUserAgent());
        return new ExtractorSampleSource(MUSIC_URI, dataSource, allocator, BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);
    }

    private SampleSource getEncryptedSampleSource(){
        Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
        DataSource dataSource = new DefaultUriDataSource(this, getUserAgent());
        AesDataSource aesDataSource = new AesDataSource(dataSource, AES_KEY, AES_IV);
        return new ExtractorSampleSource(ENCRYPTED_MUSIC_URI, aesDataSource, allocator, BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);
    }

    private String getUserAgent() {
        String versionName;
        try {
            String packageName = getPackageName();
            PackageInfo info = getPackageManager().getPackageInfo(packageName, 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "?";
        }
        String applicationName = getString(R.string.app_name);
        return applicationName + "/" + versionName + " (Linux;Android " + Build.VERSION.RELEASE
                + ") " + "ExoPlayerLib/" + ExoPlayerLibraryInfo.VERSION;
    }

    private static byte[] hexStringToByteArray(String s) {
        s = s.toLowerCase();
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}

