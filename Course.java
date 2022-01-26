import java.util.Map;

/**
 * [Course.java]
 * Class for storing information for each course
 * @version 1.0 Jan 5, 2022
 */
public class Course {
    private String code;
    private String title;
    private int grade;
    private String level;
    private int classSize;
    private String roomType;
    
    /**
     * Creates a new Course object
     * @param code the course code as a string
     * @param title the name of this course
     * @param grade the grade level of this course
     * @param level the level of this course (e.g. applied, academic, etc.)
     * @param classSize the class size cap for this course
     */
    public Course(String code, String title, int grade, String level,int classSize){
        this.code = code;
        this.title = title;
        this.grade = grade;
        this.level = level;
        this.classSize = classSize;
        this.roomType = findRoomType();

    }

    /**
     * Finds the room type for this course 
     * @return the room type for this course
     */
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
