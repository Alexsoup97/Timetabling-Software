import java.util.ArrayList;

public class Course {

    private String code;
    private String title;
    private int grade;
    private String type;
    private int classSize;
    private String roomType;
    
    public Course(String code, String title, int grade, String type,int classSize){
        this.code = code;
        this.title = title;
        this.grade = grade;
        this.type = type;
        this.classSize = classSize;
        this.roomType = findRoomType();
    }

    private String findRoomType(){
        
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
    public int getClassSize(){
        return this.classSize;
    }
    public String getRoomType(){
        return this.roomType;
    }
// -----------------------------------------------------------------------------------------
}
