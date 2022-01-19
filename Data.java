import java.util.HashMap;

public class Data{

    public static HashMap<Integer, String> roomMap; // room, room type
    public static HashMap<String, Course> courseMap; // course code, course object
    public static HashMap<Integer, Student> studentMap; // student number, Student object
    public static HashMap<Integer, Teacher> teacherMap; // teacher id, Teacher object
    
    public Data(){
        loadData();
    }

    public static void loadData(){
        DataReader dataReader = new DataReader();
        try{
            //RoomMap = dataReader.getRooms(); //Rooom Map currently does not work 
            courseMap = dataReader.getCourses();
            studentMap = dataReader.getStudents();
            // teacherMap = dataReader.getTeachers();
        }catch(Exception e){
            System.out.println("Error Loading Data");
            e.printStackTrace();
        }
    }

}