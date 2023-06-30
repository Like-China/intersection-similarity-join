package evaluation;

import java.util.ArrayList;
import java.util.Arrays;

import utils.ContactPair;
import utils.Utils;

public class Check {

        // test with the default parameter settings of all methods
        public static void defaultTest() {
                Loader l = new Loader();
                long t1 = 0, t2 = 0;
                long bruteCTime = 0, ballCTime = 0, mCTime = 0, ternaryCTime = 0, ballCTimePara = 0,
                                ternaryCTimePara = 0;
                long bruteFTime = 0, ballFTime = 0, mFTime = 0, ternaryFTime = 0, ballFTimePara = 0,
                                ternaryFTimePara = 0;
                long basicRTime = 0, advanceRTime = 0, advanceRTimePara = 0, basicRTimePara = 0;

                int expNum = 5;
                int searchCountSumOfBall = 0;
                int searchCountSumOfTernary = 0;
                l.getAllData(1000, Settings.maxSpeed);
                for (int i = 0; i < expNum; i++) {
                        long loopStart = System.currentTimeMillis();
                        System.out.println("Round: " + (i + 1));
                        l.getBatch(Settings.cardinality);

                        BruteEvaluation brute = new BruteEvaluation(l.queries, l.db);
                        ArrayList<ContactPair> bruteCandidate = brute.getCandidate();
                        bruteFTime += brute.fTime;

                        MTreeEvaluation m = new MTreeEvaluation(l.queries, l.db);
                        ArrayList<ContactPair> mCandidate = m.getCandidate();
                        mCTime += m.cTime;
                        mFTime += m.fTime;
                        brute = null;
                        m = null;
                        assert bruteCandidate.size() == mCandidate.size();

                        ArrayList<ContactPair> ballCandidate = new ArrayList<>();
                        BallTreeEvaluation ball = new BallTreeEvaluation(l.queries, l.db, Settings.minLeafNB);
                        ballCandidate = ball.getCandidate();
                        searchCountSumOfBall += ball.searchCount;
                        ballCTime += ball.cTime;
                        ballFTime += ball.fTime;
                        ball = null;
                        // assert bruteCandidate.size() == ballCandidate.size();
                        ballCandidate = null;
                        System.gc();

                        TernaryBallTreeEvaluation ternaryBall = new TernaryBallTreeEvaluation(l.queries, l.db,
                                        Settings.repartitionRatio, Settings.minLeafNB);
                        ballCandidate = ternaryBall.getCandidate();
                        searchCountSumOfTernary += ternaryBall.searchCount;
                        ternaryCTime += ternaryBall.cTime;
                        ternaryFTime += ternaryBall.fTime;
                        ternaryBall = null;

                        ParallelTernaryBallTreeEvaluation ternaryPara = new ParallelTernaryBallTreeEvaluation(l.queries,
                                        l.db, Settings.repartitionRatio, Settings.minLeafNB);
                        ballCandidate = ternaryPara.getCandidate();
                        ternaryCTimePara += ternaryPara.cTime;
                        ternaryFTimePara += ternaryPara.fTime;
                        ternaryPara = null;

                        ParallelBallTreeEvaluation ballPara = new ParallelBallTreeEvaluation(l.queries, l.db,
                                        Settings.minLeafNB);
                        ballCandidate = ballPara.getCandidate();
                        ballCTimePara += ballPara.cTime;
                        ballFTimePara += ballPara.fTime;

                        t1 = System.currentTimeMillis();
                        Refine.monitor(ballCandidate, Settings.interRatio, false, Settings.sampleNum);
                        t2 = System.currentTimeMillis();
                        basicRTime += (t2 - t1);
                        t1 = System.currentTimeMillis();
                        Refine.monitor(ballCandidate, Settings.interRatio, true, Settings.sampleNum);
                        t2 = System.currentTimeMillis();
                        advanceRTime += (t2 - t1);
                        t1 = System.currentTimeMillis();
                        Refine.monitorPara(ballCandidate, Settings.interRatio, false, Settings.sampleNum);
                        t2 = System.currentTimeMillis();
                        basicRTimePara += (t2 - t1);
                        t1 = System.currentTimeMillis();
                        Refine.monitorPara(ballCandidate, Settings.interRatio, true, Settings.sampleNum);
                        t2 = System.currentTimeMillis();
                        advanceRTimePara += (t2 - t1);

                        ballCandidate = null;
                        System.gc();
                        long loopEnd = System.currentTimeMillis();
                        System.out.println("Round Time Cost: " + (loopEnd - loopStart));
                }
                searchCountSumOfBall /= expNum;
                searchCountSumOfTernary /= expNum;
                bruteFTime /= expNum;
                mCTime /= expNum;
                mFTime /= expNum;
                ballCTime /= expNum;
                ballFTime /= expNum;
                ternaryCTime /= expNum;
                ternaryFTime /= expNum;
                ballCTimePara /= expNum;
                ballFTimePara /= expNum;
                ternaryCTimePara /= expNum;
                ternaryFTimePara /= expNum;
                basicRTime /= expNum;
                advanceRTime /= expNum;
                advanceRTimePara /= expNum;
                System.out.println("\n***Construction Time***");
                System.out.printf("Brute: %8d MTree: %8d Ball: %8d Ternary: %8d ParaBall: %8d ParaTernary: %8d\n",
                                bruteCTime, mCTime, ballCTime,
                                ternaryCTime, ballCTimePara, ternaryCTimePara);
                System.out.println("***Filter Time***");
                System.out.printf("Brute: %8d MTree: %8d Ball: %8d Ternary: %8d ParaBall: %8d ParaTernary: %8d\n",
                                bruteFTime, mFTime, ballFTime, ternaryFTime, ballFTimePara, ternaryFTimePara);
                System.out.println("***Refine Time***");
                System.out.printf("Brute: %8d MTree: %8d Ball: %8d Ternary: %8d ParaBall: %8d ParaTernary: %8d\n",
                                basicRTime, basicRTime, basicRTime, advanceRTime, basicRTimePara, advanceRTimePara);
                System.out.println("***Total Time***");
                System.out.printf("Brute: %8d MTree: %8d Ball: %8d Ternary: %8d ParaBall: %8d ParaTernary: %8d\n",
                                bruteCTime + bruteFTime + basicRTime, mCTime + mFTime + basicRTime,
                                ballCTime + ballFTime + basicRTime, ternaryCTime + ternaryFTime + advanceRTime,
                                ballFTimePara + ballCTimePara + basicRTimePara,
                                ternaryFTimePara + ternaryCTimePara + advanceRTimePara);
                System.out.println("***Node Access***");
                System.out.printf("Ball: %8d Ternary: %8d\n", searchCountSumOfBall, searchCountSumOfTernary);
                System.exit(0);
        }

