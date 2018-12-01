package online.propaans.ptfit.ptfit;

import android.os.Handler;

public class FakeRun {


    private MainActivity activity;

    public FakeRun(MainActivity act){
        this.activity = act;
    }

    public void startRun(){
        increaseHeartRate(63, 2000);
    }

    private void increaseHeartRate(final double hrLevel, double delay){
        if (hrLevel > activity.getHeartRate()) {
            final Handler handler = new Handler();
            Runnable runnableCode = new Runnable() {
                double hr = activity.getHeartRate();
                @Override
                public void run() {
                    // Do something here on the main thread
                    activity.setHeartRate(hr);
                    hr++;
                    // Repeat this the same runnable code block again another 2 seconds
                    // 'this' is referencing the Runnable object
                    handler.postDelayed(this, 2000);
                    if (hr >= hrLevel){
                        handler.removeCallbacks(this);
                    }
                }
            };
            handler.post(runnableCode);
        }
    }

    public void startRunActiveResponse(){
        //activity.setStepPerMin(100);
        final double integrationStep = 1;
        final Handler handler = new Handler();
        Runnable runnableCode = new Runnable(){
            @Override
            public void run(){
                respondToMusic(integrationStep, 0.1);
                handler.postDelayed(this, (long) (integrationStep*1000));
            }
        };
        handler.post(runnableCode);
    }

    private boolean descent_started = false;
    private int start_descent_time = 0;
    private double inertia_heartrate = 3;

    private void respondToMusicOld(double integrationStep, double l){
        // pas'(t) = l*(getStepsPace(t) - Pas(t))
        double delta = activity.getStepObjective()-activity.getStepPerMin();
        //activity.setStepPerMin(activity.getStepPerMin()+l*integrationStep*delta);
        if (activity.getStepPerMin() < activity.getStepObjective()) {
            descent_started = false;

            activity.setStepPerMin(activity.getStepPerMin()+3);
            activity.setHeartRate(activity.getHeartRateModel().getHeartRateFromStepPerMin(activity.getStepPerMin()));
        }
        else if (activity.getStepPerMin() > activity.getStepObjective()) {
            if (!descent_started) {
                start_descent_time = activity.getSeconds();
                descent_started = true;
            }


            double old_steps = activity.getStepPerMin();
            double delta_ = old_steps-activity.getStepObjective();
            int descent_time = activity. getSeconds() - start_descent_time;

            activity.setHeartRate(activity.getHeartRate()-activity.getHeartRateModel().getHeartRateFromStepPerMin_dec(activity.getStepPerMin(),delta_, descent_time)*activity.getHeartRate());

            activity.setStepPerMin(activity.getStepPerMin()-2);
        }
        else {
            if (activity.getHeartRate() < activity.getStepPerMin()) {
                activity.setHeartRate(activity.getHeartRate() + 2); // to boost the heart rate, so as it converges faster
            }
            else {
                activity.setHeartRate(activity.getHeartRate() -1);
            }
        }
        activity.setSpeed(60*1.25*activity.getStepPerMin()/1000);
    }

    private void respondToMusic(double integrationStep, double l){
        // pas'(t) = l*(getStepsPace(t) - Pas(t))
        double delta = activity.getStepObjective()-activity.getStepPerMin();
        //activity.setStepPerMin(activity.getStepPerMin()+l*integrationStep*delta);
        if (activity.getStepPerMin() < activity.getStepObjective()) {
            descent_started = false;

            activity.setStepPerMin(activity.getStepPerMin()+3);
            activity.setHeartRate(activity.getHeartRateModel().getHeartRateFromStepPerMin(activity.getStepPerMin()));

            inertia_heartrate = 3;
        }
        else if (activity.getStepPerMin() > activity.getStepObjective()) {
            if (!descent_started) {
                start_descent_time = activity.getSeconds();
                descent_started = true;
            }


            double old_steps = activity.getStepPerMin();
            double delta_ = old_steps-activity.getStepObjective();
            int descent_time = activity. getSeconds() - start_descent_time;

            //activity.setHeartRate(activity.getHeartRate()-activity.getHeartRateModel().getHeartRateFromStepPerMin_dec(activity.getStepPerMin(),delta_, descent_time));
            double overshootHeartRate = activity.getHeartRateModel().getHeartRateFromStepPerMin(activity.getStepPerMin()) - activity.getHeartRate();
            activity.setHeartRate(activity.getHeartRate() + overshootHeartRate/5.0);

            activity.setStepPerMin(activity.getStepPerMin()-1);

        }
        else {
            if(inertia_heartrate >0) {
                activity.setHeartRate(activity.getHeartRate() + inertia_heartrate); // to boost the heart rate, so as it converges faster
                inertia_heartrate -=0.5;
            }else{
                double overshootHeartRate = activity.getHeartRateModel().getHeartRateFromStepPerMin(activity.getStepPerMin()) - activity.getHeartRate();
                activity.setHeartRate(activity.getHeartRate() + overshootHeartRate/5.0);
            }
        }

        activity.setSpeed(60*1.25*activity.getStepPerMin()/1000);
    }
}
