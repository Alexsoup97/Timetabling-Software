import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;


public class StudentAssignment{
    private ArrayList<ClassInfo> timetable;
    private ArrayList<Student> students = new ArrayList<Student>(Data.studentMap.values());
    
    
    public StudentAssignment(ArrayList<ClassInfo> timetable){
        this.timetable = timetable;

    }



    public void getStudentTimetables(){
        

    }

    private int getStudentTimetableFitness(ArrayList<Student> studentListCandidate){
        String[] studentTimetable;
        for(Student student: studentListCandidate){
            studentTimetable = student.getTimetable();
            
        }
        return 0;
    }





}