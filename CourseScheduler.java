import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class CourseScheduler {

    private ArrayList<ClassInfo> timetable;
    private static Random random = new Random();
    static HashMap<String, Integer> studentCount = new HashMap<String, Integer>(); // Number of students in each course
    static HashMap<String, Integer> courseCount = new HashMap<String, Integer>(); // Number of courses running
    HashMap<Integer, int[]> roomTime = new HashMap<Integer, int[]>();
    HashMap<Integer, int[]> teacherTime = new HashMap<Integer, int[]>(); // what are these for?

    public CourseScheduler(SpecialCourseScheduler s) {
//            - Determines # of each course required using Student map
//            - Calls method in SpecialCourseScheduler to schedule special courses
//              ArrayList<ClassInfo> timetable = new ArrayList<ClassInfo>();
        studentCount = countStudents();
        courseCount = coursesRunning();
        System.out.println(courseCount);
    }

    //        - Fills in all non-special needed courses randomly
    //        - Then performs the main algorithm which calls fitness function & mutation 
    //        - Fitness function & mutation in private methods
    public ArrayList<ClassInfo> getNewTimetable(){
        createInitialTimetable();
        while(timetableFitness() != 0){
            mutateTimetable();
        }
        return timetable;
    }

    private static void createInitialTimetable(){
        
    }

    
    private HashMap<String, Integer> coursesRunning(){
        double threshold = 0.50;
        HashMap<String, Integer> courseCount = new HashMap<String,Integer>();
        for(String c: courseCount.keySet()){
            double maxClassSize = Data.courseMap.get(c).getClassSize();
            int numberCourses = (int) Math.floor(studentCount.get(c)/maxClassSize);
  
            double additionalCourse = (studentCount.get(c)/maxClassSize) - numberCourses  / 100;
            if(additionalCourse > threshold){
                numberCourses++;
            }
            courseCount.put(c, numberCourses);
            
        }
        return courseCount;
    }


    private HashMap<String, Integer> countStudents(){
        HashMap<String, Integer> courseCount = new HashMap<String, Integer>();
        for(Student s: Data.studentMap.values()){
            String[] temp = s.getCourseChoices();
            
            for(int i = 0; i < temp.length;i++){
                if(temp[i].equals("")){
                    continue;
                }
                if (courseCount.containsKey(temp[i])){
                    courseCount.put(temp[i], courseCount.get(temp[i]) +1);
                }else{
                    courseCount.put(temp[i],1);
                }
            }
        }
        return courseCount;
    }

    public int timetableFitness(){
        int score=0;
        //dupliace time slots
        for (ClassInfo x: timetable){
            int add1=roomDuplicates(x);
            score +=add1;
            if (add1==0){
                int time[]= new int [roomTime.get(x.getRoom()).length+1];
                time [roomTime.get(x.getRoom()).length] = x.getTimeslot();
                roomTime.put(x.getRoom(), time);
            }

            int add2=teacherDuplicates(x);
            score+=add2;
            if (add2==0){
                int time[]= new int [teacherTime.get(x.getTeacher()).length+1];
                time [teacherTime.get(x.getTeacher()).length] = x.getTimeslot();
                teacherTime.put(x.getTeacher(), time);
            }

            score+=teacherTimeSlots();
        }

        
        return score;
     }

    public int teacherTimeSlots(){
        //teachers less or more than 6
        //TODO just check the number inside the array 3 on each semester
        for (int[] x :teacherTime.values()){
            if (x.length < 6 || x.length >6){
                return 10;
            }
            
            for (int j: x){
            }  
        }

        return 0;
    }


     public int roomDuplicates(ClassInfo x){
        if (roomTime.containsKey(x.getRoom())){
            int time[]= new int [roomTime.get(x.getRoom()).length+1];
                for (int i=0; i< roomTime.get(x.getRoom()).length;i++){
                    time[i]= roomTime.get(x.getRoom())[i];

                    if (roomTime.get(x.getRoom())[i] ==x.getTimeslot()){
                        return 10;
                    }
                }       
        }else{
            int time[]= {x.getTimeslot()};
            roomTime.put(x.getRoom(), time);
        }
        return 0;
    }

    public int teacherDuplicates(ClassInfo x){
        if (teacherTime.containsKey(x.getTeacher())){
            int time[]= new int [teacherTime.get(x.getTeacher()).length+1];
                for (int i=0; i< teacherTime.get(x.getTeacher()).length;i++){
                    time[i]= teacherTime.get(x.getTeacher())[i];

                    if (teacherTime.get(x.getTeacher())[i] ==x.getTimeslot()){
                        return 10;
                    }
                }
        }else{
            int time[]= {x.getTimeslot()};
            teacherTime.put(x.getTeacher(), time);
        }  
        return 0;
    }

    public void mutateTimetable() {
        int mutationTypeSelect = random.nextInt(100);
        if(mutationTypeSelect < 40){
            swapClassTimeslots();
        }else if(mutationTypeSelect < 55){
            swapRoom();
        }else if(mutationTypeSelect < 70){
            moveRoom();
        }else if(mutationTypeSelect < 85){
            swapTeacher();
        }else{
            changeTeacher();
        }
    }
    // swap the timeslots of two random classes
    private void swapClassTimeslots(){
        // choose 2 classes that does not require special consideration
        int class1Index = random.nextInt(timetable.size()); 
        int class2Index = random.nextInt(timetable.size()); 
        while(class1Index == class2Index || timetable.get(class1Index).isFixed() == true || timetable.get(class2Index).isFixed() == true){
            class1Index = random.nextInt(timetable.size());
            class2Index = random.nextInt(timetable.size());
        }
        int swap = timetable.get(class1Index).getTimeslot();
        timetable.get(class1Index).setTimeslot(timetable.get(class2Index).getTimeslot());
        timetable.get(class2Index).setTimeslot(swap);

        // might need to check teacherTime??
    }
    // swap the room of two random classes
    private void swapRoom(){
        int class1Index = random.nextInt(timetable.size()); 
        int class2Index = random.nextInt(timetable.size()); 
        while(class1Index == class2Index || timetable.get(class1Index).isFixed() == true || timetable.get(class2Index).isFixed() == true){
            class1Index = random.nextInt(timetable.size());
            class2Index = random.nextInt(timetable.size());
        }
        int swap = timetable.get(class1Index).getRoom();
        timetable.get(class1Index).setRoom(timetable.get(class2Index).getRoom());
        timetable.get(class2Index).setRoom(swap);
    }
    // move a random class to a random other room that is suitable
    private void moveRoom(){
        int classIndex = random.nextInt(timetable.size());
        while(timetable.get(classIndex).isFixed() == true){
            classIndex = random.nextInt(timetable.size());
        }

        // where do I check the avaliable rooms?
    }
    // Switch two teachers, ensuring both are still qualified for the course they are teaching
    private void swapTeacher(){
        int class1Index = random.nextInt(timetable.size()); 
        int class2Index = random.nextInt(timetable.size()); 
        while(class1Index == class2Index || timetable.get(class1Index).isFixed() == true || timetable.get(class2Index).isFixed() == true){
            class1Index = random.nextInt(timetable.size());
            class2Index = random.nextInt(timetable.size());
        }

        // where do I check teacher qulification

        // int swap = timetable.get(class1Index).getTeacher();
        // timetable.get(class1Index).setRoom(timetable.get(class2Index).getTeacher());
        // timetable.get(class2Index).setRoom(swap);
    }

    private void changeTeacher(){
        // change the teacher for a class to another random teacher that is qualified

        //where do I check the teachers? lol
    }
}

