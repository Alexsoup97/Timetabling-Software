/******* Graphics imports *******/
import java.awt.Graphics;
import java.awt.Color;

/**
 * [Table.java]
 * This class represents a table that contains a set amount of Student
 * objects. This table can be drawn onto a java.awt.Graphics object.
 * 
 * @author Edison Du
 * @author Peter Gu
 * @author Jeffrey Xu
 * @version 1.0 Oct 13, 2021
 */
public class Table {

    /**
     * The width of the table when drawn onto a Graphics object
     */
    public static final int WIDTH = 150;
    /**
     * The height of the table when drawn onto a Graphics object
     */
    public static final int HEIGHT = 90;
    
    /* Student logic Variables */
    private int id;
    private int capacity;
    private int numStudents;
    private String group;
    private Student[] students;

    /* Graphics related variables */
    private int x, y;
    private Color color;
    
    /**
     * Constructs a new table that contains a set amount of Student objects
     * @param id the id of the table, used to differenciate between tables
     * @param capacity the maximum amount of Student objects that the table can contain
     */
    Table(int id, int capacity) {
        this.id = id;
        this.capacity = capacity;
        this.numStudents = 0;
        this.students = new Student[capacity];
    }

    /**
     * addStudent
     * This method is used to add a student into the table's array of students
     * @param student the student to be added
     * @return whether or not the student has been succesfully added
     */
    public boolean addStudent(Student student) {
        if (this.seatsRemaining() == 0) {
            return false;
        }
        this.students[this.numStudents++] = student;
        return true;
    }

    /**
     * removeStudent
     * This method is used to remove a student from the table's array of students
     * @param id the id of the student of be removed
     * @return whether or not the student has been succesfully removed
     */
    public boolean removeStudent(int id) {
        for (int i = 0; i < this.numStudents; i++) {
            if (students[i].getId() == id) {
                students[i] = null;
            }
            // Shift students downwards in the array once a student has been removed
            if (i > 0 && students[i-1] == null) {
                students[i-1] = students[i];
                students[i] = null;
            }
        }
        // Return true of the student exists and has been removed
        if (numStudents > 0 && students[numStudents-1] == null) {
            numStudents--;
            return true;
        }
        return false;
    }

    /**
     * getStudents
     * Returns an array containing the students in the table
     * @return the array containing the students in the table
     */
    public Student[] getStudents() {
        return this.students;
    }

    /**
     * isEmpty
     * Checks whether or not the table contains any students
     * @return whether or not the table contains any students
     */
    public boolean isEmpty() {
        return this.numStudents == 0;
    }
    
    /**
     * seatsRemaining
     * Returns the number of seats remaining in the table
     * @return the number of seats remain
     */
    public int seatsRemaining() {
        return this.capacity - this.numStudents;
    }

    
    /**
     * getNumStudents
     * Returns the number of students at the table
     * @return the number of students at the table
     */
    public int getNumStudents() {
        return this.numStudents;
    }

    /**
     * getCapacity
     * Returns the limit of students that the table can contain
     * @return the limit of students that the table can contain
     */
    public int getCapacity() {
        return this.capacity;
    }
    
    /**
     * getID
     * Returns the id of the table
     * @return the id of the table
     */
    public int getID() {
        return this.id;
    }

    /**
     * setID
     * Modifies the id of the table
     * @param id the id of the table to change to
     */
    public void setID(int id) {
        this.id = id;
    }

    /**
     * getGroup
     * Returns the group of the table
     * @return the group of the table
     */
    public String getGroup() {
        return this.group;
    }

    /**
     * setGroup
     * Changes the group associated with the table
     * @param group the name of the group to change to
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * getX
     * Returns the x position of the table
     * @return the x position of the table
     */
    public int getX() {
        return this.x;
    }

    /**
     * setX
     * Changes the x position of the table
     * @param x the x position to change to
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * getY
     * Returns the y position of the table
     * @return the y position of the table
     */
    public int getY() {
        return this.y;
    }
    /**
     * setY
     * Changes the y position of the table
     * @param y theS y position to change to
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * getColor
     * Returns a color object representing color of the table
     * @return the color object representing color of the table
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * setColor
     * Changes the color of the table
     * @param color the color object to change to
     */
    public void setColor(Color color) {
        this.color = color;
    }
    
