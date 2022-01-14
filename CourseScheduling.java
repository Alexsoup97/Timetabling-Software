import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class CourseScheduling {

    public void generateInitalTimetable() {
        ArrayList<ClassInformation> timetable = new ArrayList<ClassInformation>();

    }

}

class MasterTimetable {
    ArrayList<ClassInformation> timetable = new ArrayList<ClassInformation>();
    static Random random = new Random();

    public int timetableFitness(){
        int score=0;
        HashMap<Integer, int[]> roomTime = new HashMap<Integer, int[]>();
        HashMap<Integer, int[]> teacherTime = new HashMap<Integer, int[]>();


        for (ClassInformation x: timetable){
            if (roomTime.containsKey(x.getRoom())){

                    for (int i=0; i< roomTime.get(x.getRoom()).length;i++){
                        // roomTime.get(x.getRoom()) [i] =
                    }
                
            }
            
            
        }

        
        return score;
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

    private void swapClassTimeslots(){
        // swap the timeslots of two random classes
        // need to check required period (APs, etc.)
        int class1Index = random.nextInt(timetable.size());
        int class2Index = random.nextInt(timetable.size());  

        Integer swaping = timetable.get(class1Index).getTimeslot();
        timetable.get(class1Index).setRoom(timetable.get(class2Index).getTimeslot());
        timetable.get(class2Index).setRoom(swaping);
    }

    private void swapRoom(){
        // swap the room of two random classes
        // need to check special rooms later
        int class1Index = random.nextInt(timetable.size());
        int class2Index = random.nextInt(timetable.size());

        Integer swaping = timetable.get(class1Index).getRoom();
        timetable.get(class1Index).setRoom(timetable.get(class2Index).getRoom());
        timetable.get(class2Index).setRoom(swaping);
    }

    private void moveRoom(){
        // move a random class to a random other room that is suitable
    }

    private void swapTeacher(){
        // Switch two teachers, ensuring both are still qualified for the course they are teaching
        // check teacher qualification later
        int class1Index = random.nextInt(timetable.size());
        int class2Index = random.nextInt(timetable.size());

        Integer swaping = timetable.get(class1Index).getTeacher();
        timetable.get(class1Index).setRoom(timetable.get(class2Index).getTeacher());
        timetable.get(class2Index).setRoom(swaping);
    }

    private void changeTeacher(){
        // change the teacher for a class to another random teacher that is qualified
    }
}

class ClassInformation {
    int teacher; // I think we should have this as a String maybe like "1FRENCH/CS" meaning teacher 1 who can teach CS and FRENCH? 
    Integer room; // why is this not primitive?
    int timeslot;
    Course course;

    public int getTeacher() {
        return this.teacher;
    }

    public void setTeacher(int teacher) {
        this.teacher = teacher;
    }

    public Integer getRoom() {
        return this.room;
    }

    public void setRoom(int room) {
        this.room = room;
    }

    public int getTimeslot() {
        return this.timeslot;
    }

    public void setTimeslot(int timeslot) {
        this.timeslot = timeslot;
    }

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
