import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.util.HashMap;

public class Decoder {

    public static void main(String[] args) throws Exception {

        Scanner input = new Scanner(new File("obfuscated.csv"));
        Scanner keyFile = new Scanner(new File("key.csv"));
        HashMap<String, String> key = new HashMap<String, String>();
        ArrayList<String[]> studentData = new ArrayList<String[]>();

        while (keyFile.hasNext()) {
            String[] temp = keyFile.nextLine().split(",");
            key.put(temp[1], temp[0]);
        }
        keyFile.close();
        System.out.println(key);

        while (input.hasNext()) {
            studentData.add(input.nextLine().split(","));
        }
        input.close();

        for (int i = 1; i < studentData.size(); i++) {
            String name = key.get(studentData.get(i)[1].substring(0,studentData.get(i)[1].length() -1 ) +  " " + studentData.get(i)[0].substring(1));
            String firstName = name.substring(0, name.indexOf(" "));
            String lastName = name.substring(name.indexOf(" "), name.length());

            studentData.get(i)[0] = "\"" + lastName;
            studentData.get(i)[1] = firstName + "\""; 
            int studentNumber = Integer.parseInt(studentData.get(i)[3]);

            studentNumber = studentNumber / 2 + 160283747;
            studentNumber = (studentNumber + 378271723) / 2;
            studentNumber = studentNumber / 3 + 231299368;

            studentData.get(i)[3] = Integer.toString(studentNumber);
            studentData.get(i)[4] = studentNumber + "@gapps.yrdsb.ca";

        }

        keyFile.close();

        PrintWriter newFile = new PrintWriter("original.csv");
        for (String[] s : studentData) {
            for (int i = 0; i < s.length; i++) {
                if (i == s.length - 1) {
                    newFile.print(s[i]);
                } else {
                    newFile.print(s[i] + ",");
                }

            }
            newFile.println();
        }
        newFile.close();
    }
}