/**
 * Created by Fredrik on 2015-08-06.
 */
public class MissionInfo {


    private int radius;
    private double currentLat = Double.MAX_VALUE;
    private double currentLong = Double.MAX_VALUE;
    private double missionLat;
    private double missionLong;
    private boolean missionComplete = false;


    public MissionInfo(int radius, double missionLat, double missionLong) {
        this.missionLat = missionLat;
        this.missionLong = missionLong;
        this.radius = radius;
    }

    public void setCurrentLong(double currentLong) {
        this.currentLong = currentLong;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public void setMissionLong(double missionLong) {
        this.missionLong = missionLong;
    }

    public void setMissionLat(double missionLat) {
        this.missionLat = missionLat;
    }


    public double distanceLeft(double missionLat, double missionLong, double currentLat, double currentLong) {
        int R = 6371;
        double a =
                0.5 - Math.cos((currentLat - missionLat) * Math.PI / 180) / 2 +
                        Math.cos(missionLat * Math.PI / 180) * Math.cos(currentLat * Math.PI / 180) *
                                (1 - Math.cos((currentLong - missionLong) * Math.PI / 180)) / 2;
        return 1000 * R * 2 * Math.asin(Math.sqrt(a));
    }

    public boolean checkIfDone() {
        if (currentLat != Double.MAX_VALUE && currentLong != Double.MAX_VALUE && radius != 0) {
            double distanceLeft = distanceLeft(missionLat, missionLong, currentLat, currentLong);
            System.out.println("Distance left:" + distanceLeft);
            if (distanceLeft < radius && !missionComplete) {
                missionComplete = true;
                return true;
            }
        }
        return false;

    }
}
