
/*
 * Robotics Attendance Parser - By Rohit Mittal (Class of 2019)
 * Parses the data.csv file from the robotics system and calculates the total attendance for each robotics student.
 * Prints to system.out. 
*/
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class AttendanceParser {

    public static void main(String[] args) throws Exception {
        // Create a scanner for the saves file.
        File dataCSV = new File("data.csv");
        Scanner input = new Scanner(System.in);
        // If dataCSV does not exist, ask user for file name.
        while (!dataCSV.isFile()) {
            System.out.println("Data file does not seem to exist! Please enter the name of the file to parse");
            dataCSV = new File(input.nextLine());
        }
        input.close();
        Scanner sc = new Scanner(dataCSV);

        // Initialize variables.
        // Data to store information and direct input. (Hashmaps store <Name, Time>)
        HashMap<String, Long> StudentData = new HashMap<String, Long>();
        HashMap<String, Long> CurrentDateData = new HashMap<String, Long>();
        HashMap<String, Long> CurrentDateDataFINAL = new HashMap<String, Long>();
        String currentInput, inputName, inputDate, currentDate = "";
        long inputTime = 1;

        String format = "^(.+),(\\d?\\d:\\d\\d:\\d\\d),(.+),(.+)$"; // Formatting data for regexes.

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss"); // Formatting data for timestamps.

        while (sc.hasNextLine()) { // Is terminated by a break statement inside the while loop.
            // Check if new input matches the format.
            currentInput = sc.nextLine();
            if (!currentInput.matches(format)) {
                System.out.println("IGNORING LINE: " + currentInput);
                continue;
            }
            // Collect information from input (destructive!)
            inputName = currentInput.substring(0, currentInput.indexOf(","));
            currentInput = currentInput.substring(currentInput.indexOf(",") + 1);
            inputTime = dateFormat.parse(currentInput.substring(0, currentInput.indexOf(","))).getTime();
            currentInput = currentInput.substring(currentInput.indexOf(",") + 1);
            currentInput = currentInput.substring(currentInput.indexOf(",") + 1);
            inputDate = currentInput;

            if (currentDate.equals(inputDate)) {
                if (CurrentDateData.containsKey(inputName)) {
                    if (CurrentDateDataFINAL.containsKey(inputName)) {
                        CurrentDateDataFINAL.put(inputName,
                                CurrentDateDataFINAL.get(inputName) + inputTime - CurrentDateData.get(inputName));
                        CurrentDateData.remove(inputName);
                    } else {
                        CurrentDateDataFINAL.put(inputName, inputTime - CurrentDateData.get(inputName));
                        CurrentDateData.remove(inputName);
                    }
                } else {
                    CurrentDateData.put(inputName, inputTime);
                }
            } else {
                // If on a new date, merge all collected data for previous day and clear values
                // of intermediate hashmaps.
                currentDate = inputDate;
                for (Map.Entry<String, Long> value : CurrentDateDataFINAL.entrySet())
                    StudentData.merge(value.getKey(), value.getValue(), (v1, v2) -> v1 + v2);
                CurrentDateData = new HashMap<String, Long>();
                CurrentDateDataFINAL = new HashMap<String, Long>();
                CurrentDateData.put(inputName, inputTime);
            }
        }
        sc.close();
        for (Map.Entry<String, Long> value : CurrentDateDataFINAL.entrySet())
            StudentData.merge(value.getKey(), value.getValue(), (v1, v2) -> v1 + v2);
        System.out.println("-----------------TOTAL ATTENDANCE----------------");
        for (Map.Entry<String, Long> value : StudentData.entrySet()) {
            System.out.println(value.getKey() + ": " + Math.floor((value.getValue() / 3600000.) * 100) / 100. + "h");
        }
        System.out.println("-------------------------------------------------");
    }
}
