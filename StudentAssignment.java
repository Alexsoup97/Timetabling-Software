import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.Arrays;

/**
 * [StudentAssignment.java]
 * Class for filling students into the master timetable
 * @author Alex, Samson, Suyu
 * @version 1.0
 */
public class StudentAssignment {
    private final String EMPTY = "EMPTY";
    private final String SPARE = "SPARE";
    private Random random = new Random();
    private HashMap<String, HashSet<Integer>> courseToPeriods = new HashMap<String, HashSet<Integer>>();// coures to which period it has that class
    private HashMap<Integer, HashSet<String>> periodToCourses = new HashMap<Integer, HashSet<String>>(); // periods to all courses running in that period
    private ClassInfo[] spares = new ClassInfo[Data.NUM_PERIODS];
    private ClassInfo[] emptyCourse = new ClassInfo[Data.NUM_PERIODS];

    /**
     * Creates a new StudentAssignment object and initializes variables
     * @param masterTimetable the master timetable to fill
     */
    public StudentAssignment(ArrayList<ClassInfo> masterTimetable) {
        for(int i=0; i<spares.length; i++){
            spares[i] = new ClassInfo("Cafeteria", i, "SPARE", false);
            emptyCourse[i] = new ClassInfo("Cafeteria", i, "EMPTY", false);
        }
        getCoursePeriods();
        getPeriodToCourses(masterTimetable);
    }

