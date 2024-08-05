package petri.entity;

import java.util.ArrayList;

/**
 * PetriNet对象类
 */
public class PetriNet {

    //petriNet类型
    public int petriType;
    //关联矩阵
    public int[][] array;
    //初始标识
    public Mark im;
    //终止标识
    public Mark fm;
    //库所数
    public int pnum;
    //变迁数
    public int tnum;
    //库所下标数组
    public String[] placeIndex;
    //变迁数组
    public String[] tranArray;
    //product变迁数组
    public ArrayList<TransitionOfProduct> topArray;
    //processNet的里程碑
    public ArrayList<String> milestones;

    /**
     * 变迁无标签petriNet
     * type == 0
     */
    public PetriNet(int[][] array, int pnum, int tnum, Mark im, Mark fm) {
        this.array = array;
        this.pnum = pnum;
        this.tnum = tnum;
        this.im = im;
        this.fm = fm;
        petriType = 0;
    }

    /**
     * 变迁有标签petriNet
     * type == 1
     */
    public PetriNet(int[][] array, int pnum, int tnum, Mark im, Mark fm, String[] tranArray, String[] placeIndex) {
        this.array = array;
        this.pnum = pnum;
        this.tnum = tnum;
        this.im = im;
        this.fm = fm;
        this.tranArray = tranArray;
        this.placeIndex = placeIndex;
        this.milestones = new ArrayList<>();
        petriType = 1;
    }

    /**
     * productNet of eventNet and processNet
     * type == 2
     */
    public PetriNet(int[][] array, int pnum, int tnum, Mark im, Mark fm, ArrayList<TransitionOfProduct> topArray, String[] placeIndex) {
        this.array = array;
        this.pnum = pnum;
        this.tnum = tnum;
        this.im = im;
        this.fm = fm;
        this.topArray = topArray;
        this.placeIndex = placeIndex;
        petriType = 2;
    }


    public Mark tranHappen(Mark fromMark, int tranIndex) {
        int i;
        boolean cover = true;
        Mark nextMark = new Mark(pnum);
        for (i = 0; i < pnum; ++i) {
            nextMark.m[i] = fromMark.m[i] + array[i][tranIndex];
            if (nextMark.m[i] < 0) return null;
            if (nextMark.m[i] < fromMark.m[i]) cover = false;
        }
        if (cover) return null;
        return nextMark;
    }

    public int getArcNum() {
        int num = 0;
        for (int[] line : array) {
            for (int val : line) {
                if (val != 0) ++num;
            }
        }
        return num;
    }

    public void printPetriNet() {
        int i, j;
        switch (petriType) {
            case 0:
                System.out.println("------普通petri网------");
                break;
            case 1:
                System.out.println("------标签petri网------");
                System.out.print("所有标签：");
                for (String str : tranArray) {
                    System.out.print(str + " ");
                }
                System.out.println();
                break;
            case 2:
                System.out.println("-------product网-------");
                System.out.print("所有变迁:");
                for (TransitionOfProduct tran : topArray) {
                    tran.printTopWithLabel();
                    System.out.print(" ");
                }
                System.out.println();
                break;
        }
        System.out.println("----库所数：" + pnum + ",变迁数：" + tnum + "----");
        for (i = 0; i < pnum; ++i) {
            for (j = 0; j < tnum; ++j) {
                System.out.print(" " + String.format("%3d", array[i][j]));
            }
            System.out.println();
        }
        System.out.print("初始标识:");
        im.printMarkWithIndex(placeIndex);
        System.out.print("终止标识:");
        fm.printMarkWithIndex(placeIndex);
        System.out.println();

    }
}