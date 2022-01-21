public class Room {

    private String roomNum;
    private String roomType;
    private boolean[] availability = new boolean[8];

    public Room(String roomNum, String roomType) {
        this.roomNum = roomNum;
        this.roomType = roomType;
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

    public boolean getAvailability(int time){
        return availability[time];

    }

    public void setAvailability(int time){
        availability[time] = true;
    }

}
