import java.util.ArrayList;
import java.util.HashMap;

public class SpecialCourseScheduler { 
    private final String CLASSROOM= "classroom";
    private final String SCIENCE= "science";
    private final String ART= "art";
    private final boolean FIXED = true;
    private final String CALCULAS= "MCV4U";
    private final String ADVANCED_FUNCTIONS= "MHF4U";
    private final String PHYSICS= "SPH4UE";
    private final String CHEMISTRY= "SCH4UE";
    private final String BIOLOGY= "SBI4UE";
    private final String ART_PROTFOLIO= "AEA4O";
    private final String CO_OP="COP";
    private final String LIBRARY_ROOM="2001";
    private final int PERIODS=4;
    private final int SEMESTER1=0;
    private final int SEMESTER2=4;

    private ArrayList<ClassInfo> specialCourses = new ArrayList<ClassInfo>();
    SpecialCourseScheduler() {
    }
    public ArrayList<ClassInfo> getSpecialCourseTimetable(HashMap<String, Integer> numberOfClasses) {
        int timeslot = 0;
        String room = "";
        for (String s : numberOfClasses.keySet()) {
            if (s.contains(ADVANCED_FUNCTIONS)) {
                addRooms(s,numberOfClasses,SEMESTER1,CLASSROOM);
            } else if (s.contains(CALCULAS)) {
                addRooms(s,numberOfClasses,SEMESTER2,CLASSROOM);
            } else if (s.equals(BIOLOGY) || s.equals(CHEMISTRY) || s.equals(PHYSICS)) {
                addRooms(s,numberOfClasses,SEMESTER1,SCIENCE);
            } else if (s.equals(ART_PROTFOLIO)) {
                addRooms(s,numberOfClasses,SEMESTER2,ART);
            } else if (s.contains(CO_OP)) {
                for (int i = 0; i < numberOfClasses.get(s); i++) {
                    room = LIBRARY_ROOM;
                    // semester1 period 1,2 - period 3,4
                    if (s.contains("1")) {
                        timeslot = 1;
                    } else if (s.contains("2")) {
                        timeslot = 3;
                        // semester2
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

            for(String course: Data.userSpecialCourses.keySet()){
                if(course.equals(s)){
                    addRooms(course, numberOfClasses, Data.userSpecialCourses.get(course),"PlaceHolder");
                }

            }
        }
        return specialCourses;
    }

    public void addRooms(String s, HashMap<String, Integer> numberOfClasses, ArrayList<Integer> timeSlot, String roomType){
        
        for(int i = 0; i < numberOfClasses.get(s); i++){
            String room = Data.roomTypeMap.get(roomType).get(i/timeSlot.size());
            specialCourses.add(new ClassInfo(room, timeSlot.get(i),s,FIXED);
            Data.roomMap.get(room).setUnavailable(timeSlot.get(i));
        }
    }

    public void addRooms(String s, HashMap<String, Integer> numberOfClasses,int semester,String roomtype){
        String room="";
        int time=0;
        int timeslot=0;
        for (int i = 0; i < numberOfClasses.get(s); i++) {
            time++;
            room = Data.roomTypeMap.get(roomtype).get(time / PERIODS);
            timeslot = time % PERIODS  + semester;
            Data.roomMap.get(room).setUnavailable(timeslot);
            specialCourses.add(new ClassInfo(room,timeslot,s,FIXED));
        }
    }
}
