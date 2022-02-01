import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Collections;
import java.util.Comparator;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Modifies the master timetable
 * @author Alex, Samson, Suyu, Eric
 * @version 1.0
 */
public class CourseScheduler {

    private Random random = new Random();
    private ArrayList<ClassInfo> timetable; // master timetable
    private HashMap<String, Integer> studentCount; // Number of students in each course
    private HashMap<String, Integer> coursesRunning; // Number of sections of each course running
    private ArrayList<HashSet<String>> commonlyTakenTogetherCourses; // based on student course choices (ex. calc and advFunc)
    private HashMap<String, ArrayList<ClassInfo>> coursesToClassInfo; // course

    /**
     * Creates a new CourseScheduler object
     * @param s SpecialCourseScheduler object to schedule special courses
     */
    public CourseScheduler(SpecialCourseScheduler s) {
        studentCount = countStudents();
        coursesRunning = calculateCoursesRunning();
        int a = 0;
        for(int value:coursesRunning.values()){ // how many sections of each course are running
            a+=value;
        }
        System.out.println("running courses " + a);
        this.commonlyTakenTogetherCourses = getCommonlyTakenTogetherCourses();
        timetable = s.getSpecialCourseTimetable(coursesRunning);
    }

    /**
     * Generates a new master timetable allocating all courses into timeslots and rooms
     * @return a new master timetable
     */
    public ArrayList<ClassInfo> getNewTimetable() {        
        timetable = new InitialTimetableGenerator().createInitialTimetable(timetable, coursesRunning); 
        improveTimetable(timetable);
        Collections.sort(timetable, new Comparator<ClassInfo>(){
            public int compare(ClassInfo c1, ClassInfo c2){
                return coursesRunning.get(c1.getCourse()) - coursesRunning.get(c2.getCourse());
            }
        });
        coursesToClassInfo = getCoursesToClassInfos(timetable);
        Data.coursesToClassInfo = coursesToClassInfo;
        try{
            outputCSV();
        }catch(FileNotFoundException e){};
        return timetable;
    }

    /**
     * Outputs the master timetable to a CSV file called "MasterTimetable.csv"
     * @throws FileNotFoundException file errors
     */
    private void outputCSV() throws FileNotFoundException {
        File studentFile = new File("MasterTimetable.csv");
        PrintWriter output = new PrintWriter(studentFile);
        output.println("Course, Room, Semester, Period,");
        for(ClassInfo c:timetable){
            output.print(c.getCourse() + ", ");
            output.print(c.getRoom() + ", ");
            output.print((c.getTimeslot()/(Data.NUM_PERIODS/2)+1) + ", ");
            output.println((c.getTimeslot()%(Data.NUM_PERIODS/2)+1) + ", ");
        }
        output.close();
    }

    /**
     * Counts the number of students in each course
     * @author Alex
     * @return HashMap mapping each course code to the number of students taking that course
     */
    private HashMap<String, Integer> countStudents() {
        HashMap<String, Integer> courseCount = new HashMap<String, Integer>();
        for (Student s : Data.studentMap.values()) {
            ArrayList<String> temp = s.getCourseChoices();
            for (int i = 0; i < temp.size(); i++) {
                if (temp.get(i).equals("")) {
                    continue; 
                } 
                if (courseCount.containsKey(temp.get(i))) {
                    courseCount.put(temp.get(i), courseCount.get(temp.get(i)) + 1);
                } else {
                    courseCount.put(temp.get(i), 1);
                } 
            }
        }
        return courseCount;
    }
    
    /**
     * Determines how many sections of each course to run. Sections are added if the
     * classes will be more than a certain threshold full
     * @author Alex
     * @return a HashMap mapping course codes to the number of sections required
     */
    private HashMap<String, Integer> calculateCoursesRunning() {
        final double THRESHOLD = 0.5; 
        HashMap<String, Integer> courseCount = new HashMap<String, Integer>();
        for (String c : studentCount.keySet()) {
            double maxClassSize;
            if(Data.courseMap.containsKey(c)){
               maxClassSize = Data.courseMap.get(c).getClassSize();
            }else{
                maxClassSize = 30;
            }
           
            int numberCourses = (int) Math.floor(studentCount.get(c) / maxClassSize);
            double additionalCourse = (studentCount.get(c)*1.0 / maxClassSize) - numberCourses*1.0 / 100;
            if (additionalCourse > THRESHOLD) {
                numberCourses++;
            }
            courseCount.put(c, numberCourses);
            Data.coursesToClassInfo.put(c, new ArrayList<ClassInfo>());
        }
        return courseCount;
    }

