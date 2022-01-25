import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;
import java.io.PrintWriter;
import java.io.File;
import java.util.TreeMap;
import java.util.LinkedHashSet;
import java.util.HashSet;
import java.util.*;

public class StudentAssignment {
    private Random random = new Random(0);
    private HashMap<String, HashSet<Integer>> courseToPeriods = new HashMap<String, HashSet<Integer>>();// coures to which period it has that class
    private HashMap<Integer, HashSet<String>> periodToCourses = new HashMap<Integer, HashSet<String>>(); // periods to all courses running in that period
    private ClassInfo[] spares = new ClassInfo[Data.NUM_PERIODS];
    private ClassInfo[] emptyCourse = new ClassInfo[Data.NUM_PERIODS];
    private ArrayList<ClassInfo> masterTimetable;
    static int completeTimetableCount = 0;

    public StudentAssignment(ArrayList<ClassInfo> timetable) {
        for(int i=0; i<spares.length; i++){
            spares[i] = new ClassInfo("Cafeteria", i, "SPARE", false);
            emptyCourse[i] = new ClassInfo("Cafeteria", i, "EMPTY", false);
        }

        this.masterTimetable = timetable;
        getCoursePeriods();
        getPeriodToCoursesAndClasses();
        fillStudents(masterTimetable, new ArrayList<Student>(Data.studentMap.values()));

        try {
            outputCSV();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void outputCSV() throws Exception {
        File studentFile = new File("StudentTimetables.csv");
        PrintWriter output = new PrintWriter(studentFile);

        for (Student s : Data.studentMap.values()) {
            output.print(s.getName() + ",");
            ClassInfo[] currentTimetable = s.getTimetable();
            for (int i = 0; i < currentTimetable.length; i++) {
                if (currentTimetable[i].getCourse() == null) {
                    output.print(",");
                }
                output.print(currentTimetable[i].getCourse() + ",");
            }
            output.println();
        }
        output.close();
    }

    public void getCoursePeriods() {
        for (String s : Data.coursesToClassInfo.keySet()) {
            HashSet<Integer> times = new HashSet<Integer>();
            for (int i = 0; i < Data.coursesToClassInfo.get(s).size(); i++) {
                times.add(Data.coursesToClassInfo.get(s).get(i).getTimeslot());
            }
            courseToPeriods.put(s, times);
        }
    }

    public void getPeriodToCoursesAndClasses(){
        for(int i=0; i<Data.NUM_PERIODS; i++){
            periodToCourses.put(i, new HashSet<String>());
        }
        for(ClassInfo section:masterTimetable){
            periodToCourses.get(section.getTimeslot()).add(section.getCourse());
        }
    }

    public int fillStudents(ArrayList<ClassInfo> masterTimetable, ArrayList<Student> students) {
        int fullTimetables = 0;

        LinkedList<String> courseChoices = new LinkedList<String>();
        for (Student student : students) { 
            courseChoices.clear();
            courseChoices.addAll(student.getCourseChoices()); // sort this from least to most sections
            for(int i=0; i<student.getNumSpares(); i++){
                courseChoices.add("SPARE");
            }
            courseChoices.addAll(student.getAlternateChoices());

            // System.out.println("\n\nStudent");
            // System.out.println("NAME: "+student.getName());
            // System.out.println("studentcoursechoices: "+student.getCourseChoices());
            // System.out.println("Spares: " +student.getNumSpares());
            // System.out.println("alternates: "+ student.getAlternateChoices());
            // System.out.println("compiledcoursechoices" + courseChoices);
            int[] fillOrder = generateRandomNumberSequence(Data.NUM_PERIODS);
            // System.out.println("Fill order "+Arrays.toString(fillOrder));
            // System.out.println();

            student.setTimetable(backtrackFillStudentTimetable(student.getTimetable(), student, courseChoices, fillOrder, 0));
            for(ClassInfo course: student.getTimetable()){
                course.addStudent(student.getStudentNumber());
            }
            if(student.isTimetableComplete()){
                fullTimetables++;
            }
        }


        System.out.println("full timetables: " + fullTimetables);
        System.out.println("Top choices fulfilled" + getStudentTimetableFitness(students));
        return 0;
    }

    private ClassInfo[] backtrackFillStudentTimetable(ClassInfo[] studentTimetable, Student s, LinkedList<String> courseChoices, int[] periodOrder, int orderIndex){
        // System.out.println("_________________");
        // System.out.println(isStudentTimetableFinished(studentTimetable, s));
        if(isStudentTimetableFinished(studentTimetable, s))
            return studentTimetable;
    
        // TODO this crashes if the student takes more than 8 courses
        // make copy of student timetable
        ClassInfo[] st = Arrays.copyOf(studentTimetable, studentTimetable.length);
        int period = periodOrder[orderIndex];
        HashSet<String> availableCourses = periodToCourses.get(period);
        ClassInfo toAdd = null;

        // printTimetable(st);
        // System.out.println("COURSE CHOICES" + courseChoices);
        // System.out.println("OrderIndex: " + orderIndex);
        // System.out.println("Period:" + period);

        // repeats through courseChoices list, which first lists top choices, then the correct # of "spare" courses, then alternates
        for(String course:courseChoices){

            // System.out.println("looking at course: " + course);
            // System.out.println("Available courses: " + availableCourses);
            // System.out.println("course is available in period? " + availableCourses.contains(course));

            // if it's a spare, that means all of the top choices have already been added or could not be found, so add a spare TODO maybe move down which would prioritize alternates over spares
            if(course.equals("SPARE")){
                toAdd = spares[period];
                courseChoices.remove(course);
            // Otherwise, it's either a top choice or alternate that needs to be added
            }else if(availableCourses.contains(course)){ // if it's available in this period
                for(ClassInfo section:Data.coursesToClassInfo.get(course)){
                    if(section.getTimeslot() == period){
                        toAdd = section;
                        courseChoices.remove(course);
                    }
                }
            }  

            // i.e. if a top choice, spare, or alternate could be scheduled in this period, add it and look for the next one
            if(toAdd != null){
                st[period] = toAdd;

                // printTimetable(st);

                ClassInfo[] recurse = backtrackFillStudentTimetable(st, s, courseChoices, periodOrder, orderIndex+1);
                if(recurse!= null){ //TODO maybe don't need to check
                    return recurse;
                }
            }                      
        }

        st[period] = emptyCourse[period]; //TODO maybe random course that is available
        ClassInfo[] recurse = backtrackFillStudentTimetable(st, s, courseChoices, periodOrder, orderIndex+1);
        if(recurse!= null){ //TODO maybe don't need to check
            return recurse;
        }

        return null;
    }

    private void printTimetable(ClassInfo[] st){
        System.out.print("ST   ");
        for(ClassInfo c:st){
            System.out.print(c + " // ");
        }
        System.out.println();
    }

    private int[] generateRandomNumberSequence(int size) {
        int[] order = new int[size];
        for(int i=0; i<size; i++){
            order[i] = i;
        }
        int swap;
        int randomIndex;
        for(int i=0; i<size; i++){
            randomIndex = random.nextInt(size);
            swap = order[i];
            order[i] = order[randomIndex];
            order[randomIndex] = swap;
        }
        return order;
    }
       
    private boolean isStudentTimetableFinished(ClassInfo[] studentTimetable, Student s){
        int courseCounter = 0;
        int spareCounter = 0;
        for(int i=0; i<studentTimetable.length; i++){
            if(studentTimetable[i] != null)
                if(studentTimetable[i].getCourse() == "SPARE")
                    spareCounter ++;
                else
                    courseCounter++;
        }
        return courseCounter == s.getNumCourseChoices() && spareCounter == s.getNumSpares();
    }


    /**
     * Determines a fitness score for all student timetables, based on how many
     * students have top choices, alternate choices, or incomplete timetables
     * 
     * @param students
     * @return
     */
    private int getStudentTimetableFitness(ArrayList<Student> students) {
        final int ALTERNATE_CHOICE_WEIGHT = 0;
        final int TOP_CHOICE_WEIGHT = 1;
        final int INCOMPLETE_TIMETABLE_COURSE_WEIGHT = 0;
        int correctTopChoices = 0;
        int correctAlternateChoices = 0;
        int coursesMissing = 0;
        int[] studentChoicesReceived;

        for (Student student : students) {
            studentChoicesReceived = student.getNumChoicesReceived();
            correctTopChoices += studentChoicesReceived[0];
            correctAlternateChoices += studentChoicesReceived[1];
            coursesMissing += (student.getNumCourseChoices() - studentChoicesReceived[2]);
        }
        return correctTopChoices * TOP_CHOICE_WEIGHT + correctAlternateChoices * ALTERNATE_CHOICE_WEIGHT
                + coursesMissing * INCOMPLETE_TIMETABLE_COURSE_WEIGHT;
    }
    
    //     private ArrayList<Student> improveStudentTimetables(ArrayList<Student> initialStudents) {
    //     final int SURVIVORS_PER_GENERATION = 5;
    //     final int NUM_CHILDREN = 4;
    //     final int NUM_GENERATIONS = 500;

    //     TreeMap<Integer, ArrayList<Student>> timetableCandidates = new TreeMap<Integer, ArrayList<Student>>();
    //     ArrayList<ArrayList<Student>> currentGeneration = new ArrayList<ArrayList<Student>>();
    //     ArrayList<Student> mutatedTimetable;
    //     int mutatedTimetableFitness;
    //     timetableCandidates.put(getStudentTimetableFitness(initialStudents), initialStudents);

    //     for (int i = 0; i < NUM_GENERATIONS; i++) {
    //         currentGeneration.clear();
    //         currentGeneration.addAll(timetableCandidates.values()); // fill current generation of candidates with the survivors from last generation
    //         // timetableCandidates.clear(); //TODO consider - by not including parents in the next generation, might increase mutations/stop algorithm from getting stuck on the same couple ones?
    //         for (ArrayList<Student> candidate : currentGeneration) {
    //             for (int j = 0; j < NUM_CHILDREN; j++) { // make certain number of children of each candidate by mutating it
    //                 mutatedTimetable = mutateStudentTimetable(candidate);
    //                 mutatedTimetableFitness = getStudentTimetableFitness(mutatedTimetable);
    //                 if (timetableCandidates.size() < SURVIVORS_PER_GENERATION) { // if the next generation hasn't been populated yet, just add the child
    //                     timetableCandidates.put(mutatedTimetableFitness, mutatedTimetable);
    //                 } else if (mutatedTimetableFitness > timetableCandidates.firstKey()) { // otherwise, if the new child is better than the worst one, add it and discard the worst one
    //                     timetableCandidates.put(mutatedTimetableFitness, mutatedTimetable);
    //                     timetableCandidates.remove(timetableCandidates.firstKey());
    //                 }
    //             }
    //         }
    //         System.out.println("Student assmgnt Gen " + i + " Fitness " + timetableCandidates.lastEntry());
    //     }
    //     return timetableCandidates.lastEntry().getValue();
    // }

    // private ArrayList<Student> mutateStudentTimetable(ArrayList<Student> studentTimetable) {
    //     ArrayList<Student> mutated = new ArrayList<Student>(studentTimetable);
    //     int mutationTypeSelect = random.nextInt(100);
    //     if (mutationTypeSelect < 50) {
    //     } else {
    //         swapPeriods(mutated);
    //     }
    //     return mutated;
    // }

//if class is full, move people around
    private void moveAround() {
        for (String c: Data.coursesToClassInfo.keySet()){
            for(int i=0; i< Data.coursesToClassInfo.get(c).size();i++){
                if(Data.coursesToClassInfo.get(c).get(i).isFull()){
                    swapStudents(Data.coursesToClassInfo.get(c).get(i));
                }
            }
        }
    }

    //move students in full classes
    private void swapStudents(ClassInfo fullClass){
        //loop through all studnets in class
        for (int i =0; i<fullClass.getStudents().size();i++){
            //loop timetable for student
            ClassInfo [] studentTimeTable=Data.studentMap.get(fullClass.getStudent(i)).getTimetable();
            for (int j= 0; j < studentTimeTable.length; j++){
                if (fullClass.getTimeslot() != j && courseToPeriods.get(studentTimeTable[j].getCourse()).contains(fullClass.getTimeslot()) && courseToPeriods.get(fullClass.getCourse()).contains(studentTimeTable[j].getTimeslot()) ){
                    swap(Data.studentMap.get(fullClass.getStudent(i)), fullClass,studentTimeTable[j]);
                }
            }
        } 
    }
    
    private void swap(Student c, ClassInfo c1, ClassInfo c2) {
        int x = 0;
        boolean done1 = false;
        while (x < Data.coursesToClassInfo.get(c1.getCourse()).size() && !done1) {
            // if there is a course c1 at the c2 timeslot, and if the course x at the y timeslot is not full
            if (c2.getTimeslot() == Data.coursesToClassInfo.get(c1.getCourse()).get(x).getTimeslot()&& !Data.coursesToClassInfo.get(c1.getCourse()).get(x).isFull()) {
                done1 = true;
            }
            x++;
        }
        int y = 0;
        boolean done2 = false;
        while (x < Data.coursesToClassInfo.get(c2.getCourse()).size() &&!done2) {
            if (c1.getTimeslot() == Data.coursesToClassInfo.get(c2.getCourse()).get(y).getTimeslot()&&!Data.coursesToClassInfo.get(c2.getCourse()).get(y).isFull()) {
                done2 = true;
            }
        }
        if (done1 && done2) {
            c1.removeStudent(c.getStudentNumber());
            c2.removeStudent(c.getStudentNumber());
            //add student to new class
            Data.coursesToClassInfo.get(c1.getCourse()).get(x).addStudent(c.getStudentNumber());
            Data.coursesToClassInfo.get(c2.getCourse()).get(y).addStudent(c.getStudentNumber());
            c.swapTimeTable(Data.coursesToClassInfo.get(c1.getCourse()).get(x),x,Data.coursesToClassInfo.get(c2.getCourse()).get(y),y);
            return;
        }
    }

    //move periods that studnets have around
    private void swapPeriods(ArrayList<Student> studentTimetable){
        Collections.shuffle(studentTimetable);
        for (Student c: studentTimetable){
            ClassInfo [] timetable=c.getTimetable();
            for (int i=0; i<timetable.length;i++){
                if (Data.coursesToClassInfo.get(timetable[i].getCourse()).size() != 1) {
                    for (int j=i+1;j<timetable.length;j++){
                        if (Data.coursesToClassInfo.get(timetable[j].getCourse()).size() != 1){
                            if (courseToPeriods.get(timetable[i].getCourse()).contains(timetable[j].getTimeslot()) && courseToPeriods.get(timetable[j].getCourse()).contains(timetable[i].getTimeslot())){
                                swap(c,timetable[i],timetable[j]);
            
                            }
                        }
                    }
                }
            }
        }
    }
}