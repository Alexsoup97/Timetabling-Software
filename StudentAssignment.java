import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;
import java.io.PrintWriter;
import java.io.File;
import java.util.TreeMap;
import java.util.LinkedHashSet;
import java.util.HashSet;
public class StudentAssignment{
    private Random random = new Random();
    private ArrayList<ClassInfo> timetable;
    static int completeTimetableCount = 0; 
    
    
    public StudentAssignment(ArrayList<ClassInfo> timetable){
        this.timetable = timetable;
        System.out.println("COMPLETE TIMETABLES:" + fillTimetable());
        
        //getStudentTimetableFitness(new ArrayList<Student>(Data.studentMap.values()));
       
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

    // start at period 0
    // find course that the student took that is available in period 0
    // if none available, check alternates
    // if none available, leave empty?

    public int fillTimetable(){
        int counter = 0;
        // String[] test  = {"ENG4U1", null, "EMPTY", null, null, null, null, null,null};
        // setUp(Data.studentMap.get(469091303).getCourseChoices());
        // findValidTimetable(new String[9], 0,null);
        for(Student s: Data.studentMap.values()){
           ArrayList<String> studentChoices= s.getCourseChoices();
            String[] studentChoicesArray = new String[9]; 
             String [] temp = {null};
             

            for(int i =0; i < studentChoicesArray.length; i++){
                if(i < studentChoices.size()){
                    studentChoicesArray[i] = studentChoices.get(i);
                }else{
                    studentChoicesArray[i] = "EMPTY";
                }
            }
            
            setUp(studentChoices);

            String[] newTimetable = new String[9];
            String[] currentTimetable = findValidTimetable(newTimetable,-1, null);
            
          
            
         
            if(currentTimetable != null){
                counter++;
                for(int i = 0; i < currentTimetable.length; i++){
                    if(currentTimetable[i] == null){
                        break;
                    }
                    if(!currentTimetable[i].equals("EMPTY")){
                        for(ClassInfo c: Data.coursesToClassInfo.get(currentTimetable[i])){
                            if(c.getTimeslot() == i){
                                c.addStudents(s.getStudentNumber());
                                break;
                            }
                        }
                    }
                }
                s.setTimetable(currentTimetable);     
            }else{
                s.setTimetable(temp);
            }
          
    
        }
        return counter;
    }

    static HashMap<String, HashSet<Integer>> coursetoPossibleTimeslots = new HashMap<String,HashSet<Integer>>();
    static String[] order;
    static String[] bestTimetable;
    static ArrayList<String> selectedCourses;
    static int bestScore;

    public void setUp(ArrayList<String> choices){
        order = new String[choices.size()];
        bestScore = 0;
        bestTimetable = null;
        selectedCourses  = choices;

        for(int i = 0; i < order.length;i++){
            order[i] = choices.get(i);
            System.out.print(order[i] + " ");

        }
        System.out.println();
        for(String s: choices){
            HashSet<Integer> temp = new HashSet<Integer>();
            for( ClassInfo i:Data.coursesToClassInfo.get(s)){
                if(!i.isFull()){
                    temp.add(i.getTimeslot());
                }
                
            }
            coursetoPossibleTimeslots.put(s, temp);
        }
    
    }

    public String[] findValidTimetable(String[] choices, int timeslot, String course){
        if(timeslot != -1){
            choices[timeslot] = course;
        }

        for(int i = 0; i < choices.length;i++){
            System.out.print(choices[i] + " ");
        }
        System.out.println();
        if(getScore(choices) > bestScore){
            bestScore = getScore(choices);
            bestTimetable = new String[9];
            for(int i = 0 ; i < choices.length;i++){
                bestTimetable[i] = choices[i];
            }
           
           
        }

        if(getScore(choices) == choices.length){   
            
            bestTimetable = new String[9];
            for(int i = 0 ; i < choices.length;i++){
                bestTimetable[i] = choices[i];
              
            }
        }

        for(int i =0 ; i < order.length; i++){
            for(int j = timeslot; j < choices.length; j++){
                if( j+ 1 < choices.length &&noConflicts(j +1, order[i], choices )){
                    return findValidTimetable(choices, j +1, order[i]);
                }
              
            }
           
        }
          if(timeslot +1 < choices.length){
                    return findValidTimetable(choices, timeslot+1, "EMPTY");
         }
        
      
      
        return bestTimetable;
    }

    public boolean noConflicts( int timeslot, String course, String[] choices){
        
        if(coursetoPossibleTimeslots.get(course).contains(timeslot) ){
            for(int i = 0; i < choices.length;i++){
                if(course.equals(choices[i])){
                    return false;
                }
            }
            return true;
        }else{
            return false;
        }
    }

 

    public int getScore(String[] choices){
        int counter = 0;
        int emptyCounter =0;
        for(int i =0; i < selectedCourses.size(); i++){
            int courseCount = 0;
            for(int j = 0; j < choices.length; j++){
                
                if(choices[j] != null &&selectedCourses.get(i).equals(choices[j])){
                    courseCount++;
                }
            }
            if(courseCount ==1){
                counter++;
            }
          
        }

        for(int i = 0; i < choices.length;i++){
            if(choices[i] != null &&choices[i].equals("EMPTY")){
                emptyCounter++;
            }
        }

        
        if(choices.length - selectedCourses.size() >= emptyCounter){
            return counter + emptyCounter;
        }
        return counter;

    }

    // public static boolean isValid(String[] choices, Integer student){
    //     int timeslot = 0;
    //     int counter = 0;
    //     for(String c: choices){
    //         ArrayList<ClassInfo> currentCourse = Data.coursesToClassInfo.get(c);
           
    //         if(c.equals("EMPTY")){
    //             timeslot++;
    //             counter++;
    //             break;
    //         }
    //         for(ClassInfo classes: currentCourse){
              
    //             if(classes.getTimeslot() == timeslot && !classes.isFull()){  
    //                 counter++;
                    
    //                 break;
    //             }
    //         }
    //         timeslot++;
    //    }
       
    //    if(counter == choices.length){
    //        return true;
    //    }else{
    //        return false;
    //    }
       
    // }

    // static String[] bestTimetable;
    // public static String[]  findValidTimetable(String[] courses, int size, int n,Integer student){
       
    //     if (size == 1 ){
    //         if(isValid(courses, student)){
    //             bestTimetable = new String[9];
                
    //             for(int i = 0 ; i < courses.length;i++){
    //                 bestTimetable[i] = courses[i];
    //             }
    //         }
    //     }
       
    //         for (int i = 0; i < size; i++) {
    //             findValidTimetable(courses, size - 1, n, student);
     
    //             // if size is odd, swap 0th i.e (first) and
    //             // (size-1)th i.e (last) element
    //             if (size % 2 == 1) {
    //                 String temp = courses[0];
    //                 courses[0] = courses[courses.length -1];
    //                 courses[courses.length -1] = temp;
    //             }
     
    //             // If size is even, swap ith
    //             // and (size-1)th i.e last element
    //             else {
    //                 String temp = courses[i];
    //                 courses[i] = courses[courses.length -1];
    //                 courses[courses.length -1] = temp;
    //             }
    //         }
        
        
    //     return bestTimetable;
    // }

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