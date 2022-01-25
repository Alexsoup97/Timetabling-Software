import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

/**
 * Class for generating an initial master timetable allocating all courses running into timeslots and rooms
 * 
 * @author Suyu, Eric
 */
class InitialTimetableGenerator{
    static Random random = new Random();

    /**
     * Generates an initial master timetable allocating all needed classes into timeslots and rooms.
     * Prevents duplicate rooms and ensures sections of the same course are balanced across semesters
     * @author Suyu
     * @param specialCourseTimetable an ArrayList<ClassInfo> of classes that are pre-filled into the timetable and will not be touched by this method
     * @param coursesRunning a HashMap<String, Integer> mapping course codes to the number of that course required
     * @return a filled timetable containing all courses required, allocated into non-conflicting rooms and timeslots.
     // deal with running out of rooms
     */
    public ArrayList<ClassInfo> createInitialTimetable(ArrayList<ClassInfo> specialCourseTimetable, HashMap<String, Integer> coursesRunning) {
        ArrayList<ClassInfo> initialTimetable = new ArrayList<ClassInfo>();
        HashSet<String> specialClasses = new HashSet<String>();
      
        for(ClassInfo i: specialCourseTimetable){
            initialTimetable.add(i);
            specialClasses.add(i.getCourse());
      
        }

        HashMap<String, RoomType> roomTypes = new HashMap<String, RoomType>(Data.roomTypeMap.size());
        int roomTypeIdCounter = 0;
        for(Map.Entry<String, ArrayList<String>> entry : Data.roomTypeMap.entrySet()){
            roomTypes.put(entry.getKey(), new RoomType(entry.getKey(), entry.getValue(), roomTypeIdCounter));
            roomTypeIdCounter++;
        }    
 
        // TODO make not hard coded
        HashMap<String, String> roomTypeBackups = new HashMap<String, String>();
        roomTypeBackups.put("science-biology", "science");
        roomTypeBackups.put("science-physics", "science");
        roomTypeBackups.put("computer sci", "classroom");
        roomTypeBackups.put("family studies", "classroom");
        roomTypeBackups.put("science", "classroom");
        
        // each type of room has a different random order of filling classes into time periods for each room
        // The fill order alternates between sem1 and sem2 periods so multi section courses are guaranteed to be balanced between semesters
        int[][] fillOrder = new int[roomTypes.size()][Data.NUM_PERIODS];
        for(int i=0; i<fillOrder.length; i++){
            fillOrder[i] = generatePeriodFillOrder();
        }
        
        // Sort the courses running by the number of sections, so courses with few sections are placed first
        // This ensures courses with few sections are put into different periods, since they will all be beside each other and classes are added
        // to timeslots in the order of the fill order
        ArrayList<CourseRunning> sortedCoursesRunning = new ArrayList<CourseRunning>();
        for(Map.Entry<String, Integer> entry : coursesRunning.entrySet()){
            sortedCoursesRunning.add(new CourseRunning(entry.getKey(), entry.getValue()));
        }
        Collections.sort(sortedCoursesRunning, new Comparator<CourseRunning>(){
            public int compare(CourseRunning c1, CourseRunning c2){
                return c1.sections-c2.sections;
            }
        });    

        RoomType roomType;
        String chosenRoom = null; 
        int chosenTimeslot = -1; 
        for (CourseRunning course : sortedCoursesRunning) {
            if (!specialClasses.contains(course.code)) { // special classes were already added
                roomType = roomTypes.get(Data.courseMap.get(course.code).getRoomType());
                for (int i = 0; i < course.sections; i++) {
                    // if there are no more room/timeslot pairs of the correct room type, change to backup room type if available
                    if (roomType.counter / Data.NUM_PERIODS >= roomType.rooms.size()) {
                        if (roomTypeBackups.containsKey(roomType.name))
                            roomType = roomTypes.get(roomTypeBackups.get(roomType.name));
                    }
                    // check if not out of room/timeslot pairs
                    if (roomType.counter / Data.NUM_PERIODS < roomType.rooms.size()) {
                        do {
                            chosenRoom = roomType.rooms.get(roomType.counter / Data.NUM_PERIODS); 
                            chosenTimeslot = fillOrder[roomType.id][roomType.counter % Data.NUM_PERIODS];
                            roomType.counter++;
                        } while (!Data.roomMap.get(chosenRoom).isAvailable(chosenTimeslot)); // must be checked since special classes may have already taken the room/timeslot
                        Data.roomMap.get(chosenRoom).setUnavailable(chosenTimeslot);
                        ClassInfo test = new ClassInfo(chosenRoom, chosenTimeslot, course.code, false);
                        initialTimetable.add(test); 
                       
                    }else{ 
                        System.out.println("Ran out of " + roomType);
                    } 
                }
            } 
        }
       
       
        return initialTimetable;
    }

    /**
     * Generates a fill order for filling in sections of course
     * 
     * @author Eric
     * @return a randomized integer array of length Data.NUM_PERIODS, containing all
     *         periods and alternating between sem1 and sem2 periods
     */
    private int[] generatePeriodFillOrder(){
        int[] alternatingPeriods = new int[Data.NUM_PERIODS]; 
        HashSet<Integer> periods = new HashSet<>();
        for(int i=0; i<Data.NUM_PERIODS; i++){
            periods.add(i);
        }
        int adding;
        
        for (int i = 0; i < alternatingPeriods.length; i++) {
            if(i%2 == 0){
                adding = random.nextInt(4) + 4;
                while(!periods.contains(adding)){
                    adding = random.nextInt(4) + 4;
                }
                alternatingPeriods[i] = adding;
                periods.remove(adding);
                adding = 0;                
            }
            else {
                adding = random.nextInt(4);
                while(!periods.contains(adding)){
                    adding = random.nextInt(4);
                }
                alternatingPeriods[i] = adding;
                periods.remove(adding);
                adding = 0;
            }
        }
        return alternatingPeriods;
    }

    /**
     * Helper class for generating initial timetable
     * Stores some information about each room type
     * @author Suyu
     */
    private class RoomType{
        String name;
        ArrayList<String> rooms = new ArrayList<String>();;
        int id;
        int counter;
        RoomType(String name, ArrayList<String> rooms, int id){
            this.name = name;
            this.rooms = rooms; 
            this.id = id;
            this.counter = 0;
        }
        @Override
        public String toString() {
            return rooms.toString()+" "+id+" "+ counter;
        }
    }

    /**
     * Helper class for generating initial timetable
     * Stores some information about a course that is running
     * @author Suyu
     */
    private class CourseRunning{
        String code;
        int sections;
        CourseRunning(String courseName, int sections){
            this.code = courseName;
            this.sections = sections;
        } 
    }
}