import java.util.ArrayList;

public class Student {

    private String name;
    private char gender;
    private int id;
    private int grade;
    private ArrayList<String> courseChoices = new ArrayList<String>(11);
    private ArrayList<String> alternateChoices = new ArrayList<String>(3);
    private ClassInfo[] timetable = new ClassInfo[8]; // final timetable

    public Student(String name, char gender, int studentNumber, int grade, ArrayList<String> courseChoices, ArrayList<String> alternateChoices) {
        this.name = name;
        this.gender = gender;
        this.id = studentNumber;
        this.grade = grade;
        this.courseChoices = courseChoices;
        this.alternateChoices = alternateChoices;
    }

    public String getName() {
        return this.name;
    }

    public char getGender() {
        return this.gender;
    }

    public int getStudentNumber() {
        return this.id;
    }

    public int getGrade() {
        return this.grade;
    }

    public ArrayList<String> getCourseChoices() {
        return this.courseChoices;
    }

    public int getNumCourseChoices(){
        return courseChoices.size();
    }

    public int getNumSpares(){
        return Data.NUM_PERIODS - courseChoices.size();
    }

    /**
     * Returns the number of top choices, alternate choices, and courses in this
     * student's timetable
     * 
     * @author Suyu
     * @return An integer array, with the number of honored top choices in index 0,
     *         the number of honored alternate choices in index 1, and the total
     *         number of courses in their timetable in index 2
     */
    public int[] getNumChoicesReceived() {
        int[] choicesReceived = { 0, 0 ,0 };
        for (ClassInfo course : timetable) {
            if (course != null) {
                choicesReceived[2]++;
                if (courseChoices.contains(course.getCourse()))
                    choicesReceived[0]++;
                else if (alternateChoices.contains(course.getCourse()))
                    choicesReceived[1]++;
            }
        }

        return choicesReceived;
    }

    public boolean isTimetableComplete(){
        for(ClassInfo c:timetable){
            if(c.getCourse().equals("EMPTY")){
                return false;
            }
        }
        return true;
    }

    public boolean isTimeslotAvailable(int timeslot) {
        if (timetable[timeslot] == null)
            return true;
        return false;
    }

    public ArrayList<String> getAlternateChoices() {
        return this.alternateChoices;
    }

    public void swapTimeTable(ClassInfo c1,int index1, ClassInfo c2, int index2){
        timetable[index1]=c1;
        timetable[index2]=c2;
    }

    public ClassInfo [] getTimetable() {
        return this.timetable;
    }

    public void setTimetable(ClassInfo timetable[]) {
        this.timetable = timetable;
    }

    public void addToTimetable(ClassInfo section) {
        timetable[section.getTimeslot()] = section;
    }

    // -----------------------------------------------------------------------------------------
    @Override
    public String toString(){
        return name+", "+id;
    }

}
