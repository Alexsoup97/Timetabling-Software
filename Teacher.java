import java.util.ArrayList;

public class Teacher{
    private int id;
    private String name;
    private ArrayList<String> qualifications;
    private int coursesPerSemester;
    private int numberOfCourses; // how many courses are is already assigned

    Teacher(int id, String name, ArrayList<String> qualifications){
        this(id,name,qualifications,6);
    }

    Teacher(int id, String name, ArrayList<String> qualifications, int coursesPerSemester){
        this.id = id;
        this.name = name;
        this.qualifications = qualifications;
        this.coursesPerSemester = coursesPerSemester;
    }
    public int getNumberOfCourse(){
        return this.numberOfCourses;
    }
    public void setNumberOfCourse(int i){
        this.numberOfCourses = i;
    }
    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<String> getQualifications() {
        return this.qualifications;
    }

    public int getCoursesPerSemester() {
        return this.coursesPerSemester;
    }

}