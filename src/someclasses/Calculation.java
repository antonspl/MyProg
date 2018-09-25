package someclasses;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Calculation {
    public static void main(String[] args) throws IOException {
        Data data = new Data();
        data.getAddresses();
        /*data.getDataFromFile(data.addressOfDir + "\\Erase[03.06.2018 15_29_48].prn");
        data.getDataFromFile(data.addressOfDir + "\\Write[03.06.2018 15_37_07].prn");
        data.addressOfDir = "C:\\Users\\alebedev\\Desktop\\Результаты ReRAM\\Результаты измерений ReRAM\\3.06.2018\\8-3-9 Hf\\Classic_Cyc Vreset=-2.2";
        data.addressOfGraphsAndTextFiles = "C:\\Users\\alebedev\\Documents";*/
        // data.drawGraph(data.addressOfGraphsAndTextFiles);
        ArrayList<String> erase = new ArrayList<>();
        ArrayList<String> write = new ArrayList<>();
        File dirOfFiles = new File(data.addressOfDir);
        String[] files = dirOfFiles.list();
        for (int i = 0; i < files.length; i++) {
            if (files[i].startsWith("Erase")) {
                erase.add(files[i]);
            }
            if (files[i].startsWith("Write")) {
                write.add(files[i]);
            }
        }
        Collections.sort(erase);
        Collections.sort(write);


        File dirOfGraphs = new File(data.addressOfGraphsAndTextFiles + "\\IV");
        dirOfGraphs.mkdirs();
        double[][] uSet = new double[write.size()][2];
        double[][] rOff = new double[write.size()][2];
        double[][] rOn = new double[write.size()][2];
        double[][] uReset = new double[write.size()][2];
        for (Integer i = 0; i < write.size(); i++) {
            data.list.clear();
            data.list = data.getDataFromFile(data.addressOfDir + "\\" + write.get(i));
            int size = data.list.size();
            ArrayList<Data> b = new ArrayList<>();
            for (int j = 0; j < 2 * size - 1; j++) {
                if (j % 2 == 0) {
                    b.add(data.list.get(j / 2));
                } else {
                    Data c = new Data();
                    c.voltage = (data.list.get(j / 2 + 1).voltage + data.list.get(j / 2).voltage) / 2;
                    c.current = (data.list.get(j / 2 + 1).current + data.list.get(j / 2).current) / 2;
                    c.r = (data.list.get(j / 2 + 1).r + data.list.get(j / 2).r) / 2;
                    c.s = (data.list.get(j / 2 + 1).s + data.list.get(j / 2).s) / 2;
                    b.add(c);
                }
            }
            ArrayList<Data> allData = new ArrayList<>(b);
            double sMax = 0;
            boolean flag = true;
            for (int j = 0; j < b.size(); j++) {
                if (b.get(j).s >= sMax) {
                    sMax = b.get(j).s;
                    uSet[i][1] = b.get(j).voltage;
                    uSet[i][0] = i;
                }
            }
            for (int j = 0; j < b.size(); j++) {
                if (Math.abs(b.get(j).voltage - 0.5) < Double.MIN_VALUE) {
                    if (flag) {
                        rOff[i][1] = b.get(j).r;
                        rOff[i][0] = i;
                        flag = false;
                    } else {
                        rOn[i][1] = b.get(j).r;
                        rOn[i][0] = i;
                        flag = true;
                    }
                }
            }
            data.list.clear();
            data.list = data.getDataFromFile(data.addressOfDir + "\\" + erase.get(i));
            size = data.list.size();
            b.clear();
            for (int j = 0; j < 2 * size - 1; j++) {
                if (j % 2 == 0) {
                    b.add(data.list.get(j / 2));
                } else {  // Вот здесь что-то работает неправильно, потому что получается раздвоение у Reset'а на ВАХе (прим. - я просто копирнул код для Set'ов, видимо, он не работает здесь)
                    Data c = new Data();
                    c.voltage = (data.list.get(j / 2 + 1).voltage + data.list.get(j / 2).voltage) / 2;
                    c.current = (data.list.get(j / 2 + 1).current + data.list.get(j / 2).current) / 2;
                    c.r = (data.list.get(j / 2 + 1).r + data.list.get(j / 2).r) / 2;
                    c.s = (data.list.get(j / 2 + 1).s + data.list.get(j / 2).s) / 2;
                    b.add(c);
                }
            }
            allData.addAll(b);
            double sMin = 0;
            for (int j = 0; j < b.size(); j++) {
                if (b.get(j).s >= sMin) {
                    sMin = b.get(j).s;
                    uReset[i][1] = b.get(j).voltage;
                    uReset[i][0] = i;
                }
            }
            b.clear();
            data.drawGraph(data.addressOfGraphsAndTextFiles + "\\IV", data.ivToDoubleArray(allData), i.toString());
            allData.clear();
        }
        data.drawGraph(data.addressOfGraphsAndTextFiles + "\\IV", uReset, "uReset");
        data.drawGraph(data.addressOfGraphsAndTextFiles + "\\IV", uSet, "uSet");
        data.drawGraph(data.addressOfGraphsAndTextFiles + "\\IV", rOff, "Roff");
        data.drawGraph(data.addressOfGraphsAndTextFiles + "\\IV", rOn, "Ron");
        File outputMassives = new File(data.addressOfGraphsAndTextFiles + "\\IV\\Stats");
        outputMassives.mkdirs();
        for (int i = 0; i < 4; i++) {
            switch (i) {
                case 0:
                    File outputMassives2 = new File(data.addressOfGraphsAndTextFiles + "\\IV\\Stats\\Ron_List.txt");
                    outputMassives2.createNewFile();
                    FileWriter output = new FileWriter(outputMassives2);
                    for (int j = 0; j < rOn.length; j++) {
                        Double a = rOn[j][1];
                        output.write(a.toString());
                        output.write("\n");
                    }
                    output.close();
                    break;
                case 1:
                    File outputMassives3 = new File(data.addressOfGraphsAndTextFiles + "\\IV\\Stats\\Roff_List.txt");
                    outputMassives3.createNewFile();
                    FileWriter output3 = new FileWriter(outputMassives3);
                    for (int j = 0; j < rOff.length; j++) {
                        Double a = rOff[j][1];
                        output3.write(a.toString());
                        output3.write("\n");
                    }
                    output3.close();
                    break;
                case 2:
                    File outputMassives4 = new File(data.addressOfGraphsAndTextFiles + "\\IV\\Stats\\Vset_List.txt");
                    outputMassives4.createNewFile();
                    FileWriter output4 = new FileWriter(outputMassives4);
                    for (double[] anUSet : uSet) {
                        Double a = anUSet[1];
                        output4.write(a.toString());
                        output4.write("\n");
                    }
                    output4.close();
                    break;
                case 3:
                    File outputMassives5 = new File(data.addressOfGraphsAndTextFiles + "\\IV\\Stats\\Vreset_List.txt");
                    outputMassives5.createNewFile();
                    FileWriter output5 = new FileWriter(outputMassives5);
                    for (int j = 0; j < uReset.length; j++) {
                        Double a = uReset[j][1];
                        output5.write(a.toString());
                        output5.write("\n");
                    }
                    output5.close();
                    break;
                default: break;
            }
        }
    }
}