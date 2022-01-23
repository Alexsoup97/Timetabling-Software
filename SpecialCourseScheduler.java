import java.util.ArrayList;
import java.util.HashMap;

public class SpecialCourseScheduler { 

    SpecialCourseScheduler() {
    }

    public ArrayList<ClassInfo> getSpecialCourseTimetable(HashMap<String, Integer> numberOfClasses) {
        ArrayList<ClassInfo> specialCourses = new ArrayList<ClassInfo>();
        boolean fixed = true;
        int timeslot = 0;
        String room = "";

        for (String s : numberOfClasses.keySet()) {
            if (s.equals("MHF4U1") || s.equals("MHF4UE")) {
            specialCourses.add(addRooms(s,numberOfClasses,0,"classroom"));
                // calculas
            } else if (s.contains("MCV4U1") || s.contains("MCV4UE")) {
                specialCourses.add(addRooms(s,numberOfClasses,4,"classroom"));
                // Ap Sciences
            } else if (s.contains("SBI4UE") || s.contains("SCH4UE") || s.contains("SPH4UE")) {
                specialCourses.add(addRooms(s,numberOfClasses,0,"science"));
                // art protfolio semseter 2
            } else if (s.contains("AEA4O")) {
                specialCourses.add(addRooms(s,numberOfClasses,4,"art"));
                // co op
            } else if (s.contains("COP")) {
                for (int i = 0; i < numberOfClasses.get(s); i++) {
                    room = "2001";
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
                        // just incase its some weird co-op course
                        timeslot = 1;
                    }
                    Data.roomMap.get(room).setUnavailable(timeslot);
                    specialCourses.add(new ClassInfo(room, timeslot, s, fixed));
                }
            }
        }
        return specialCourses;
    }

    public ClassInfo addRooms(String s, HashMap<String, Integer> numberOfClasses,int semester,String roomtype){
        String room="";
        int time=0;
        int timeslot=0;
        boolean fixed=true;

        for (int i = 0; i < numberOfClasses.get(s); i++) {
            time++;
            room = Data.roomTypeMap.get(roomtype).get(time / 4);
            timeslot = time % 4+ semester;
            Data.roomMap.get(room).setUnavailable(timeslot);
        }
        return new ClassInfo(room,timeslot,s,fixed);
    }
}
