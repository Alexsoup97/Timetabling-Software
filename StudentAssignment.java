import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;
import java.io.PrintWriter;
import java.io.File;
import java.util.TreeMap;
import java.util.LinkedHashSet;
import java.util.HashSet;

public class StudentAssignment {
    private Random random = new Random();
    private HashMap<String, HashSet<Integer>> courseToPeriods;// coures to which period it has that class
    private ArrayList<ClassInfo> timetable;
    static int completeTimetableCount = 0;

    public StudentAssignment(ArrayList<ClassInfo> timetable) {
        this.timetable = timetable;
        // System.out.println("COMPLETE TIMETABLES:" + fillStudents());
        this.courseToPeriods = getCoursePeriods();
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

    public HashMap<String, HashSet<Integer>> getCoursePeriods() {
        HashMap<String, HashSet<Integer>> output = new HashMap<String, HashSet<Integer>>();
        for (String s : Data.coursesToClassInfo.keySet()) {
            HashSet<Integer> times = new HashSet<Integer>();
            for (int i = 0; i < Data.coursesToClassInfo.get(s).size(); i++) {
                times.add(Data.coursesToClassInfo.get(s).get(i).getTimeslot());
            }
            output.put(s, times);
        }
        return output;
    }

    public int fillStudents(ArrayList<ClassInfo> masterTimetable, ArrayList<Student> students) {
        int studentSpares;
        ArrayList<ClassInfo> studentTimetable = new ArrayList<ClassInfo>();
        ArrayList<ClassInfo> courseClassInfos;
        ArrayList<Integer> classInfoIndices = new ArrayList<Integer>();
        int classInfoIndex;
        for (Student student : students) {
            studentSpares = student.getNumSpares();

            for(String c:student.getCourseChoices()){

                // pick a random section, that does not conflict with the
                // 

                
            }
            
        }

        // for each student:
        // pick periods in random order
        // for each period in the random order:
        // check if any courses the student chose are available in that period, if so
        // put it in
        // if timetable is incomplete:
        //

        return 0;
    }

    public int[] generateRandomNumberSequence(int size) {
        int[] order = new int[size];
        int num;
        for (int i = 0; i < size; i++) {
            do {
                num = random.nextInt(size);
            } while (!arrayContains(order, num));
            order[i] = num;
        }
        return order;
    }

    public boolean arrayContains(int[] array, int num) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == num)
                return true;
        }
        return false;
    }

    /**
     * Determines a fitness score for all student timetables, based on how many
     * students have top choices, alternate choices, or incomplete timetables
     * 
     * @param students
     * @return
     */
    private int getStudentTimetableFitness(ArrayList<Student> students) {
        final int ALTERNATE_CHOICE_WEIGHT = 1;
        final int TOP_CHOICE_WEIGHT = 3;
        final int INCOMPLETE_TIMETABLE_COURSE_WEIGHT = -4;
        int correctTopChoices = 0;
        int correctAlternateChoices = 0;
        int coursesMissing = 0;
        int[] studentChoicesReceived;

        for (Student student : students) {
            studentChoicesReceived = student.getNumChoicesReceived();
            correctTopChoices += studentChoicesReceived[0];
            correctAlternateChoices += studentChoicesReceived[1];
            coursesMissing += (student.getNumCourseChoices() - studentChoicesReceived[2]);
        }
        return correctTopChoices * TOP_CHOICE_WEIGHT + correctAlternateChoices * ALTERNATE_CHOICE_WEIGHT
                + coursesMissing * INCOMPLETE_TIMETABLE_COURSE_WEIGHT;
    }

    private ArrayList<Student> improveStudentTimetables(ArrayList<Student> initialStudents) {
        final int SURVIVORS_PER_GENERATION = 5;
        final int NUM_CHILDREN = 4;
        final int NUM_GENERATIONS = 500;

        TreeMap<Integer, ArrayList<Student>> timetableCandidates = new TreeMap<Integer, ArrayList<Student>>();
        ArrayList<ArrayList<Student>> currentGeneration = new ArrayList<ArrayList<Student>>();
        ArrayList<Student> mutatedTimetable;
        int mutatedTimetableFitness;
        timetableCandidates.put(getStudentTimetableFitness(initialStudents), initialStudents);

        for (int i = 0; i < NUM_GENERATIONS; i++) {
            currentGeneration.clear();
            currentGeneration.addAll(timetableCandidates.values()); // fill current generation of candidates with the survivors from last generation
            // timetableCandidates.clear(); //TODO consider - by not including parents in the next generation, might increase mutations/stop algorithm from getting stuck on the same couple ones?
            for (ArrayList<Student> candidate : currentGeneration) {
                for (int j = 0; j < NUM_CHILDREN; j++) { // make certain number of children of each candidate by mutating it
                    mutatedTimetable = mutateStudentTimetable(candidate);
                    mutatedTimetableFitness = getStudentTimetableFitness(mutatedTimetable);
                    if (timetableCandidates.size() < SURVIVORS_PER_GENERATION) { // if the next generation hasn't been populated yet, just add the child
                        timetableCandidates.put(mutatedTimetableFitness, mutatedTimetable);
                    } else if (mutatedTimetableFitness > timetableCandidates.firstKey()) { // otherwise, if the new child is better than the worst one, add it and discard the worst one
                        timetableCandidates.put(mutatedTimetableFitness, mutatedTimetable);
                        timetableCandidates.remove(timetableCandidates.firstKey());
                    }
                }
            }
            System.out.println("Student assmgnt Gen " + i + " Fitness " + timetableCandidates.lastEntry());
        }
        return timetableCandidates.lastEntry().getValue();
    }

    private ArrayList<Student> mutateStudentTimetable(ArrayList<Student> studentTimetable) {
        ArrayList<Student> mutated = new ArrayList<Student>(studentTimetable);
        int mutationTypeSelect = random.nextInt(100);
        if (mutationTypeSelect < 50) {
            changeElective(mutated);
        } else {
            swapPeriods(mutated);
        }
        return mutated;
    }

    private void changeElective(ArrayList<Student> studentTimetable) {
        // TODO
        // pick random student
        // pick one of their random electives (use Data.compulsoryCourses)
        // change it to one of their alternates
    }

    private void swapPeriods(ArrayList<Student> studentTimetable){
        for (Student c: studentTimetable){
            ClassInfo [] timetable=c.getTimetable();
            for (int i=0; i<timetable.length;i++){
                if (Data.coursesToClassInfo.get(timetable[i].getCourse()).size() != 1) {
                    for (int j=i;j<timetable.length;j++){
                        if (Data.coursesToClassInfo.get(timetable[j].getCourse()).size() != 1){
                            if (courseToPeriods.get(timetable[i].getCourse()).contains(timetable[j].getTimeslot()) && courseToPeriods.get(timetable[j].getCourse()).contains(timetable[i].getTimeslot())){
                               //remove student from classinfo
                               timetable[i].removeStudent(c.getStudentNumber());
                                timetable[j].removeStudent(c.getStudentNumber());
                                

                                 for (int x =0;x < Data.coursesToClassInfo.get(timetable[i].getCourse()).size();x++){
                                    if (timetable[j].getTimeslot() == Data.coursesToClassInfo.get(timetable[i].getCourse()).get(x).getTimeslot() && !Data.coursesToClassInfo.get(timetable[i].getCourse()).get(x).isFull()){
                                        
                                    }
                                 }


        //check if class is full

                                
                                //change both student class info, and insdie the classinfo object
                                ClassInfo placeHolder= timetable[j];
                                timetable[j]=timetable[i];
                                timetable[i]=placeHolder;
                                




                            }
                        }
                    }
                }
            }

            c.setTimetable(timetable);
        }
    }
}