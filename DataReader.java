import java.util.Scanner;
import java.io.File;
import java.util.HashMap;

public class DataReader{
    
    // public HashMap<Integer, String> getRooms() throws Exception{
                
    //     Scanner roomData = new Scanner (new File("RoomData.csv"));
    //     HashMap<Integer, String> rooms = new HashMap<Integer, String>();         
    //     roomData.nextLine();
    //     while(roomData.hasNext()){
    //         String [] currentLine = (roomData.nextLine().split(","));
    //         if (currentLine[0].contains("A")){
    //             currentLine[0]= currentLine[0].substring(0,currentLine[0].indexOf("A")) + 1;
    //         }else if (currentLine[0].contains("B")){
    //             currentLine[0]= currentLine[0].substring(0,currentLine[0].indexOf("B")) + 2;
    //         }else if (currentLine[0].contains("D")){
    //             currentLine[0]= currentLine[0].substring(0,currentLine[0].indexOf("D")) + 4;
    //         }else if (currentLine[0].contains("PB")){
    //             //TODO do we need plus one(check)
    //             currentLine[0]= currentLine[0].substring(currentLine[0].indexOf("PB")+1,currentLine[0].length());
    //         }
    //         int roomNumber = Integer.valueOf(currentLine[0]);
    //         String function = currentLine[1];
    //         rooms.put(roomNumber, function);
    //     }
    //     return rooms;
    // }

    //  public HashMap<String, Room> getRooms() throws Exception{
                
    //     Scanner roomData = new Scanner (new File("RoomData.csv"));
    //     HashMap<String, int[]> rooms = new HashMap<String, int[]>();         
    //     roomData.nextLine();
    //     while(roomData.hasNext()){
    //         String [] currentLine = (roomData.nextLine().split(","));
            
    //         if (currentLine[0].contains("A")){
    //             currentLine[0]= currentLine[0].substring(0,currentLine[0].indexOf("A")) + 1;
    //         }else if (currentLine[0].contains("B")){
    //             currentLine[0]= currentLine[0].substring(0,currentLine[0].indexOf("B")) + 2;
    //         }else if (currentLine[0].contains("D")){
    //             currentLine[0]= currentLine[0].substring(0,currentLine[0].indexOf("D")) + 4;
    //         }else if (currentLine[0].contains("PB")){
    //             //TODO do we need plus one(check)
    //             currentLine[0]= currentLine[0].substring(currentLine[0].indexOf("PB")+1,currentLine[0].length());
    //         }

    //         if (rooms.containsKey(currentLine[1])){
    //             int roomNumber[]= new int [rooms.get(currentLine[1]).length+1];
    //             for (int i = 0; i < rooms.get(currentLine[1]).length; i++) {
    //                 roomNumber[i] = rooms.get(currentLine[1])[i];                
    //             }
    //             roomNumber[currentLine[1].length()]=Integer.valueOf(currentLine[0]);

    //             rooms.put(currentLine[1],roomNumber);
    //         }else{
    //             int roomNumber[] = { Integer.valueOf(currentLine[0]) };
    //             rooms.put(currentLine[1], roomNumber);
    //         }
    //     }
    //     return rooms;
    // }

    public HashMap<String, Room> getRooms() throws Exception{
                
        Scanner roomData = new Scanner (new File("RoomData.csv"));
        HashMap<String, Room> rooms = new HashMap<String, Room>();         
        roomData.nextLine();
        while(roomData.hasNext()){
            String [] currentLine = (roomData.nextLine().split(","));
            rooms.put(currentLine[0],new Room(currentLine[0],currentLine[1]));
        }
        return rooms;
    }
    
    public HashMap<String, String[]> getRoomType() throws Exception{
        Scanner roomData = new Scanner (new File("RoomType.csv"));
        HashMap<String, String[]> roomTypes = new HashMap<String, String[]>();         
        roomData.nextLine();
        while(roomData.hasNext()){
            String [] currentLine = (roomData.nextLine().split(","));
            String room = currentLine[0];
            String [] courses = new String[currentLine.length-1];

            for(int i = 1; i < currentLine.length;i++){
                courses[i-1] = currentLine[i]; 
            }
            roomTypes.put(room,courses);
        }
        return roomTypes;
    }
    

    public HashMap<String, Course> getCourses() throws Exception{
        Scanner courseData = new Scanner(new File("CourseData.csv"));
        HashMap<String,Course> courses = new HashMap<String,Course>();
        courseData.nextLine();
        while(courseData.hasNext()){
            String[] currentLine = removeCommas(courseData.nextLine()).split("`");    
            String courseCode = currentLine[0];
            String courseTitle = currentLine[1];
            int grade = Integer.parseInt(currentLine[2]);
            String type = currentLine[3];
            int maxSize = Integer.parseInt(currentLine[8]);
            courses.put(courseCode, new Course(courseCode, courseTitle,grade,type, maxSize));
           
        }
        return courses;
    }

    public HashMap<Integer, Student> getStudents(){
        Scanner studentData = null;
        try{ 
            studentData = new Scanner(new File("StudentDataObfuscated.csv"));
        }catch(Exception e){}
        
       
        HashMap<Integer,Student> students = new HashMap<Integer,Student>();
        
        studentData.nextLine();
        while(studentData.hasNext()){
            
            String [] currentLine = removeCommas(studentData.nextLine()).split("`");
            
            String name = currentLine[0];
            char gender = currentLine[1].charAt(0); 
            int studentNumber= Integer.valueOf(currentLine[2]);
            int grade = Integer.valueOf(currentLine[6]);
        
            String courseChoices []= courseInputs(currentLine);
            String alternateChoices []= alternateInputs(currentLine);
            students.put(studentNumber, new Student(name, gender, studentNumber, grade, courseChoices,alternateChoices));
        }
        studentData.close();
        return students;
    }


    public static String [] courseInputs(String [] currentLine){
        String courseChoices []= new String [11]; 
        for (int i =0; i <11; i++){
            courseChoices[i]= currentLine[(3*i)+7];
        }
        return courseChoices;
    }


     public static String [] alternateInputs(String [] currentLine){
        String alternateChoices []= new String [3]; 
        for (int i =0; i <3; i++){
            //alternateChoices[i]= currentLine[(3*i)+42];
            // if (alternateCourses.containsKey(alternateChoices[i])){
            //     alternateCourses.put(alternateChoices[i], alternateCourses.get(alternateChoices[i]) +1);
            // }else{
            //     alternateCourses.put(alternateChoices[i],1);
            // }
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