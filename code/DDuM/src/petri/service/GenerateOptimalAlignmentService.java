package petri.service;

import net.sf.javailp.*;
import petri.entity.AlignmentData;
import petri.entity.Mark;
import petri.entity.PetriNet;
import petri.entity.TransitionOfProduct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;


public class GenerateOptimalAlignmentService {
    public PetriNet product;
    public ArrayList<TransitionOfProduct> alignments;
    public PriorityQueue<QNode> queue;
    public ArrayList<QNode> visited;
    long start;
    long end;
    long costtime;

    public GenerateOptimalAlignmentService() {

    }


    public AlignmentData generateOptimalAlignment(PetriNet product) {
        int count = 0;
        this.product = product;
        QNode.product = product;
        AlignmentData data = new AlignmentData();
        //设置优先级队列升序排列
        Comparator<QNode> cmp = Comparator.comparingInt(q -> (q.mintotalDist + q.underestimation));
        queue = new PriorityQueue<>(cmp);
        alignments = new ArrayList<>();
        visited = new ArrayList<>();
        QNode currentNode;
        Mark nextMark;
        int i;
        QNode qNode = new QNode(product.im, null, null);

        start = System.currentTimeMillis();
        queue.offer(qNode);
        ++count;
        visited.add(qNode);
        while (!queue.isEmpty()) {
            currentNode = queue.poll();
            //到达终止标识则停止
            if (currentNode != null) {
                if (currentNode.mark.equal(product.fm)) {
                    QNode tempNode = currentNode;
                    while (tempNode != null && tempNode.fromTran != null) {
                        alignments.add(tempNode.fromTran);
                        tempNode = tempNode.preNode;
                    }
                    Collections.reverse(alignments);
                    break;
                } else {
                    for (i = 0; i < product.tnum; ++i) {
                        nextMark = product.tranHappen(currentNode.mark, i);
                        if (null != nextMark) {
                            int qIndex = ifExisted(nextMark);
                            if (-1 == qIndex) {
                                //未访问过的节点加入队列
                                QNode succNode = new QNode(nextMark, currentNode, product.topArray.get(i));
                                ++count;
                                queue.offer(succNode);
                                visited.add(succNode);
                            } else {
                                //访问过的节点检查是否需要更新数据
                                int visitedMinTotalDist = visited.get(qIndex).mintotalDist;
                                int currentMinTotalDist = currentNode.mintotalDist + product.topArray.get(i).getClc();
                                if (visitedMinTotalDist > currentMinTotalDist) {
                                    visited.get(qIndex).mintotalDist = currentMinTotalDist;
                                    visited.get(qIndex).fromTran = product.topArray.get(i);
                                    visited.get(qIndex).preNode = currentNode;
                                }
                            }
                        }
                    }
                }
            }
        }
        end = System.currentTimeMillis();
        costtime = end - start;
        data.costTime = costtime;
        data.reachableMarkcount = count;
        /*for (QNode q : visited) {
            q.printQNode();
        }*/
        return data;
    }

    public int ifExisted(Mark mark) {
        for (QNode qNode : visited) {
            if (qNode.mark.equal(mark)) {
                return visited.indexOf(qNode);
            }
        }
        return -1;
    }

    public void printAlignment() {
        System.out.println("----------计算Optimal Alignment耗时：" + costtime + "毫秒--------------");
        System.out.print("|");
        for (TransitionOfProduct tran : alignments) {
            System.out.print(String.format("  %" + (tran.label.length() == 1 ? 5 : tran.label.length()) + "s", tran.et) + "  |");
        }

        System.out.println();
        System.out.print("|");
        for (TransitionOfProduct tran : alignments) {
            System.out.print(String.format("  %" + (tran.label.length() == 1 ? 5 : tran.label.length()) + "s", tran.pt) + "  |");

        }
        System.out.println();
        System.out.print("|");
        for (TransitionOfProduct tran : alignments) {
            System.out.print(String.format("  %" + (tran.label.length() == 1 ? 5 : tran.label.length()) + "s", tran.label) + "  |");
        }
        System.out.println();
    }
}


class QNode {
    Mark mark;
    QNode preNode;
    TransitionOfProduct fromTran;
    static PetriNet product;
    int mintotalDist;
    int underestimation;

    QNode(Mark mark, QNode preNode, TransitionOfProduct fromTran) {
        this.mark = mark;
        this.preNode = preNode;
        this.fromTran = fromTran;
        this.mintotalDist = getMinTotalDist();
        this.underestimation = heuristicFunc();
    }

    public int getMinTotalDist() {
        if (preNode == null || fromTran == null) return 0;
        return preNode.mintotalDist + fromTran.getClc();
    }

    public int heuristicFunc() {
        int i, j;
        int[] temp = new int[product.pnum];
        for (i = 0; i < product.pnum; ++i) {
            temp[i] = product.fm.m[i] - mark.m[i];
        }
        SolverFactory factory = new SolverFactoryLpSolve();
        factory.setParameter(Solver.VERBOSE, 0);
        factory.setParameter(Solver.TIMEOUT, 100);
        Problem problem = new Problem();

        Linear linear = new Linear();
        for (i = 0; i < product.tnum; ++i) {
            linear.add(product.topArray.get(i).getClc(), ("x" + i));
            problem.setVarType(("x" + i), Integer.class);
        }

        problem.setObjective(linear, OptType.MIN);

        for (i = 0; i < product.pnum; ++i) {
            linear = new Linear();
            for (j = 0; j < product.tnum; ++j) {
                linear.add(product.array[i][j], ("x" + j));
            }
            problem.add(linear, "=", temp[i]);
        }

        Solver solver = factory.get();
        Result result = solver.solve(problem);
        if(null==result)return Integer.MAX_VALUE;
        return result.getObjective().intValue();
    }

    public void printQNode() {
        mark.printMarkWithIndex(product.placeIndex);
        System.out.print("  from:");
        if (preNode != null) preNode.mark.printMarkWithIndex(product.placeIndex);
        if (fromTran != null) fromTran.printTopWithLabel();
        System.out.println("  min:" + mintotalDist + "  under" + underestimation);
    }
}
