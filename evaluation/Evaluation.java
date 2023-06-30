package evaluation;

import java.util.ArrayList;
import java.util.HashSet;

import utils.ContactPair;

public  class Evaluation {
    public static double getRecall(ArrayList<ContactPair> real, ArrayList<ContactPair> pred) {
        double TP = 0;
        double FN = 0;
        for (ContactPair p: real) {
            if (pred.contains(p)) {
                TP += 1;
            } else {
                FN += 1;
            }
        }
        return TP / (TP + FN);
    }

    public static double getPrecision(ArrayList<ContactPair> real, ArrayList<ContactPair> pred) {
        double TP = 0;
        double FP = 0;
        for (ContactPair p : pred) {
            if (real.contains(p)) {
                TP += 1;
            } else {
                FP += 1;
            }
        }
        return TP / (TP + FP);
    }
}
