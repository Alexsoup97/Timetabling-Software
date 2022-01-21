import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.Map;
import java.util.Arrays;
import java.util.*;

public class CourseScheduler {

    private Random random = new Random();

    private int numFixedClasses;
    private ArrayList<ClassInfo> initialTimetable;
    private HashMap<String, Integer> studentCount; // Number of students in each course
    private HashMap<String, Integer> coursesRunning; // Number of sections of each course running

    public CourseScheduler(SpecialCourseScheduler s) {
        // - Determines # of each course required using Student map
        // - Calls method in SpecialCourseScheduler to schedule special courses
        // ArrayList<ClassInfo> timetable = new ArrayList<ClassInfo>();
        studentCount = countStudents();
        coursesRunning = calculateCoursesRunning();
        initialTimetable = s.getSpecialCourseTimetable(coursesRunning);

    }

    public ArrayList<ClassInfo> getNewTimetable() {        
        initialTimetable = createInitialTimetable(initialTimetable); // first fill all remaining needed classes into the timetable, without worrying about conflicts

        final int SURVIVORS_PER_GENERATION = 5;
        final int NUM_CHILDREN = 4;

        TreeMap<Integer, ArrayList<ClassInfo>> timetableCandidates = new TreeMap<Integer, ArrayList<ClassInfo>>();
        ArrayList<ArrayList<ClassInfo>> currentGeneration = new ArrayList<ArrayList<ClassInfo>>();
        ArrayList<ClassInfo> mutatedTimetable;
        int mutatedTimetableFitness;
        int generationCount = 0;
        timetableCandidates.put(getTimetableFitness(initialTimetable), initialTimetable);

        while(timetableCandidates.firstKey() > 0){  // keep repeating mutation + checking fitness until a solution is found
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
        }
        System.out.println("Course scheduling generations: " + generationCount);
        return timetableCandidates.firstEntry().getValue();
    }

    // puts all courses in a time slot
    // assign qualified teacher & suitable room
    // TRY to minimize conflicts
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
            roomTypes.put(entry.getKey(), new RoomType(entry.getValue(), roomTypeIdCounter));
            roomTypeIdCounter++; // TODO give room types that have not really conflicitng courses the same ID
        }    
        
        int[] fillOrder = {1,7,2,6,3,5,0,4}; // TODO generate this so it's different each time. Must still be alternating sem1/sem2 periods

        ArrayList<CourseRunning> sortedCoursesRunning = new ArrayList<CourseRunning>();
        for(Map.Entry<String, Integer> entry : coursesRunning.entrySet()){
            sortedCoursesRunning.add(new CourseRunning(entry.getKey(), entry.getValue()));
        }
        Collections.sort(sortedCoursesRunning, new Comparator<CourseRunning>(){
            public int compare(CourseRunning c1, CourseRunning c2){
                return c1.sections-c2.sections;
            }
        });    


        RoomType roomType = null;
        String lastCourse = null;
        String chosenRoom = null;
        int chosenTimeslot = -1;
        for (Map.Entry<String, Integer> course : entries) {
            if (!specialClasses.contains(course.getKey())) {
                if (course != lastCourse) {
                    roomType = roomTypes.get(Data.courseMap.get(course).getRoomType());
                }
                lastCourse = course;
                do {
                    roomType.counter++;
                    chosenRoom = roomType.rooms.get(roomType.counter / 8);
                    chosenTimeslot = fillOrder[(roomType.counter + roomType.id) % fillOrder.length];
                } while (Data.roomMap.get(chosenRoom).isAvailable(chosenTimeslot) == false && roomType.counter < roomType.rooms.size());

                if()
            }
        }

    // list of rooms of each room type -> in Data
    // counter for each room type -> roomType class
    // each room type given a id number -> roomType class

    // array with alternating sem1 and sem2 periods, eg. 1,7,2,6,3,5,0,4

    // sort coursesrunning from fewest sections to most sections
    // group C courses together at beginning, potentially add more stuff like this
    // go through all courses running
        // if it isnt special course
            // need to add to the timetable
            // increment counter of the corresponding room type
            // place the course into the room at the index of the counter/8 and the period of the index of the array (counter + id number given to room type)%8
            // if that room/timeslot pair was already occupied by special course, increment counter and put it into the next one

        
        return initialTimetable;

    }

    private class RoomType{
        ArrayList<String> rooms = new ArrayList<String>();;
        int id;
        int counter;
        RoomType(ArrayList<String> rooms, int id){
            this.rooms = rooms; 
            this.id = id;
            this.counter = 0;
        }
    }

    private class CourseRunning{
        String courseName;
        int sections;
        CourseRunning(String courseName, int sections){
            this.courseName = courseName;
            this.sections = sections;
        } 
    }

    private HashMap<String, Integer> countStudents() {
        HashMap<String, Integer> courseCount = new HashMap<String, Integer>();
        for (Student s : Data.studentMap.values()) {
            String[] temp = s.getCourseChoices();
            for (int i = 0; i < temp.length; i++) {
                if (temp[i].equals("")) {
                    continue;
                }
                if (courseCount.containsKey(temp[i])) {
                    courseCount.put(temp[i], courseCount.get(temp[i]) + 1);
                } else {
                    courseCount.put(temp[i], 1);
                }
            }
        }
        return courseCount;
    }

    private HashMap<String, Integer> calculateCoursesRunning() {
        double threshold = 0.50;
        HashMap<String, Integer> courseCount = new HashMap<String, Integer>();
        for (String c : studentCount.keySet()) {
            double maxClassSize;
            if(Data.courseMap.containsKey(c)){
               maxClassSize = Data.courseMap.get(c).getClassSize();
            }else{
                maxClassSize = 30;
            }
           
            int numberCourses = (int) Math.floor(studentCount.get(c) / maxClassSize);
            double additionalCourse = (studentCount.get(c) / maxClassSize) - numberCourses / 100;
            if (additionalCourse > threshold) {
                numberCourses++;
            }
            courseCount.put(c, numberCourses);

        }
        return courseCount;
    }
