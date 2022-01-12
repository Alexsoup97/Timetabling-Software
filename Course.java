import java.util.ArrayList;

public class Course {

    private String code;
    private String title;
    private int grade;
    private String type;
    private double credit; // for 0.5 credits?
    private int classSize;
    private ArrayList<String> corequisites = new ArrayList<String>();
    // add periods? for conflicts

    public Course(String code, String title, int grade, String type, double credit, int classSize, ArrayList<String> corequisites){
        this.code = code;
        this.title = title;
        this.grade = grade;
        this.type = type;
        this.credit = credit;
        this.classSize = classSize;
        this.corequisites = corequisites;
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
    public String getCorequisite(int index){
        return this.corequisites.get(index);
    }
// -----------------------------------------------------------------------------------------
}
