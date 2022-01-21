import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;
import java.io.PrintWriter;


public class StudentAssignment{
    private ArrayList<ClassInfo> timetable;
    private ArrayList<Student> students = new ArrayList<Student>(Data.studentMap.values());
    
    
    public StudentAssignment(ArrayList<ClassInfo> timetable){
        this.timetable = timetable;
        fillTimetable();


    }



    public void fillTimetable(){
        for(ClassInfo c: timetable){
            for(Student s: Data.studentMap.values()){
                if(c.isFull()){
                    break;
                }
                if(s.hasCourse(c.getCourse()) && s.setTimetable(c.getCourse(), c.getTimeslot())){
                    c.addStudents(s.getStudentNumber());
                }
            }
        }
    }


    private int getStudentTimetableFitness(ArrayList<Student> studentListCandidate){
        String[] studentTimetable;
        for(Student student: studentListCandidate){
            studentTimetable = student.getTimetable();
            
        }
        return 0;
    }





}