        public static Long[][] evaluate(double maxSpeed, int cardinality, int minLeafNB, double interRatio,
                        double repartitionRatio, int sampleNum, boolean useBF, boolean useMJ, boolean useBall) {

                Long[][] res = new Long[6][4];
                Loader l = new Loader();
                long t1 = 0, t2 = 0;
                long bruteCTime = 0, ballCTime = 0, mCTime = 0, ternaryCTime = 0;
                long bruteFTime = 0, ballFTime = 0, mFTime = 0, ternaryFTime = 0;
                long basicRTime = 0, advanceRTime = 0;

                int expNum = 2;
                int searchCountSumOfBall = 0;
                int searchCountSumOfTernary = 0;
                l.getAllData(Settings.objectNB, maxSpeed);
                for (int i = 0; i < expNum; i++) {
                        long loopStart = System.currentTimeMillis();
                        System.out.print("Round: " + (i + 1));
                        l.getBatch(cardinality);

                        if (useBF) {
                                BruteEvaluation brute = new BruteEvaluation(l.queries, l.db);
                                ArrayList<ContactPair> bruteCandidate = brute.getCandidate();
                                bruteFTime += brute.fTime;
                        }

                        if (useMJ) {
                                MTreeEvaluation m = new MTreeEvaluation(l.queries, l.db);
                                ArrayList<ContactPair> mCandidate = m.getCandidate();
                                mCTime += m.cTime;
                                mFTime += m.fTime;
                        }

                        ArrayList<ContactPair> ballCandidate = new ArrayList<>();
                        if (useBall) {
                                BallTreeEvaluation ball = new BallTreeEvaluation(l.queries, l.db, minLeafNB);
                                ballCandidate = ball.getCandidate();
                                searchCountSumOfBall += ball.searchCount;
                                ballCTime += ball.cTime;
                                ballFTime += ball.fTime;
                                ball = null;
                                ballCandidate = null;
                                System.gc();
                        }

                        TernaryBallTreeEvaluation ternaryBall = new TernaryBallTreeEvaluation(l.queries, l.db,
                                        repartitionRatio, minLeafNB);
                        ballCandidate = ternaryBall.getCandidate();
                        searchCountSumOfTernary += ternaryBall.searchCount;
                        ternaryCTime += ternaryBall.cTime;
                        ternaryFTime += ternaryBall.fTime;
                        ternaryBall = null;

                        t1 = System.currentTimeMillis();
                        Refine.monitor(ballCandidate, interRatio, false, sampleNum);
                        t2 = System.currentTimeMillis();
                        basicRTime += (t2 - t1);
                        t1 = System.currentTimeMillis();
                        Refine.monitor(ballCandidate, interRatio, true, sampleNum);
                        t2 = System.currentTimeMillis();
                        advanceRTime += (t2 - t1);
                        long loopEnd = System.currentTimeMillis();
                        System.out.println("\t Round Time Cost: " + (loopEnd - loopStart));
                }
                searchCountSumOfBall /= expNum;
                searchCountSumOfTernary /= expNum;
                bruteFTime /= expNum;
                mCTime /= expNum;
                mFTime /= expNum;
                ballCTime /= expNum;
                ballFTime /= expNum;
                ternaryCTime /= expNum;
                ternaryFTime /= expNum;
                basicRTime /= expNum;
                advanceRTime /= expNum;
                String conTime = String.format(
                                "***Construction Time***\nBrute: %8d MTree: %8d Ball: %8d Ternary: %8d\n", bruteCTime,
                                mCTime, ballCTime, ternaryCTime);
                res[0] = new Long[] { bruteCTime, mCTime, ballCTime, ternaryCTime };
                System.out.println(conTime);
                String filterTime = String.format("***Filter Time***\nBrute: %8d MTree: %8d Ball: %8d Ternary: %8d\n",
                                bruteFTime, mFTime, ballFTime, ternaryFTime);
                res[1] = new Long[] { bruteFTime, mFTime, ballFTime, ternaryFTime };
                System.out.println(filterTime);
                String conTimePlusfilterTime = String.format(
                                "***Construction && Filter Time***\nBrute: %8d MTree: %8d Ball: %8d Ternary: %8d\n",
                                bruteCTime + bruteFTime, mFTime + mCTime, ballFTime + ballCTime,
                                ternaryFTime + ternaryCTime);
                res[2] = new Long[] { bruteCTime + bruteFTime, mFTime + mCTime, ballFTime + ballCTime,
                                ternaryFTime + ternaryCTime };
                System.out.println(conTimePlusfilterTime);
                String refineTime = String.format("***Refine Time***\nBrute: %8d MTree: %8d Ball: %8d Ternary: %8d\n",
                                basicRTime, basicRTime, advanceRTime, advanceRTime);
                res[3] = new Long[] { basicRTime, basicRTime, advanceRTime, advanceRTime };
                System.out.println(refineTime);
                String totalTime = String.format("***Total Time***\nBrute: %8d MTree: %8d Ball: %8d Ternary: %8d\n",
                                bruteCTime + bruteFTime + basicRTime, mCTime + mFTime + basicRTime,
                                ballCTime + ballFTime + advanceRTime, ternaryCTime + ternaryFTime + advanceRTime);
                res[4] = new Long[] { bruteCTime + bruteFTime + basicRTime, mCTime + mFTime + basicRTime,
                                ballCTime + ballFTime + advanceRTime, ternaryCTime + ternaryFTime + advanceRTime };
                System.out.println(totalTime);
                String nodeAccess = String.format("***Node Access***\nBall: %8d Ternary: %8d\n", searchCountSumOfBall,
                                searchCountSumOfTernary);
                res[5] = new Long[] { (long) searchCountSumOfBall, (long) searchCountSumOfTernary, 0l, 0l };
                System.out.println(nodeAccess);
                String otherInfo = conTime + filterTime + conTimePlusfilterTime + refineTime + totalTime + nodeAccess;
                String setInfo = String.format(
                                "maxSpeed=%f, cardinality=%d, minLeafNB=%d, interRatio=%f, repartitionRatio=%f",
                                maxSpeed, cardinality, minLeafNB, interRatio, repartitionRatio);
                Utils.writeFile(setInfo, otherInfo);
                return res;
        }

