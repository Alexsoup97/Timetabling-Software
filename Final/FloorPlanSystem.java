/******* Graphics and GUI imports *******/
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Font;

/******* Keyboard imports *******/
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/******* Mouse imports *******/
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/******* Utility imports *******/
import java.util.ArrayList;
import java.util.Random;

/**
 * [FloorPlanSystem.java]
 * This class manages a list of Table objects. It creates an
 * interactive JFrame that allows the user the manage the
 * floor plan of the tables.
 * 
 * @author Edison Du
 * @author Peter Gu
 * @author Jeffrey Xu
 * @version 1.0 Oct 13, 2021
 */
public class FloorPlanSystem {

    /***** Graphics Related Constants ******/
    private final int FRAME_WIDTH = 1300;
    private final int FRAME_HEIGHT = 780;
    private final int GRID_SQUARE_SIZE = 30;
    private final int MAX_RGB = 255;
    private final int MOUSE_X_OFFSET = -10;
    private final int MOUSE_Y_OFFSET = -30;
    private final int LEGEND_X_POSITION = FRAME_WIDTH - 180;
    private final int LEGEND_Y_POSITION = GRID_SQUARE_SIZE;
    private final int EXIT_BUTTON_X = GRID_SQUARE_SIZE;
    private final int EXIT_BUTTON_Y = FRAME_HEIGHT - 140;
    private final int EXIT_BUTTON_WIDTH = GRID_SQUARE_SIZE*5;
    private final int EXIT_BUTTON_HEIGHT = GRID_SQUARE_SIZE*2;
    private final int TABLE_HELPER_X = FRAME_WIDTH-440;
    private final int TABLE_HELPER_Y = FRAME_HEIGHT-220;
    private final int HELP_BUTTON_X = GRID_SQUARE_SIZE/2;
    private final int HELP_BUTTON_Y = GRID_SQUARE_SIZE/2;

    private final Color GRAY_TEXT_COLOR = new Color(50, 50, 50);
    private final Color TRANSPARENT_WHITE_COLOR = new Color(255, 255, 255, 200);
    private final Color BUTTON_COLOR = new Color(100, 100, 220);
    private final Color GRID_LINES_COLOR = new Color(200, 200, 200);

    private final Font SANS_SERIF_SMALL = new Font("SansSerif", Font.PLAIN, 12);
    private final Font SANS_SERIF_MEDIUM = new Font("SansSerif", Font.PLAIN, 18);
    private final Font SANS_SERIF_LARGE = new Font("SansSerif", Font.PLAIN, 24);

    /***** Camera Shift *****/
    private int offsetX = 0;
    private int offsetY = 0;

    /***** Table Logic ******/
    private int numTables;
    private int numGroups;
    private String[] groups;
    private Color[] groupColors;
    private ArrayList<Table> tableList;

    /***** Table Movement *****/
    private int selectedTable;
    private int selectedTableX, selectedTableY;
    private boolean isSelected;

    /***** Help Popup *****/
    private boolean helpIsOpen;
    private String[] helpMessage = {
        "Use...", 
        "1. Mouse to drag tables", 
        "2. Arrow keys to move camera", 
        "3. R to refresh layout"
    };

    /***** Utility ******/
    private Random random = new Random();
    
    /**
     * Constructs a new floor plan system with a list of Table objects.
     * @param groups The different groups that the tables belong to
     */
    public FloorPlanSystem(String[] groups) {
        this.groups = groups;
        this.numGroups = groups.length;
        tableList = new ArrayList<Table>();
        groupColors = new Color[numGroups];
        selectedTable = -1;
        
        // Assign colors associated with each group
        for (int i = 0; i < numGroups; i++) {
            int r = random.nextInt(MAX_RGB/2) + MAX_RGB/2;
            int g = random.nextInt(MAX_RGB/2) + MAX_RGB/2;
            int b = random.nextInt(MAX_RGB/2) + MAX_RGB/2;
            groupColors[i] = new Color(r, g, b);
        }
    }

    /**
     * displayTables
     * Creates an interactive JFrame used to display all the
     * tables and allows the user to move them around.
     */
    public void displayTables() {
        new FloorPlanFrame();
    }

    /**
     * addTable
     * Adds a table object to the floor plan's list of tables.
     * @param table the table object to add
     * @param groupName the group associated with the table
     * @return whether the table was succesfully added
     */
    public boolean addTable(Table table, String groupName) {
        for (int i = 0; i < numGroups; i++) {
            if (groups[i].equals(groupName)) {

                table.setColor(groupColors[i]);
                table.setGroup(groupName);
                tableList.add(table);
                numTables++;

                // Reset the table layout to accommodate for the newly added table
                rearrangeTables();
                return true;
            }
        }
        // If none of the groups match the group name, the table is not added
        return false;
    }
    
