package evaluation;

import java.util.ArrayList;

import utils.ContactPair;
import utils.Data;
import utils.Ellipse;

public class Refine {

    // further refine results
    public static ArrayList<ContactPair> monitor(ArrayList<ContactPair> candidates, double interRatio,
            boolean isPrecheck, int sampleNum) {
        ArrayList<ContactPair> refinedResult = new ArrayList<>();
        for (ContactPair pair : candidates) {
            // check if pairs in candidate contact or not
            if (isContact(pair.query, pair.db, interRatio, isPrecheck, sampleNum)) {
                refinedResult.add(pair);
            }
        }
        // System.out.println("candidate size / match size: " + candidates.size() + "/"
        // + refinedResult.size());
        return refinedResult;
    }

    public static boolean isContact(Data qData, Data dbData, double interRatio, boolean isPrecheck, int sampleNum) {
        Ellipse qE = qData.bead;
        Ellipse dbE = dbData.bead;
        double[][] points = null;
        double areaSum = qE.getArea() + dbE.getArea();
        // pre-checking 1: using rectangle (MBR)
        if (isPrecheck) {
            double interArea = qE.interAreaTo(dbE);
            double upper = 2 * interArea / areaSum;
            if (upper < interRatio) {
                return false;
            }
        }
        points = qE.sampleByLayout(sampleNum);
        // use two-phase sampling strategy
        double count = 0;
        for (double[] point : points) {
            if (dbE.cover(point[0], point[1])) {
                count++;
            }
        }
        double ratio1 = qE.getArea() / areaSum * (count / sampleNum);
        count = 0;
        // pre-checking 2: using upper bound
        if (isPrecheck) {
            if (ratio1 >= interRatio)
                return true;
            if (ratio1 + dbE.getArea() / areaSum < interRatio)
                return false;
        }
        points = dbE.sampleByLayout(sampleNum);
        for (double[] point : points) {
            if (qE.cover(point[0], point[1])) {
                count++;
            }
        }
        double ratio2 = dbE.getArea() / areaSum * (count / sampleNum);
        if (ratio1 + ratio2 >= interRatio) {
            return true;
        }
        return false;
    }

}
