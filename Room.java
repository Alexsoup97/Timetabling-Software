/**
 * [Room.java]
 * Stores information pertaining to a
 */
public class Room {

    private String roomNum;
    private String roomType;
    private boolean[] availability = new boolean[Data.NUM_PERIODS];

    public Room(String roomNum, String roomType) {
        this.roomNum = roomNum;
        this.roomType = roomType;
        for(int i=0; i<availability.length; i++) {
            availability[i] = true;
        }
    }

    public String getRoomNum() {
        return roomNum;
    }

    public String getRoomType() {
        return roomType;
    }

    public boolean isAvailable(int time){
        return availability[time];
    }

    public void setUnavailable(int time){
        availability[time] = false;
    }

    public boolean getAvailability(int time){
        return availability[time];
    }

    public void setAvailability(int time, boolean status){
        availability[time] = status;
    }

    public String toString(){
        return this.roomNum + " (" + this.roomType + ") "; 
    }

}