        public static double[] presision(double maxSpeed, int cardinality, int minLeafNB, double interRatio,
                        int sampleNum) {

                Loader l = new Loader();
                double precision = 0, recall = 0;
                int expNum = 2;
                l.getAllData(Settings.objectNB, maxSpeed);
                for (int i = 0; i < expNum; i++) {
                        long loopStart = System.currentTimeMillis();
                        System.out.print("Round: " + (i + 1));
                        l.getBatch(cardinality);

                        ArrayList<ContactPair> ballCandidate = new ArrayList<>();
                        ParallelBallTreeEvaluation ball = new ParallelBallTreeEvaluation(l.queries, l.db, minLeafNB);
                        ballCandidate = ball.getCandidate();
                        ArrayList<ContactPair> my = Refine.monitor(ballCandidate, interRatio, true, sampleNum);
                        ArrayList<ContactPair> groundtruth = Refine.monitor(ballCandidate, interRatio, true, 100);

                        double TP = 0, FP = 0, FN = 0;
                        for (ContactPair p1 : my) {
                                boolean flag = false;
                                for (ContactPair p2 : groundtruth) {
                                        if (p1.query == p2.query && p1.db == p2.db) {
                                                flag = true;
                                                break;
                                        }
                                }
                                if (flag) {
                                        TP += 1;
                                } else {
                                        FP += 1;
                                }
                        }
                        precision = TP / (TP + FP);

                        TP = 0;
                        FP = 0;
                        FN = 0;
                        for (ContactPair p1 : groundtruth) {
                                boolean flag = false;
                                for (ContactPair p2 : my) {
                                        if (p1.query == p2.query && p1.db == p2.db) {
                                                flag = true;
                                                break;
                                        }
                                }
                                if (flag) {
                                        TP += 1;
                                } else {
                                        FN += 1;
                                }
                        }
                        recall = TP / (TP + FN);

                        long loopEnd = System.currentTimeMillis();
                        System.out.println("\t Round Time Cost: " + (loopEnd - loopStart));
                }
                return new double[] { precision, recall };
        }

