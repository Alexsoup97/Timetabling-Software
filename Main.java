import java.util.ArrayList;

public class Main {

    public static void main(String args[]){

        Data.loadData(new DataReader());
// System.out.println(Data.courseMap); 
// System.out.println(Data.roomMap);
// System.out.println(Data.roomTypeCourses);
// System.out.println(Data.roomTypeMap);
// System.out.println(Data.studentMap);

        SpecialCourseScheduler s = new SpecialCourseScheduler();
        CourseScheduler courseScheduler = new CourseScheduler(s);
        ArrayList<ClassInfo> timetable =  courseScheduler.getNewTimetable();
        StudentAssignment studentAssignment =  new StudentAssignment(timetable);
        UserInterface UI = new UserInterface(timetable);
       //System.out.println(courseScheduler.getNewTimetable());
        

    }
    
}
