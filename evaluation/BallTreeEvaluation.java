package evaluation;

import java.util.ArrayList;
import balltree.BallNode;
import balltree.BallTree;
import utils.ContactPair;
import utils.Data;

public class BallTreeEvaluation {
    public ArrayList<Data> queries = new ArrayList<>();
    public ArrayList<Data> db = new ArrayList<>();
    public long cTime = 0;
    public long fTime = 0;
    public int searchCount = 0;
    public int minLeafNB = 0;

    public BallTreeEvaluation(ArrayList<Data> queries, ArrayList<Data> db, int minLeafNB) {
        this.queries = queries;
        this.db = db;
        this.minLeafNB = minLeafNB;
    }

    public ArrayList<ContactPair> getCandidate() {
        long t1 = System.currentTimeMillis();
        BallTree bt = new BallTree(minLeafNB, db);
        BallNode root = bt.buildBallTree();
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
        // System.out.printf("Construction time: %7d Filter time:%7d**BallTree**\n",
        // cTime, fTime);
        return candidates;
    }

}