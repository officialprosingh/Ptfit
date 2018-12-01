package online.propaans.ptfit.ptfit;

public class HeartRateModel {

    private final double stepPerMinMax;
    private final double stepPerMinBase;
    private final double heartRateBase;

    public HeartRateModel(double stepPerMinBase, double stepPerMinMax, double heartRateBase) {
        this.stepPerMinBase = stepPerMinBase;
        this.stepPerMinMax = stepPerMinMax;
        this.heartRateBase = heartRateBase;
    }

    public double getStepPerMinMax() {
        return this.stepPerMinMax;
    }

    public double getStepPerMinBase() {
        return this.stepPerMinBase;
    }

    public double getStepPerMinFromHeartRate(double heartRate) {
        double c = getHeartRateFromStepPerMin(100) * (100 - stepPerMinMax); // the value 100 is arbitrary
        return stepPerMinMax - c / heartRate;
    }

    public double getHeartRateFromStepPerMin(double stepPerMin) {
        // aim for the sweet spot heartRate activity.getStepPerMinBase() at 180
        //double c =  heartRateBase* (stepPerMinMax - stepPerMinBase);
        //return c / (stepPerMinMax-stepPerMin);
        double ret = (220-160)*(stepPerMin-160)/(280-160)+190;
        if(ret<60){
            return 60;
        }
        else{
            return ret;
        }
    }


    public double getHeartRateFromStepPerMin_dec(double stepPerMin, double delta, int descent_time) {
        // diff entre le nouveau et l'ancien heartbeat
        return (220-160)/(280-160);//descent_time*descent_time/stepPerMin;//(stepPerMin*stepPerMin) ;
    }
}
