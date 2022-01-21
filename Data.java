import java.util.HashMap;

public class Data {

    public static HashMap<String, Course> courseMap; // course code, course object
    public static HashMap<Integer, Student> studentMap; // student number, Student object
    public static HashMap<String, Room> roomMap; // room number (as string), Room object
    public static HashMap<String, String[]> roomTypeMap; // room type, room numbers TODO loading this from the file

    public Data(DataReader dataReader) {
        try {
            // RoomMap = dataReader.getRooms(); //Rooom Map currently does not work
            roomMap = dataReader.getRooms();
            courseMap = dataReader.getCourses();
            studentMap = dataReader.getStudents();
        } catch (Exception e) {
            System.out.println("Error Loading Data");
            e.printStackTrace();
        }
    }

}