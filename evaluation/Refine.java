package evaluation;

import java.util.ArrayList;

import utils.ContactPair;
import utils.Data;
import utils.Ellipse;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.List;

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

    public static ArrayList<ContactPair> monitorPara(ArrayList<ContactPair> candidates, double interRatio,
            boolean isPrecheck, int sampleNum) {
        ArrayList<ContactPair> refinedResult = new ArrayList<>();

        int size = candidates.size();
        // return the number of logical CPUs
        // int processorsNum = Runtime.getRuntime().availableProcessors();
        int processorsNum = Settings.threadNB;
        // set the threadNum as 2*(the number of logical CPUs) for handling IO Tasks,
        // if Computing Tasks set the threadNum as (the number of logical CPUs) + 1
        int threadNum = processorsNum * 2;
        final ExecutorService executor = Executors.newFixedThreadPool(threadNum);
        CountDownLatch cdl = new CountDownLatch(threadNum);
        ReentrantLock resLock = new ReentrantLock();
        // the number of each group data, split group based on the number of threads
        int eachGroupNum = size / threadNum;
        List<List<ContactPair>> groupList = new ArrayList<>();
        for (int i = 0; i < threadNum; i++) {
            int start = i * eachGroupNum;
            if (i == threadNum - 1) {
                int end = size;
                groupList.add(candidates.subList(start, end));
            } else {
                int end = (i + 1) * eachGroupNum;
                groupList.add(candidates.subList(start, end));
            }
        }
        // begin
        for (List<ContactPair> group : groupList) {
            executor.execute(() -> {
                try {
                    ArrayList<ContactPair> temp = new ArrayList<>();
                    for (ContactPair pair : group) {
                        if (isContact(pair.query, pair.db, interRatio, isPrecheck, sampleNum)) {
                            temp.add(pair);
                        }
                    }
                    resLock.lock();
                    refinedResult.addAll(temp);
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
