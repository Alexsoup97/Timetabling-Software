import java.util.HashMap;
import java.util.ArrayList;
public class Data {

    public static HashMap<String, Course> courseMap; // course code, course object
    public static HashMap<Integer, Student> studentMap; // student number, Student object
    public static HashMap<String, Room> roomMap; // room number (as string), Room object
    public static HashMap<String, ArrayList<String>> roomTypeMap; // room type, room numbers
    public static HashMap<String, String[]> roomTypeCourses; // room type, courses
    public static final int NUM_PERIODS = 8;

    public static void loadData(DataReader dataReader) {
        try {
          
            roomTypeCourses = dataReader.getRoomTypeCourses();
            roomMap = dataReader.getRooms();
            roomTypeMap = dataReader.getRoomTypes();
            courseMap = dataReader.getCourses();
            studentMap = dataReader.getStudents();
           
           
        } catch (Exception e) {
            System.out.println("Error Loading Data");
            e.printStackTrace();
        }
    }

}