    /**
     * Generates a HashMap mapping course codes to all classInfo objects representing all sections of that course
     * 
     * @return HashMap mapping each course code to an ArrayList of all ClassInfo objects of sections of that course
     */
    private HashMap<String, ArrayList<ClassInfo>> getCoursesToClassInfos(ArrayList<ClassInfo> masterTimetable){
        HashMap<String, ArrayList<ClassInfo>> courseToClassInfoMap = new HashMap<String, ArrayList<ClassInfo>>();
        ArrayList<ClassInfo> classInfoList;
        for(ClassInfo classInfo:masterTimetable){
            if(!courseToClassInfoMap.containsKey(classInfo.getCourse())){
                classInfoList = new ArrayList<ClassInfo>();
                classInfoList.add(classInfo);
                courseToClassInfoMap.put(classInfo.getCourse(), classInfoList);
            }else{
                courseToClassInfoMap.get(classInfo.getCourse()).add(classInfo);
            }
        }
        return courseToClassInfoMap;
    }

    /**
     * based on student choices, get pairs of courses that are frequently chosen together
     * @author Eric
     * @return pairs of frequently taken together courses
     */
    private ArrayList<HashSet<String>> getCommonlyTakenTogetherCourses(){  
        final int FREQUENCY_THRESHOLD = 10; 
        final int STUDENT_SAMPLE_SIZE = 400; //to reduce run time, by only taking a sample of students

        ArrayList<HashSet<String>> allPairs = new ArrayList<>();
        ArrayList<HashSet<String>> frequentlyTakenTogetherCourses = new ArrayList<>();
        
        int sample = 0;
        HashSet<String> pair;
        for(Student student: Data.studentMap.values()){ // for each student
            int start = 1; 
            for(String choice : student.getCourseChoices()){ // for each of their courses
                for (int i = start; i < student.getCourseChoices().size(); i++) { // create all possible PAIRS between courses
                    pair = new HashSet<String>();
                    pair.add(choice);
                    pair.add(student.getCourseChoices().get(i)); 
                    allPairs.add(pair);
                }
                start += 1;     
            }
            sample ++;
            if(sample > STUDENT_SAMPLE_SIZE){break;} // from a student sample to reduce run time
        }    
        for (HashSet<String> h : allPairs) {
            int counter = 0;
            for (HashSet<String> i : allPairs) {
                if(h.equals(i)){
                    counter ++;
                }
            }
            if(counter >= FREQUENCY_THRESHOLD && !frequentlyTakenTogetherCourses.contains(h)){
                frequentlyTakenTogetherCourses.add(h);
            }
        }
        return frequentlyTakenTogetherCourses;
    }

    /**
     * quantify conflicts between commonly taken together courses
     * @author Eric
     * @param timetable master timetable
     * @return an integer score based on how many commonly taken together courses are on the same period
     */
    private int conflictsBetweenCommonlyTakenTogetherCourses(ArrayList<ClassInfo> timetable){ // AKA CTTC
        int conflictScoreCTTC = 0;
        ArrayList<ClassInfo> periods = new ArrayList<>();
        HashMap<Integer, Integer> counter = new HashMap<>(); // period, # of periods

        for (HashSet<String> h : commonlyTakenTogetherCourses) {
            for (ClassInfo c : timetable) { // for all courses running
                if(h.contains(c.getCourse())){ 
                    periods.add(c);  
                }
            }
            for (ClassInfo c : periods) {
                if(counter.containsKey(c.getTimeslot())){
                    counter.put(c.getTimeslot(), counter.get(c.getTimeslot()) + 1);
                }
                else{
                    counter.put(c.getTimeslot(), 1);
                }
            }
            for (Integer i : counter.keySet()) {
                if(counter.get(i) > 2){
                    conflictScoreCTTC ++;
                }
                if(counter.get(i) < 1){
                    conflictScoreCTTC ++;
                }
            }
            counter.clear();
            periods.clear();
        }
        return conflictScoreCTTC;
    }

    /**
     * improves timetable by swapping around courses on the master timetable
     * @author Eric
     */
    private void improveTimetable(ArrayList<ClassInfo> timetable){
        ArrayList<ClassInfo> sem1UnfixedClasses = new ArrayList<ClassInfo>();
        ArrayList<ClassInfo> sem2UnfixedClasses = new ArrayList<ClassInfo>();
        for (ClassInfo c : timetable) {
            if(!c.isFixed()){
                if (c.getTimeslot() < Data.NUM_PERIODS / 2)
                    sem1UnfixedClasses.add(c);
                else
                    sem2UnfixedClasses.add(c);
            }
        }
        improveSemester(sem1UnfixedClasses); // for each semester, no inter semester operations
        improveSemester(sem2UnfixedClasses);
    }

