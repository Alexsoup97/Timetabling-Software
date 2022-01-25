import java.util.ArrayList;

class ClassInfo {
    private String room; // room #
    private int timeslot; // integer from 1-8, 1-4 being sem 1 and 5-8 being sem 2
    private String course;
    private boolean fixed; // for courses that cannot be mutated (set period, etc.)
    private ArrayList<Integer> students= new ArrayList<Integer>();;

    public ClassInfo(String room, int timeslot, String course, boolean fixed) {
        this.room = room;
        this.timeslot = timeslot;
        this.course = course;
        this.fixed = fixed;
    }

    public ClassInfo(String room, int timeslot, String course, boolean fixed, ArrayList<Integer> students) {
        this.room = room;
        this.timeslot = timeslot;
        this.course = course;
        this.fixed = fixed;
        this.students = students;
    }

    public boolean isFull(){
        int maxClassSize = 30;
        if(Data.courseMap.containsKey(course)){
            maxClassSize = Data.courseMap.get(course).getClassSize();
        }   
        if(students.size() >= maxClassSize){
            return true;
        }
        return false;
    }

    public void addStudent(Integer student){
        students.add(student);
    }
    
    public void removeStudent(Integer student){
        students.remove(student);
    }

    public Integer getStudent(int index){
        return students.get(index);
    }

    public ArrayList<Integer> getStudents(){
        return students;
    }

    public String getRoom() {
        return this.room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public int getTimeslot() {
        return this.timeslot;
    }

    public void setTimeslot(int timeslot) {
        this.timeslot = timeslot;
    }

    public String getCourse() {
        return this.course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public boolean isFixed() {
        return this.fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    @Override
    public String toString() {
        return course + ", " + room + ", " + timeslot;
    }
}
