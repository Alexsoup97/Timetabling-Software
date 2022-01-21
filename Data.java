import java.util.HashMap;
import java.util.ArrayList;

public class Data{

    //public static HashMap<Integer, Room> roomMap; // room, room type
    public static HashMap<String, Course> courseMap; // course code, course object
    public static HashMap<Integer, Student> studentMap; // student number, Student object
    // public static HashMap<Integer, Teacher> teacherMap; // teacher id, Teacher object
    public static HashMap<String, Room> roomMap; // room number (as string), Room object
    public static HashMap<String, String[]> typesOfRooms; // room type, room numbers
    public static HashMap<String, ArrayList<Room>> coursesToRooms;

    public Data(){
        loadData();
    }

    public static void loadData(){
        DataReader dataReader = new DataReader();
        try{
            typesOfRooms = dataReader.getRoomType();
            roomMap=dataReader.getRooms();
            courseMap = dataReader.getCourses();
            studentMap = dataReader.getStudents();
            coursesToRooms = dataReader.courseTypeToRooms();
            

        }catch(Exception e){
            System.out.println("Error Loading Data");
            e.printStackTrace();
        }
    }

}