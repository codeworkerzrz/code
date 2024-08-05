package petri;

import petri.entity.AlignmentData;
import petri.entity.PetriNet;
import petri.service.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class test {
    public static void main(String[] args) throws IOException {
        String filename1 = System.getProperty("user.dir") + "/resources/petritestdata1.txt";
        String filename2 = System.getProperty("user.dir") + "/resources/milestones.txt";

        InitializePetriNetService initializePetriNetService;
        GenerateRandomDeviatedTraceService generateRandomDeviatedTraceService;
        InitializeMileStonesService initializeMileStones;
        GenerateProductService generateProduct;
        GenerateOptimalAlignmentService generateOptimalAlignmentService;
        HashMap<Integer, ArrayList<String>> milestoneMap;
        PetriNet eventNet;
        PetriNet product;
        PetriNet processNet;
        int i, j, k;
        //舍弃迹长9
        int[] len = {25, 29, 30, 34, 38};
        int avgcount;
        long avgtime;
        int avgpnum;
        int avgtnum;
        int avgarcnum;
        AlignmentData data;
        ArrayList<ArrayList<String>> traces;
        ArrayList<ArrayList<String>> temptraces;
        ArrayList<ArrayList<PetriNet>> eventNetSet;
        ArrayList<PetriNet> eventNets;

        initializeMileStones = new InitializeMileStonesService();
        initializePetriNetService = new InitializePetriNetService();
        processNet = initializePetriNetService.initProcessPetriNet(filename1);
        milestoneMap = initializeMileStones.getMileStonesMap(filename2);
        if (null == processNet || null == milestoneMap) {
            System.err.println("-----文件读取失败-----");
            return;
        }
        generateProduct = new GenerateProductService();
        generateRandomDeviatedTraceService = new GenerateRandomDeviatedTraceService(processNet);
        generateRandomDeviatedTraceService.printAllTracesLength();
        generateOptimalAlignmentService = new GenerateOptimalAlignmentService();

        System.out.println("-----第一组：完全拟合迹 开始-----");
        //迹长{25, 29, 30, 34, 38}
        for (i = 0; i < 5; ++i) {
            System.out.println("迹长：" + len[i]);
            //100条完全拟合迹数据集
            traces = new ArrayList<>(generateRandomDeviatedTraceService.getRandomTraces(len[i], 0, 100));
            eventNets = new ArrayList<>();
            for (j = 0; j < traces.size(); ++j) {
                eventNet = initializePetriNetService.initEventPetriNet(traces.get(j));
                eventNets.add(eventNet);
            }
            for (j = 0; j < 19; j = j + 2) {
                System.out.println("里程碑数：" + j);
                processNet.milestones = milestoneMap.get(j);
                avgpnum = 0;
                avgtnum = 0;
                avgarcnum = 0;
                avgcount = 0;
                avgtime = 0;
                for (k = 0; k < eventNets.size(); ++k) {
                    eventNet = eventNets.get(k);
                    product = generateProduct.getProductOfEventNetAndProcessNet(eventNet, processNet);
                    avgpnum += product.pnum;
                    avgtnum += product.tnum;
                    avgarcnum += product.getArcNum();
                    data = generateOptimalAlignmentService.generateOptimalAlignment(product);
                    avgcount += data.reachableMarkcount;
                    avgtime += data.costTime;
                }
                int size = eventNets.size();
                avgpnum = calResult(avgpnum, size);
                avgtnum = calResult(avgtnum, size);
                avgarcnum = calResult(avgarcnum, size);
                avgcount = calResult(avgcount, size);
                avgtime = calResult((int) avgtime, size);
                System.out.println("库所数：" + avgpnum + ",变迁数：" + avgtnum + ",弧数：" + avgarcnum + ",平均耗时：" + avgtime + ",可达标识数：" + avgcount);
            }
        }
        System.out.println("-----第一组：完全拟合迹 结束-----");
        System.out.println("-----第一组：不完全拟合迹 开始-----");
        for (i = 0; i < 5; ++i) {
            System.out.println("迹长：" + len[i]);
            //100条完全拟合迹数据集
            traces = new ArrayList<>(generateRandomDeviatedTraceService.getRandomTraces(len[i], 0, 100));
            eventNetSet = new ArrayList<>();
            //迹长{25, 29, 30, 34, 38}
            for (k = 1; k < 7; ++k) {
                temptraces = generateRandomDeviatedTraceService.generateDevTraces(k * 5, traces);
                eventNets = new ArrayList<>();
                for (j = 0; j < temptraces.size(); ++j) {
                    eventNet = initializePetriNetService.initEventPetriNet(temptraces.get(j));
                    eventNets.add(eventNet);
                }
                eventNetSet.add(eventNets);
            }
            for (j = 0; j < 19; j = j + 2) {
                System.out.println("里程碑数：" + j);
                processNet.milestones = milestoneMap.get(j);
                avgpnum = 0;
                avgtnum = 0;
                avgarcnum = 0;
                avgcount = 0;
                avgtime = 0;
                for (ArrayList<PetriNet> tempEventNets : eventNetSet) {
                    for (k = 0; k < tempEventNets.size(); ++k) {
                        eventNet = tempEventNets.get(k);
                        product = generateProduct.getProductOfEventNetAndProcessNet(eventNet, processNet);
                        avgpnum += product.pnum;
                        avgtnum += product.tnum;
                        avgarcnum += product.getArcNum();
                        data = generateOptimalAlignmentService.generateOptimalAlignment(product);
                        while (data.costTime > 2000) {
                            data = generateOptimalAlignmentService.generateOptimalAlignment(product);
                        }
                        avgcount += data.reachableMarkcount;
                        avgtime += data.costTime;
                    }
                }
                int size = 100 * eventNetSet.size();
                avgpnum = calResult(avgpnum, size);
                avgtnum = calResult(avgtnum, size);
                avgarcnum = calResult(avgarcnum, size);
                avgcount = calResult(avgcount, size);
                avgtime = calResult((int) avgtime, size);
                System.out.println("库所数：" + avgpnum + ",变迁数：" + avgtnum + ",弧数：" + avgarcnum + ",平均耗时：" + avgtime + ",可达标识数：" + avgcount);
            }
        }
        System.out.println("-----第一组：不完全拟合迹 结束-----");
        System.out.println("-----第二组：迹长25-29 开始-----");
        for (k = 0; k < 7; ++k) {
            System.out.println("Noise-Ratio:" + k*5 + "%");
            eventNets = new ArrayList<>();
            //迹长{25, 29, 30, 34, 38}
            for (i = 0; i < 2; ++i) {
                //100条完全拟合迹数据集
                traces = new ArrayList<>(generateRandomDeviatedTraceService.getRandomTraces(len[i], 0, 100));
                temptraces = generateRandomDeviatedTraceService.generateDevTraces(k * 5, traces);
                for (j = 0; j < temptraces.size(); ++j) {
                    eventNet = initializePetriNetService.initEventPetriNet(temptraces.get(j));
                    eventNets.add(eventNet);
                }
            }
            for (j = 0; j < 19; j = j + 2) {
                System.out.println("里程碑数：" + j);
                processNet.milestones = milestoneMap.get(j);
                avgpnum = 0;
                avgtnum = 0;
                avgarcnum = 0;
                avgcount = 0;
                avgtime = 0;
                for (PetriNet tempEventNet : eventNets) {
                    product = generateProduct.getProductOfEventNetAndProcessNet(tempEventNet, processNet);
                    avgpnum += product.pnum;
                    avgtnum += product.tnum;
                    avgarcnum += product.getArcNum();
                    data = generateOptimalAlignmentService.generateOptimalAlignment(product);
                    while (data.costTime > 2000) {
                        System.out.println("eeeee");
                        data = generateOptimalAlignmentService.generateOptimalAlignment(product);
                    }
                    avgcount += data.reachableMarkcount;
                    avgtime += data.costTime;
                }
                int size = eventNets.size();
                avgpnum = calResult(avgpnum, size);
                avgtnum = calResult(avgtnum, size);
                avgarcnum = calResult(avgarcnum, size);
                avgcount = calResult(avgcount, size);
                avgtime = calResult((int) avgtime, size);
                System.out.println("库所数：" + avgpnum + ",变迁数：" + avgtnum + ",弧数：" + avgarcnum + ",平均耗时：" + avgtime + ",可达标识数：" + avgcount);
            }
        }
        System.out.println("-----第二组：迹长25-29 结束-----");
        System.out.println("-----第二组：迹长34-38 开始-----");
        for (k = 0; k < 7; ++k) {
            System.out.println("Noise-Ratio:" + k*5 + "%");
            eventNets = new ArrayList<>();
            //迹长{25, 29, 30, 34, 38}
            for (i = 3; i < 5; ++i) {
                //100条完全拟合迹数据集
                traces = new ArrayList<>(generateRandomDeviatedTraceService.getRandomTraces(len[i], 0, 100));
                temptraces = generateRandomDeviatedTraceService.generateDevTraces(k * 5, traces);
                for (j = 0; j < temptraces.size(); ++j) {
                    eventNet = initializePetriNetService.initEventPetriNet(temptraces.get(j));
                    eventNets.add(eventNet);
                }
            }
            for (j = 0; j < 19; j = j + 2) {
                System.out.println("里程碑数：" + j);
                processNet.milestones = milestoneMap.get(j);
                avgpnum = 0;
                avgtnum = 0;
                avgarcnum = 0;
                avgcount = 0;
                avgtime = 0;
                for (PetriNet tempEventNet : eventNets) {
                    product = generateProduct.getProductOfEventNetAndProcessNet(tempEventNet, processNet);
                    avgpnum += product.pnum;
                    avgtnum += product.tnum;
                    avgarcnum += product.getArcNum();
                    data = generateOptimalAlignmentService.generateOptimalAlignment(product);
                    while (data.costTime > 2000) {
                        System.out.println("eeeee");
                        data = generateOptimalAlignmentService.generateOptimalAlignment(product);
                    }
                    avgcount += data.reachableMarkcount;
                    avgtime += data.costTime;
                }
                int size = eventNets.size();
                avgpnum = calResult(avgpnum, size);
                avgtnum = calResult(avgtnum, size);
                avgarcnum = calResult(avgarcnum, size);
                avgcount = calResult(avgcount, size);
                avgtime = calResult((int) avgtime, size);
                System.out.println("库所数：" + avgpnum + ",变迁数：" + avgtnum + ",弧数：" + avgarcnum + ",平均耗时：" + avgtime + ",可达标识数：" + avgcount);
            }
        }
        System.out.println("-----第二组：迹长34-38 结束-----");
    }

    static int calResult(int num1, int num2) {
        return (int) Math.round(((double) num1) / num2);
    }
}
