import java.util.ArrayList;

public class Course {

    private String code;
    private String title;
    private int grade;
    private String type;
    private double credit; // for 0.5 credits?
    private int classSize;
    private String corequisites;
    static private ArrayList<Course> courseList = new ArrayList<Course>();
    // add periods? for conflicts

    public Course(){

    }

    public Course(String code, String title, int grade, String type, int credit, String corequisites,int classSize){
        this.code = code;
        this.title = title;
        this.grade = grade;
        this.type = type;
        this.credit = credit;
        this.classSize = classSize;
        this.corequisites = corequisites;
        courseList.add(this);
    }


    

    public Course getCourse(String courseCode){
        for(Course c: courseList){
            if(c.getCode().equals(courseCode)){
                return c;
            }
        }
        return courseList.get(0);
    }
// -----------------------------------------------------------------------------------------
    // getters
    public String getCode(){
        return this.code;
    }
    public String getTitle(){
        return this.title;
    }
    public int getGrade(){
        return this.grade;
    }
    public String getType(){
        return this.type;
    }
    public double getCredit(){
        return this.credit;
    }
    public int getClassSize(){
        return this.classSize;
    }
    // public String getCorequisite(int index){
    //     return this.corequisites.get(index);
    // }
// -----------------------------------------------------------------------------------------
}
