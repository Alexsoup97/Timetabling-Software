import java.util.ArrayList;
import java.util.HashMap;

public class SpecialCourseScheduler {

    SpecialCourseScheduler(){}

    public ArrayList<ClassInfo> getSpecialCourseTimetable (HashMap <String,Integer> numberOfClasses){
        ArrayList<ClassInfo> specialCourses = new ArrayList<ClassInfo>();
        ArrayList<Integer> students= null;
        boolean fixed=true;
        String course;
        int timeslot;
        int room;
        int teacher=0;

        for ( String s: numberOfClasses.keySet()){
            for (int i= 0; i<numberOfClasses.get(s); i++){
                course=s;

                if (s.contains("MHF")){

                }else if (s.contains())

                //teacher make random number;


                //getroom,gettimeslot
                Data.get

                specialCourses.add(new ClassInfo(teacher,  room,  timeslot,  course, fixed, students));
            }
        }





        return null;
    }

    

}
