/**
 * @author Eliot PORTEVIN
 * @version 11.04.22
 */

import flanagan.io.Db;
import flanagan.plot.PlotGraph;

import java.util.Arrays;
import java.util.stream.DoubleStream;

public class Eliot_2{
    public static void run(){
        Db.setTypeInfoOption(2);
        int size=-1, max=0;
        Db.show("THE PROBABILITY CALCULATOR" +
                "\n\nAn amount n of numbers between 0 and x are going" +
                "\nto be randomly generated. Afterwards, it will be" +
                "\nshown how often they were generated. The sum of all" +
                "\ntheir probabilities should be, if math is correct," +
                "\nequal to 1.");
        while (size <= 0 || size > 100000){  //Too big numbers will cause an erroneous graph
            size = Db.readInt("How many numbers should be generated?", 1000);
        }
        while (max <= 0){
            max = Db.readInt("What should be the range of generation x such as:\n" +
                    "0 <= random number <= x", 9);
        }

        int[] zufallszahlen = generateArray(size, max);
        plotArray(zufallszahlen, max);
    }
    private static int[] generateArray(int size, int max){
        int[] zufallszahlen = new int[size];
        for (int n=0; n<size; n++){
            zufallszahlen[n] = (int) (Math.random() * (max+1));
        }

        return zufallszahlen;
    }
    private static double[] getProbabilities(int[] zufallszahlen, int max){
        double[] frequencies = new double[max+1];
        Arrays.fill(frequencies, 0);
        //Counting frequency of elements
        for (int n : zufallszahlen) {
            frequencies[n] += 1;
        }
        //Conversion of frequencies to probabilities
        for (int i=0; i<frequencies.length; i++){
            frequencies[i] = frequencies[i]/ (double) zufallszahlen.length;
        }
        return frequencies;
    }
    private static void plotArray(int[] zufallszahlen, int max){
        double[] frequencies = getProbabilities(zufallszahlen, max);
        double probabilitiesSum = DoubleStream.of(frequencies).sum();
        double average = probabilitiesSum/frequencies.length;

        //Generate plot data
        double[][] cdata = PlotGraph.data(2, max+1);
        Arrays.setAll(cdata[0], i -> i);//Fill x-data from 0 to max
        Arrays.setAll(cdata[2], i -> i);//Fill x-data from 0 to max
        cdata[1] = frequencies;//y-data of frequencies
        Arrays.fill(cdata[3], average);//horizontal line for average

        //Plotting arrays
        PlotGraph pg = new PlotGraph(cdata);
        pg.setGraphTitle(String.format("Sum of probabilities: %s", probabilitiesSum));
        pg.setGraphTitle2(String.format("Average probability: %.5s", average));
        pg.setPoint(4);
        pg.setXaxisLegend("x");
        pg.setYaxisLegend("p(x)");
        pg.setLine(new int[]{3, 3});
        pg.setPoint(new int[]{4, 0});
        pg.setDashLength(1);
        pg.plot();

        Db.show(String.format("-- Output --\n" +
                        "%d numbers between 0 and %d were generated.\n" +
                        "Their probability distribution was plotted with PlotGraph from Flanagan.",
                zufallszahlen.length,
                max));
        System.out.println(Arrays.toString(frequencies));
    }
}