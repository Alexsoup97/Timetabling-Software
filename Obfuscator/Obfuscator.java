import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.util.Random;

public class Obfuscator{

    public static void main(String[] args) throws Exception{
        Random random = new Random();
        
        Scanner input = new Scanner(new File("StudentData.csv"));
        ArrayList<String[]> studentData = new ArrayList<String[]>();

        while(input.hasNext()){
          studentData.add(input.nextLine().split(","));
        }
        input.close();

        Scanner randomNames = new Scanner(new File("randomNames.txt"));
        ArrayList<String> names = new ArrayList<String>();
        while(randomNames.hasNext()){
            names.add(randomNames.nextLine());
        }
        
        PrintWriter keyFile = new PrintWriter("key.csv");
        
        for(int i = 1; i < studentData.size();i++){
            String firstName = names.get(random.nextInt(names.size()));
            String lastName = names.get(random.nextInt(names.size()));
            keyFile.print(studentData.get(i)[1].substring(0,studentData.get(i)[1].length() -1 ) +  " " + studentData.get(i)[0].substring(1) + "," + firstName + " " + lastName);
            studentData.get(i)[0] =  "\"" + lastName;
            studentData.get(i)[1] = firstName + "\"";
            int studentNumber  =  Integer.parseInt(studentData.get(i)[3]);
            keyFile.print("," + studentNumber);
            studentNumber = random.nextInt(99999999) + 100000000;
            keyFile.print("," + studentNumber);
            studentData.get(i)[3] = Integer.toString(studentNumber);
            studentData.get(i)[4] = studentNumber + "@gapps.yrdsb.ca";
            
        }
        keyFile.close();
            
            
        PrintWriter newFile = new PrintWriter("obfuscated.csv");
        for(String[] s: studentData){
            for(int i = 0; i < s.length; i++){
                if(i == s.length -1){
                    newFile.print(s[i]);
                }else{
                    newFile.print(s[i] + ",");
                }
                
            }
            newFile.println();
        }
        newFile.close();
    }
}