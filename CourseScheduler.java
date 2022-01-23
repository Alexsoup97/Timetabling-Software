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

    private int numFixedClasses;
    private ArrayList<ClassInfo> initialTimetable;
    private HashMap<String, Integer> studentCount; // Number of students in each course
    private HashMap<String, Integer> coursesRunning; // Number of sections of each course running
    private HashMap<String, ArrayList<ClassInfo>> coursesToTimeslot = new HashMap<String, ArrayList<ClassInfo>>(); 
    private ArrayList<HashSet<String>> commonlyTakenTogetherCourses;

    public CourseScheduler(SpecialCourseScheduler s) {
        studentCount = countStudents();
        coursesRunning = calculateCoursesRunning();
        int a = 0;
        for(int value:coursesRunning.values()){
            a+=value;
        }
        System.out.println("running courses " + a);

        commonlyTakenTogetherCourses = getCommonlyTakenTogetherCourses();
        initialTimetable = s.getSpecialCourseTimetable(coursesRunning);
        
    }

    public ArrayList<ClassInfo> getNewTimetable() {        
        initialTimetable = createInitialTimetable(initialTimetable); 
        // initialTimetable = evolveTimetable(initialTimetable);// TODO
        Collections.sort(initialTimetable, new Comparator<ClassInfo>(){
            public int compare(ClassInfo c1, ClassInfo c2){
                return coursesRunning.get(c1.getCourse()) - coursesRunning.get(c2.getCourse());
            }
        });
        for(ClassInfo i:initialTimetable){
            System.out.println(i);
        }
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
        double threshold = 0.75; 
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
            coursesToTimeslot.put(c,new ArrayList<ClassInfo>());

        }
        courseCount.put("ZREMOT", 0); 
        return courseCount;
    }

    private ArrayList<ClassInfo> createInitialTimetable(ArrayList<ClassInfo> specialCourseTimetable) {
        ArrayList<ClassInfo> initialTimetable = new ArrayList<ClassInfo>();
        HashSet<String> specialClasses = new HashSet<String>();
        
        for(ClassInfo i: specialCourseTimetable){
            initialTimetable.add(i);
            specialClasses.add(i.getCourse());
            coursesToTimeslot.get(i.getCourse()).add(i);
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
        
        int[][] fillOrder = new int[Data.NUM_PERIODS][Data.NUM_PERIODS];
        for(int i=0; i<Data.NUM_PERIODS; i++){
            fillOrder[i] = generatePeriodFillOrder();
        }
        
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
        ClassInfo newClass;
        for (CourseRunning course : sortedCoursesRunning) {
            if (!specialClasses.contains(course.code)) {
                roomType = roomTypes.get(Data.courseMap.get(course.code).getRoomType());

                for (int i = 0; i < course.sections; i++) {
                    if (roomType.counter / fillOrder.length >= roomType.rooms.size()) {
                        if (roomTypeBackups.containsKey(roomType.name)) {
                            roomType = roomTypes.get(roomTypeBackups.get(roomType.name));
                        }
                    }
                    if (roomType.counter / fillOrder.length < roomType.rooms.size()) {
                        do {
                            chosenRoom = roomType.rooms.get(roomType.counter / fillOrder.length); 
                            chosenTimeslot = fillOrder[roomType.id % fillOrder.length][roomType.counter % fillOrder[0].length];
                            roomType.counter++;
                        } while (!Data.roomMap.get(chosenRoom).isAvailable(chosenTimeslot));
                        Data.roomMap.get(chosenRoom).setUnavailable(chosenTimeslot);
                    }else{ 
                        System.out.println("Ran out of " + roomType);
                        // System.exit(0);
                    } 

                    newClass = new ClassInfo(chosenRoom, chosenTimeslot, course.code, false);
                    initialTimetable.add(newClass);
                    coursesToTimeslot.get(course.code).add(newClass); 
                }
            } 
        }
        Data.coursesToTimeslot = coursesToTimeslot;       
       
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

        //TODO remove
        @Override
        public String toString() {
            return rooms.toString()+" "+id+" "+ counter;
        }
    }

    private class CourseRunning{
        String code;
        int sections;
        CourseRunning(String courseName, int sections){
            this.code = courseName;
            this.sections = sections;
        } 
    }
    
    private ArrayList<ClassInfo> evolveTimetable(ArrayList<ClassInfo> initialTimetable) {        
        final int SURVIVORS_PER_GENERATION = 5;
        final int NUM_CHILDREN = 4;
        final int NUM_GENERATIONS = 100;

        TreeMap<Integer, ArrayList<ClassInfo>> timetableCandidates = new TreeMap<Integer, ArrayList<ClassInfo>>();
        ArrayList<ArrayList<ClassInfo>> currentGeneration = new ArrayList<ArrayList<ClassInfo>>();
        ArrayList<ClassInfo> mutatedTimetable;
        int mutatedTimetableFitness;
        int generationCount = 0;
        timetableCandidates.put(getTimetableFitness(initialTimetable), initialTimetable);

        for(int j=0; j<NUM_GENERATIONS; j++){
            currentGeneration.clear();
            currentGeneration.addAll(timetableCandidates.values());  // fill current generation of candidates with the survivors from last generation
            // timetableCandidates.clear(); //TODO consider - by not including parents in the next generation, might increase mutations/stop algorithm from getting stuck on the same couple ones?
            for (ArrayList<ClassInfo> candidate : currentGeneration){   
                for(int i=0; i<NUM_CHILDREN; i++){  // make certain number of children of each candidate by mutating it
                    mutatedTimetable = mutateTimetable(candidate);
                    mutatedTimetableFitness = getTimetableFitness(mutatedTimetable);
                    if (timetableCandidates.size() < SURVIVORS_PER_GENERATION){ // if the next generation hasn't been populated yet, just add the child
                        timetableCandidates.put(mutatedTimetableFitness, mutatedTimetable);
                    }else if (mutatedTimetableFitness < timetableCandidates.lastKey()){ // otherwise, if the new child is better than the worst one, add it and discard the worst one
                        timetableCandidates.put(mutatedTimetableFitness, mutatedTimetable);
                        timetableCandidates.remove(timetableCandidates.lastKey());
                    }
                }
            }
            generationCount++;
            printGenereation(currentGeneration);
        }
        System.out.println("Course scheduling generations: " + generationCount); //TODO
        return timetableCandidates.firstEntry().getValue();
    }

    //TODO
    private void printGenereation(ArrayList<ArrayList<ClassInfo>> currentGeneration){
        try{
             File studentFile = new File("test.csv");
            PrintWriter output  = new PrintWriter(studentFile);

            for(ArrayList<ClassInfo> a:currentGeneration){
                output.println();
                output.println(a);
            }

            output.println("");
            output.close();
        }catch(Exception e){}
        
    }

    private int getTimetableFitness(ArrayList<ClassInfo> timetable) {
        HashMap<String, int[]> roomTime = new HashMap<String, int[]>();
        int score = 0;
        // dupliace time slots
        for (ClassInfo x : timetable) {
            int add1 = findRoomConflicts(x, roomTime);
            score += add1;
            if (add1 == 0) {
                int time[] = new int[roomTime.get(x.getRoom()).length + 1];
                time[roomTime.get(x.getRoom()).length] = x.getTimeslot();
                roomTime.put(x.getRoom(), time);
            }
        }

        //TODO check how balanced the courses are between semesters
        score += conflictsBetweenCommonlyTakenTogetherCourses(timetable); // added as part of fitness? 

        return score;
    }

    private int findRoomConflicts(ClassInfo x, HashMap<String, int[]> roomTime) {
        if (roomTime.containsKey(x.getRoom())) {
            int time[] = new int[roomTime.get(x.getRoom()).length + 1];
            for (int i = 0; i < roomTime.get(x.getRoom()).length; i++) {
                time[i] = roomTime.get(x.getRoom())[i];
                if (roomTime.get(x.getRoom())[i] == x.getTimeslot()) {
                   return 10;
                 }
            }
        } else {
            int time[] = { x.getTimeslot() };
            roomTime.put(x.getRoom(), time);
        }
        return 0;
    }

    private int conflictsBetweenCommonlyTakenTogetherCourses(ArrayList<ClassInfo> timetable){
        int conflictScoreCTTC = 0;

        for (HashSet<String> h : commonlyTakenTogetherCourses) {
            String[] check = new String[2];
            int period1 = 0;
            int period2 = 0;
            int counter = 0;

            for (String s : h) {
                check[counter] = s;
                counter ++; 
            }
            for (ClassInfo c : timetable) {
                if(c.getCourse().equals(check[0]) || c.getCourse().equals(check[1])){
                    if(c.getCourse().equals(check[0])){
                        period1 = c.getTimeslot();
                    }
                    else{
                        period2 = c.getTimeslot();
                    }
                }
            }
            if(period1 == period2){
                conflictScoreCTTC ++;
            }
            period1 = 0;
            period2 = 0;
            counter = 0;

        }
        return conflictScoreCTTC;
    }
// TODO might have to check functionality later (after testing)
    private ArrayList<HashSet<String>> getCommonlyTakenTogetherCourses(){  
        final int FREQUENCY_THRESHOLD = 10;

        HashMap<HashSet<String>, Integer> frequency = new HashMap<>(); // pair of courses, frequency
        ArrayList<HashSet<String>> frequentlyTakenTogetherCourses = new ArrayList<>();

        for(Student student:Data.studentMap.values()){
            int start = 1; 
            for(String choice : student.getCourseChoices()){
                for (int i = start; i < student.getCourseChoices().size(); i++) { // create all PAIRS of chosen courses
                    HashSet<String> check = new HashSet<>();
                    check.add(choice);
                    check.add(student.getCourseChoices().get(i)); 
                    if(frequency.containsKey(check)){ 
                        frequency.put(check, frequency.get(check) + 1); 
                    }
                    else{
                        frequency.put(check, 1); 
                    } // output is an arraylist of pairs of commonly taken courses,
                    if(frequency.get(check) > FREQUENCY_THRESHOLD && !frequentlyTakenTogetherCourses.contains(check)){
                        frequentlyTakenTogetherCourses.add(check);
                    }
                    check.remove(choice);
                    check.remove(student.getCourseChoices().get(i));
                }
                start += 1;     
            }
            start = 1;
        }        
        System.out.println(frequentlyTakenTogetherCourses);
        return frequentlyTakenTogetherCourses;
    }
    // can check mutations now
    private ArrayList<ClassInfo> mutateTimetable(ArrayList<ClassInfo> timetable) {
        ArrayList<ClassInfo> mutated = new ArrayList<ClassInfo>(timetable);
        int mutationTypeSelect = random.nextInt(100);
        if (mutationTypeSelect < 50) {
            swapClassTimeslots(mutated);
        } else if (mutationTypeSelect < 75) {
            swapRoom(mutated);
        } else {
            moveRoom(mutated);
        }
        return mutated;
    }
// course obj
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
    // move a random class to a random other room that is suitable
    private void moveRoom(ArrayList<ClassInfo> timetable) {
        int classIndex = random.nextInt(timetable.size());
        while (timetable.get(classIndex).isFixed() == true) {
            classIndex = random.nextInt(timetable.size());
        }
        String currentRoomType = Data.courseMap.get(timetable.get(classIndex).getCourse()).getRoomType();
        for (Room r : Data.roomMap.values()) {
            if(r.getRoomType().equals(currentRoomType) && r.isAvailable(timetable.get(classIndex).getTimeslot()) == true && r.getRoomNum() != timetable.get(classIndex).getRoom()){
                timetable.get(classIndex).setRoom(r.getRoomNum());
                r.setAvailability(timetable.get(classIndex).getTimeslot(), false);
                break;
            }
        }
    }

    private int[] getTwoUniqueUnfixedClasses(ArrayList<ClassInfo> timetable){
        int class1Index, class2Index;
        do{
            class1Index = random.nextInt(timetable.size() - numFixedClasses) + numFixedClasses;
            class2Index = random.nextInt(timetable.size() - numFixedClasses) + numFixedClasses;
        }while (class1Index == class2Index || timetable.get(class1Index).isFixed() == true || timetable.get(class2Index).isFixed() == true);
        return new int[]{class1Index, class2Index};
    }
}