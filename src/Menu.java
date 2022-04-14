/**
 * @author Eliot PORTEVIN
 * @version 07.04.22
 */

import flanagan.io.Db;
import java.util.Arrays;

public class Menu{
    public static void main(String[] args){
        boolean Continue = true;
        String[] methodsArray = {"Random (double) array generation, output\n" +
                "and determination of minima/maxima\n",
                "Probability distribution of numbers based on\n" +
                        "a randomly generated array\n",
                "Your personal calendar\n",
                "Coffee temperature calculator\n",
                "Quit"};
        //empty array for buttons
        String[] methodsButtons = new String[methodsArray.length];
        Arrays.fill(methodsButtons, "");

        //Menu
        StringBuilder menuOutput = new StringBuilder(String.format("Welcome!\n\n" +
                "This program is Eliot PORTEVIN's portfolio project.\n" +
                "There are %d methods in this program, which one do\n" +
                "you wish to run?\n\n", methodsArray.length));
        for (int i=0; i<methodsArray.length; i++){
            menuOutput.append(String.format("\n%d. %s", i + 1, methodsArray[i]));
        }
        int defaultBox = 1;

        while (Continue){
            int opt =  Db.optionBox("Menu", menuOutput.toString(), methodsButtons, defaultBox);

            //Calling methods
            if (opt == 1){
                try{
                    //Min and Max returned as integer array
                    int[] minMax = Eliot_1.run();
                }catch (java.io.IOException ioe){
                    Db.show(ioe.toString());
                }
            }else if (opt == 2){
                Eliot_2.run();
            }else if (opt == 3){
                Kalender.run();
            }else if (opt == 4){
                Eliot_4.run();
            }else if (opt == 5){
                Db.show("Goodbye! The program is being closed.");
                Continue = false;
            }
        }
        System.exit(0);
    }
}
