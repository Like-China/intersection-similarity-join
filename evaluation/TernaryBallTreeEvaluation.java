package evaluation;

import java.util.ArrayList;
import balltree.TernaryBallNode;
import balltree.TernaryBallTree;
import utils.ContactPair;
import utils.Data;

public class TernaryBallTreeEvaluation {
    public ArrayList<Data> queries = new ArrayList<>();
    public ArrayList<Data> db = new ArrayList<>();
    public long cTime = 0;
    public long fTime = 0;
    public int searchCount = 0;
    public double repartirionRatio = 0;
    public int minLeafNB = 0;

    public TernaryBallTreeEvaluation(ArrayList<Data> queries, ArrayList<Data> db, double repartirionRatio, int minLeafNB) {
        this.queries = queries;
        this.db = db;
        this.repartirionRatio = repartirionRatio;
        this.minLeafNB = minLeafNB;
    }

    public ArrayList<ContactPair> getCandidate() {
        long t1 = System.currentTimeMillis();
        TernaryBallTree bt = new TernaryBallTree(minLeafNB, db, repartirionRatio);
        TernaryBallNode root = bt.buildBallTree();
        // root.levelOrder(root);
        long t2 = System.currentTimeMillis();
        cTime = t2 - t1;

        ArrayList<ContactPair> candidates = new ArrayList<>();
        t1 = System.currentTimeMillis();
        for (Data qdata : queries) {
            ArrayList<ContactPair> ballRangeResult = bt.searchRange(root, qdata);
            candidates.addAll(ballRangeResult);
        }
        searchCount = bt.searchCount;
        t2 = System.currentTimeMillis();
        fTime = t2 - t1;
        // System.out.printf("Construction time: %7d Filter time:%7d**Ternary**\n", cTime, fTime);
        return candidates;
    }

}