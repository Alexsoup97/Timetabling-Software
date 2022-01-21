public class Room {

    private String roomNum;
    private String roomType;
    private boolean[] availability = new boolean[8];

    public Room(String roomNum, String roomType) {
        this.roomNum = roomNum;
        this.roomType = roomType;
        for(int i=0; i<availability.length; i++) {
            availability[i] = true;
        }
    }

    /**
     * @return int return the roomNum
     */
    public String getRoomNum() {
        return roomNum;
    }

    /**
     * @return String return the roomType
     */
    public String getRoomType() {
        return roomType;
    }

    public boolean isAvailable(int time){
        return availability[time];

    }

    public void setAvailability(int time, boolean status){
        availability[time] = status;
    }

}
