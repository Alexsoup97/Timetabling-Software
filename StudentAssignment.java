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
    private ClassInfo[] spareClasses = new ClassInfo[Data.NUM_PERIODS];
    private ClassInfo[] emptyClasses = new ClassInfo[Data.NUM_PERIODS];

    /**
     * Creates a new StudentAssignment object and initializes variables
     * @param masterTimetable the master timetable to fill
     */
    public StudentAssignment(ArrayList<ClassInfo> masterTimetable) {
        for(int i=0; i<spareClasses.length; i++){
            spareClasses[i] = new ClassInfo("Cafeteria", i, SPARE, false);
            emptyClasses[i] = new ClassInfo("Cafeteria", i,EMPTY , false);
        }
        getCoursePeriods();
        getPeriodToCourses(masterTimetable);
        Data.coursesToClassInfo.put(EMPTY, new ArrayList<ClassInfo>(Arrays.asList(emptyClasses)));
        Data.coursesToClassInfo.put(SPARE, new ArrayList<ClassInfo>(Arrays.asList(spareClasses)));

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
             
        System.out.println(getNumFullTimetables(students));
        
        improveStudentTimetables(students, studentsWithIncompleteTimetables, masterTimetable); 
        
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
        courseToPeriods.put(spareClasses[0].getCourse(), allPeriods);
        courseToPeriods.put(emptyClasses[0].getCourse(), allPeriods);
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
     * Fills all student timetables
     * @author Suyu
     * @param masterTimetable master timetable of all classes running
     * @param students ArrayList of all students with unfilled timetables
     * @return ArrayList of all students with timetables filled
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
                courseChoices.add(SPARE);
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
     * Backtracking recursive algorithm for filling in a student's timetable based on a master timetable
     * @author Suyu
     * @param studentTimetable  Array of ClassInfo objects representing the classes in the student's timetable
     * @param s                 the student
     * @param courseChoices     a LinkedList<String> containing the student's top course choices sorted in order of 
     *                          the number of sections of that course running, then the appropriate number
     *                          of 'spare' courses, then the student's alternate choices
     * @param periodOrder       an integer array of the order to fill periods. Must contain all possible periods only 
     *                          once, ideally in random order
     * @param orderIndex        Set to 0 initially, used during recursion. Used to track the index in periodOrder
     * @return A ClassInfo array containing the student's filled timetable
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
            if(course.equals(SPARE)){
                toAdd = spareClasses[period];
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
        st[period] = emptyClasses[period]; //TODO maybe random course that is available
        recurse = backtrackFillStudentTimetables(st, s, courseChoices, periodOrder, orderIndex+1);
        if(recurse!= null){
            return recurse;
        }

        return null;
    }

    /**
     * Generates array containing each of the numbers smaller than the number
     * specified once, in random order
     * @param size the size of array to generate
     * @return the generated array
     */
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
    
    /**
     * Helper method for backtracking algorithm
     * Determines if a student's timetable has been finished for the purposes of the
     * algorithm (either filled with course choices, spares, alternates, or EMPTY
     * placeholder courses)
     * 
     * @param studentTimetable the student timetable
     * @param s                the student
     * @return if the student's timetable is completed for the purposes of the backtracking algorithm
     */
    private boolean isStudentTimetableFinished(ClassInfo[] studentTimetable, Student s){
        int courseCounter = 0;
        int spareCounter = 0;
        for(int i=0; i<studentTimetable.length; i++){
            if(studentTimetable[i] != null)
                if(studentTimetable[i].getCourse() == SPARE)
                    spareCounter ++;
                else
                    courseCounter++;
        }
        return courseCounter == s.getNumCourseChoices() && spareCounter == s.getNumSpares();
    }

    /**
     * Gets the number of students with full timetables (no empty timeslots)
     * @param students ArrayList of all students
     * @return the number of students with full timetables
     */
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
     * Uses an iterative algorithm to improve student timetables (give more top choices)
     * @author Suyu, Samson
     * @param students Arraylist of all students with timetables already generated
     * @param studentsWithIncompleteTimteables Arraylist of all students with incomplete timetables
     * @param masterTimetable the master timetable of all classes running
     */
    private void improveStudentTimetables(ArrayList<Student> students, ArrayList<Student> studentsWithIncompleteTimteables, ArrayList<ClassInfo> masterTimetable){
        final int NUM_ITERATIONS = 200;
        for (int i = 0; i < NUM_ITERATIONS; i++) {           
            if (random.nextInt(10)<5){
                moveAround(masterTimetable);
            }else{
                swapPeriods(students);
            }
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

    /**
     * Looks at all the classes that are 70 percent full, and swaps the students to free up space
     * @author Samson
     * @param masterTimetable the master timetable of all classes
     */
    private void moveAround(ArrayList<ClassInfo> masterTimetable) {
        final int PERCENTAGE_ACCEPTED= 70;
        Collections.sort(masterTimetable, new Comparator<ClassInfo>(){
            public int compare(ClassInfo c1, ClassInfo c2){
                return c2.getPercentageFull() - c1.getPercentageFull();    
            }
        });
        int index = 0;
        while(masterTimetable.get(index).getPercentageFull()> PERCENTAGE_ACCEPTED){
            swapStudents(masterTimetable.get(index));
            index++;
        }
    }

    /**
     * Takes the class and looks through all the students and swap their class
     * @author Samson
     * @param fullClass class that is full
     */
    private void swapStudents(ClassInfo fullClass){
        int i=0;
        int studentNumber;
        while(i<fullClass.getStudents().size()){
            studentNumber=fullClass.getStudents().get(i);
            ClassInfo [] studentTimeTable=Data.studentMap.get(studentNumber).getTimetable();
            for (int j= 0; j < studentTimeTable.length; j++){
                if (fullClass.getTimeslot() != j && courseToPeriods.get(studentTimeTable[j].getCourse()).contains(fullClass.getTimeslot()) && courseToPeriods.get(fullClass.getCourse()).contains(studentTimeTable[j].getTimeslot()) ){
                    swap(Data.studentMap.get(studentNumber), fullClass,studentTimeTable[j]);
                }
            }
            i++;
        } 
    }

    /**
     * takes the student, and two courses, and see if there classes timeslot can be swapped
     * @author Samson
     * @param student the student
     * @param c1 course 1
     * @param c2 course 2
     */
    private void swap(Student student, ClassInfo c1, ClassInfo c2) {
        int index1 = 0;
        boolean c1Avaliable = false;
        int index2 = 0;
        boolean c2Avaliable = false; 
        while (index1 < Data.coursesToClassInfo.get(c1.getCourse()).size() && !c1Avaliable) {
            if (c2.getTimeslot() == Data.coursesToClassInfo.get(c1.getCourse()).get(index1).getTimeslot() && !Data.coursesToClassInfo.get(c1.getCourse()).get(index1).isFull()) {
                c1Avaliable = true;
            }else{
                index1++;
            }
        }       
        while (index2 < Data.coursesToClassInfo.get(c2.getCourse()).size() && !c2Avaliable) {
            if (c1.getTimeslot() == Data.coursesToClassInfo.get(c2.getCourse()).get(index2).getTimeslot()&&!Data.coursesToClassInfo.get(c2.getCourse()).get(index2).isFull()) {
                c2Avaliable = true;
            }else{
                index2++;
            }
        }
        if (c1Avaliable && c2Avaliable) {
            c1.removeStudent(student.getStudentNumber());
            c2.removeStudent(student.getStudentNumber());
            Data.coursesToClassInfo.get(c1.getCourse()).get(index1).addStudent(student.getStudentNumber());
            Data.coursesToClassInfo.get(c2.getCourse()).get(index2).addStudent(student.getStudentNumber());
            student.swapTimeTable(Data.coursesToClassInfo.get(c1.getCourse()).get(index1),Data.coursesToClassInfo.get(c1.getCourse()).get(index1).getTimeslot(),Data.coursesToClassInfo.get(c2.getCourse()).get(index2),Data.coursesToClassInfo.get(c2.getCourse()).get(index2).getTimeslot());
        }
        return;
    }

    /**
     * looks at all the students, and see if they can swap there classes around to free up space
     * @author Samson
     * @param studentTimetable all students timetable
     */
    private void swapPeriods(ArrayList<Student> studentTimetable){
        Collections.shuffle(studentTimetable);
        for (Student c: studentTimetable){
            ClassInfo [] timetable=c.getTimetable();
            for (int i=0; i<timetable.length;i++){
                if (Data.coursesToClassInfo.get(timetable[i].getCourse()).size() != 1 ) {
                    for (int j=i+1;j<timetable.length;j++){
                        if (Data.coursesToClassInfo.get(timetable[j].getCourse()).size() != 1 ){
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