import javax.swing.JFrame;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.Image;

/**
 * [SystemManager.java]
 * Manages the seating assignment, student enrollment and floor plan system
 * @author Alex, Nicholas, Samson
 * @version 1.0 Oct 14, 2021
**/

public class SystemManager extends JFrame {

    private static EnrollmentSystemPanel enrollSys = new EnrollmentSystemPanel();
    private static FloorPlanSystem floorPlan = new FloorPlanSystem(new String[]{"intro", "contest", "web"});
    private static SeatingAssignmentSystem seatingPlan  = new SeatingAssignmentSystem();
    private Image csLogo;
    JFrame thisFrame;

    /**
     * SystemManager constructor
     */
    public SystemManager() {
        super("Seating Assignment Manager");
        this.thisFrame = this;
        JPanel mainPanel = new MainPanel();
        
        //configure the window  
        this.setSize(900, 500);
        this.setLocationRelativeTo(null); 
        this.setResizable(false);

        //enrollmentButton
        JButton enrollButton = new JButton("Enrollment System");
        enrollButton.setPreferredSize(new Dimension(240, 50));
        enrollButton.setBackground(new Color(255, 255, 255));
        enrollButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                enrollSys.generateJTable();
            }
        });

        //Floorplansystem button
        JButton floorPlanButton = new JButton("Floor Plan System");
        floorPlanButton.setPreferredSize(new Dimension(240, 50));
        floorPlanButton.setBackground(new Color(255, 255, 255));
        floorPlanButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                floorPlan = new FloorPlanSystem(new String[]{"intro", "contest", "web"});
                seatingPlan.arrangeStudents(floorPlan, enrollSys.getStudentList());
                floorPlan.displayTables();
            }
        });

        //Exitbutton
        JButton exitButton = new JButton("Exit");
        exitButton.setPreferredSize(new Dimension(240, 50));
        exitButton.setBackground(new Color(255, 255, 255));
        exitButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event) {
                 System.exit(0);
            }
        });

        mainPanel.add(enrollButton);
        mainPanel.add(floorPlanButton);
        mainPanel.add(exitButton);

        this.add(mainPanel);
        this.setVisible(true);
        this.requestFocusInWindow();
    }

    /**drawLogo 
     * draws the RHHS Computer Science Logo
     * @param g the graphics panel
     */
    public void drawLogo(Graphics g) {
        try {
            csLogo = ImageIO.read(new File("cslogo.png")).getScaledInstance(300, 200, Image.SCALE_DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        g.drawImage(csLogo, 300, 100, null);
    }

    /**
     * [MainPanel.java]
     * paints images on the system manager, this is an inner class
     * @author Alex, Nicholas, Samson
     * @version 1.0 Oct 14, 2021
     */
    class MainPanel extends JPanel {
        public void paintComponent(Graphics g) {
            super.paintComponent(g); 
            setDoubleBuffered(true);
            drawLogo(g);
        }
    }
   
    /**
     * main 
     * Main method that starts this application
     * @param args, String array arguments
    */
    public static void main(String[] args) {
        new SystemManager();
    }
}