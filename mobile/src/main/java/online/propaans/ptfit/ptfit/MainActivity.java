package online.propaans.ptfit.ptfit;

import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Queue;
import java.lang.Math;

public class MainActivity extends AppCompatActivity {


    MediaPlayer bass_player;
    MediaPlayer high_player;
    MediaPlayer mid_player;

    private static double stepObjective;
    {
        stepObjective = 160;
    }

    private static double hrObjective;
    {
        hrObjective = 130;
    }

    private static double integration_time;
    {
        integration_time = 5;
    }

    private static int seconds = 0;

    private double heartRate = 60;
    private double stepPerMin = 100;
    private double speed = 10;
    //    private double pace = 180;
    private boolean streaming = false;


    private double stepPerMinBase =130;
    private double stepPerMinMax = 280;
    private double heartRateBase= 80;

    private HeartRateModel heartRateModel;

    private static double musicPace = 140;
    private static float bass_volume = (float) (1 + Math.log(hrObjective / 200));
    private static float high_volume = (float) (0.5*Math.log(220 / stepObjective));


    private double HR_mean = hrObjective;
    private double step_mean = stepObjective;

    Queue<Double> HRs = new LinkedList<>();
    Queue<Double> steps = new LinkedList<>();

    private void initialize_queues() {
        for (int i = 0; i < (int) integration_time; i++) {
            steps.add(stepObjective);
            HRs.add(hrObjective);
        }
    }


    Handler handler = new Handler();
    private Runnable PeriodicUpdate = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void run() {


            handler.postDelayed(PeriodicUpdate, 1000);


            if(streaming) {
                seconds++;


                double HR_now = getHeartRate();
                HRs.add(HR_now);

                double step_now = getStepPerMin();
                steps.add(step_now);
                double HR_old = HRs.remove();
                double step_old = steps.remove();

                //Volume Computation
                HR_mean += (HR_now - HR_old) / integration_time;
                bass_volume = (float) (1 + Math.log(HR_mean));

                step_mean += (step_now - step_old) / integration_time;
                high_volume = (float) (0.5*Math.log(220 / step_mean));


                //Music Pace
                PlaybackParams plbParam = new PlaybackParams();
                plbParam.setSpeed((float) (stepObjective / musicPace));
                //            plbParam.setSpeed((float) (pace / absolutePace));

                //Music Transformation :D
                if (bass_player != null) {
                    bass_player.setPlaybackParams(plbParam);
                    bass_player.setVolume(bass_volume, bass_volume);

                }
                if (high_player != null) {
                    high_player.setPlaybackParams(plbParam);
                    high_player.setVolume(high_volume, high_volume);
                }
                if (mid_player != null) {
                    mid_player.setPlaybackParams(plbParam);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playPause(findViewById(R.id.imageButtonPlayPause));
        initialize_queues();
    }

    public void play1(View v) {

        if (bass_player == null) {
            bass_player = MediaPlayer.create(this, R.raw.basshappy);
            bass_player.setLooping(true);
            bass_player.setVolume(bass_volume, bass_volume);

        }
    }

    public void play2(View v) {
        if (high_player == null) {
            high_player = MediaPlayer.create(this, R.raw.highhappy);
            high_player.setLooping(true);
            high_player.setVolume(high_volume, high_volume);

        }
    }

    public void play3(View v) {
        if (mid_player == null) {
            mid_player = MediaPlayer.create(this, R.raw.mihappy);
            mid_player.setLooping(true);
            float mid_volume = Math.min(high_volume, bass_volume);
            mid_player.setVolume(mid_volume, mid_volume);

        }
    }

    public void play_all(View v) {
        play1(v);
        play2(v);
        play3(v);

        bass_player.start();
        high_player.start();
        mid_player.start();
    }

    public void pause(View v) {
        //pause the music
        if (bass_player != null) {
            bass_player.pause();
        }
        if (high_player != null) {
            high_player.pause();
        }
        if (mid_player != null) {
            mid_player.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.heartRateModel = new HeartRateModel(stepPerMinBase, stepPerMinMax, heartRateBase);
        this.setStepObjective(Double.parseDouble(getIntent().getStringExtra("stepPerMinute")));
        FakeRun fakeRun = new FakeRun(MainActivity.this);
        fakeRun.startRunActiveResponse();
    }

    public void setHeartRate(double heartRate) {

        this.heartRate = heartRate;

        final TextView textView = findViewById(R.id.valueBPM);
        textView.setText(String.valueOf((int)(heartRate)));
    }

    public void setStepPerMin(double stepPerMin) {
        this.stepPerMin = stepPerMin;
        final TextView textView = findViewById(R.id.textStepsPerMin);
        textView.setText(String.valueOf((int)(stepPerMin)));
        this.checkSmiley();
    }

    public void setStepObjective(double stepPerMin){
        this.stepObjective = stepPerMin - (stepPerMin % 5);
        final TextView textView = findViewById(R.id.textPace);
        textView.setText(String.valueOf((int)(stepObjective)));
        this.checkSmiley();
    }

    public void setSpeed(double speed) {
        this.speed = speed;
        final TextView textView = findViewById(R.id.textSpeed);
        textView.setText(String.valueOf((int)(speed)));
    }

    public int getSeconds() {
        return seconds;
    }

    public double getHeartRate() {
        return this.heartRate;
    }

    public double getStepPerMin() {
        return this.stepPerMin;
    }

    public double getSpeed() {
        return this.speed;
    }

    public double getStepObjective(){
        return stepObjective;
    }

    public HeartRateModel getHeartRateModel() {
        return heartRateModel;
    }

    public void increaseStepObjective(View view) {
        if (stepObjective < 240) {
            stepObjective += 5;
            final TextView textView = findViewById(R.id.textPace);
            textView.setText(String.valueOf((int)stepObjective));
        }
        this.checkSmiley();
    }

    public void increasePace(View view) {
        increaseStepObjective(view);
    }


    public void decreaseStepObjective(View view) {
        if (stepObjective > 100) {
            stepObjective -= 5;
            final TextView textView = findViewById(R.id.textPace);
            textView.setText(String.valueOf((int)stepObjective));
        }
        this.checkSmiley();
    }

    public void decreasePace(View view) {
        decreaseStepObjective(view);
    }

    public void playPause(View view) {
        final ImageButton imageButton = findViewById(R.id.imageButtonPlayPause);
        if (streaming) {
            imageButton.setImageResource(R.drawable.ic_play_arrow_black_86dp);

            pause(view);
        } else {
            imageButton.setImageResource(R.drawable.ic_pause_black_86dp);

            play_all(view);

            PeriodicUpdate.run();
        }
        this.streaming = !this.streaming;
    }


    public void checkSmiley(){
        final ImageView imageView = findViewById(R.id.imageSmiley);
        if (Math.abs(this.stepPerMin - stepObjective) < 5.0) {
            imageView.setImageResource(R.drawable.happy);
        } else if (Math.abs(this.stepPerMin - stepObjective) < 15.0){
            imageView.setImageResource(R.drawable.bof);
        } else {
            imageView.setImageResource(R.drawable.bad);
        }
    }

    public void stop(View v) {
        if (streaming){
            playPause(v);
        }
        stopPlayer();
    }

    private void stopPlayer() {
        if (bass_player != null) {
            bass_player.release();
            bass_player = null;

        }
        if (high_player != null) {
            high_player.release();
            high_player = null;

        }
        if (mid_player != null) {
            mid_player.release();
            mid_player = null;

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
    }
}