    /**
     * Constantly checks room availability and period availability and swaps courses to reduce CTTC score
     * @author Eric
     * @return unfixedSemesterTimetable pass from the method above
     */
    private void improveSemester(ArrayList<ClassInfo> unfixedSemesterTimetable){
        final int NUM_ITERATIONS = 100; 

        for(int i=0; i<NUM_ITERATIONS; i++){// note: the two courses are in the same semester
            int class1Index = random.nextInt(unfixedSemesterTimetable.size()); // index to switch
            int class2Index = random.nextInt(unfixedSemesterTimetable.size());
            ClassInfo class1 = unfixedSemesterTimetable.get(class1Index); // for easier access
            ClassInfo class2 = unfixedSemesterTimetable.get(class2Index);

            int CTTCScore = conflictsBetweenCommonlyTakenTogetherCourses(unfixedSemesterTimetable); // conflict score before switching
            // make the switch
            int switch1 = class1.getTimeslot();
            String switch1Room = class1.getRoom();
            int switch2 = class2.getTimeslot();
            String switch2Room = class2.getRoom();
            class1.setTimeslot(switch2);
            class1.setRoom(switch2Room); 
            class2.setTimeslot(switch1);
            class2.setRoom(switch1Room);
            Data.roomMap.get(switch1Room).setAvailability(switch2, true); // making sure rooms are available at right time
            Data.roomMap.get(switch1Room).setAvailability(switch1, false);
            Data.roomMap.get(switch2Room).setAvailability(switch2, false);
            Data.roomMap.get(switch2Room).setAvailability(switch1, true);

            while(Data.roomMap.get(switch1Room).getRoomType() != Data.roomMap.get(switch2Room).getRoomType()){
                class1.setTimeslot(switch1);
                class1.setRoom(switch1Room);
                class2.setTimeslot(switch2); // switch back and do it again
                class2.setRoom(switch2Room);
                Data.roomMap.get(switch1Room).setAvailability(switch2, false); // making sure rooms are available at right time
                Data.roomMap.get(switch1Room).setAvailability(switch1, true);
                Data.roomMap.get(switch2Room).setAvailability(switch2, true);
                Data.roomMap.get(switch2Room).setAvailability(switch1, false);
                //System.out.println("score: "+conflictsBetweenCommonlyTakenTogetherCourses(timetable));
                class1Index = random.nextInt(unfixedSemesterTimetable.size()); // index to switch
                class2Index = random.nextInt(unfixedSemesterTimetable.size());
                class1 = unfixedSemesterTimetable.get(class1Index); // for easier access
                class2 = unfixedSemesterTimetable.get(class2Index);

                switch1 = class1.getTimeslot(); // switch again, until the conflict score become better or equal
                switch2 = class2.getTimeslot();
                switch1Room = class1.getRoom();
                switch2Room = class2.getRoom();
                class1.setTimeslot(switch2);
                class1.setRoom(switch2Room);
                class2.setTimeslot(switch1); 
                class2.setRoom(switch1Room);
                Data.roomMap.get(switch1Room).setAvailability(switch2, true); // making sure rooms are available at right time
                Data.roomMap.get(switch1Room).setAvailability(switch1, false);
                Data.roomMap.get(switch2Room).setAvailability(switch2, false);
                Data.roomMap.get(switch2Room).setAvailability(switch1, true);

                if(CTTCScore >= conflictsBetweenCommonlyTakenTogetherCourses(timetable)){break;}
            }
            //check if each of the two courses have overlapping periods (same course, but diff period)
            assertNoDuplicatePeriods(class1Index, unfixedSemesterTimetable);
            assertNoDuplicatePeriods(class2Index, unfixedSemesterTimetable);   
        }
    }
    
