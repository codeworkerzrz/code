package petri.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class InitializeMileStonesService {
    public HashMap<Integer, ArrayList<String>> getMileStonesMap(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
        HashMap<Integer, ArrayList<String>> mileStonesMap = new HashMap<>();
        String str;
        ArrayList<String> milestoneset = new ArrayList<>();
        mileStonesMap.put(0, milestoneset);
        for (int i = 1; i < 10; ++i) {
            str = reader.readLine();
            milestoneset = new ArrayList<>();
            for (int j = 0; j < 2 * i; j++) {
                milestoneset.add("tran"+str.split(" ")[j]);
            }
            mileStonesMap.put(i * 2, milestoneset);
        }
        reader.close();
        return mileStonesMap;
    }
}
