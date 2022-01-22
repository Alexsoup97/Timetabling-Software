import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;
import java.io.PrintWriter;
import java.io.File;
import java.util.TreeMap;

public class StudentAssignment{
    private Random random = new Random();
    private ArrayList<ClassInfo> timetable;
    static int completeTimetableCount = 0; 
    
    
    public StudentAssignment(ArrayList<ClassInfo> timetable){
        this.timetable = timetable;
        fillTimetable();
        getStudentTimetableFitness(new ArrayList<Student>(Data.studentMap.values()));
        System.out.println(completeTimetableCount);
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

    // public void fillTimetable(){
    //     for(Student s: Data.studentMap.values()){
    //         ArrayList<String> studentTimetable = s.getCourseChoices();
    //         int counter = 0;
    //             for(int i = 0;i < studentTimetable.size(); i++){
    //                 if(!studentTimetable.get(i).equals("")){
    //                     for(ClassInfo c: timetable){
                            
    //                         if(studentTimetable.get(i).equals(c.getCourse()) && !c.isFull() && s.checkTimeslot(c.getTimeslot())){
                               
    //                             c.addStudents(s.getStudentNumber());
    //                             s.fillTimeslot(c.getCourse(), c.getTimeslot());
    //                             s.hasCourse(c.getCourse());
    //                             counter++;
    //                             break;
    //                         }
    //                     }
    //                 }else{
    //                     counter++;
    //                 }
    //             }
    //             if(counter == studentTimetable.size()){
    //                 completeTimetableCount++; 
    //             }
    //     }
    // }

    public void fillTimetable(){
        for(Student s: Data.studentMap.values()){
            ArrayList<String> studentTimetable = s.getCourseChoices();
            for()
        }
    }

    private double getStudentTimetableFitness(ArrayList<Student> students){
        
        double counter = 0;

        for(Student student: students){
            counter = counter + student.correctCourses();
        }
        
        System.out.println(counter/Data.courseCount);
        return counter/Data.courseCount;
    }

    private ArrayList<Student> evolveStudentTimetables(ArrayList<Student> initialStudents) {        
        final int SURVIVORS_PER_GENERATION = 5;
        final int NUM_CHILDREN = 4;

        TreeMap<Double, ArrayList<Student>> timetableCandidates = new TreeMap<Double, ArrayList<Student>>();
        ArrayList<ArrayList<Student>> currentGeneration = new ArrayList<ArrayList<Student>>();
        ArrayList<Student> mutatedTimetable;
        double mutatedTimetableFitness;
        int generationCount = 0;
        timetableCandidates.put(getStudentTimetableFitness(initialStudents), initialStudents);

        while(timetableCandidates.firstKey() > 0){  // keep repeating mutation + checking fitness until a solution is found
            currentGeneration.clear();
            currentGeneration.addAll(timetableCandidates.values());  // fill current generation of candidates with the survivors from last generation
            // timetableCandidates.clear(); //TODO consider - by not including parents in the next generation, might increase mutations/stop algorithm from getting stuck on the same couple ones?
            for (ArrayList<Student> candidate : currentGeneration){   
                for(int i=0; i<NUM_CHILDREN; i++){  // make certain number of children of each candidate by mutating it
                    mutatedTimetable = mutateStudentTimetable(candidate);
                    mutatedTimetableFitness = getStudentTimetableFitness(mutatedTimetable);
                    if (timetableCandidates.size() < SURVIVORS_PER_GENERATION){ // if the next generation hasn't been populated yet, just add the child
                        timetableCandidates.put(mutatedTimetableFitness, mutatedTimetable);
                    }else if (mutatedTimetableFitness < timetableCandidates.lastKey()){ // otherwise, if the new child is better than the worst one, add it and discard the worst one
                        timetableCandidates.put(mutatedTimetableFitness, mutatedTimetable);
                        timetableCandidates.remove(timetableCandidates.lastKey());
                    }
                }
            }
            generationCount++;
        }
        System.out.println("Student inputting generations: " + generationCount);
        return timetableCandidates.firstEntry().getValue();
    }

    private ArrayList<Student> mutateStudentTimetable(ArrayList<Student> studentTimetable){
        ArrayList<Student> mutated = new ArrayList<Student>(studentTimetable);
        int mutationTypeSelect = random.nextInt(100);
        if (mutationTypeSelect < 50) {
            changeElective(mutated);
        } else {
            swapPeriods(mutated);
        }
        return mutated;
    }

    private void changeElective(ArrayList<Student> studentTimetable){
        //  TODO 
        // pick random student
        // pick one of their random electives (use Data.compulsoryCourses)
        // change it to one of their alternates
    }

    private void swapPeriods(ArrayList<Student> studentTimetable){

    }
}