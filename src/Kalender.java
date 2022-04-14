/**
 * @author Eliot PORTEVIN
 * @version 11.04.2022
 */

import flanagan.io.Db;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Kalender{
    public static void run(){
        Db.setTypeInfoOption(2);
        boolean Continue = true;

        //Get current date
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String currentDate = dateFormat.format(new Date());

        String text = "Welcome to Eliot's Calendar!\n" +
                "Do you want to:\n\n" +
                "1. See your events for a certain date\n" +
                "2. Create a new event\n" +
                "3. Exit the calendar";

        String[] buttons = new String[3];
        Arrays.fill(buttons, "");
        while(Continue){
            int opt =  Db.optionBox("Menu", text, buttons, 1);
            if (opt == 1){
                liefereEreignisse(currentDate);
            } else if (opt == 2){
                neuerTermin(currentDate);
            } else if (opt == 3){
                Continue = false;
            }
        }
    }
    private static void liefereEreignisse(String currentDate){
        Db.setTypeInfoOption(2);

        //Read events from file
        List<String> eventsList = readEvents();
        String[][] eventsArray = new String[eventsList.size()][3];
        String eventDate = null;
        boolean validInput = false;

        for (int i=0; i<eventsList.size(); i++){
            String singleEventStr = eventsList.get(i);
            String[] singleEventArr = singleEventStr.split(";");
            eventsArray[i] = singleEventArr;
        }

        while (!validInput) {
            eventDate = Db.readLine("What day would you like to see events for?", currentDate).replace(";", "");
            if (eventDate.replace(".", "").matches("\\d+")){
                if (eventDate.length() == 10){
                    validInput = true;
                }
            }
        }

        StringBuilder output = new StringBuilder(String.format("Here are your events for the %s!\n" +
                "Format: Event, Addition Date\n", eventDate));

        boolean dateExists = false;
        for (String[] strings : eventsArray) {
            if (strings[0].equals(eventDate)) {
                output.append("\n").append(String.format("%s, %s",
                        strings[1].trim(),
                        strings[2].trim()));//In case there are double spaces
                dateExists = true;
            }
        }
        if (dateExists){
            Db.show(output.toString());
        }
        else{
            Db.show(String.format("You have no events planned for the %s.",
                    eventDate));
        }
    }
    private static List<String> readEvents(){
        String filePath = "src/CalendarEvents/Dates.txt";

        // load the data from file
        List<String> eventsList = new ArrayList<>();
        try{
            eventsList = Files.readAllLines(Paths.get(filePath));
        }catch (java.io.IOException ioe){
            Db.show(ioe.toString());
        }

        return eventsList;
    }
    private static void neuerTermin(String currentDate){
        List<String> eventsList = readEvents();
        boolean validInput = false;
        String eventDate = null;

        while (!validInput) {
            eventDate = Db.readLine("When is your event?", currentDate).replace(";", "");
            if (eventDate.replace(".", "").matches("\\d+")){
                if (eventDate.length() == 10){
                    validInput = true;
                }
            }
        }
        String eventText = Db.readLine("What do you want to call your event?", "Event text").replace(";", "");

        eventsList.add(String.format("%s; %s; %s",
                eventDate,
                eventText,
                currentDate));

        String output = String.join("\n", eventsList);
        try (PrintWriter out = new PrintWriter("src/CalendarEvents/Dates.txt")) {
            out.println(output);
        }catch(java.io.FileNotFoundException e){
            Db.show(e.toString());
        }
    }
}