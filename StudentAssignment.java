import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;
import java.io.PrintWriter;
import java.io.File;


public class StudentAssignment{
    private ArrayList<ClassInfo> timetable;
    private ArrayList<Student> students = new ArrayList<Student>(Data.studentMap.values());
    
    
    public StudentAssignment(ArrayList<ClassInfo> timetable){
        this.timetable = timetable;
        fillTimetable();
        getStudentTimetableFitness();
        try{
            outputCSV();
        }catch(Exception e){e.printStackTrace();}
    }

    public void outputCSV() throws Exception{
        File studentFile = new File("StudentTimetables.csv");
        PrintWriter output  = new PrintWriter(studentFile);

        for(Student s: Data.studentMap.values()){
            output.print(s.getName() + ",");
            String[] currentTimetable = s.getTimetable();
            for(int i =0; i <currentTimetable.length;i++){
                if(currentTimetable[i] == null){
                    output.print(",");
                }
                output.print(currentTimetable[i]+",");
                
            }
            output.println();
            
        }
        output.close();
    }

    // public void fillTimetable(){
    //     for(ClassInfo c: timetable){
           
    //         for(Student s: Data.studentMap.values()){
    //             if(c.isFull()){
    //                break;
    //             }
               
    //             if(s.hasCourse(c.getCourse()) && s.checkTimeslot(c.getTimeslot())){
               
    //                 c.addStudents(s.getStudentNumber());
    //                 s.fillTimeslot(c.getCourse(), c.getTimeslot());
    //             }
    //         }
    //     }
    // }


    public void fillTimetable(){
        for(Student s: Data.studentMap.values()){
            String[] studentTimetable = s.getTimetable();
                for(int i = 0;i < studentTimetable.length; i++){
                    if(studentTimetable[i] != null){
                        for(ClassInfo c: timetable){
                            if(studentTimetable[i].equals(c.getCourse()) && !c.isFull() && s.checkTimeslot(c.getTimeslot())){
                                c.addStudents(s.getStudentNumber());
                                s.fillTimeslot(c.getCourse(), c.getTimeslot());
                            }
                        }
                    }
                 
                }

        }
    }

    private double getStudentTimetableFitness(){
        int totalCourses = Data.studentMap.size() *11;
        double counter = 0;

        for(Student student: Data.studentMap.values()){
            counter = counter + student.correctCourses();
        }
        
        System.out.println(counter/totalCourses);
        return counter/totalCourses;
    }

}