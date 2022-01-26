import java.util.ArrayList;
import java.util.HashMap;
/**
 * Courses that have special exceptions
 * @author Alex, Samson
 * @version 1.0
 */
public class SpecialCourseScheduler { 
    private final boolean FIXED = true;
    private final String CO_OP = "COP";
    private final String LIBRARY_ROOM = "2001";
    private final int PERIODS = Data.NUM_PERIODS / 2;
    private final int SEMESTER1 = 0;
    private final int SEMESTER2 = 4;
    private int semester = 0;
    private ArrayList<ClassInfo> specialCourses = new ArrayList<ClassInfo>();

    /**
     * puts special courses in speicifed semster
     * @author Samson
     * @param numberOfClasses course code and the number of class running
     * @return arraylist of special courses
     */
    //param - course code, to number of classes of that course code
    public ArrayList<ClassInfo> getSpecialCourseTimetable(HashMap<String, Integer> numberOfClasses) {
        int timeslot = 0;
        String room = "";
        for (String s : numberOfClasses.keySet()) {
            if (Data.specialCourses.containsKey(s)){
                if(Data.specialCourses.get(s) == 1){
                    semester = SEMESTER1;
                }else{
                    semester=SEMESTER2;
                }
                addRooms(s,numberOfClasses,semester,Data.courseMap.get(s).getRoomType());
            } else if (s.contains(CO_OP)) {
                for (int i = 0; i < numberOfClasses.get(s); i++) {
                    //co-op course numbers are really weird
                    room = LIBRARY_ROOM;
                    if (s.contains("1")) {
                        timeslot = 1;
                    } else if (s.contains("2")) {
                        timeslot = 3;
                    } else if (s.contains("4")) {
                        timeslot = 5;
                    } else if (s.contains("5")) {
                        timeslot = 7;
                    } else {
                        timeslot = 1;
                    }
                    Data.roomMap.get(room).setUnavailable(timeslot);
                    specialCourses.add(new ClassInfo(room, timeslot, s, FIXED));
                }
            }
        }
        return specialCourses;
    }

    /**
     * gives the course a room
     * @author Samson
     * @param s courseCode
     * @param numberOfClasses HashMap of course code and number of courses running
     * @param semester semester number
     * @param roomtype course roomtype
     */
    public void addRooms(String s, HashMap<String, Integer> numberOfClasses,int semester,String roomtype){
        String room="";
        int time=0;
        int timeslot=0;
        for (int i = 0; i < numberOfClasses.get(s); i++) {
            do{
                time++;
                room = Data.roomTypeMap.get(roomtype).get(time/PERIODS);
            }while(!Data.roomMap.get(room).getAvailability(time/PERIODS));
            timeslot = time % PERIODS  + semester;
            Data.roomMap.get(room).setUnavailable(timeslot);
            specialCourses.add(new ClassInfo(room,timeslot,s,FIXED));
        } 
    }
}