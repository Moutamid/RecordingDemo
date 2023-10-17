package com.moutamid.voicedemo;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.moutamid.voicedemo.databinding.ActivityMainBinding;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    // creating a variable for media recorder object class.
    private MediaRecorder mRecorder;

    // creating a variable for mediaplayer class
    private MediaPlayer mPlayer;

    // string variable is created for storing a file name
    private static String mFileName = null;

    private ActivityMainBinding b;

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.start.setOnClickListener(v -> {
            startRecording();

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    stopRecording();
                    startRecording();
                }
            }, 5000, 5000);
        });

        b.stop.setOnClickListener(v -> {
            stopRecording();
            if (timer != null)
                timer.cancel();
        });

        b.play.setOnClickListener(v -> {
            playAudio(mFileName);
        });

    }

    private String getFilePath() {
        String path_save_vid = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            path_save_vid =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                            File.separator +
                            getResources().getString(R.string.app_name) +
                            File.separator + "Audio" +
                            File.separator;
        } else {
            path_save_vid =
                    Environment.getExternalStorageDirectory().getAbsolutePath() +
                            File.separator +
                            getResources().getString(R.string.app_name) +
                            File.separator + "Audio" +
                            File.separator;
        }
        File newFile2 = new File(path_save_vid);
        newFile2.mkdir();
        newFile2.mkdirs();

        return path_save_vid;
    }

    private void startRecording() {
        // we are here initializing our filename variable
        // with the path of the recorded audio file.

        mFileName = getFilePath() + "Audio" + System.currentTimeMillis() + ".3gp";

        runOnUiThread(() -> {
            Button button = new Button(MainActivity.this);
            button.setText(mFileName);
            button.setOnClickListener(v -> {
                playAudio(button.getText().toString());
            });

            b.linearLayout.addView(button);

            b.files.setText("--" + b.files.getText().toString() + "\n" + mFileName);
        });

        // below method is used to initialize
        // the media recorder class
        mRecorder = new MediaRecorder();

        // below method is used to set the audio
        // source which we are using a mic.
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        // below method is used to set
        // the output format of the audio.
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        // below method is used to set the
        // audio encoder for our recorded audio.
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        // below method is used to set the
        // output file location for our recorded audio
        mRecorder.setOutputFile(mFileName);

        try {
            // below method will prepare
            // our audio recorder class
            mRecorder.prepare();
            mRecorder.start();

            runOnUiThread(() -> {
                b.status.setText("5 sec recording started");
            });
        } catch (Exception e) {
            Log.e("TAG", "prepare() failed" + e.getMessage());
            Log.e("TAG", "prepare() failed" + e.getLocalizedMessage());
            Log.e("TAG", "prepare() failed" + e.getStackTrace().toString());
            timer.cancel();
            runOnUiThread(() -> {
                b.status.setText("cancelled with error: " + e.getLocalizedMessage());
            });
        }
    }

    public void playAudio(String s) {
        // for playing our recorded audio
        // we are using media player class.
        mPlayer = new MediaPlayer();
        try {
            // below method is used to set the
            // data source which will be our file name
            mPlayer.setDataSource(s);
//            mPlayer.setDataSource(mFileName);

            // below method will prepare our media player
            mPlayer.prepare();

            // below method will start our media player.
            mPlayer.start();

        } catch (IOException e) {
            Log.e("TAG", "prepare() failed");
        }
        runOnUiThread(() -> {
            Toast.makeText(MainActivity.this, "started playing", Toast.LENGTH_SHORT).show();
        });
    }

    public void stopRecording() {
        // below method will stop
        // the audio recording.
        mRecorder.stop();

        // below method will release
        // the media recorder class.
        mRecorder.release();
        mRecorder = null;

        runOnUiThread(() -> {
            b.status.setText("5 sec recording stopped");
        });
    }

}