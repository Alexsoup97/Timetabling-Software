import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;
import java.io.PrintWriter;
import java.io.File;
import java.util.HashSet;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.Arrays;

public class StudentAssignment {
    private Random random = new Random();
    private HashMap<String, HashSet<Integer>> courseToPeriods = new HashMap<String, HashSet<Integer>>();// coures to which period it has that class
    private HashMap<Integer, HashSet<String>> periodToCourses = new HashMap<Integer, HashSet<String>>(); // periods to all courses running in that period
    private ClassInfo[] spares = new ClassInfo[Data.NUM_PERIODS];
    private ClassInfo[] emptyCourse = new ClassInfo[Data.NUM_PERIODS];

    public StudentAssignment() {
        for(int i=0; i<spares.length; i++){
            spares[i] = new ClassInfo("Cafeteria", i, "SPARE", false);
            emptyCourse[i] = new ClassInfo("Cafeteria", i, "EMPTY", false);
        }
    }

    public void getStudentTimetables(ArrayList<ClassInfo> masterTimetable){
        getCoursePeriods();
        getPeriodToCourses(masterTimetable);

        ArrayList<Student> students = new ArrayList<Student>(Data.studentMap.values());
        
        fillStudentTimetables(masterTimetable, students);
        
        int fullTimetables = getNumFullTimetables(students);
        int[] choicesHonored = getStudentChoicesHonored(students);
        System.out.println("Full timetables: " + fullTimetables);
        System.out.println("Top choices fulfilled: " + choicesHonored[0]);
        System.out.println("Alternate choices fulfilled: " + choicesHonored[1]);
        System.out.println("Empty timeslots: " + choicesHonored[2]);
        System.out.println("Of total courses chosen: " + Data.courseCount);

        Data.results = new int[]{fullTimetables, choicesHonored[0], choicesHonored[1], choicesHonored[2]};

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

        HashSet<Integer> allPeriods = new HashSet<Integer>();
        for(int i=0; i<Data.NUM_PERIODS; i++){
            allPeriods.add(i);
        }
        courseToPeriods.put(spares[0].getCourse(), allPeriods);
        courseToPeriods.put(emptyCourse[0].getCourse(), allPeriods);
    }

    public void getPeriodToCourses(ArrayList<ClassInfo> masterTimetable){
        for(int i=0; i<Data.NUM_PERIODS; i++){
            periodToCourses.put(i, new HashSet<String>());
        }
        for(ClassInfo section:masterTimetable){
            periodToCourses.get(section.getTimeslot()).add(section.getCourse());
        }
    }

    /**
     * Determines a fitness score for all student timetables, based on how many
     * students have top choices, alternate choices, or incomplete timetables
     * 
     * @param students ArrayList of all Student objects after timetabling
     * @return
     */
    private int[] getStudentChoicesHonored(ArrayList<Student> students) {
        int[] studentChoicesReceived;
        int correctTopChoices = 0;
        int correctAlternateChoices = 0;
        int missingCourses = 0;
        for (Student student : students) {
            studentChoicesReceived = student.getNumChoicesReceived();
            correctTopChoices += studentChoicesReceived[0];
            correctAlternateChoices += studentChoicesReceived[1];
            missingCourses += studentChoicesReceived[2];
        }
        return new int[]{correctTopChoices, correctAlternateChoices, missingCourses};
    }

    // private int getStudentTimetableFitness(ArrayList<Student> students) {
    //     final int TOP_CHOICE_WEIGHT = 4;
    //     final int ALTERNATE_WEIGHT = 1;
    //     final int EMPTY_WEIGHT = -3;
    //     int[] studentChoicesReceived;
    //     int score = 0;
    //     for (Student student : students) {
    //         studentChoicesReceived = student.getNumChoicesReceived();
    //         score += studentChoicesReceived[0]*TOP_CHOICE_WEIGHT;
    //         score += studentChoicesReceived[1]*ALTERNATE_WEIGHT;
    //         score += studentChoicesReceived[2]*EMPTY_WEIGHT;
    //     }
    //     return score;
    // }

