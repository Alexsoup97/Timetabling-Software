import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class CourseScheduling {

    ArrayList<ClassInformation> timetable;
    static Random random = new Random();
    HashMap<Integer, int[]> roomTime = new HashMap<Integer, int[]>();
    HashMap<Integer, int[]> teacherTime = new HashMap<Integer, int[]>();

    // ArrayList<Teacher> teacher = new ArrayList<Teacher>();
    // ArrayList<Room> room = new ArrayList<Room>();


    public CourseScheduling() {
        //ArrayList<> timetable = new ArrayList<ClassInformation>();
        System.out.println(count());

    }

    public HashMap<String, Integer> count(){
        HashMap<String, Integer> courseCount = new HashMap<String, Integer>();
        for(Student s: Main.StudentMap.values()){
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
        for (ClassInformation x: timetable){
            int add1=roomDuplicates(x);
            score = score+add1;
            if (add1==0){
                int time[]= new int [roomTime.get(x.getRoom()).length+1];
                time [roomTime.get(x.getRoom()).length] = x.getTimeslot();
                roomTime.put(x.getRoom(), time);
            }

            int add2=teacherDuplicates(x);
            score = score+add2;
            if (add2==0){
                int time[]= new int [teacherTime.get(x.getTeacher()).length+1];
                time [teacherTime.get(x.getTeacher()).length] = x.getTimeslot();
                teacherTime.put(x.getTeacher(), time);
            }
        }
        //teachers less or more than 6
        for (int[] x :teacherTime.values()){
            if (x.length < 6 || x.length >6){
                score+=10;
            }
        }
        return score;
     }

     public int roomDuplicates(ClassInformation x){
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

    public int teacherDuplicates(ClassInformation x){
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

