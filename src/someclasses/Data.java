package someclasses;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;
import com.panayotis.gnuplot.terminal.FileTerminal;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class Data {
    public Data(){}
    double voltage;
    double current;
    double s;
    double r;
    ArrayList<Data> list = new ArrayList<>();

    String addressOfDir;
    String addressOfGraphsAndTextFiles;

    /*public double getVoltage() {
        return voltage;
    }
    public double getCurrent() {
        return current;
    }
    public void setCurrent(double current) {
        this.current = current;
    }
    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }*/

    /*Collect data from file to ArrayList
     * Can also add data from other file to this.ArrayList*/

    public ArrayList<Data> getDataFromFile (String address) throws FileNotFoundException {
        File file = new File(address);
        Scanner scanner = new Scanner(file);
        scanner.nextLine();
        scanner.useLocale(Locale.ENGLISH);
        while (scanner.hasNext()) {
            Data data = new Data();
            data.voltage = scanner.nextDouble();
            data.current = scanner.nextDouble();
            data.r = data.voltage / data.current;
            data.s = data.current / data.voltage;
            list.add(data);
        }
        list.get(0).s = 0d;
        list.get(list.size() - 1).s = 0d;
        return list;
    }

    /*Create [][]points for constructor for DataSetPlot and Sets data in ArrayList to zero*/

    public double[][] ivToDoubleArray(ArrayList<Data> list){
        double[][] points = new double[list.size()][2];
        for (int i = 0; i < points.length; i++) {
            points[i][0] = list.get(i).voltage;
            points[i][1] = list.get(i).current;
        }
        return points;
    }

    /*Draw a graph using current data in [][]points
     * Supports address and name only in English*/

    public void drawGraph (String address, double[][] points, String  name) {
        JavaPlot plot = new JavaPlot();
        PlotStyle plotStyle = new PlotStyle();
        plotStyle.setStyle(Style.LINES);
        plotStyle.setLineWidth(1);
        DataSetPlot dataSetPlot = new DataSetPlot(points);
        dataSetPlot.setPlotStyle(plotStyle);
        plot.addPlot(dataSetPlot);
        plot.setTitle("IV-Curve");
        plot.setTerminal(new FileTerminal("png", address + "\\" + name + ".png"));
        plot.plot();
    }

    // public void drawGraphs (){}

    public void getAddresses (){
        Scanner in = new Scanner(System.in);
        System.out.println("Введи адрес папки с данными");
        addressOfDir = in.nextLine();
        System.out.println("Введи адрес папки, где будут лежать графики");
        addressOfGraphsAndTextFiles = in.nextLine();
    }
}