    // private void fillStudentTimetables(ArrayList<ClassInfo> masterTimetable, ArrayList<Student> students) {
    //     ArrayList<ClassInfo> masterTimetableCopy = new ArrayList<ClassInfo>();
    //     for(ClassInfo section:masterTimetable){
    //         masterTimetableCopy.add(new ClassInfo(section));
    //     }
    //     ArrayList<Student> studentsCopy = new ArrayList<Student>();
    //     for(Student student:students){
    //         studentsCopy.add(new Student(student));
    //     }

    //     ArrayList<ClassInfo> bestMasterTimetableCopy;
    //     ArrayList<Student> bestStudentTimetableCopy;

    //     int bestFitness = Integer.MIN_VALUE;
    //     int fitness;

    //     for(int i=0; i<10; i++){
    //         generateFilledStudentTimetable(masterTimetableCopy, studentsCopy);
    //         fitness = getStudentTimetableFitness(studentsCopy);
    //         if(fitness > bestFitness){
    //             bestFitness = fitness;
                
    //         }
    //     }

    //     //  improveStudentTimetables(students,studentsWithIncompleteTimteables, masterTimetable); 
    // }

    /**
     * Timetables students into the master timetable
     * 
     */
    private void fillStudentTimetables(ArrayList<ClassInfo> masterTimetable, ArrayList<Student> students) {
        ArrayList<Student> studentsWithIncompleteTimteables = new ArrayList<Student>();

        LinkedList<String> courseChoices = new LinkedList<String>();
        for (Student student : students) { 
            courseChoices.clear();
            courseChoices.addAll(student.getCourseChoices());
            Collections.sort(courseChoices, new Comparator<String>(){
                public int compare(String c1, String c2){
                    ArrayList<ClassInfo> c1classes = Data.coursesToClassInfo.get(c1);
                    ArrayList<ClassInfo> c2classes = Data.coursesToClassInfo.get(c2);
                    if (c1classes == null) {
                        if (c2classes == null) return 0;
                        return -1;
                    }
                    if (c2classes == null) return 1;
                    return Data.coursesToClassInfo.get(c1).size() - Data.coursesToClassInfo.get(c2).size();
                }
            });
            for(int i=0; i<student.getNumSpares(); i++){
                courseChoices.add("SPARE");
            }
            courseChoices.addAll(student.getAlternateChoices());
            student.setTimetable(backtrackFillStudentTimetables(student.getTimetable(), student, courseChoices, generateRandomNumberSequence(Data.NUM_PERIODS), 0));
            for(ClassInfo course: student.getTimetable()){
                course.addStudent(student.getStudentNumber());
            }
            if(!student.hasFullTimetable())
                studentsWithIncompleteTimteables.add(student);
        }

        //  improveStudentTimetables(students,studentsWithIncompleteTimteables, masterTimetable); 
         //TODO
    }

    private int getNumFullTimetables(ArrayList<Student> students){
        int fullTimetables = 0;
        for (Student s: students){
            if(s.hasFullTimetable()){
                fullTimetables++;
            }
        }
        return fullTimetables;
    }

