/**
 * @author Eliot PORTEVIN
 * @version 07.04.22
 */

import flanagan.io.Db;
import flanagan.io.FileNameSelector;
import flanagan.plot.PlotGraph;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Eliot_1{
    public static int[] run() throws IOException {
        Db.setTypeInfoOption(2);
        boolean fileExists;
        boolean fileOpened = false;
        double[] Numbers = {};
        //Creating arrays corresponding to methods

        //noinspection resource
        fileExists = Files.list(Paths.get("src/Arrays/")).findAny().isPresent();

        if (fileExists){
            boolean generate = Db.yesNo("You already have an array generated and saved,\n" +
                    "do you wish to generate a new one?");
            if (generate){
                Numbers = randomArray();
            }
            else{
                try{
                    Numbers = Einlesen();
                    fileOpened = true;
                } catch(Exception e){
                    Db.show("Could not read file.");
                    System.exit(0);
                }
            }
        }
        else{
            Numbers = randomArray();
        }
        //Output arrays
        outputData(Numbers);

        //Close program and save array to file
        try{
            if (!fileOpened){
                boolean save = Db.yesNo("Do you want to save your array to a file?");
                if (save){
                    Speichern(Numbers);
                }
            }
        }catch(Exception e){
            Db.show("ERROR\n" +
                    "Could not save array to file.\n" +
                    "The program is going to be ended.");
        }
        return getMinAndMax(Numbers);
    }
    private static double[] randomArray(){
        int max = 1000;
        int size = 1000;
        boolean validInput = false;

        while (!validInput){
            String input = Db.readLine("How many numbers do you wish to generate?", "1000");
            if (input.matches("\\d+")){
                try {
                    size = Integer.parseInt(input);
                    if (0<size) { //1000<=size
                        validInput = true;
                    }
                }catch (Exception ignored) {
                }
            }
        } validInput = false;
        while (!validInput){
            String input = Db.readLine("Random numbers will be generated such as:\n\n" +
                    "     -x <= random number <= x\n\n" +
                    "What is x?", "1000");
            if (input.matches("\\d+")){
                try {
                    max = Integer.parseInt(input);
                    if (0<max) {
                        validInput = true;
                    }
                }catch (Exception ignored) {
                }
            }
        }

        double[] Numbers = ThreadLocalRandom.current().doubles(size, -max, max).toArray();
        //Rounding numbers to whole numbers
        for(int i = 0; i < Numbers.length; i++){
            Numbers[i] = Math.round(Numbers[i]); // assigning back to same element
        }
        return Numbers;
    }
    private static double[] Einlesen() {
        double[] numbersArray = {};
        boolean validFile = false;
        String fileName, filePath = "src/Arrays/";
        FileNameSelector fc = new FileNameSelector(filePath);
        fc.setExtension("txt");
        List<String> numbersList = null;

        while (!validFile) {
            try {
                filePath = "src/Arrays/";
                fileName = fc.selectFile("Please choose your file.\n");
                filePath += fileName;
                // load the data from file
                numbersList = Files.readAllLines(Paths.get(filePath));

                validFile = true;
            } catch (Exception ignored) {
            }
        }
        // convert arraylist to array
        String[] numbersStrArray = numbersList.toArray(new String[0]);

        try {
            //Convert String array to Double Array
            numbersArray = Arrays.stream(numbersStrArray)
                    .mapToDouble(Double::parseDouble)
                    .toArray();
        } catch (Exception e) {
            Db.show("Careful! Your txt can only contain integers!\n" +
                    "The program is going to be ended.");
            System.exit(0);
        }

        return numbersArray;
    }
    private static void Speichern(double[] Numbers) {
        String fileName = "";
        StringBuilder filePath = new StringBuilder("src/Arrays/");
        FileNameSelector fc = new FileNameSelector("src/Arrays/");
        fc.setExtension("txt");
        String output = Arrays.toString(Numbers).replace("[", "")
                .replace("]", "")
                .replace(", ", "\n")
                .replace(".0", "");

        boolean validName = false;
        while (!validName){
            fileName = fc.selectFile("Please choose your file to save to.\n" +
                    "To create a new one, input its name without any extension.");
            if (fileName.contains(".txt")){
                filePath.append(fileName);
                validName = true;
            }else if (!fileName.contains(".")){
                filePath.append(fileName).append(".txt");
                validName = true;
            }else{
                Db.show("Your file name cannot contain any dots.");
            }
        }
        try (PrintWriter out = new PrintWriter(filePath.toString())) {
            out.println(output);
        }catch(java.io.FileNotFoundException e){
            Db.show("Could not find the file. The program is going to be ended.");
        }
        Db.show(String.format("Your array was saved under %s.txt", fileName.replace(".txt", "")));
    }
    private static double[] sortArray(double[] Numbers){
        double[] sortedNumbers = Numbers.clone();
        Arrays.sort(sortedNumbers);
        return sortedNumbers;
    }
    private static int[] getMinAndMax(double[] sortedNumbers){
        Arrays.sort(sortedNumbers);//In case the wrong array is inputted
        double min = sortedNumbers[0];
        double max = sortedNumbers[sortedNumbers.length-1];

        return new int[]{(int)Math.round(min), (int)Math.round(max)};
    }
    private static void outputData(double[] Numbers){
        //Clone numbers and sort array
        double[] sortedNumbers = sortArray(Numbers);

        String headerComment = "            --OUTPUT--\n\n" +
                "How would you wish to output your data?";
        String[] comments = {"Print using System.out.println", "Plot using PlotGraph"};
        String[] boxTitles = {"", ""};
        int defaultBox = 1;
        int opt =  Db.optionBox(headerComment, comments, boxTitles, defaultBox);

        if (opt == 1){
            System.out.println("# n| Random Number| Sorted Number");
            System.out.println("-----------------------------------");
            for (int n=0; n<Numbers.length; n++){
                System.out.printf("n=%d  |   %d |    %d%n",
                        n+1,
                        (int) Math.round(Numbers[n]),
                        (int) Math.round(sortedNumbers[n]));
            }
        }
        else if (opt == 2){
            if (Numbers.length > 50) {
                Db.show("-- WARNING --\n\n" +
                        "With large arrays (more than 50 values),\n" +
                        "the data can become unreadable.");
            }
            //Generate plot data
            double[][] cdata = PlotGraph.data(2, Numbers.length);
            Arrays.setAll(cdata[0], i -> i + 1);
            cdata[1] = Numbers;
            Arrays.setAll(cdata[2], i -> i + 1);
            cdata[3] = sortedNumbers;

            //Plotting arrays
            PlotGraph pg = new PlotGraph(cdata);
            pg.setGraphTitle("\n\nOutput");
            pg.setXaxisLegend("n");
            pg.setYaxisLegend("Random x");
            pg.setLine(new int[]{3, 0});
            pg.setDashLength(1);
            pg.plot();
        }
        //Calculating Average
        int sum = 0;
        for (double d : Numbers) sum += d;
        double average = 1.0d * sum / Numbers.length;

        int[] minMax = getMinAndMax(sortedNumbers);//Getting min and max

        Db.show(String.format("-- OUTPUT --\n\n" +
                        "Numbers generated: %s\n" +
                        "Average: %s\n" +
                        "Minimum: %s\n" +
                        "Maximum: %s\n",
                Numbers.length,
                average,
                minMax[0],
                minMax[1]));
    }
}