/**
 * @author Eliot PORTEVIN
 * @version 11.04.2022
 */

import flanagan.io.Db;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Eliot_4{
    public static void run(){
        Db.setTypeInfoOption(2);

        //Get current time
        Calendar calendar = Calendar.getInstance();
        Date currentTime = calendar.getTime();
        Date startTime;
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");

        //Calendar for end time
        Calendar endCal = new GregorianCalendar();

        //Input variables
        double V=0; int T_s=-1;
        while (V<=0 || V>100){
            V = Db.readDouble("How much coffee do you have (in cL)?\n" +
                    "Min: 0 cl\n" +
                    "Max: 100 cl", 15)/100;
        }while (T_s<0 || T_s>40){
            T_s = (int) Math.round(Db.readDouble("What is the current room temperature in °C?\n" +
                    "Min: 0°C \n" +
                    "Max: 40°C", 20));
        }while (true){
            try{
                startTime = sdf.parse(
                        Db.readLine("When did you make your coffee?",
                                sdf.format(currentTime)));
                endCal.setTime(startTime);
                break;
            }catch (java.text.ParseException pe){
                pe.printStackTrace();
            }
        }

        //According to Newton’s law of cooling,
        //T(t) = Ts + (T0 – Ts)*e^(kt)
        //Coffee is considered too cold when below ~49°C
        //The cooling constant k varies depending on the volume
        //of the cup. For small volumes it can be approximated
        //with the function:
        //k(V)=0.0274 - 0.0169*ln(V)

        double k = 0.0274 - 0.0169*(Math.log(V));

        double timeFromBeginning = (Math.log(49-T_s)-Math.log(100-T_s))/(-k);

        endCal.add(Calendar.MINUTE, (int) timeFromBeginning);
        Date endTime = endCal.getTime();

        String output = String.format("--OUTPUT--\n\n" +
                        "At %s, you made yourself a boiling cup of coffee.\n" +
                        "Its volume was %s L, and the room temperature is\n" +
                        "%s °C. Coffee is commonly accepted to be drinkable\n" +
                        "as long as it is hotter than ~49°C. According to Newton's\n" +
                        "Law of Cooling, this gives you %s minutes to drink\n" +
                        "your coffee before it becomes too cold.\n\n" +
                        "This therefore gives you time to drink your coffee\n" +
                        "until %s.", sdf.format(startTime), V, T_s,
                Math.round(timeFromBeginning),
                sdf.format(endTime));
        Db.show(output);
    }
}