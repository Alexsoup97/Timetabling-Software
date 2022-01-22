import java.util.HashMap;
public class Main {

    public static void main(String args[]){

        Data.loadData(new DataReader());
System.out.println(Data.courseMap);
System.out.println(Data.roomMap);
System.out.println(Data.roomTypeCourses);
System.out.println(Data.roomTypeMap);
System.out.println(Data.studentMap);

        SpecialCourseScheduler s = new SpecialCourseScheduler();
        CourseScheduler courseScheduler = new CourseScheduler(s);
        System.out.println(courseScheduler.getNewTimetable());

    }
    
}
// barely lol, its small its like less than a half inch tall okay oh no maybe ill add it okay