    /**
     * checkOverlap
     * This method checks if a point overlaps with a table
     * @param x the x value of the point
     * @param y the y value of the point
     * @return whether or not the point overlaps with the table
     */
    public boolean checkOverlap(int x, int y) {
        return ( (this.x <= x) && (x <= this.x + WIDTH) && (this.y <= y) && (y <= this.y + HEIGHT) );
    }

    /**
     * draw
     * Display the table onto a Graphics object
     * @param g the graphics object for drawing
     * @param xShift the horizontal offset of the tables when drawn
     * @param yShift the vertical offset of the tables when drawn
     */
    public void draw(Graphics g, int xShift, int yShift) {
        int nameGap = 23;
        g.setColor(this.color);
        g.fillRoundRect(this.x + xShift, this.y + yShift, WIDTH, HEIGHT, 15, 15);
        g.setColor(Color.BLACK);
        g.drawRoundRect(this.x + xShift, this.y + yShift, WIDTH, HEIGHT, 15, 15);

        for (int i = 0; i < this.numStudents; i++){
            g.setColor(new Color(30, 30, 30));
            g.drawString(students[i].getName(), this.x + xShift + 5, this.y + yShift + i * nameGap + 15);
        }
    }

    /**
     * drawTransparent
     * Display the table onto a Graphics object with transparency
     * @param g the graphics object for drawing
     * @param xShift the horizontal offset of the tables when drawn
     * @param yShift the vertical offset of the tables when drawn
     */
    public void drawTransparent(Graphics g, int xShift, int yShift) {
        
        int nameGap = 23;
        
        int red = this.color.getRed();
        int green = this.color.getGreen();
        int blue = this.color.getBlue();
        int opacity = 125;
        
        g.setColor(new Color(red, green, blue, opacity));
        g.fillRoundRect(this.x + xShift, this.y + yShift, WIDTH, HEIGHT, 15, 15);
        g.setColor(new Color(0, 0, 0, 125));
        g.drawRoundRect(this.x + xShift, this.y + yShift, WIDTH, HEIGHT, 15, 15);

        for (int i = 0; i < this.numStudents; i++){
            g.setColor(new Color(30, 30, 30, 125));
            g.drawString(students[i].getName(), this.x + xShift + 5, this.y + yShift + i * nameGap + 15);
        }
    }

    /**
     * drawOutline
     * Display an outline of the table onto a Graphics object
     * @param g the graphics object for drawing
     * @param xShift the horizontal offset of the tables when drawn
     * @param yShift the vertical offset of the tables when drawn
     */
    public void drawOutline(Graphics g, int xShift, int yShift, Color color) {
        g.setColor(color);
        g.drawRoundRect(this.x + xShift, this.y + yShift, WIDTH, HEIGHT, 15, 15);
    }

    /**
     * drawInformation
     * Display the information of the table and the students in the table
     * @param g the graphics object for drawing
     * @param xShift the horizontal offset of the tables when drawn
     * @param yShift the vertical offset of the tables when drawn
     */
    public void drawInformation(Graphics g, int xShift, int yShift) {

        int nameGap = 23;
        int informationWidth = 400;
        int informationHeight = 150;

        g.setColor(new Color(255, 255, 255, 200));

        g.fillRoundRect(xShift, yShift, informationWidth, informationHeight, 15, 15);
        g.setColor(Color.BLACK);
        g.drawRoundRect(xShift, yShift, informationWidth, informationHeight, 15, 15);

        g.drawString("TABLE " + this.id, xShift + 15, yShift  + nameGap);
        g.drawString("GROUP: " + this.group, xShift + 190, yShift  + nameGap);

        for (int i = 0; i < this.numStudents; i++) {

            String studentID = String.format("%d. ID: %-9d", i+1, students[i].getId());
            String studentGrade = String.format("GR: %-2d", students[i].getGrade());
            String studentName = String.format("Name: %-10s", students[i].getName());

            g.drawString(studentID, xShift + 15, yShift + 23 * (i+2));
            g.drawString(studentGrade, xShift + 130, yShift + 23 * (i+2));
            g.drawString(studentName, xShift + 190, yShift + 23 * (i+2));
        }
    }
}
