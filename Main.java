import java.util.ArrayList;

public class Main {
    public static ArrayList<ClassInfo> timetable = null;
    public static void main(String args[]){
        new UserInterface();
    }
    
    public static void generateTimetable(){
        Data.loadData(new DataReader());
        SpecialCourseScheduler s = new SpecialCourseScheduler();
        CourseScheduler courseScheduler = new CourseScheduler(s);       
        timetable = new ArrayList<ClassInfo>();
        timetable =  courseScheduler.getNewTimetable();
        new StudentAssignment(timetable).getStudentTimetables(timetable);;
    }
    
    
}