    /**
     * removeTable
     * Removes a table object from the floor plan's list of tables.
     * @param id the id of the table to remove
     * @return whether the table exists and was succesfully removed
     */
    public boolean removeTable(int id) {
        for (int i = 0; i < numTables; i++) {
            if (tableList.get(i).getID() == id) {
                tableList.remove(i);
                numTables--;  
                return true;
            }
        }
        return false;
    }

    /**
     * getTableList
     * Returns an arraylist containing the list of tables.
     * @return the arraylist containing the list of tables
     */
    public ArrayList<Table> getTableList() {
        return this.tableList;
    }

    /**
     * rearrangeTables
     * Rearranges all the tables into a grid formation in the floor 
     * plan layout.
     */
    public void rearrangeTables() {
        int currentRow = GRID_SQUARE_SIZE;
        int currentColumn = GRID_SQUARE_SIZE;

        for (int i = 0; i < numTables; i++) {
            
            tableList.get(i).setX(currentColumn);
            tableList.get(i).setY(currentRow);
            
            /**
             *  Make sure that the table moves onto the next row of the grid
             *  when it reaches the end of the JFrame window.
             */
            currentColumn += Table.WIDTH + GRID_SQUARE_SIZE;
            if (currentColumn + GRID_SQUARE_SIZE >= FRAME_WIDTH) {
                currentColumn = GRID_SQUARE_SIZE;
                currentRow += Table.HEIGHT + GRID_SQUARE_SIZE;
            }
        }
    }

    /**
     * FloorPlanFrame
     * A JFrame that graphically displays the table list and allows 
     * user interaction via mouse and keyboard.
     */
    private class FloorPlanFrame extends JFrame {

        /**
         * Creates a new JFrame with all the appropriate listeners, and starts
         * a thread to update what is displayed on the screen
         */
        public FloorPlanFrame() {
            super("Floor Plan System");

            // Initialize JFrame settings
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
            this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
            this.setResizable(false);
            this.requestFocusInWindow();
            this.setVisible(true);   

            // Add panel and listeners
            this.add(new FloorPlanPanel());
            this.addKeyListener(new KeyEventListener());    
            this.addMouseListener(new MouseEventListener());  
            this.addMouseMotionListener(new MouseMotionEventListener());
        
            // Create thread
            Thread t = new Thread(new Runnable(){     
                public void run() { 
                    animate();
                }
            }); 
              
            t.start();  
        }
        
