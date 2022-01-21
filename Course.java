import java.util.ArrayList;
import java.util.Map;

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
        for(Map.Entry<String, String[]> map: Data.typesOfRooms.entrySet()){
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
