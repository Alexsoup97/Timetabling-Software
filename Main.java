import java.util.ArrayList;

public class Main {

    public static void main(String args[]){

        Data.loadData(new DataReader());
// System.out.println(Data.courseMap); 
// System.out.println(Data.roomMap);
// System.out.println(Data.roomTypeCourses);
// System.out.println(Data.roomTypeMap)

// System.out.println(Data.studentMap);

        SpecialCourseScheduler s = new SpecialCourseScheduler();

        long time = System.nanoTime();
        CourseScheduler courseScheduler = new CourseScheduler(s);   
        ArrayList<ClassInfo> timetable =  courseScheduler.getNewTimetable();
        System.out.println("COURSE SCHEDULER TIME: " + (System.nanoTime() - time));
     
        time = System.nanoTime();
        new StudentAssignment(timetable).getStudentTimetables(timetable);;
        System.out.println("STUDENT ASSIGNMENT TIME: " + (System.nanoTime() - time));

        new UserInterface(timetable);        

    }
    
}