    /**
     * making sure that the mutated courses did not place courses in overlapping periods
     * @author Eric
     * @return unfixedSemesterTimetable pass from the method above
     */
    private void assertNoDuplicatePeriods(int course, ArrayList<ClassInfo> unfixedSemesterTimetable){
        ArrayList<ClassInfo> sameCourses = new ArrayList<>();
        ArrayList<ClassInfo> shortList = new ArrayList<>();
        ArrayList<Integer> periods = new ArrayList<>();
        ClassInfo targetClass = unfixedSemesterTimetable.get(course);
        int iterations = 0;

        for (ClassInfo s: timetable) { // get all the same courses but with different time, etc.
            if(s.getCourse().equals(targetClass.getCourse())){
                sameCourses.add(s);
            }
        }
        for (ClassInfo s : sameCourses){ // get all duplicated periods
            if(periods.contains(s.getTimeslot())){
                shortList.add(s);
            }
            else{
                periods.add(s.getTimeslot());
            }
        }
        for(ClassInfo s : shortList){ // find available period
            int randomTime = random.nextInt(8);
            if(periods.size() == 8){periods.clear();}
            while(periods.contains(randomTime)){randomTime = random.nextInt(8);
            periods.add(randomTime);
            int originalTime = s.getTimeslot();
            s.setTimeslot(randomTime);

                for (Room r : Data.roomMap.values()) { // find available rooms
                    String roomType = Data.roomMap.get(s.getRoom()).getRoomType();
                    if(iterations > 69){
                        s.setTimeslot(originalTime);
                        return; // total of 69 rooms so if non of them are available, this change is , revert back
                    }
                    if(r.getRoomType().equals(roomType) && r.isAvailable(s.getTimeslot())){
                        Data.roomMap.get(s.getRoom()).setAvailability(originalTime, false); // current room is not occupied
                        s.setRoom(r.getRoomNum());
                        Data.roomMap.get(s.getRoom()).setAvailability(randomTime, true); // new room is occupied
                        
                        break;
                    }
                    iterations ++;
                }
            }
        }   
    }

    
    // old mutation approach
    // // swap the timeslots of two random classes
    // private void swapClassTimeslots(ArrayList<ClassInfo> timetable) {
    //     int[] classesToSwap = getTwoUniqueUnfixedClasses(timetable); // not fixed, checked
    //     int swap = timetable.get(classesToSwap[0]).getTimeslot();
    //     timetable.get(classesToSwap[0]).setTimeslot(timetable.get(classesToSwap[1]).getTimeslot());
    //     timetable.get(classesToSwap[1]).setTimeslot(swap);
    // }

    // // swap the room of two random classes
    // private void swapRoom(ArrayList<ClassInfo> timetable) {
    //     int[] classesToSwap = getTwoUniqueUnfixedClasses(timetable); // not fixed
        
    //     String swap = timetable.get(classesToSwap[0]).getRoom();
    //     String swap1 = timetable.get(classesToSwap[1]).getRoom();

    //     boolean equal = checkRoomType(timetable, classesToSwap[0], classesToSwap[1]);
    //     while(equal != true){
    //         classesToSwap = getTwoUniqueUnfixedClasses(timetable);
    //         equal = checkRoomType(timetable, classesToSwap[0], classesToSwap[1]);
    //     }

    //     timetable.get(classesToSwap[0]).setRoom(swap1);
    //     timetable.get(classesToSwap[1]).setRoom(swap);
    // }
    
    // private boolean checkRoomType(ArrayList<ClassInfo> timetable, int courseIndex1, int courseIndex2){

    //     String swapRoomType = Data.courseMap.get(timetable.get(courseIndex1).getCourse()).getRoomType();
    //     String swapRoomType1 = Data.courseMap.get(timetable.get(courseIndex2).getCourse()).getRoomType();
    //     if(swapRoomType.equals(swapRoomType1)){
    //         return true;
    //     }
    //     else{
    //         return false;
    //     }
    // }
    // // move a random class to a random other room that is suitable
    // private void moveRoom(ArrayList<ClassInfo> timetable) {
    //     int classIndex = random.nextInt(timetable.size());
    //     while (timetable.get(classIndex).isFixed() == true) {
    //         classIndex = random.nextInt(timetable.size());
    //     }
    //     String currentRoomType = Data.courseMap.get(timetable.get(classIndex).getCourse()).getRoomType();
    //     for (Room r : Data.roomMap.values()) {
    //         if(r.getRoomType().equals(currentRoomType) && r.isAvailable(timetable.get(classIndex).getTimeslot()) == true && r.getRoomNum() != timetable.get(classIndex).getRoom()){
    //             timetable.get(classIndex).setRoom(r.getRoomNum());
    //             r.setAvailability(timetable.get(classIndex).getTimeslot(), false);
    //             break;
    //         }
    //     }
    // }
    // private int[] getTwoUniqueUnfixedClasses(ArrayList<ClassInfo> timetable){
    //     int class1Index, class2Index;
    //     do{
    //         class1Index = random.nextInt(timetable.size() - numFixedClasses) + numFixedClasses;
    //         class2Index = random.nextInt(timetable.size() - numFixedClasses) + numFixedClasses;
    //     }while (class1Index == class2Index || timetable.get(class1Index).isFixed() == true || timetable.get(class2Index).isFixed() == true);
    //     return new int[]{class1Index, class2Index};
    // }
}