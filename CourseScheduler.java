import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.Map;

public class CourseScheduler {

    private Random random = new Random();

    private int numFixedClasses;
    private ArrayList<ClassInfo> initialTimetable;
    private HashMap<String, Integer> studentCount = new HashMap<String, Integer>(); // Number of students in each course
    private HashMap<String, Integer> courseCount = new HashMap<String, Integer>(); // Number of sections of each course running

    public CourseScheduler(SpecialCourseScheduler s) {
        // - Determines # of each course required using Student map
        // - Calls method in SpecialCourseScheduler to schedule special courses
        // ArrayList<ClassInfo> timetable = new ArrayList<ClassInfo>();
        studentCount = countStudents();
        courseCount = getCoursesRunning();
        System.out.println(courseCount);
        initialTimetable = s.getSpecialCourseTimetable(courseCount);
        numFixedClasses = initialTimetable.size();
    }

    public ArrayList<ClassInfo> getNewTimetable() {        
        initialTimetable = createInitialTimetable(); // first fill all remaining needed classes into the timetable, without worrying about conflicts

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

    private static ArrayList<ClassInfo> createInitialTimetable() {
        // puts all courses in a time slot
        // assign qualified teacher & suitable room
        // try to minimize conflicts, but does not need to be conflict-free
        return null;
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
        for (String c : courseCount.keySet()) {
            double maxClassSize = Data.courseMap.get(c).getClassSize();
            int numberCourses = (int) Math.floor(studentCount.get(c) / maxClassSize);
            double additionalCourse = (studentCount.get(c) / maxClassSize) - numberCourses / 100;
            if (additionalCourse > threshold) {
                numberCourses++;
            }
            courseCount.put(c, numberCourses);

        }
        return courseCount;
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

            int add2 = findTeacherConflicts(x, teacherTime);
            score += add2;
            if (add2 == 0) {
                int time[] = new int[teacherTime.get(x.getTeacher()).length + 1];
                time[teacherTime.get(x.getTeacher()).length] = x.getTimeslot();
                teacherTime.put(x.getTeacher(), time);
            }

            score += checkTeacherTimeslots(teacherTime);
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

    public int findTeacherConflicts(ClassInfo x, HashMap<Integer, int[]> teacherTime) {
        if (teacherTime.containsKey(x.getTeacher())) {
            int time[] = new int[teacherTime.get(x.getTeacher()).length + 1];
            for (int i = 0; i < teacherTime.get(x.getTeacher()).length; i++) {
                time[i] = teacherTime.get(x.getTeacher())[i];

                if (teacherTime.get(x.getTeacher())[i] == x.getTimeslot()) {
                    return 10;
                }
            }
        } else {
            int time[] = { x.getTimeslot() };
            teacherTime.put(x.getTeacher(), time);
        }
        return 0;
    }

    public int checkTeacherTimeslots(HashMap<Integer, int[]> teacherTime) {
        // teachers less or more than 6
        // TODO just check the number inside the array 3 on each semester
        for (int[] x : teacherTime.values()) {
            if (x.length < 6 || x.length > 6) {
                return 10;
            }
            for (int j : x) {
            }
        }
        return 0;
    }

    public ArrayList<ClassInfo> mutateTimetable(ArrayList<ClassInfo> timetable) {
        ArrayList<ClassInfo> mutated = new ArrayList<ClassInfo>(timetable);
        int mutationTypeSelect = random.nextInt(100);
        if (mutationTypeSelect < 40) {
            swapClassTimeslots(timetable);
        } else if (mutationTypeSelect < 55) {
            swapRoom(timetable);
        } else if (mutationTypeSelect < 70) {
            moveRoom(timetable);
        } else if (mutationTypeSelect < 85) {
            swapTeacher(timetable);
        } else {
            changeTeacher(timetable);
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

    // Switch two teachers, ensuring both are still qualified for the course they are teaching
    private void swapTeacher(ArrayList<ClassInfo> timetable) {
        int[] classesToSwap = getTwoUniqueUnfixedClasses(timetable);

        // where do I check teacher qulification

        // int swap = timetable.get(class1Index).getTeacher();
        // timetable.get(class1Index).setRoom(timetable.get(class2Index).getTeacher());
        // timetable.get(class2Index).setRoom(swap);
    }

    private void changeTeacher(ArrayList<ClassInfo> timetable) {
        // change the teacher for a class to another random teacher that is qualified

        // where do I check the teachers? lol
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