// TODO might have to check functionality later (after testing)
    private ArrayList<HashSet<String>> getCommonlyTakenTogetherCourses(){  
        final int FREQUENCY_THRESHOLD = 20;

        // also i changed the course into hashSet 
        HashMap<HashSet<String>, Integer> frequency = new HashMap<>(); // pair of course, frequency
        ArrayList<HashSet<String>> commonlyTakenTogetherCourses = new ArrayList<>();

        for(Student student:Data.studentMap.values()){
            int start = 1; 
            for(String choice : student.getCourseChoices()){
                HashSet<String> check = new HashSet<>();
                check.add(choice); 
                for (int i = start; i < student.getCourseChoices().length; i++) { // create all PAIRS of chosen courses
                    check.add(student.getCourseChoices()[i]); // idk i think it hshould work
                    if(frequency.containsKey(check)){ 
                        frequency.put(check, frequency.get(check) + 1); 
                    } // 20 is just random, like if 20 people picked this pair then its considered as frequently picked
                    else{ // 20 seems like a class size of people? idk like ex. adv and calc XD
                        frequency.put(check, 1); 
                    } // output is an arraylist of pairs of commonly taken courses,
                    if(frequency.get(check) > FREQUENCY_THRESHOLD && !commonlyTakenTogetherCourses.contains(check)){ // is 20 enough?
                        commonlyTakenTogetherCourses.add(check);
                    }
                    check.remove(student.getCourseChoices()[i]);
                }
                start += 1;
                check.remove(choice);
            }
            start = 0;
        }
        
        return commonlyTakenTogetherCourses;
    }

    public int getTimetableFitness(ArrayList<ClassInfo> timetable) {
        HashMap<String, int[]> roomTime = new HashMap<String, int[]>();
        HashMap<Integer, int[]> teacherTime = new HashMap<Integer, int[]>();
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

            //TODO check how balanced the courses are between semesters

            // int add2 = findTeacherConflicts(x, teacherTime);
            // //score += add2; dont worry about teachers 
            
            // if (add2 == 0) {
            //     int time[] = new int[teacherTime.get(x.getTeacher()).length + 1];
            //     time[teacherTime.get(x.getTeacher()).length] = x.getTimeslot();
            //     teacherTime.put(x.getTeacher(), time);
            // }

            // score += checkTeacherTimeslots(teacherTime);
        }

        return score;
    }

    public int findRoomConflicts(ClassInfo x, HashMap<String, int[]> roomTime) {
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

    // public int findTeacherConflicts(ClassInfo x, HashMap<Integer, int[]> teacherTime) {
    //     if (teacherTime.containsKey(x.getTeacher())) {
    //         int time[] = new int[teacherTime.get(x.getTeacher()).length + 1];
    //         for (int i = 0; i < teacherTime.get(x.getTeacher()).length; i++) {
    //             time[i] = teacherTime.get(x.getTeacher())[i];

    //             if (teacherTime.get(x.getTeacher())[i] == x.getTimeslot()) {
    //                 return 10;
    //             }
    //         }
    //     } else {
    //         int time[] = { x.getTimeslot() };
    //         teacherTime.put(x.getTeacher(), time);
    //     }
    //     return 0;
    // }

    // public int checkTeacherTimeslots(HashMap<Integer, int[]> teacherTime) {
    //     // teachers less or more than 6
    //     // TODO just check the number inside the array 3 on each semester
    //     for (int[] x : teacherTime.values()) {
    //         if (x.length < 6 || x.length > 6) {
    //             return 10;
    //         }
    //         //you dont neeed to check semester 2, if there is 2 in 1 then that means 4 in 2
    //         boolean semster1= true;
    //         for (int j: x){
    //             if (j==1 || j==2|| j==3|| j==4){
    //                 semster1=!semster1;
    //             }
    //         }  
    //         if (semster1==true){
    //             return 10;
    //         }
    //     }
    //     return 0;
    // }

    public ArrayList<ClassInfo> mutateTimetable(ArrayList<ClassInfo> timetable) {
        ArrayList<ClassInfo> mutated = new ArrayList<ClassInfo>(timetable);
        int mutationTypeSelect = random.nextInt(100);
        if (mutationTypeSelect < 50) {
            swapClassTimeslots(timetable);
        } else if (mutationTypeSelect < 75) {
            swapRoom(timetable);
        } else {
            moveRoom(timetable);
        }
        return mutated;
    }

    // swap the timeslots of two random classes
    private void swapClassTimeslots(ArrayList<ClassInfo> timetable) {
        int[] classesToSwap = getTwoUniqueUnfixedClasses(timetable);
        int swap = timetable.get(classesToSwap[0]).getTimeslot();
        timetable.get(classesToSwap[0]).setTimeslot(timetable.get(classesToSwap[1]).getTimeslot());
        timetable.get(classesToSwap[1]).setTimeslot(swap);
    }

    // swap the room of two random classes
    private void swapRoom(ArrayList<ClassInfo> timetable) {
        int[] classesToSwap = getTwoUniqueUnfixedClasses(timetable);
        int swap = timetable.get(classesToSwap[0]).getRoom();
        timetable.get(classesToSwap[0]).setRoom(timetable.get(classesToSwap[1]).getRoom());
        timetable.get(classesToSwap[1]).setRoom(swap);
    }

    // move a random class to a random other room that is suitable
    private void moveRoom(ArrayList<ClassInfo> timetable) {
        int classIndex = random.nextInt(timetable.size());
        while (timetable.get(classIndex).isFixed() == true) {
            classIndex = random.nextInt(timetable.size());
        }
        // where do I check the avaliable rooms?
    }

    // // Switch two teachers, ensuring both are still qualified for the course they are teaching
    // private void swapTeacher(ArrayList<ClassInfo> timetable) {
    //     int[] classesToSwap = getTwoUniqueUnfixedClasses(timetable);

    //     // where do I check teacher qulification

    //     // int swap = timetable.get(class1Index).getTeacher();
    //     // timetable.get(class1Index).setRoom(timetable.get(class2Index).getTeacher());
    //     // timetable.get(class2Index).setRoom(swap);
    // }

    // private void changeTeacher(ArrayList<ClassInfo> timetable) {
    //     // change the teacher for a class to another random teacher that is qualified

    //     // where do I check the teachers? lol
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