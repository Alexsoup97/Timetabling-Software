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

    private Random random = new Random(1);

    private int numFixedClasses;
    private ArrayList<ClassInfo> initialTimetable;
    private HashMap<String, Integer> studentCount; // Number of students in each course
    private HashMap<String, Integer> coursesRunning; // Number of sections of each course running
    private ArrayList<HashSet<String>> commonlyTakenTogetherCourses;

    public CourseScheduler(SpecialCourseScheduler s) {
        studentCount = countStudents();
        coursesRunning = calculateCoursesRunning();
        int a = 0;
        for(int value:coursesRunning.values()){
            a+=value;
        }
        System.out.println("running courses " + a);

        // commonlyTakenTogetherCourses = getCommonlyTakenTogetherCourses();
        initialTimetable = s.getSpecialCourseTimetable(coursesRunning);
        
    }

    public ArrayList<ClassInfo> getNewTimetable() {        
        initialTimetable = createInitialTimetable(initialTimetable); 
        //initialTimetable = evolveTimetable(initialTimetable);// TODO
        Collections.sort(initialTimetable, new Comparator<ClassInfo>(){
            public int compare(ClassInfo c1, ClassInfo c2){
                return coursesRunning.get(c1.getCourse()) - coursesRunning.get(c2.getCourse());
            }
        });
        // for(ClassInfo i:initialTimetable){
        //     System.out.println(i);
        // }
        return initialTimetable;
    }

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

        }
        return courseCount;
    }

    //TODO javadoc
    /**
     * Generates an initial master timetable allocating all needed classes into timeslots and rooms.
     * Prevents duplicate rooms and ensures sections of the same course are balanced across semesters
     * @author Suyu
     * @param specialCourseTimetable
     * @return
     */
    private ArrayList<ClassInfo> createInitialTimetable(ArrayList<ClassInfo> specialCourseTimetable) {
        ArrayList<ClassInfo> initialTimetable = new ArrayList<ClassInfo>();
        HashSet<String> specialClasses = new HashSet<String>();
        for(ClassInfo i: specialCourseTimetable){
            initialTimetable.add(i);
            specialClasses.add(i.getCourse());
        }

        HashMap<String, RoomType> roomTypes = new HashMap<String, RoomType>(Data.roomTypeMap.size());
        int roomTypeIdCounter = 0;
        for(Map.Entry<String, ArrayList<String>> entry : Data.roomTypeMap.entrySet()){
            roomTypes.put(entry.getKey(), new RoomType(entry.getKey(), entry.getValue(), roomTypeIdCounter));
            roomTypeIdCounter++; // TODO give room types that have not really conflicitng courses the same ID
        }    

        // TODO make not hard coded
        HashMap<String, String> roomTypeBackups = new HashMap<String, String>();
        roomTypeBackups.put("science-biology", "science");
        roomTypeBackups.put("science-physics", "science");
        roomTypeBackups.put("computer sci", "classroom");
        roomTypeBackups.put("family studies", "classroom");
        roomTypeBackups.put("science", "classroom");
        
        // each type of room has a different random order of filling classes into time periods for each room
        // The fill order alternates between sem1 and sem2 periods so multi section courses are guaranteed to be balanced between semesters
        int[][] fillOrder = new int[roomTypes.size()][Data.NUM_PERIODS];
        for(int i=0; i<fillOrder.length; i++){
            fillOrder[i] = generatePeriodFillOrder();
        }
        
        // Sort the courses running by the number of sections, so courses with few sections are placed first
        // This ensures courses with few sections are put into different periods, since they will all be beside each other and classes are added
        // to timeslots in the order of the fill order
        ArrayList<CourseRunning> sortedCoursesRunning = new ArrayList<CourseRunning>();
        for(Map.Entry<String, Integer> entry : coursesRunning.entrySet()){
            sortedCoursesRunning.add(new CourseRunning(entry.getKey(), entry.getValue()));
        }
        Collections.sort(sortedCoursesRunning, new Comparator<CourseRunning>(){
            public int compare(CourseRunning c1, CourseRunning c2){
                return c1.sections-c2.sections;
            }
        });    

        RoomType roomType;
        String chosenRoom = null; 
        int chosenTimeslot = -1; 
        for (CourseRunning course : sortedCoursesRunning) {
            if (!specialClasses.contains(course.code)) { // special classes were already added
                roomType = roomTypes.get(Data.courseMap.get(course.code).getRoomType());
                for (int i = 0; i < course.sections; i++) {
                    // if there are no more room/timeslot pairs of the correct room type, change to backup room type if available
                    if (roomType.counter / Data.NUM_PERIODS >= roomType.rooms.size()) {
                        if (roomTypeBackups.containsKey(roomType.name))
                            roomType = roomTypes.get(roomTypeBackups.get(roomType.name));
                    }
                    // check if not out of room/timeslot pairs
                    if (roomType.counter / Data.NUM_PERIODS < roomType.rooms.size()) {
                        do {
                            chosenRoom = roomType.rooms.get(roomType.counter / Data.NUM_PERIODS); 
                            chosenTimeslot = fillOrder[roomType.id][roomType.counter % Data.NUM_PERIODS];
                            roomType.counter++;
                        } while (!Data.roomMap.get(chosenRoom).isAvailable(chosenTimeslot)); // must be checked since special classes may have already taken the room/timeslot
                        Data.roomMap.get(chosenRoom).setUnavailable(chosenTimeslot);
                        initialTimetable.add(new ClassInfo(chosenRoom, chosenTimeslot, course.code, false)); 
                    }else{ 
                        System.out.println("Ran out of " + roomType);
                    } 
                }
            } 
        }
        //Data.coursesToClassInfo = coursesToClassInfo;       
       
        return initialTimetable;
    }

    // generate alternating periods to fill in classes
    private int[] generatePeriodFillOrder(){
        int[] alternatingPeriods = new int[Data.NUM_PERIODS]; 
        HashSet<Integer> periods = new HashSet<>();
        for(int i=0; i<Data.NUM_PERIODS; i++){
            periods.add(i);
        }
        int adding;
        
        for (int i = 0; i < alternatingPeriods.length; i++) {
            if(i%2 == 0){
                adding = random.nextInt(4) + 4;
                while(!periods.contains(adding)){
                    adding = random.nextInt(4) + 4;
                }
                alternatingPeriods[i] = adding;
                periods.remove(adding);
                adding = 0;                
            }
            else {
                adding = random.nextInt(4);
                while(!periods.contains(adding)){
                    adding = random.nextInt(4);
                }
                alternatingPeriods[i] = adding;
                periods.remove(adding);
                adding = 0;
            }
        }
        return alternatingPeriods;
    }

    /**
     * Helper class for generating initial timetable
     * Stores some information about each room type
     * @author Suyu
     */
    private class RoomType{
        String name;
        ArrayList<String> rooms = new ArrayList<String>();;
        int id;
        int counter;
        RoomType(String name, ArrayList<String> rooms, int id){
            this.name = name;
            this.rooms = rooms; 
            this.id = id;
            this.counter = 0;
        }
        @Override
        public String toString() {
            return rooms.toString()+" "+id+" "+ counter;
        }
    }

    /**
     * Helper class for generating initial timetable
     * Stores some information about a course that is running
     * @author Suyu
     */
    private class CourseRunning{
        String code;
        int sections;
        CourseRunning(String courseName, int sections){
            this.code = courseName;
            this.sections = sections;
        } 
    }
    
    // private ArrayList<ClassInfo> evolveTimetable(ArrayList<ClassInfo> initialTimetable) {        
    //     final int SURVIVORS_PER_GENERATION = 5;
    //     final int NUM_CHILDREN = 4;
    //     final int NUM_GENERATIONS = 100;

    //     TreeMap<Integer, ArrayList<ClassInfo>> timetableCandidates = new TreeMap<Integer, ArrayList<ClassInfo>>();
    //     ArrayList<ArrayList<ClassInfo>> currentGeneration = new ArrayList<ArrayList<ClassInfo>>();
    //     ArrayList<ClassInfo> mutatedTimetable;
    //     int mutatedTimetableFitness;
    //     timetableCandidates.put(getTimetableFitness(initialTimetable), initialTimetable);

    //     // while(timetableCandidates.firstKey() > 0){  // keep repeating mutation + checking fitness until a solution is found
    //     for(int gen=0; gen<1000; gen++){
    //         currentGeneration.clear();
    //         currentGeneration.addAll(timetableCandidates.values());  // fill current generation of candidates with the survivors from last generation
    //         // timetableCandidates.clear(); //TODO consider - by not including parents in the next generation, might increase mutations/stop algorithm from getting stuck on the same couple ones?
    //         for (ArrayList<ClassInfo> candidate : currentGeneration){   
    //             for(int i=0; i<NUM_CHILDREN; i++){  // make certain number of children of each candidate by mutating it
    //                 mutatedTimetable = mutateTimetable(candidate);
    //                 mutatedTimetableFitness = getTimetableFitness(mutatedTimetable);
    //                 if (timetableCandidates.size() < SURVIVORS_PER_GENERATION){ // if the next generation hasn't been populated yet, just add the child
    //                     timetableCandidates.put(mutatedTimetableFitness, mutatedTimetable);
    //                 }else if (mutatedTimetableFitness < timetableCandidates.lastKey()){ // otherwise, if the new child is better than the worst one, add it and discard the worst one
    //                     timetableCandidates.put(mutatedTimetableFitness, mutatedTimetable);
    //                     timetableCandidates.remove(timetableCandidates.lastKey());
    //                 }
    //             }
    //         }
    //         // System.out.println("Generation " + gen);
    //         printGeneration(currentGeneration);
    //     }
    //     return timetableCandidates.firstEntry().getValue();
    // }
         
    // private void printGeneration(ArrayList<ArrayList<ClassInfo>> currentGeneration){
    //     try{
    //         File studentFile = new File("test.csv");
    //         PrintWriter output  = new PrintWriter(studentFile);   

    //         for(ArrayList<ClassInfo> a:currentGeneration){
    //             output.println();
    //             output.println(a);
    //         }

    //         output.println("");
    //         output.close();
    //     }catch(Exception e){}
        
    // }
    

    // private int getTimetableFitness(ArrayList<ClassInfo> timetable) {
    //     int score = 0;
    //     score += conflictsBetweenCommonlyTakenTogetherCourses(timetable); // added as part of fitness? 
    //   //  System.out.println("Fitness "+score);
    //     return score;
    // }

    // private int conflictsBetweenCommonlyTakenTogetherCourses(ArrayList<ClassInfo> timetable){
    //     int conflictScoreCTTC = 0;
    //     int period;
    //     for (HashSet<String> h : commonlyTakenTogetherCourses) {
    //         period = -1;
    //         for (ClassInfo c : timetable) {
    //             if(h.contains(c.getCourse())){
    //                 if(period == c.getTimeslot()){
    //                     conflictScoreCTTC ++;
    //                     break;
    //                 }
    //                 period = c.getTimeslot();
    //             }
    //         }
    //     }
    //     return conflictScoreCTTC;
    // }

    private ArrayList<HashSet<String>> getCommonlyTakenTogetherCourses(){  
        final int FREQUENCY_THRESHOLD = 10;

        ArrayList<HashSet<String>> allPairs = new ArrayList<>();
        ArrayList<HashSet<String>> frequentlyTakenTogetherCourses = new ArrayList<>();
        
        for(Student student: Data.studentMap.values()){ // for each student
            int start = 1; 
            for(String choice : student.getCourseChoices()){ // for each of their courses
                for (int i = start; i < student.getCourseChoices().size(); i++) { // create all possible PAIRS between courses
                    HashSet<String> check = new HashSet<>();
                    HashSet<String> pairsOfCourses = new HashSet<String>();
                    pairsOfCourses.add(choice);
                    pairsOfCourses.add(student.getCourseChoices().); 
                    allPairs.add(pairsOfCourses);
                }
                start += 1;     
            }
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

    // // can check mutations now
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
        ArrayList<ClassInfo> sem1classes;
        ArrayList<ClassInfo> sem2classes;

        // check if switching two classes in the same semester would reduce conflicts between commonly taken together courses
        // Don't switch them if it means more sections of the same course than necessary at the same time
        // can also move courses between semesters if they are still balanced?
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