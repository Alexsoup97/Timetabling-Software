import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

public class Main{
    public static void main(String[] args) throws Exception{

        Scanner studentData = new Scanner(new File("StudentDataObfuscated.csv"));
        ArrayList<Student> students = new ArrayList<Student>();
        while(studentData.hasNext()){
            String [] currentLine = studentData.nextLine().split(",");
            String name = currentLine[0] +"," +currentLine[1];
            char gender = currentLine[2].charAt(0); 
            int studentNumber= Integer.valueOf(currentLine[3]);
            String email= currentLine[4];
            int grade = Integer.valueOf(currentLine[7]);

            String courseChoices []= new String [11]; 
            for (int i =0; i <11; i++){
                courseChoices[i]= currentLine[(3*i)+8];
            }
            students.add(new Student(name, gender, studentNumber, email, grade, courseChoices));
        }
        
    }
}