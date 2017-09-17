package com.project.mymusic.mymusic;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.File;
import java.util.ArrayList;

public class Player extends AppCompatActivity implements View.OnClickListener {
    static MediaPlayer mp;
    SeekBar seekBar;
    Button buttonPlay;
    Button buttonFastBackward;
    Button buttonFastForward;
    Button buttonPrevious;
    Button buttonNext;
    ArrayList<File> mySongs;
    int position;
    Uri u;
    Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        buttonPlay = (Button) findViewById(R.id.buttonPlay);
        buttonFastBackward = (Button) findViewById(R.id.buttonFastBackward);
        buttonFastForward = (Button) findViewById(R.id.buttonFastForward);
        buttonPrevious = (Button) findViewById(R.id.buttonPrevious);
        buttonNext = (Button) findViewById(R.id.buttonNext);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        buttonPlay.setOnClickListener(this);
        buttonFastBackward.setOnClickListener(this);
        buttonFastForward.setOnClickListener(this);
        buttonPrevious.setOnClickListener(this);
        buttonNext.setOnClickListener(this);

        thread = new Thread() {
            @Override
            public void run() {
                int totalDuration = mp.getDuration();
                int currentPosition = 0;
                //seekBar.setMax(totalDuration);
                while (currentPosition < totalDuration) {
                    try {
                        sleep(500);
                        currentPosition = mp.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


                //super.run();
            }
        };


        if (mp != null) {
            mp.stop();
            mp.release();
        }

        Intent i = getIntent();
        Bundle b = i.getExtras();
        mySongs = (ArrayList) b.getParcelableArrayList("songlist");

        position = b.getInt("pos", 0);

        u = Uri.parse(mySongs.get(position).toString());
        mp = MediaPlayer.create(getApplicationContext(), u);
        mp.start();
        seekBar.setMax(mp.getDuration());
        thread.start();

        //Here m going to add seekBarChangeListener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mp.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(seekBar.getProgress());
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonPlay:
                if (mp.isPlaying()) {
                    buttonPlay.setBackgroundResource(android.R.drawable.ic_media_play);
                    mp.pause();
                } else {
                    mp.start();
                    buttonPlay.setBackgroundResource(android.R.drawable.ic_media_pause);
                    //buttonPlay.setText("||");
                }
                break;

            case R.id.buttonFastBackward:
                mp.seekTo(mp.getCurrentPosition() - 500);
                break;

            case R.id.buttonFastForward:
                mp.seekTo(mp.getCurrentPosition() + 500);
                break;

            case R.id.buttonPrevious:
                mp.stop();
                mp.release();
                position = (position - 1 < 0) ? mySongs.size() - 1 : position - 1;
                u = Uri.parse(mySongs.get(position).toString());
                mp = MediaPlayer.create(getApplicationContext(), u);
                mp.start();
                seekBar.setMax(mp.getDuration());
                break;

            case R.id.buttonNext:
                mp.stop();
                mp.release();
                position = (position + 1) % mySongs.size();
                u = Uri.parse(mySongs.get(position).toString());
                mp = MediaPlayer.create(getApplicationContext(), u);
                mp.start();
                seekBar.setMax(mp.getDuration());
                break;
        }
    }

/*    public void pauseMusic()
    {
        if(mp != null)
        {
            mp.pause();
            buttonNext.setBackgroundResource(android.R.drawable.ic_media_play);
        }
    }*/
}
