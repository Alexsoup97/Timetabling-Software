import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.util.HashMap;

public class Main{
        private static HashMap<String, Integer> course = new HashMap<String, Integer>();
        private static HashMap<String, Integer> alternateCourses = new HashMap<String, Integer>();
        private static Course courseData = new Course();


    public static void main(String[] args) throws Exception{

        Scanner studentData = new Scanner(new File("StudentDataObfuscated.csv"));
        Scanner courseData = new Scanner(new File("CourseData.csv"));
        ArrayList<Student> students = new ArrayList<Student>();

        courseData.nextLine();
        while(courseData.hasNext()){
            String[] currentLine = removeCommas(courseData.nextLine()).split("`");    
            String courseCode = currentLine[0];
            String courseTitle = currentLine[1];
            int grade = Integer.parseInt(currentLine[2]);
            String type = currentLine[3];
            //int credit  = Integer.parseInt(currentLine[4]);
            int credit = 1;
            String coreq = currentLine[6];
           
            int maxSize = Integer.parseInt(currentLine[8]);

            new Course(courseCode, courseTitle,grade,type,credit,coreq, maxSize);
            // System.out.println(courseCode);
            // course.put(courseCode, 0);
        }
        
        studentData.nextLine();
        while(studentData.hasNext()){
            String [] currentLine = removeCommas(studentData.nextLine()).split("`");
            String name = currentLine[0];
            char gender = currentLine[1].charAt(0); 
            int studentNumber= Integer.valueOf(currentLine[2]);
            String email= currentLine[3];
            int grade = Integer.valueOf(currentLine[6]);
            String courseChoices []= courseInputs(currentLine);
           //String alternateChoices []= alternateInputs(currentLine);
            String alternateChoices[] = new String[0];
            students.add(new Student(name, gender, studentNumber, email, grade, courseChoices,alternateChoices));
            
        }
        coursesRunning();
    }


    public static String [] courseInputs(String [] currentLine){
        String courseChoices []= new String [11]; 
        for (int i =0; i <11; i++){
                courseChoices[i]= currentLine[(3*i)+7];
                if(courseChoices[i].equals("0")){
                    for(int j = 0; j < currentLine.length;j++){
                        System.out.println(currentLine[j] + " ");

                    }
                    System.out.println();
                }
                if (course.containsKey(courseChoices[i])){
                    course.put(courseChoices[i], course.get(courseChoices[i]) +1);
                }else{
                    course.put(courseChoices[i],1);
                }
        }
        return courseChoices;
    }

    public static void coursesRunning(){
        double threshold = 0.50;
       
        for(String c: course.keySet()){
            double maxClassSize = courseData.getCourse(c).getClassSize();
            int numberCourses = (int) Math.floor(course.get(c)/maxClassSize);
  
            double additionalCourse = (course.get(c)/maxClassSize) - numberCourses  / 100;
             if(c.equals("SBI4UE")){
                 System.out.println(additionalCourse);
             }
            
            if(additionalCourse > threshold){
                numberCourses++;
           
            }

            System.out.println(c + ":" + numberCourses);
        }

    }

     public static String [] alternateInputs(String [] currentLine){
        String alternateChoices []= new String [3]; 
        for (int i =0; i <3; i++){
                alternateChoices[i]= currentLine[(3*i)+42];
                if (alternateCourses.containsKey(alternateChoices[i])){
                    alternateCourses.put(alternateChoices[i], alternateCourses.get(alternateChoices[i]) +1);
                }else{
                    alternateCourses.put(alternateChoices[i],1);
                }
        }
        return alternateChoices;
    }

    public static String removeCommas(String line){
        boolean remove = false;
            for(int i = 0; i <line.length();i ++){
                if(line.charAt(i) == '"' && remove){
                    remove = false;
                }else if(line.charAt(i) == '"' && !remove){
                    remove = true;
                }
                if(line.charAt(i) == ',' && !remove){
                    line = line.substring(0, i) + "`" + line.substring(i+1);
                }
            }
        return line;
    }
}