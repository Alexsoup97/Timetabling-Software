import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.TreeMap;
import java.util.Map;

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
        coursesRunning = getCoursesRunning();
        System.out.println(coursesRunning);
        initialTimetable = s.getSpecialCourseTimetable(coursesRunning);
        numFixedClasses = initialTimetable.size();
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
            // timetableCandidates.clear(); // TODO consider - by not including parents in the next generation, might increase mutations/stop algorithm from getting stuck on the same couple ones?
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
        ArrayList<String> specialClasses = new ArrayList<String>();
        ArrayList<Integer> students = null;

        HashMap<String, Integer> typeCounter = new HashMap<String, Integer>();
        for(String s : coursesRunning.keySet()){
            typeCounter.put(s, 0);
        }
        int[] fillOrder = {1,7,2,6,3,5,0,4};

        for(ClassInfo i: specialCourseTimetable){
            initialTimetable.add(i);
            specialClasses.add(i.getCourse());
            
        }

        for(Map.Entry<String, Integer> map: coursesRunning.entrySet()){
            if(!specialClasses.contains(map.getKey())){
                String roomType = Data.courseMap.get(map.getKey()).getType();
                String[] rooms = Data.typesOfRooms.get(roomType);
                int counter = typeCounter.get(map.getKey());

                for(int i = 0; i < map.getValue();i ++){
                    boolean valid = false;
                    do{
                        String currentRoom = rooms[(int)Math.floor(counter/8)];
                        int timeslot = counter % 8;

                        if(Data.roomMap.get(map.getKey()).get){

                        }
                       

                    }while(!valid);

                }

            }
        }
    // list of rooms of each room type
    // counter for each room type
    // each room type given a id number

    // array with alternating sem1 and sem2 periods, eg. 1,7,2,6,3,5,0,4

    // sort coursesrunning from fewest sections to most sections
    // group C courses together at beginning
    // potentially add more stuff like this
    // go through all courses running
        // if it isnt special course
            // need to add to the timetable
            // increment counter of the corresponding room type
            // place the course into the room at the index of the counter/8 and the period of the index of the array (counter + id number given to room type)%8
            // if that room/timeslot pair was already occupied by special course, increment counter and put it into the next one

        
        return initialTimetable;

    }
    private boolean roomChecker(){
        return false;
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

    private HashMap<String, Integer> getCoursesRunning() {
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


//TODO finish
    private HashMap<String, ArrayList<String>> getCommonlyTakenTogetherCourses(){
        HashMap<String, ArrayList<String>> output = new HashMap<String, ArrayList<String>>();
        HashMap<String, Integer> commonCoursePopularity = new HashMap<String, Integer>();
        for(String course:coursesRunning.keySet()){
            output.put(course, new ArrayList<String>());
            
        }
        
        
        for(Student student:Data.studentMap.values()){
            for(String course: student.getCourseChoices()){
                
            }
        }
        return output;
    }

    public int getTimetableFitness(ArrayList<ClassInfo> timetable) {
        HashMap<Integer, int[]> roomTime = new HashMap<Integer, int[]>();
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

    public int findRoomConflicts(ClassInfo x, HashMap<Integer, int[]> roomTime) {
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
