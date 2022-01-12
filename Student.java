
public class Student {
    
    private String name;
    private char gender;
    private int studentNumber;
    private int grade;
    private String email;
    private String courseChoices []= new String [11]; // last 3 are alternatives
    private String timetable []=new String[9]; // final timetable
    

    public Student(String name, char gender, int studentNumber, String email,int grade, String courseChoices[]){
        this.name = name;
        this.gender = gender;
        this.studentNumber = studentNumber;
        this.email = email;
        this.courseChoices = courseChoices;
    }
// -----------------------------------------------------------------------------------------
    // getters
    
    
}
