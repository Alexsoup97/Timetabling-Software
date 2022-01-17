import java.util.HashMap;
public class Main {

    public static HashMap<Integer, String> RoomMap;
    public static HashMap<String, Course> CourseMap;
    public static HashMap<Integer, Student> StudentMap;

    public static void main(String args[]){
        DataReader dataReader = new DataReader();
        try{
            //RoomMap = dataReader.getRooms(); //Rooom Map currently does not work
            CourseMap = dataReader.getCourses();
            StudentMap = dataReader.getStudents();
        }catch(Exception e){
            System.out.println("error");
        }
        

        CourseScheduling courseScheduler = new CourseScheduling();
        


    }
    
}
