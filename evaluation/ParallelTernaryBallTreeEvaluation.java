package evaluation;

import balltree.TernaryBallNode;
import balltree.TernaryBallTree;
import utils.ContactPair;
import utils.Data;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;
import java.util.List;

public class ParallelTernaryBallTreeEvaluation {
    public ArrayList<Data> queries = new ArrayList<>();
    public ArrayList<Data> db = new ArrayList<>();
    public long cTime = 0;
    public long fTime = 0;
    public double repartirionRatio = 0;
    public int minLeafNB = 0;

    public ParallelTernaryBallTreeEvaluation(ArrayList<Data> queries, ArrayList<Data> db, double repartirionRatio,
            int minLeafNB) {
        this.queries = queries;
        this.db = db;
        this.repartirionRatio = repartirionRatio;
        this.minLeafNB = minLeafNB;
    }

    public ArrayList<ContactPair> getCandidate() {
        long t1 = System.currentTimeMillis();
        TernaryBallTree bt = new TernaryBallTree(minLeafNB, db, repartirionRatio);
        TernaryBallNode root = bt.buildBallTree();
        long t2 = System.currentTimeMillis();
        cTime = t2 - t1;

        ArrayList<ContactPair> candidates = new ArrayList<>();
        t1 = System.currentTimeMillis();
        int size = queries.size();
        // return the number of logical CPUs
        // int processorsNum = Runtime.getRuntime().availableProcessors();
        int processorsNum = Settings.threadNB;
        // set the threadNum as 2*(the number of logical CPUs) for handling IO Tasks,
        // if Computing Tasks set the threadNum as (the number of logical CPUs) + 1
        int threadNum = processorsNum * 2;
        final ExecutorService executor = Executors.newFixedThreadPool(threadNum);
        CountDownLatch cdl = new CountDownLatch(threadNum);
        ReentrantLock resLock = new ReentrantLock();
        // the number of each group data
        int eachGroupNum = size / threadNum;
        List<List<Data>> groupList = new ArrayList<>();
        for (int i = 0; i < threadNum; i++) {
            int start = i * eachGroupNum;
            if (i == threadNum - 1) {
                int end = size;
                groupList.add(queries.subList(start, end));
            } else {
                int end = (i + 1) * eachGroupNum;
                groupList.add(queries.subList(start, end));
            }
        }
        // begin
        for (List<Data> group : groupList) {
            executor.execute(() -> {
                try {
                    ArrayList<ContactPair> temp = new ArrayList<>();
                    for (Data qdata : group) {
                        ArrayList<ContactPair> ballRangeResult = bt.searchRange(root, qdata);
                        temp.addAll(ballRangeResult);
                    }
                    resLock.lock();
                    candidates.addAll(temp);
                    resLock.unlock();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // let counter minus one
                    cdl.countDown();
                }

            });
        }

        try {
            cdl.await();
        } catch (InterruptedException e) {
            System.out.println(e);
        }
        executor.shutdown();
        t2 = System.currentTimeMillis();
        fTime = t2 - t1;
        // System.out.printf("Construction time: %7d Filter time:%7d**TernaryPara**\n",
        // cTime, fTime);
        return candidates;
    }

}