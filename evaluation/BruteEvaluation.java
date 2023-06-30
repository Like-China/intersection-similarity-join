package evaluation;

import java.util.ArrayList;
import utils.ContactPair;
import utils.Data;

public class BruteEvaluation {
    // at each timestamp, we update mtree by adding circle data in database set
    public ArrayList<Data> queries = new ArrayList<>();
    public ArrayList<Data> db = new ArrayList<>();
    public long fTime = 0;

    public BruteEvaluation(ArrayList<Data> queries, ArrayList<Data> db) {
        this.queries = queries;
        this.db = db;
    }

    public ArrayList<ContactPair> getCandidate() {
        long t1 = System.currentTimeMillis();
        ArrayList<ContactPair> candidates = new ArrayList<>();
        // bruteforce
        for (Data qdata : queries) {
            for (Data dbdata : db) {
                // pre-checking
                if (Math.abs(qdata.get(0) - dbdata.get(0)) > (qdata.bead.a + dbdata.bead.a)
                        && Math.abs(qdata.get(1) - dbdata.get(1)) > (qdata.bead.b + dbdata.bead.b)) {
                    continue;
                }
                double centerDist = Math
                        .sqrt(Math.pow(qdata.get(0) - dbdata.get(0), 2) + Math.pow(qdata.get(1) - dbdata.get(1), 2));
                if (centerDist <= (qdata.radius + dbdata.radius)) {
                    candidates.add(new ContactPair(qdata, dbdata, centerDist));
                }
            }
        }
        long t2 = System.currentTimeMillis();
        fTime = t2-t1;
        // System.out.printf("Construction time: %7d Filter time:%7d**Brute**\n",
        //         0, (t2 - t1));
        return candidates;
    }

}