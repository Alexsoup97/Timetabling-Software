import java.util.ArrayList;

public class Main {

    public static void main(String args[]){

        Data.loadData(new DataReader());
// System.out.println(Data.courseMap); 
// System.out.println(Data.roomMap);
// System.out.println(Data.roomTypeCourses);
// System.out.println(Data.roomTypeMap)

// System.out.println(Data.studentMap);

        // long time = System.nanoTime();
      
        SpecialCourseScheduler s = new SpecialCourseScheduler();
        
        CourseScheduler courseScheduler = new CourseScheduler(s);

        // System.out.println(System.nanoTime() - time);
        ArrayList<ClassInfo> timetable =  courseScheduler.getNewTimetable();
        StudentAssignment studentAssignment =  new StudentAssignment(timetable);
        UserInterface UI = new UserInterface(null);
        
       //System.out.println(courseScheduler.getNewTimetable());
        

    }
    
}
