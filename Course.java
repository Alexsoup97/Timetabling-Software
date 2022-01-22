import java.util.Map;
public class Course {

    private String code;
    private String title;
    private int grade;
    private String level;
    private int classSize;
    private String roomType;
    
    public Course(String code, String title, int grade, String level,int classSize){
        this.code = code;
        this.title = title;
        this.grade = grade;
        this.level = level;
        this.classSize = classSize;
        this.roomType = findRoomType();

    }

    private String findRoomType(){
        for(Map.Entry<String, String[]> map: Data.roomTypeCourses.entrySet()){
            for(int i = 0; i < map.getValue().length; i++){
                if(map.getValue()[i].equals(this.code)){
                    return map.getKey();
                }
            }
        }
        return "classroom";
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
    public String getLevel(){
        return this.level;
    }
    public int getClassSize(){
        return this.classSize;
    }
    public String getRoomType(){
        return this.roomType;
    }
// -----------------------------------------------------------------------------------------
    @Override
    public String toString() {
        return code+", "+title+", "+roomType;
    }

}
