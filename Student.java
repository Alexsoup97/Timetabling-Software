import java.util.ArrayList;

public class Student {

    private String name;
    private char gender;
    private int id;
    private int grade;
    private ArrayList<String> courseChoices = new ArrayList<String>(11);
    private ArrayList<String> alternateChoices = new ArrayList<String>(3);
    private String[] timetable = new String[9]; // final timetable

    public Student(String name, char gender, int studentNumber, int grade, ArrayList<String> courseChoices, ArrayList<String> alternateChoices) {
        this.name = name;
        this.gender = gender;
        this.id = studentNumber;
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

    public boolean hasCourse(String course) {
        for (int i = 0; i < courseChoices.size(); i++) {
            if (courseChoices.get(i).equals(course)) {
                courseChoices.set(i, courseChoices.get(i) + "COMPLETE");
                return true;
            }

        }

        return false;
    }

    public int correctCourses() {
        int counter = 0;
        for (int i = 0; i < courseChoices.size(); i++) {
            if (courseChoices.get(i).endsWith("COMPLETE")) {
                counter++;
            }
        }
        return counter;
    }

    public ArrayList<String> getAlternateChoices() {
        return this.alternateChoices;
    }

    public String[] getTimetable() {
        return this.timetable;
    }

    public void setTimetable(String timetable[]) {
        this.timetable = timetable;
    }

    public void fillTimeslot(String course, int timeslot) {
        timetable[timeslot] = course;
    }

    public boolean checkTimeslot(int timeslot) {
        if (timetable[timeslot] == null) {
            return true;
        } else {
            return false;
        }
    }

    // -----------------------------------------------------------------------------------------
    @Override
    public String toString(){
        return name+", "+id;
    }

}
