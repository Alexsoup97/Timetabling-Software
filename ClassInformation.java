import java.util.ArrayList;

class ClassInformation {
    private int teacher; // I think we should have this as a String maybe like "1FRENCH/CS" meaning teacher 1 who can teach CS and FRENCH? 
    private int room; // room #
    private int timeslot; // integer from 1-8, 1-4 being sem 1 and 5-8 being sem 2
    private Course course; 
    private boolean fixed; // for courses that cannot be mutated (set period, etc.) 
    private ArrayList<Integer> students;

    public int getTeacher() {
        return this.teacher;
    }

    public void setTeacher(int teacher) {
        this.teacher = teacher;
    }

    public int getRoom() {
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

    public boolean isFixed() {
        return this.fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed= fixed;
    }
}
