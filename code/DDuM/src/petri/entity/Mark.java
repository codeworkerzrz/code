package petri.entity;

import java.util.Arrays;

/**
 * 情态标识类
 */
public class Mark {
    public int[] m;

    public Mark(int size) {
        m = new int[size];
    }

    public Mark(int[] m) {
        this.m = m;
    }

    /**
     * 判断两情态标识是否相同
     */
    public boolean equal(Mark mark) {
        return Arrays.equals(m, mark.m);
    }

    /**
     * 打印带标签的情态标识
     */
    public void printMarkWithIndex(String[] placeIndex) {
        int i, count = 0;
        if (m.length > 0) {
            for (i = 0; i < m.length; ++i) {
                if (m[i] > 0 && count == 0) {
                    ++count;
                    System.out.print("[" + placeIndex[i]);
                } else if (m[i] > 0 && count > 0) {
                    ++count;
                    System.out.print("," + placeIndex[i]);
                }
            }
            System.out.print("]");
        }
    }
}

