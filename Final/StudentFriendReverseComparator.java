/* StudentFriendReverseComparator.java  
 * @author Dhruv, Alex, Suyu
 * @version October 11, 2021
 * Compares students based on their friends
 */

import java.util.Comparator;

/**
* StudentFriendReverseComparator
* This class compares students based on their friends
*/
public class StudentFriendReverseComparator implements Comparator<Student> {
    
    /**
    * compare
    * Compares the students
    * @param s1 (Student) - student1
    * @param s2 (ArrayList<Student>) - student2
    * @return int - if student1's # of friends is less than student2's, return 1, else -1, and if equal, return 0
    */
    @Override
    public int compare(Student s1, Student s2) {
        int s1Friends = getNumFriends(s1);
        int s2Friends = getNumFriends(s2);
        if (s1Friends < s2Friends) {
            return 1;
        } else if (s1Friends == s2Friends) {
            return 0;
        } else {
            return -1;
        }
    }
    
    /**
    * getNumFriends
    * Gets the number of friends
    * @param s (Student) - student to get the number of friends of
    * @return numFriends (int) - the number of that student's friends
    */
    private int getNumFriends(Student s) {
        int[] friends = s.getFriendPreferences();
        int numFriends = friends.length;
        for (int i = 0; i < friends.length; i++) {
            if (friends[i] == -1) {
                numFriends--;
            }
        }
        return numFriends;
    }
}
