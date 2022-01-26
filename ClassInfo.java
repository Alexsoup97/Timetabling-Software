import java.util.ArrayList;

/**
 * [ClassInfo.java]
 * Class for storing information for each class (i.e. each section)
 * @version 1.0 Jan 5, 2022
 */
class ClassInfo {
    private String room; // room #
    private int timeslot; // integer from 1-8, 1-4 being sem 1 and 5-8 being sem 2
    private String course;
    private boolean fixed; // for courses that cannot be mutated (set period, etc.)
    private ArrayList<Integer> students= new ArrayList<Integer>();;

    /**
     * Makes a new ClassInfo object with no students
     * @param room the room of this class
     * @param timeslot the timeslot of this class
     * @param course the course of this class
     * @param fixed marker variable for whether or not this class is a special course and should thus not be changed later
     */
    public ClassInfo(String room, int timeslot, String course, boolean fixed) {
        this.room = room;
        this.timeslot = timeslot;
        this.course = course;
        this.fixed = fixed;
    }

    /**
     * gets the percentage full the class is
     * @author Samson
     * @return how full this class is in percent
     */
    public int getPercentageFull(){
        int maxClassSize=30;
        if(Data.courseMap.containsKey(course)){
            maxClassSize = Data.courseMap.get(course).getClassSize();
        } 
        return (int) Math.round(100*(students.size()*1.0/maxClassSize));
    }

    /**
     * Gets if this class is full
     * @author Suyu
     * @return true if this section is full, else otherwise
     */
    public boolean isFull(){
        int maxClassSize = 30;
        if(course.equals("EMPTY") || course.equals("SPARE"))
            maxClassSize = Integer.MAX_VALUE;
        if(Data.courseMap.containsKey(course))
            maxClassSize = Data.courseMap.get(course).getClassSize();
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