        public static void varyTest() {
                long t1 = System.currentTimeMillis();
                ArrayList<Long[][]> allRes = new ArrayList<>();
                Long[][] res;
                // vary speed
                // Utils.writeFile("", "Varying speed");
                // for (double maxSpeed : Settings.maxSpeeds) {
                // System.out.println("Vary speed");
                // res = evaluate(maxSpeed, Settings.cardinality, Settings.minLeafNB,
                // Settings.interRatio,
                // Settings.repartitionRatio, Settings.sampleNum, false, true, true);
                // allRes.add(res);
                // }
                // for (Long[][] item : allRes) {
                // Utils.writeFile("speed", Arrays.deepToString(item));
                // }
                // allRes = new ArrayList<>();
                // vary cardinality
                // Utils.writeFile("", "Varying cardinality");
                // for (int cardinality : Settings.cardinalities) {
                // System.out.println("Vary cardinality");
                // res = evaluate(Settings.maxSpeed, cardinality, Settings.minLeafNB,
                // Settings.interRatio,
                // Settings.repartitionRatio, Settings.sampleNum, false, true, true);
                // allRes.add(res);
                // }
                // for (Long[][] item : allRes) {
                // Utils.writeFile("cardinality", Arrays.deepToString(item));
                // }
                // allRes = new ArrayList<>();

                // vary interRatio
                // Utils.writeFile("", "Varying interRatio");
                // for (double interRatio : Settings.interRatios) {
                //         System.out.println("Vary interRatio");
                //         res = evaluate(Settings.maxSpeed, Settings.cardinality, Settings.minLeafNB, interRatio,
                //                         Settings.repartitionRatio, Settings.sampleNum, false, false, false);
                //         allRes.add(res);
                // }
                // for (Long[][] item : allRes) {
                //         Utils.writeFile("interRatio", Arrays.deepToString(item));
                // }
                // allRes = new ArrayList<>();
                // // vary repartition ratio
                // Utils.writeFile("", "Varying repartition ratio");
                // for (double repartitionRatio : Settings.repartitionRatios) {
                //         System.out.println("Vary repartition ratio");
                //         res = evaluate(Settings.maxSpeed, Settings.cardinality, Settings.minLeafNB,
                //                         Settings.interRatio, repartitionRatio, Settings.sampleNum, false, false, false);
                //         allRes.add(res);
                // }
                // for (Long[][] item : allRes) {
                //         Utils.writeFile("repartition ratio", Arrays.deepToString(item));
                // }
                // allRes = new ArrayList<>();
                // // vary minLeaf
                Utils.writeFile("", "Varying minLeaf");
                for (int minLeafNB : new int[] { 20, 30, 40, 50, 60 }) {
                        System.out.println("Vary minLeaf");
                        res = evaluate(Settings.maxSpeed, Settings.cardinality, minLeafNB,
                                        Settings.interRatio, Settings.repartitionRatio, Settings.sampleNum, false,
                                        false, true);
                        allRes.add(res);
                }
                for (Long[][] item : allRes) {
                        Utils.writeFile("minLeaf", Arrays.deepToString(item));
                }
                allRes = new ArrayList<>();

                long t2 = System.currentTimeMillis();
                System.out.println("Time cost: " + (t2 - t1) / 1000);
        }

        public static void main(String[] args) {
                // defaultTest();
                varyTest();
                // double[] precision = presision(Settings.maxSpeed, Settings.cardinality,
                // Settings.minLeafNB,
                // Settings.interRatio, Settings.sampleNum);
                // System.out.println(Arrays.toString(precision));
        }
}
