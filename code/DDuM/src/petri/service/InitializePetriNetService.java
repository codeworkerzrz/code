package petri.service;

import petri.entity.Mark;
import petri.entity.PetriNet;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class InitializePetriNetService {
    /**
     * 初始化普通petri网
     */
    public PetriNet initPetri(String filename) throws IOException {
        //System.out.println("------开始初始化petri net------");
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
        String str;
        str = reader.readLine();
        int pnum = Integer.parseInt(str.split(" ")[0]);
        int tnum = Integer.parseInt(str.split(" ")[1]);
        //初始化关联矩阵
        int i, j;
        int[][] array = new int[pnum][tnum];
        for (i = 0; i < pnum; ++i) {
            str = reader.readLine();
            for (j = 0; j < tnum; ++j) {
                array[i][j] = Integer.parseInt(str.split(" ")[j]);
            }
        }
        //初始化起始标识
        Mark im = new Mark(pnum);
        str = reader.readLine();
        for (i = 0; i < pnum; ++i) {
            im.m[i] = Integer.parseInt(str.split(" ")[i]);
        }
        //初始化终止标识
        Mark fm = new Mark(pnum);
        str = reader.readLine();
        for (i = 0; i < pnum; ++i) {
            fm.m[i] = Integer.parseInt(str.split(" ")[i]);
        }
        PetriNet petriNet = new PetriNet(array, pnum, tnum, im, fm);
        //petriNet.printPetriNet();
        //System.out.println("------初始化完成------");
        reader.close();
        return petriNet;
    }

    public PetriNet initProcessPetriNet(String filename) throws IOException {
        //System.out.println("------开始初始化process net------");
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
        String str;
        str = reader.readLine();
        int pnum = Integer.parseInt(str.split(" ")[0]);
        int tnum = Integer.parseInt(str.split(" ")[1]);
        int i, j;
        int[][] array = new int[pnum][tnum];
        //初始化变迁标签数组
        String[] tranArray = new String[tnum];
        for (i = 0; i < tnum; ++i) {
            tranArray[i] = reader.readLine();
        }
        //初始化库所下标数组
        String[] placeIndex = new String[pnum];
        for (i = 0; i < pnum; ++i) {
            placeIndex[i] = "p" + (i + 1);
        }
        //初始化关联矩阵
        for (i = 0; i < pnum; ++i) {
            str = reader.readLine();
            for (j = 0; j < tnum; ++j) {
                array[i][j] = Integer.parseInt(str.split(" ")[j]);
            }
        }
        //初始化起始标识
        Mark im = new Mark(pnum);
        str = reader.readLine();
        for (i = 0; i < pnum; ++i) {
            im.m[i] = Integer.parseInt(str.split(" ")[i]);
        }
        //初始化终止标识
        Mark fm = new Mark(pnum);
        str = reader.readLine();
        for (i = 0; i < pnum; ++i) {
            fm.m[i] = Integer.parseInt(str.split(" ")[i]);
        }
        PetriNet petriNet = new PetriNet(array, pnum, tnum, im, fm, tranArray, placeIndex);
        //petriNet.printPetriNet();
        //System.out.println("------初始化完成------");
        reader.close();
        return petriNet;
    }

    public PetriNet initEventPetriNet(ArrayList<String> trace) {
        //System.out.println("------开始初始化event net------");
        int tnum = trace.size();
        int pnum = tnum + 1;
        int i, j;
        int[][] array = new int[pnum][tnum];
        //初始化变迁标签数组
        String[] tranArray = trace.toArray(new String[tnum]);
        //初始化库所下标数组
        String[] placeIndex = new String[pnum];
        for (i = 0; i < pnum; ++i) {
            placeIndex[i] = "p" + (i + 1) + "'";
        }
        //初始化关联矩阵
        for (i = 0; i < pnum; ++i) {
            for (j = 0; j < tnum; ++j) {
                if (i == j) {
                    array[i][j] = -1;
                } else if (i == j + 1) {
                    array[i][j] = 1;
                } else {
                    array[i][j] = 0;
                }
            }
        }
        //初始化起始标识
        Mark im = new Mark(pnum);
        im.m[0] = 1;
        for (i = 1; i < pnum; ++i) {
            im.m[i] = 0;
        }
        //初始化终止标识
        Mark fm = new Mark(pnum);
        fm.m[pnum - 1] = 1;
        for (i = 0; i < pnum - 1; ++i) {
            fm.m[i] = 0;
        }
        PetriNet petriNet = new PetriNet(array, pnum, tnum, im, fm, tranArray, placeIndex);
        //petriNet.printPetriNet();
        //System.out.println("------初始化完成------");
        return petriNet;
    }

    public PetriNet initEventPetriNet(String filename) throws IOException {
        //System.out.println("------开始初始化event net------");
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
        String str;
        str = reader.readLine();
        int tnum = Integer.parseInt(str);
        int pnum = tnum + 1;
        int i, j;
        int[][] array = new int[pnum][tnum];
        //初始化变迁标签数组
        String[] tranArray = new String[tnum];
        for (i = 0; i < tnum; ++i) {
            tranArray[i] = reader.readLine();
        }
        //初始化库所下标数组
        String[] placeIndex = new String[pnum];
        for (i = 0; i < pnum; ++i) {
            placeIndex[i] = "p" + (i + 1) + "'";
        }
        //初始化关联矩阵
        for (i = 0; i < pnum; ++i) {
            for (j = 0; j < tnum; ++j) {
                if (i == j) {
                    array[i][j] = -1;
                } else if (i == j + 1) {
                    array[i][j] = 1;
                } else {
                    array[i][j] = 0;
                }
            }
        }
        //初始化起始标识
        Mark im = new Mark(pnum);
        im.m[0] = 1;
        for (i = 1; i < pnum; ++i) {
            im.m[i] = 0;
        }
        //初始化终止标识
        Mark fm = new Mark(pnum);
        fm.m[pnum - 1] = 1;
        for (i = 0; i < pnum - 1; ++i) {
            fm.m[i] = 0;
        }
        PetriNet petriNet = new PetriNet(array, pnum, tnum, im, fm, tranArray, placeIndex);
        //petriNet.printPetriNet();
        //System.out.println("------初始化完成------");
        reader.close();
        return petriNet;
    }
}
