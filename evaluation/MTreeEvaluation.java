package evaluation;

import java.util.ArrayList;

import mtree.MTreeClass;
import utils.ContactPair;
import utils.Data;

public class MTreeEvaluation {
    public ArrayList<Data> queries = new ArrayList<>();
    public ArrayList<Data> db = new ArrayList<>();
    public long cTime = 0;
    public long fTime = 0;

    public MTreeEvaluation(ArrayList<Data> queries, ArrayList<Data> db) {
        this.queries = queries;
        this.db = db;
    }

    // As we increase the value of minLeafNum of Mtree, much less computational cost
    // After adjusting the hashcode and equals() functions of Data.class, the
    // candidates is correct (2023/4/22)
    public ArrayList<ContactPair> getCandidate() {
        long t1 = System.currentTimeMillis();
        MTreeClass mtree = new MTreeClass();
        for (Data data : db) {
            mtree.add(data);
            // System.out.println(data.radius);
        }
        long t2 = System.currentTimeMillis();
        cTime = (t2 - t1);

        t1 = System.currentTimeMillis();
        ArrayList<ContactPair> candidates = new ArrayList<>();
        for (Data qdata : queries) {
            MTreeClass.Query query = mtree.getNearestByRange(qdata, qdata.radius);
            candidates.addAll(query.rangeQuery());
        }
        t2 = System.currentTimeMillis();
        fTime = (t2 - t1);
        // System.out.printf("Construction time: %7d Filter time:%7d**MTree**\n", cTime,
        // fTime);
        return candidates;
    }
}