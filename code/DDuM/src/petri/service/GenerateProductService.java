package petri.service;

import petri.entity.Mark;
import petri.entity.PetriNet;
import petri.entity.TransitionOfProduct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenerateProductService {

    /**
     * -------------eventNet------------------
     * ||||| t1' | t2'
     * |p1'
     * |p2'
     * |p3'
     * -------------processNet----------------
     * ||||| t1 | t2 | t3
     * |p1
     * |p2
     * -------------productNet-----------------
     * ||||| (t1',t1) | (t2',t2) | (t1',>>) | (t2',>>) | (>>,t1) | (>>,t2) | (>>,t3)
     * |p1'
     * |p2'
     * |p3'
     * |p1
     * |p2
     */
    public PetriNet getProductOfEventNetAndProcessNet(PetriNet eventNet, PetriNet processNet) {
        int i, j, k;
        int syncTnum = 0;

        //初始化product变迁数组
        ArrayList<TransitionOfProduct> topArray = new ArrayList<>();
        TransitionOfProduct tran;
        //sync变迁所对应的eventNet中的变迁下标与processNet中的变迁下标
        ArrayList<Integer> syncTranInEvent = new ArrayList<>();
        ArrayList<Integer> syncTranInProcess = new ArrayList<>();
        //计算sync变迁数量
        List<String> processNetTrans = Arrays.asList(processNet.tranArray);
        for (i = 0; i < eventNet.tnum; ++i) {
            if (processNetTrans.contains(eventNet.tranArray[i])) {
                int index = processNetTrans.indexOf(eventNet.tranArray[i]);
                tran = new TransitionOfProduct("t" + (i + 1) + "'", "t" + (index + 1), eventNet.tranArray[i]);
                topArray.add(tran);
                syncTranInEvent.add(i);
                syncTranInProcess.add(index);
                ++syncTnum;
            }
        }

        //初始化关联矩阵
        int pnum = eventNet.pnum + processNet.pnum;
        int tnum = eventNet.tnum + syncTnum + processNet.tnum - 2 * processNet.milestones.size();
        int[][] array = new int[pnum][tnum];

        //初始化product库所下标数组
        String[] placeIndex = new String[pnum];
        System.arraycopy(eventNet.placeIndex, 0, placeIndex, 0, eventNet.pnum);
        System.arraycopy(processNet.placeIndex, 0, placeIndex, eventNet.pnum, processNet.pnum);
        //初始化关联矩阵中的sync变迁列
        for (i = 0; i < pnum; ++i) {
            for (j = 0; j < syncTnum; ++j) {
                if (i < eventNet.pnum) {
                    array[i][j] = eventNet.array[i][syncTranInEvent.get(j)];
                } else {
                    array[i][j] = processNet.array[i - eventNet.pnum][syncTranInProcess.get(j)];
                }
            }
        }
        //初始化关联矩阵中的event变迁列
        for (i = 0, k = syncTnum; i < eventNet.tnum; ++i) {
            if (!processNet.milestones.contains(eventNet.tranArray[i])) {
                for (j = 0; j < eventNet.pnum; ++j) {
                    array[j][k] = eventNet.array[j][i];
                }
                tran = new TransitionOfProduct("t" + (i + 1) + "'", ">>", eventNet.tranArray[i]);
                topArray.add(tran);
                ++k;
            }
        }

        //初始化关联矩阵中的process变迁列
        for (i = 0, k = syncTnum + eventNet.tnum - processNet.milestones.size(); i < processNet.tnum; ++i) {
            if (!processNet.milestones.contains(processNet.tranArray[i])) {
                for (j = eventNet.pnum; j < eventNet.pnum + processNet.pnum; ++j) {
                    array[j][k] = processNet.array[j - eventNet.pnum][i];
                }
                tran = new TransitionOfProduct(">>", "t" + (i + 1), processNet.tranArray[i]);
                topArray.add(tran);
                ++k;
            }
        }

        int[] ims = new int[pnum];
        int[] fms = new int[pnum];
        System.arraycopy(eventNet.im.m, 0, ims, 0, eventNet.im.m.length);
        System.arraycopy(processNet.im.m, 0, ims, eventNet.im.m.length, processNet.im.m.length);
        System.arraycopy(eventNet.fm.m, 0, fms, 0, eventNet.fm.m.length);
        System.arraycopy(processNet.fm.m, 0, fms, eventNet.fm.m.length, processNet.fm.m.length);
        Mark ic = new Mark(ims);
        Mark fc = new Mark(fms);
        return new PetriNet(array, pnum, tnum, ic, fc, topArray, placeIndex);
    }
}
