import java.util.ArrayList;
import java.util.HashMap;

public class SpecialCourseScheduler { 

    SpecialCourseScheduler() {
    }

    public ArrayList<ClassInfo> getSpecialCourseTimetable(HashMap<String, Integer> numberOfClasses) {
        ArrayList<ClassInfo> specialCourses = new ArrayList<ClassInfo>();
        // String[] specialCoursesNameArray = { "MHF4U1", "MHF4UE", "MCV4U1", "MCV4UE", "SBI4UE", "SCH4UE", "SPH4UE",
        //         "AEA4O", "COP" };
        // HashSet<String> specialCourseNames = new HashSet<String>(Arrays.asList(specialCoursesNameArray));
        boolean fixed = true;
        // String course;
        int timeslot = 0;
        String room = "";

        int functionsTime = 0;
        int calculusTime = 0;
        int scienceTime = 0;
        int artTime = 0;

        for (String s : numberOfClasses.keySet()) {
            // if(specialCourseNames.contains(s)){
            // functions
            if (s.equals("MHF4U1") || s.equals("MHF4UE")) {
                // course = s;
                for (int i = 0; i < numberOfClasses.get(s); i++) {
                    functionsTime++;
                    room = Data.roomTypeMap.get("classroom").get(functionsTime / 4);
                    timeslot = functionsTime % 4;
                    Data.roomMap.get(room).setUnavailable(timeslot);
                    specialCourses.add(new ClassInfo(room, timeslot, s, fixed));
                }
                // calculas
            } else if (s.contains("MCV4U1") || s.contains("MCV4UE")) {
                // course = s;
                for (int i = 0; i < numberOfClasses.get(s); i++) {
                    calculusTime++;
                    room = Data.roomTypeMap.get("classroom").get(calculusTime / 4);
                    timeslot = calculusTime % 4 + 4;
                    Data.roomMap.get(room).setUnavailable(timeslot);
                    specialCourses.add(new ClassInfo(room, timeslot, s, fixed));
                }
                // Ap Sciences
            } else if (s.contains("SBI4UE") || s.contains("SCH4UE") || s.contains("SPH4UE")) {
                for (int i = 0; i < numberOfClasses.get(s); i++) {
                    scienceTime++;
                    room = Data.roomTypeMap.get("science").get(scienceTime / 4);
                    timeslot = scienceTime % 4;
                    Data.roomMap.get(room).setUnavailable(timeslot);
                    specialCourses.add(new ClassInfo(room, timeslot, s, fixed));
                }
                // art protfolio semseter 2
            } else if (s.contains("AEA4O")) {
                for (int i = 0; i < numberOfClasses.get(s); i++) {

                    artTime++;
                    timeslot = artTime % 4;
                    if (artTime > 4) {
                        room = "2005";
                    } else {
                        room = "2004";
                    }
                    Data.roomMap.get(room).setUnavailable(timeslot);
                    specialCourses.add(new ClassInfo(room, timeslot, s, fixed));
                }
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

            // }
        }
        return specialCourses;
    }
}
