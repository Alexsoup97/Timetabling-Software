import java.util.HashMap;
public class Main {

    public static void main(String args[]){

        Data data = new Data(new DataReader());
        SpecialCourseScheduler s = new SpecialCourseScheduler();
        CourseScheduler courseScheduler = new CourseScheduler(s);
        

    }
    
}