    /**
     * Backtracking algorithm for filling in a student's timetable based on a master timetable
     * @author Suyu
     * @param studentTimetable
     * @param s
     * @param courseChoices
     * @param periodOrder
     * @param orderIndex
     * @return
     */
    private ClassInfo[] backtrackFillStudentTimetables(ClassInfo[] studentTimetable, Student s, LinkedList<String> courseChoices, int[] periodOrder, int orderIndex){
        if(isStudentTimetableFinished(studentTimetable, s))
            return studentTimetable;
    
        // TODO this crashes if the student takes more than 8 courses
        // make copy of student timetable
        ClassInfo[] st = Arrays.copyOf(studentTimetable, studentTimetable.length);
        int period = periodOrder[orderIndex];
        HashSet<String> availableCourses = periodToCourses.get(period);
        ClassInfo toAdd = null;
        ClassInfo[] recurse;

        // repeats through courseChoices list, which first lists top choices, then the correct # of "spare" courses, then alternates
        for(String course:courseChoices){
            // if it's a spare, that means all of the top choices have already been added or could not be found, so add a spare TODO maybe move down which would prioritize alternates over spares
            if(course.equals("SPARE")){
                toAdd = spares[period];
                courseChoices.remove(course);
            // Otherwise, it's either a top choice or alternate that needs to be added
            }else if(availableCourses.contains(course)){ // if it's available in this period
                for(ClassInfo section:Data.coursesToClassInfo.get(course)){
                    if(section.getTimeslot() == period && !section.isFull()){
                        toAdd = section;
                        courseChoices.remove(course);
                    }
                }
            }  
            // i.e. if a top choice, spare, or alternate could be scheduled in this period, add it and look for the next one
            if(toAdd != null){
                st[period] = toAdd;
                recurse = backtrackFillStudentTimetables(st, s, courseChoices, periodOrder, orderIndex+1);
                if(recurse!= null){ // If this recursive call yields a complete timetable, this works
                    return recurse; 
                }
            }                      
        }
        st[period] = emptyCourse[period]; //TODO maybe random course that is available
        recurse = backtrackFillStudentTimetables(st, s, courseChoices, periodOrder, orderIndex+1);
        if(recurse!= null){
            return recurse;
        }

        return null;
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
     * TODO
     * @param students
     * @param studentsWithIncompleteTimteables
     */
    private void improveStudentTimetables(ArrayList<Student> students, ArrayList<Student> studentsWithIncompleteTimteables, ArrayList<ClassInfo> masterTimetable){
        final int NUM_ITERATIONS = 1;
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            // if (random.nextInt(10)<7){
                System.out.println("Start");
                long time = System.nanoTime();
                moveAround(masterTimetable);
                System.out.println("time "+(System.nanoTime()-time));

            // }else{
            //     swapPeriods(students);
            // }
            // LinkedList<String> courseChoices = new LinkedList<String>();
            // for (Student student : studentsWithIncompleteTimteables) { 
            //     courseChoices.clear();
            //     courseChoices.addAll(student.incompleteCourseChoices());
            //     student.setTimetable(backtrackFillStudentTimetables(student.getTimetable(), student, courseChoices, student.emptyPeriods(), 0));
            //     for(ClassInfo course: student.getTimetable()){
            //         course.addStudent(student.getStudentNumber());
            //     }
            // }
        }        
    }   

  

    //if class is full, move people around
    private void moveAround(ArrayList<ClassInfo> masterTimetable) {
        // sort in order of fullest to least full classes
        Collections.sort(masterTimetable, new Comparator<ClassInfo>(){
            public int compare(ClassInfo c1, ClassInfo c2){
                return c2.getPercentageFull() - c1.getPercentageFull();    
            }
        });
        int index = 0;
        while(masterTimetable.get(index).getPercentageFull()>90){
            swapStudents(masterTimetable.get(index));
            index++;
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
        int x = -1;
        boolean done1 = false;
        while (x < Data.coursesToClassInfo.get(c1.getCourse()).size() && !done1) {
            x++;
            // if there is a course c1 at the c2 timeslot, and if the course x at the y timeslot is not full
            if (c2.getTimeslot() == Data.coursesToClassInfo.get(c1.getCourse()).get(x).getTimeslot()&& !Data.coursesToClassInfo.get(c1.getCourse()).get(x).isFull()) {
                done1 = true;
            }
        }
        int y = -1;
        boolean done2 = false;
        while (y < Data.coursesToClassInfo.get(c2.getCourse()).size() &&!done2) {
            y++;
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
       // Collections.shuffle(studentTimetable);
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