    /**
     * Uses a master timetable to fill in all student timetables and outputs to a CSV file
     * @param masterTimetable the master timetable
     */
    public void getStudentTimetables(ArrayList<ClassInfo> masterTimetable){
        ArrayList<Student> students = new ArrayList<Student>(Data.studentMap.values());
        ArrayList<Student> studentsWithIncompleteTimetables;

        System.out.println("Starting student timetabling");

        final double TARGET_CHOICES_HONORED = .86;
        do{
            for(Student student:students)
                student.clearTimetable();
            for(ClassInfo ci:masterTimetable)
                ci.getStudents().clear();
            studentsWithIncompleteTimetables = fillStudentTimetables(masterTimetable, students);
        }while(getStudentChoicesHonored(students)[0]*1.0/Data.courseCount < TARGET_CHOICES_HONORED);
             
        improveStudentTimetables(students, studentsWithIncompleteTimetables, masterTimetable); 
         //TODO
        
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

    /**
     * Outputs all student timetables to a CSV file called "StudentTimetables.csv"
     * @throws FileNotFoundException file errors
     */
    private void outputCSV() throws FileNotFoundException {
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

    /**
     * Initializes the class variable courseToPeriods that maps courses to all
     * periods they are running. The map includes EMPTY and SPARE courses
     */
    private void getCoursePeriods() {
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

    /**
     * Initializes the class variable periodToCourses that maps periods to all
     * classes running in that period.
     * 
     * @param masterTimetable the master timetable
     */
    public void getPeriodToCourses(ArrayList<ClassInfo> masterTimetable){
        for(int i=0; i<Data.NUM_PERIODS; i++){
            periodToCourses.put(i, new HashSet<String>());
        }
        for(ClassInfo section:masterTimetable){
            periodToCourses.get(section.getTimeslot()).add(section.getCourse());
        }
    }

    /**
     * Timetables students into the master timetable
     * 
     */
    private ArrayList<Student> fillStudentTimetables(ArrayList<ClassInfo> masterTimetable, ArrayList<Student> students) {
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

        return studentsWithIncompleteTimteables;
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
     * Gets how many students have top choices, alternate choices, or incomplete
     * timetables
     * 
     * @author Suyu
     * @param students ArrayList of all Student objects, with filled timetables
     * @return length 3 integer array of total honored top choices, total honoured
     *         alternate choices, and total empty student timeslots
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

    /**
     * TODO
     * @param students
     * @param studentsWithIncompleteTimteables
     */
    private void improveStudentTimetables(ArrayList<Student> students, ArrayList<Student> studentsWithIncompleteTimteables, ArrayList<ClassInfo> masterTimetable){
        final int NUM_ITERATIONS = 50;
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            if (random.nextInt(10)<5){
                moveAround(masterTimetable);
            }else{
                swapPeriods(students);
            }

            System.out.print(getNumFullTimetables(students) + "  ");
            System.out.println(Arrays.toString(getStudentChoicesHonored(students)));
            
            for (Student s : studentsWithIncompleteTimteables) {
                for (String choice : s.getUnfulfilledCourseChoicesAlternates()) {
                    for (int period : s.emptyPeriods()) {
                        if(periodToCourses.get(period).contains(choice)){
                            for(ClassInfo section:Data.coursesToClassInfo.get(choice)){
                                if(section.getTimeslot() == period && !section.isFull()){
                                    s.addToTimetable(section);
                                    section.addStudent(s.getStudentNumber());
                                }
                            }
                        }
                    }
                }
            }
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
        while(masterTimetable.get(index).getPercentageFull()>70){
            swapStudents(masterTimetable.get(index));
            index++;
        }
    }


    //move students in full classes
    private void swapStudents(ClassInfo fullClass){
        int i=0;
        int studentNumber;
        //loop through all studnets in class
        while(i<fullClass.getStudents().size()){
            studentNumber=fullClass.getStudents().get(i);
            //loop timetable for student
            ClassInfo [] studentTimeTable=Data.studentMap.get(studentNumber).getTimetable();
            for (int j= 0; j < studentTimeTable.length; j++){
                if (!studentTimeTable[j].getCourse().equals(EMPTY) && !studentTimeTable[j].getCourse().equals(SPARE) && fullClass.getTimeslot() != j && courseToPeriods.get(studentTimeTable[j].getCourse()).contains(fullClass.getTimeslot()) && courseToPeriods.get(fullClass.getCourse()).contains(studentTimeTable[j].getTimeslot()) ){
                    swap(Data.studentMap.get(studentNumber), fullClass,studentTimeTable[j]);
                }
            }
            i++;
        } 
    }
    
    private void swap(Student c, ClassInfo c1, ClassInfo c2) {
        int x = 0;
        boolean done1 = false;
        while (x < Data.coursesToClassInfo.get(c1.getCourse()).size() && !done1) {
            // if there is a course c1 at the c2 timeslot, and if the course x at the y timeslot is not full
            if (c2.getTimeslot() == Data.coursesToClassInfo.get(c1.getCourse()).get(x).getTimeslot()&& !Data.coursesToClassInfo.get(c1.getCourse()).get(x).isFull()) {
                done1 = true;
            }else{
                x++;
            }
        }
        int y = 0;
        boolean done2 = false;        
        while (y < Data.coursesToClassInfo.get(c2.getCourse()).size() && !done2) {
            if (c1.getTimeslot() == Data.coursesToClassInfo.get(c2.getCourse()).get(y).getTimeslot()&&!Data.coursesToClassInfo.get(c2.getCourse()).get(y).isFull()) {
                done2 = true;
            }else{
                y++;
            }
        }
        if (done1 && done2) {
            c1.removeStudent(c.getStudentNumber());
            c2.removeStudent(c.getStudentNumber());
            //add student to new class
            Data.coursesToClassInfo.get(c1.getCourse()).get(x).addStudent(c.getStudentNumber());
            Data.coursesToClassInfo.get(c2.getCourse()).get(y).addStudent(c.getStudentNumber());
            c.swapTimeTable(Data.coursesToClassInfo.get(c1.getCourse()).get(x),Data.coursesToClassInfo.get(c1.getCourse()).get(x).getTimeslot(),Data.coursesToClassInfo.get(c2.getCourse()).get(y),Data.coursesToClassInfo.get(c1.getCourse()).get(x).getTimeslot());
            return;
        }
        return ;
    }

    //move periods that studnets have around
    private void swapPeriods(ArrayList<Student> studentTimetable){
        Collections.shuffle(studentTimetable);
        for (Student c: studentTimetable){
            ClassInfo [] timetable=c.getTimetable();
            for (int i=0; i<timetable.length;i++){
                if ( !timetable[i].getCourse().equals(EMPTY) && !timetable[i].getCourse().equals(SPARE)&&Data.coursesToClassInfo.get(timetable[i].getCourse()).size() != 1 ) {
                    for (int j=i+1;j<timetable.length;j++){
                        if ( !timetable[j].getCourse().equals(EMPTY) && !timetable[j].getCourse().equals(SPARE) && Data.coursesToClassInfo.get(timetable[j].getCourse()).size() != 1 ){
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