        /**
         * animate
         * Continuously updates what is displayed by the JFrame
         */
        private void animate() {
            while (true) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    System.out.println("Thread Interrupted");
                    e.printStackTrace();
                }
                this.repaint();
            }
        }
        
        /**
         * FloorPlanPanel
         * A JPanel that graphically displays the tables
         */
        private class FloorPlanPanel extends JPanel {

            public void paintComponent(Graphics graphics) {

                Graphics2D g = (Graphics2D) graphics;

                setDoubleBuffered(true);
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g.setFont(SANS_SERIF_SMALL);

                // Draw grid lines
                g.setColor(GRID_LINES_COLOR);
                for (int i = 0; i < FRAME_WIDTH; i += GRID_SQUARE_SIZE) {
                    g.drawLine(i, 0, i, FRAME_HEIGHT);
                }
                for (int i = 0; i < FRAME_HEIGHT; i += GRID_SQUARE_SIZE) {
                    g.drawLine(0, i, FRAME_WIDTH, i);
                }

                // Draw tables
                for (int i = 0; i < numTables; i++) {
                    if (i != selectedTable) {
                        tableList.get(i).draw(g, offsetX, offsetY);
                    }
                }

                // Draw table helper for the pressed table, as well as a transparent table that follows the mouse
                if (isSelected) {

                    Table currentTable = tableList.get(selectedTable);

                    int tableOutlineX = offsetX - currentTable.getX() % GRID_SQUARE_SIZE;
                    int tableOutlineY = offsetY - currentTable.getY() % GRID_SQUARE_SIZE;

                    currentTable.drawTransparent(g, offsetX, offsetY);    
                    currentTable.drawOutline(g, tableOutlineX, tableOutlineY, Color.BLACK);    
                    currentTable.drawInformation(g, TABLE_HELPER_X, TABLE_HELPER_Y);
                }

                // Draw color legend for groups
                g.setColor(TRANSPARENT_WHITE_COLOR);
                g.fillRoundRect(LEGEND_X_POSITION, LEGEND_Y_POSITION, GRID_SQUARE_SIZE * (numGroups+1), GRID_SQUARE_SIZE * 4, 15, 15);
                g.setColor(Color.BLACK);
                g.drawRoundRect(LEGEND_X_POSITION, LEGEND_Y_POSITION, GRID_SQUARE_SIZE * (numGroups+1), GRID_SQUARE_SIZE * 4, 15, 15);

                for (int i = 0; i < numGroups; i++) {
                    int labelX = LEGEND_X_POSITION+15;
                    int labelY = i * GRID_SQUARE_SIZE + LEGEND_Y_POSITION+15;
                    g.setColor(groupColors[i]);
                    g.fillRect(labelX, labelY, GRID_SQUARE_SIZE, GRID_SQUARE_SIZE);
                    g.setColor(GRAY_TEXT_COLOR);
                    g.setFont(SANS_SERIF_MEDIUM);
                    g.drawRect(labelX, labelY, GRID_SQUARE_SIZE, GRID_SQUARE_SIZE);
                    g.drawString(groups[i], labelX + GRID_SQUARE_SIZE + 10, labelY + 23);
                }

                // Draw help button pop up
                g.setFont(SANS_SERIF_MEDIUM);
                if (helpIsOpen) {
                    g.setColor(TRANSPARENT_WHITE_COLOR);
                    g.fillRoundRect(HELP_BUTTON_X + 15, HELP_BUTTON_X + 15, 9*GRID_SQUARE_SIZE, 4*GRID_SQUARE_SIZE, 15, 15);
                    g.setColor(Color.BLACK);
                    g.drawRoundRect(HELP_BUTTON_X + 15, HELP_BUTTON_X + 15, 9*GRID_SQUARE_SIZE, 4*GRID_SQUARE_SIZE, 15, 15);
                    g.setColor(GRAY_TEXT_COLOR);
                    for (int i = 0; i < helpMessage.length; i++) {
                        g.drawString(helpMessage[i], HELP_BUTTON_X + 30, HELP_BUTTON_Y + (i+2) * 23);
                    }
                }

                // Draw help button
                g.setColor(BUTTON_COLOR);
                g.fillOval(HELP_BUTTON_X, HELP_BUTTON_Y, GRID_SQUARE_SIZE, GRID_SQUARE_SIZE);
                g.setColor(Color.BLACK);
                g.drawOval(HELP_BUTTON_X, HELP_BUTTON_Y, GRID_SQUARE_SIZE, GRID_SQUARE_SIZE);
                g.setColor(Color.WHITE);
                g.drawString("?", HELP_BUTTON_X + 11, HELP_BUTTON_Y + 22);

                // Draw close button
                g.setFont(SANS_SERIF_LARGE);
                g.setColor(BUTTON_COLOR);
                g.fillRoundRect(EXIT_BUTTON_X, EXIT_BUTTON_Y, EXIT_BUTTON_WIDTH, EXIT_BUTTON_HEIGHT, 15, 15);
                g.setColor(Color.BLACK);
                g.drawRoundRect(EXIT_BUTTON_X, EXIT_BUTTON_Y, EXIT_BUTTON_WIDTH, EXIT_BUTTON_HEIGHT, 15, 15);
                g.setColor(Color.WHITE);
                g.drawString("Back", EXIT_BUTTON_X + 33, EXIT_BUTTON_Y + 40);

            }
        }

        /**
         * KeyEventListener
         * A key listener that receives keyboard inputs to allow
         * the user to shift the tables.
         */
        private class KeyEventListener implements KeyListener {
            public void keyPressed (KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    FloorPlanFrame.this.dispose();
                    

                } else if (e.getKeyCode() == KeyEvent.VK_R) {
                    FloorPlanSystem.this.rearrangeTables();

                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT){
                    offsetX -= GRID_SQUARE_SIZE;

                } else if (e.getKeyCode() == KeyEvent.VK_LEFT){
                    offsetX += GRID_SQUARE_SIZE;
                    
                } else if (e.getKeyCode() == KeyEvent.VK_UP){
                    offsetY += GRID_SQUARE_SIZE;
                    
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN){
                    offsetY -= GRID_SQUARE_SIZE;
                }
            }
            public void keyReleased (KeyEvent e) {}
            public void keyTyped (KeyEvent e) {}
        }

        /**
         * MouseEventListener
         * A mouse listener that receives mouse inputs to allow
         * the user to move tables around.
         */
        private class MouseEventListener implements MouseListener {
            public void mousePressed (MouseEvent e) {
                
                // Initialize mouse coordinates 
                int mouseX = e.getX() + MOUSE_X_OFFSET;
                int mouseY = e.getY() + MOUSE_Y_OFFSET;

                // Open the help pop up if it is closed, close it if it is open
                if (clickedButton(mouseX, mouseY, HELP_BUTTON_X, HELP_BUTTON_Y, GRID_SQUARE_SIZE, GRID_SQUARE_SIZE)) {
                    helpIsOpen ^= true;
                    return;
                }

                // Dispose the frame if exit button is pressed
                if (clickedButton(mouseX, mouseY, EXIT_BUTTON_X, EXIT_BUTTON_Y, EXIT_BUTTON_WIDTH, EXIT_BUTTON_HEIGHT)) {
                    FloorPlanFrame.this.dispose();
                    return;
                }

                // Move table
                if (!isSelected){
                    mouseX -= offsetX;
                    mouseY -= offsetY;
                    // Checking for which table is currently being clicked
                    for (int i = 0; i < numTables; i++) {
                        Table t = tableList.get(i);
                        if (t.checkOverlap(mouseX, mouseY)){
                            selectedTable = i;
                            selectedTableX = t.getX();
                            selectedTableY = t.getY();
                            isSelected = true;
                        }
                    }
                }
            }
            public void mouseReleased (MouseEvent e) {
                
                // Initialize mouse coordinates 
                int mouseX = e.getX() + MOUSE_X_OFFSET - offsetX;
                int mouseY = e.getY() + MOUSE_Y_OFFSET - offsetY;

                // Initialize closest rectangle corner coordinates
                int newTableX = ((mouseX - Table.WIDTH/2)/GRID_SQUARE_SIZE)*GRID_SQUARE_SIZE;
                int newTableY = ((mouseY - Table.HEIGHT/2)/GRID_SQUARE_SIZE)*GRID_SQUARE_SIZE;
                
                // Check if table can be moved to new location
                if (isSelected){
                    boolean canPlace = true;
                    // Check all tables for collision
                    for (int i = 0; i < numTables; i++){
                        if (i != selectedTable){
                            Table t = tableList.get(i);
                            Rectangle tableA = new Rectangle(newTableX, newTableY, Table.WIDTH, Table.HEIGHT);
                            Rectangle tableB = new Rectangle(t.getX(), t.getY(), Table.WIDTH, Table.HEIGHT);
                            if (tableA.intersects(tableB)){
                                canPlace = false;
                            }
                        }
                    }
                    // If the table cannot be placed at the new location, it returns to its old position
                    if (canPlace){
                        tableList.get(selectedTable).setX(newTableX);
                        tableList.get(selectedTable).setY(newTableY);
                    } else {
                        tableList.get(selectedTable).setX(selectedTableX);
                        tableList.get(selectedTable).setY(selectedTableY);
                    }

                    isSelected = false;
                    selectedTable = -1;
                    selectedTableX = 0;
                    selectedTableY = 0;
                }
            }
            public void mouseClicked (MouseEvent e) {}
            public void mouseEntered (MouseEvent e) {}
            public void mouseExited (MouseEvent e) {}
        }

        /**
         * MouseMotionEventListener
         * A mouse motion listener that receives mouse motion inputs to 
         * have a table follow the user's cursor when dragged.
         */
        private class MouseMotionEventListener implements MouseMotionListener {
            public void mouseDragged (MouseEvent e) {
                
                // Initialize mouse coordinates 
                int mouseX = e.getX() + MOUSE_X_OFFSET - offsetX;
                int mouseY = e.getY() + MOUSE_Y_OFFSET - offsetY;
                
                // If there is a table being moved, adjust its coordinates in relation to the mouse
                if (isSelected){
                    tableList.get(selectedTable).setX(mouseX - Table.WIDTH/2);
                    tableList.get(selectedTable).setY(mouseY - Table.HEIGHT/2);
                }
            }
            public void mouseMoved (MouseEvent e) {}
        }

        /**
         * clickedButton
         * Check if a point intersects with the rectangle of a button
         * @param x the x position of the point
         * @param y the y position of the point
         * @param buttonX the x position of the button
         * @param buttonY the y position of the button
         * @param buttonWidth the width of the button
         * @param buttonHeight the height of the button
         * @return whether or not the point intersects with the button
         */
        private boolean clickedButton(int x, int y, int buttonX, int buttonY, int buttonWidth, int buttonHeight) {
            if ( (x >= buttonX) && (x <= buttonX + buttonWidth) ) {
                if ( (y >= buttonY) && (y <= buttonY + buttonHeight) ) {
                    return true;
                }
            }
            return false;
        }
    }
}
