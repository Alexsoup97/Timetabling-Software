/**
 * Modifies the master timetable
 * @author Alex, Samson, Suyu, Eric
 * @version 2022-01-25
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.TreeMap;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;
import java.util.*;
import java.io.File;
import java.io.PrintWriter;


public class CourseScheduler {

    private Random random = new Random();
    // fixed class = special classes such as AP, etc. 
    // unfixed class = classes that doesnt need special consideration
    private int numFixedClasses;
    private ArrayList<ClassInfo> timetable; // master time table
    private HashMap<String, Integer> studentCount; // Number of students in each course
    private HashMap<String, Integer> coursesRunning; // Number of sections of each course running
    private ArrayList<HashSet<String>> commonlyTakenTogetherCourses; // based on student course choices (ex. calc and advFunc)
    private HashMap<String, ArrayList<ClassInfo>> coursesToClassInfo;

    public CourseScheduler(SpecialCourseScheduler s) {
        studentCount = countStudents();
        coursesRunning = calculateCoursesRunning();
        int a = 0;
        for(int value:coursesRunning.values()){ // how many sections of each course are running
            a+=value;
        }
        System.out.println("running courses " + a);

        //-+commonlyTakenTogetherCourses = getCommonlyTakenTogetherCourses(); // TODO fix triple loop maybe
        timetable = s.getSpecialCourseTimetable(coursesRunning);
        
    }
    /**
     * generates new time table from scratch
     */
    public ArrayList<ClassInfo> getNewTimetable() {        
        timetable = new InitialTimetableGenerator().createInitialTimetable(timetable, coursesRunning); 
        // coursesToClassInfo = getCoursesToClassInfos(timetable);
        // improveTimetable(timetable);
        Collections.sort(timetable, new Comparator<ClassInfo>(){
            public int compare(ClassInfo c1, ClassInfo c2){
                return coursesRunning.get(c1.getCourse()) - coursesRunning.get(c2.getCourse());
            }
        });
        coursesToClassInfo = getCoursesToClassInfos(timetable); // TODO fix get course to ClassInfo
        Data.coursesToClassInfo = coursesToClassInfo;
        return timetable;
    }

    /**
     *
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
     * 
     */
    private HashMap<String, Integer> calculateCoursesRunning() {
        double threshold = 0.5; 
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
            if (additionalCourse > threshold) {
                numberCourses++;
            }
            courseCount.put(c, numberCourses);
            Data.coursesToClassInfo.put(c, new ArrayList<ClassInfo>());
            

        }
        return courseCount;
    }
    /**
     *
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
     * quantify conflicts between commonly taken together courses
     * @param timetable master timetable
     * @return an integer score based on how many commonly taken together courses are on the same period
     */
    private int conflictsBetweenCommonlyTakenTogetherCourses(ArrayList<ClassInfo> timetable){
        int conflictScoreCTTC = 0;
        ArrayList<Integer> periods = new ArrayList<>();

        for (HashSet<String> h : commonlyTakenTogetherCourses) { // for each pair of CTTC (only 2 iterations)
            for (ClassInfo c : timetable) { // for all courses running
                if(h.contains(c.getCourse())){ 
                    if(periods.contains(c.getTimeslot())){ // if the period have conflicts, t
                        conflictScoreCTTC ++;
                    }   
                    periods.add(c.getTimeslot());     
                }
            }
            periods.clear();
        }
        return conflictScoreCTTC;
    }

    private ArrayList<HashSet<String>> getCommonlyTakenTogetherCourses(){  
        final int FREQUENCY_THRESHOLD = 10; 

        ArrayList<HashSet<String>> allPairs = new ArrayList<>();
        ArrayList<HashSet<String>> frequentlyTakenTogetherCourses = new ArrayList<>();
        
        for(Student student: Data.studentMap.values()){ // for each student
            int start = 1; 
            for(String choice : student.getCourseChoices()){ // for each of their courses
                for (int i = start; i < student.getCourseChoices().size(); i++) { // create all possible PAIRS between courses
                    HashSet<String> pair = new HashSet<String>();
                    pair.add(choice);
                    pair.add(student.getCourseChoices().get(i)); 
                    allPairs.add(pair);
                }
                start += 1;     
            }
        }    
        for (HashSet<String> h : allPairs) { // TODO possible breaking point: counted wrong
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

    // private ArrayList<ClassInfo> mutateTimetable(ArrayList<ClassInfo> timetable) {
    //     ArrayList<ClassInfo> mutated = new ArrayList<ClassInfo>(timetable);
    //     int mutationTypeSelect = random.nextInt(100);
    //     if (mutationTypeSelect < 50) {
    //         swapClassTimeslots(mutated);
    //     } else if (mutationTypeSelect < 75) {
    //         swapRoom(mutated);
    //     } else {
    //         moveRoom(mutated);
    //     }
    //     return mutated;
    // }

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
        improveSemester(sem1UnfixedClasses);
        improveSemester(sem2UnfixedClasses);

        // check if switching two classes in the same semester would reduce conflicts between commonly taken together courses
        // Don't switch them if it means more sections of the same course than necessary at the same time
        // can also move courses between semesters if they are still balanced?
    }
    // TODO possible breaking point: duplicate room or periods
    private void improveSemester(ArrayList<ClassInfo> unfixedSemesterTimetable){
        final int NUM_ITERATIONS = 500; // run 500 times

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
            class1.setRoom(switch2Room); // TODO did not check room type when switching
            class2.setTimeslot(switch1);
            class2.setRoom(switch1Room);
            // TODO possible breaking point, might result in infinite loop (if error in conflicts...)
            // '=' better or equal
            while(!(CTTCScore >= conflictsBetweenCommonlyTakenTogetherCourses(timetable)) || Data.roomMap.get(class1.getRoom()).isAvailable(class1.getTimeslot()) == false || Data.roomMap.get(class2.getRoom()).isAvailable(class1.getTimeslot()) == false){ // check if switching caused score to become worse
                // also if when rooms are unavaliable
                class1.setTimeslot(switch1);
                class1.setRoom(switch1Room);
                class2.setTimeslot(switch2); // switch back and do it again
                class2.setRoom(switch2Room);

                class1Index = random.nextInt(unfixedSemesterTimetable.size()); // index to switch
                class2Index = random.nextInt(unfixedSemesterTimetable.size());
                class1 = unfixedSemesterTimetable.get(class1Index); // for easier access
                class2 = unfixedSemesterTimetable.get(class2Index);

                switch1 = class1.getTimeslot(); // switch again, until the conflict score become better or equal
                switch2 = class2.getTimeslot();
                class1.setTimeslot(switch2);
                class1.setRoom(switch2Room);
                class2.setTimeslot(switch1); 
                class2.setRoom(switch1Room);
            }
            // check if each of the two courses have overlapping periods
            assertNoDuplicatePeriods(class1Index, unfixedSemesterTimetable);
            assertNoDuplicatePeriods(class2Index, unfixedSemesterTimetable);
            
            // HashSet<String> check = new HashSet<>(); 
            // check.add(unfixedSemesterTimetable.get(class1Index).getCourse());
            // check.add(unfixedSemesterTimetable.get(class2Index).getCourse());

            // max 2 in each period of same class (if less than 8, then 1 per period, less than 16, 2 per period max)
            // use coursesRunning maps course to # of classes

            // try to switch two courses (periods)
            // if it reduces conflicts between commonly taken together courses, keep the change
            // this is using only one semester's courses because we don't want to move between semesters
            // important - don't switch courses if it means there will be more classes of the same course than necessary in the same timeslot    
        }
    }
    private void assertNoDuplicatePeriods(int course, ArrayList<ClassInfo> unfixedSemesterTimetable){
        ArrayList<ClassInfo> sameCourses = new ArrayList<>();
        ArrayList<Integer> periods = new ArrayList<>();
        ClassInfo targetClass = unfixedSemesterTimetable.get(course);
        
        if(coursesRunning.get(unfixedSemesterTimetable.get(course).getCourse()) > 8){
            for (ClassInfo s: timetable) { // get all the same courses but with different time, etc.
                if(s.getCourse().equals(targetClass.getCourse())){
                    sameCourses.add(s);
                }
            }
            for (ClassInfo s : sameCourses){ // get all the courses with non duplicate periods
                if(!periods.contains(s.getTimeslot())){
                    periods.add(s.getTimeslot());
                    sameCourses.remove(s);
                }
            }
            for(ClassInfo s : sameCourses){ // assign non duplicated periods to previously conflicting periods
                int randomTime = random.nextInt(8);
                if(periods.size() == 8){periods.clear();} // clear periods if all periods have one course assigned (since courses >8)
                while(periods.contains(randomTime)){randomTime = random.nextInt(8);}
                s.setTimeslot(randomTime);
                periods.add(randomTime);
                for (Room r : Data.roomMap.values()) {
                    String roomType = Data.roomMap.get(s.getRoom()).getRoomType();
                    if(r.getRoomType().equals(roomType) && r.isAvailable(s.getTimeslot())){
                        s.setRoom(r.getRoomNum());
                        break;
                    }
                }
            }
        }
        else if(coursesRunning.get(unfixedSemesterTimetable.get(course).getCourse()) <= 8){
            for (ClassInfo s: timetable) { // get all the same courses but with different time, etc.
                if(s.getCourse().equals(targetClass.getCourse())){
                    sameCourses.add(s);
                }
            }
            for (ClassInfo s : sameCourses){ // get all the courses with non duplicate periods
                if(!periods.contains(s.getTimeslot())){
                    periods.add(s.getTimeslot());
                    sameCourses.remove(s);
                }
            }
            for(ClassInfo s : sameCourses){ // assign non duplicated periods to previously conflicting periods
                int randomTime = random.nextInt(8);
                while(periods.contains(randomTime)){randomTime = random.nextInt(8);}
                s.setTimeslot(randomTime);
                periods.add(randomTime);
                for (Room r : Data.roomMap.values()) {
                    String roomType = Data.roomMap.get(s.getRoom()).getRoomType();
                    if(r.getRoomType().equals(roomType) && r.isAvailable(s.getTimeslot())){
                        s.setRoom(r.getRoomNum());
                        break;
                    }
                }
            }
        }
    }
    // swap the timeslots of two random classes
    private void swapClassTimeslots(ArrayList<ClassInfo> timetable) {
        int[] classesToSwap = getTwoUniqueUnfixedClasses(timetable); // not fixed, checked
        int swap = timetable.get(classesToSwap[0]).getTimeslot();
        timetable.get(classesToSwap[0]).setTimeslot(timetable.get(classesToSwap[1]).getTimeslot());
        timetable.get(classesToSwap[1]).setTimeslot(swap);
        // TODO could check commonly take together courses, etc.
    }

    // swap the room of two random classes
    private void swapRoom(ArrayList<ClassInfo> timetable) {
        int[] classesToSwap = getTwoUniqueUnfixedClasses(timetable); // not fixed
        
        String swap = timetable.get(classesToSwap[0]).getRoom();
        String swap1 = timetable.get(classesToSwap[1]).getRoom();

        boolean equal = checkRoomType(timetable, classesToSwap[0], classesToSwap[1]);
        while(equal != true){
            classesToSwap = getTwoUniqueUnfixedClasses(timetable);
            equal = checkRoomType(timetable, classesToSwap[0], classesToSwap[1]);
        }

        timetable.get(classesToSwap[0]).setRoom(swap1);
        timetable.get(classesToSwap[1]).setRoom(swap);
    }
    
    private boolean checkRoomType(ArrayList<ClassInfo> timetable, int courseIndex1, int courseIndex2){

        String swapRoomType = Data.courseMap.get(timetable.get(courseIndex1).getCourse()).getRoomType();
        String swapRoomType1 = Data.courseMap.get(timetable.get(courseIndex2).getCourse()).getRoomType();
        if(swapRoomType.equals(swapRoomType1)){
            return true;
        }
        else{
            return false;
        }
    }
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

    private int[] getTwoUniqueUnfixedClasses(ArrayList<ClassInfo> timetable){
        int class1Index, class2Index;
        do{
            class1Index = random.nextInt(timetable.size() - numFixedClasses) + numFixedClasses;
            class2Index = random.nextInt(timetable.size() - numFixedClasses) + numFixedClasses;
        }while (class1Index == class2Index || timetable.get(class1Index).isFixed() == true || timetable.get(class2Index).isFixed() == true);
        return new int[]{class1Index, class2Index};
    }
}