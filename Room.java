public class Room {

    private int roomNum;
    private String roomType;
    private int numberOfClasses = 0;

    public Room(){

    }
    /**
     * @return int return the roomNum
     */
    public int getRoomNum() {
        return roomNum;
    }

    /**
     * @param roomNum the roomNum to set
     */
    public void setRoomNum(int roomNum) {
        this.roomNum = roomNum;
    }

    /**
     * @return String return the roomType
     */
    public String getRoomType() {
        return roomType;
    }

    /**
     * @param roomType the roomType to set
     */
    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    /**
     * @return int return the numberOfClasses
     */
    public int getNumberOfClasses() {
        return numberOfClasses;
    }

    /**
     * @param numberOfClasses the numberOfClasses to set
     */
    public void setNumberOfClasses(int numberOfClasses) {
        this.numberOfClasses = numberOfClasses;
    }

}
