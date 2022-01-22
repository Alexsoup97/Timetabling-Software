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
<<<<<<< HEAD
        StudentAssignment studentAssignment =  new StudentAssignment(courseScheduler.getNewTimetable());
        //courseScheduler.getNewTimetable()
        //System.out.println(courseScheduler.getNewTimetable());
=======
        // StudentAssignment studentAssignment =  new StudentAssignment(courseScheduler.getNewTimetable());
        System.out.println(courseScheduler.getNewTimetable());
>>>>>>> 0b02a307fc501809a7662f7cb9860cad0dd9ae64
        

    }
    
}
// barely lol, its small its like less than a half inch tall okay oh no maybe ill add it okay