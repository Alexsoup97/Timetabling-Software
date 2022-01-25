import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Arrays;

public class Data {

    public static HashMap<String, Course> courseMap; // course code, course object
    public static HashMap<Integer, Student> studentMap; // student number, Student object
    public static HashMap<String, Room> roomMap; // room number (as string), Room object
    public static HashMap<String, ArrayList<String>> roomTypeMap; // room type, room numbers
    public static HashMap<String, String[]> roomTypeCourses; // room type, courses
    public static final int NUM_PERIODS = 8;
    public static int courseCount;
    public static HashMap<String, ArrayList<Integer>> userSpecialCourses = new HashMap<String, ArrayList<Integer>>();
    public static HashMap<String, ArrayList<ClassInfo>> coursesToClassInfo = new HashMap<String, ArrayList<ClassInfo>>(); // map of all course codes to the ClassInfos of each class of that course


    public static void loadData(DataReader dataReader) {
        // TODO make these two HashSets not hard coded and fill in all of them
        // String[] compulsoryCoursesArray = {"SNC1D1","FSF1D1","ENG1D1","CGC1D1","MTH1W1","PPL1O8","PPL1O9",
        //                                 "SNC1DG","FSF1DG","ENG1DG","CGC1DG","MTH1WG",
        //                                 "SNC1P1","FSF1P1","ENG1P1","CGC1P1"};  // etc.. (need to finish)
        // compulsoryCourses.addAll(Arrays.asList(compulsoryCoursesArray));
        // String[] importantCoursesArray = {"MHF4U1","MHF4UE","SBI3U6","SPH3U1","SCH3U6"}; // etc... (need to finish)
        // importantCourses.addAll(Arrays.asList(importantCoursesArray));

        try {
            roomTypeCourses = dataReader.getRoomTypeCourses();
            roomMap = dataReader.getRooms();
            roomTypeMap = dataReader.getRoomTypes();
            courseMap = dataReader.getCourses();
            studentMap = dataReader.getStudents();
            courseCount = dataReader.getNumCourses();
           
           
        } catch (Exception e) {
            System.out.println("Error Loading Data");
            e.printStackTrace();
        }
    }
}