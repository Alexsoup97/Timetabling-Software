import java.util.ArrayList;
public class test {

    public static void main(String args[]){
      ArrayList a  = new ArrayList<String>();
      a.add("1");
      a.add("2");
      a.add("3");
      a.add("4");
      a.add("5");
      a.add("6");
      a.add("7");
      a.add("8");
    
     
      findPossibleTimetable(a, a.size(), a.size());
      System.out.println(counter);
      
    }
static int counter = 0;


    public static void  findPossibleTimetable(ArrayList<String> courses, int size, int n){
       
        if (size == 1 ){
           counter++;
        }
           
 
        for (int i = 0; i < size; i++) {
            findPossibleTimetable(courses, size - 1, n);
 
            // if size is odd, swap 0th i.e (first) and
            // (size-1)th i.e (last) element
            if (size % 2 == 1) {
                String temp = courses.get(0);
                courses.set(0, courses.get(size-1)); 
                courses.set(size-1, temp);
         
            }
 
            // If size is even, swap ith
            // and (size-1)th i.e last element
            else {
                String temp = courses.get(i);
                courses.set(i, courses.get(size-1)); 
                courses.set(size-1, temp);
            }
        }
    }
}
