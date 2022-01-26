import javax.swing.JTable;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import java.util.Collection;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.TableRowSorter;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import java.util.Arrays;
import javax.swing.JCheckBox;
import javax.swing.event.RowSorterListener;
import javax.swing.event.RowSorterEvent;
import java.awt.Cursor;
/**
 * [UserInterface.java]
 * Displays the user interface for the timetabling software 
 * @author Alex
 */
public class UserInterface {
    private final int SCREEN_WIDTH = 900;
    private final int SCREEN_HEIGHT = 900;
    
    /**
     * Initalizes the User interface
     * @param timetable arraylist of all classes
     */
    public UserInterface(){
        JFrame frame = new JFrame("Course Timetabler");
        Menu menu = new Menu(frame);
        menu.setOpaque(true);
        frame.setContentPane(menu);
        frame.setVisible(true);
        frame.setSize(SCREEN_WIDTH,SCREEN_HEIGHT);
        frame.setLocationRelativeTo(null); //start the frame in the center of the screen
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Displays the menu screen 
     */
    class Menu extends JPanel{
        private JButton timetableButton = new JButton("Master Timetable");
        private JButton studentButton = new JButton("Student Timetables");
        //private JButton specialCourseButton = new JButton("Special Courses");
        private JButton generateTimetableButton = new JButton("Generate New Timetable");
        private Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);
        private Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);

        /**
         * Creates new Menu
         * @param frame the JFrame that is being drawn to
         * @param timetable arraylist of all classes
         */
        public Menu(JFrame frame){   
            MasterTimetable masterTimetablePane = new MasterTimetable(frame, this);
            StudentTimetable studentPane = new StudentTimetable(frame, this);    
            //SpecialCourseUI specialCourses = new SpecialCourseUI(frame ,this);
            masterTimetablePane.setOpaque(true);
            timetableButton.addActionListener(new ActionListener(){
                 public void actionPerformed(ActionEvent e) {
                    masterTimetablePane.addData();
                    frame.setContentPane(masterTimetablePane);
                    masterTimetablePane.revalidate();
                    masterTimetablePane.repaint();
                }
            });     
            studentButton.addActionListener(new ActionListener(){
                 public void actionPerformed(ActionEvent e) {
                    studentPane.addData();
                    frame.setContentPane(studentPane);
                    studentPane.revalidate();
                    studentPane.repaint();
                }
            });     
            generateTimetableButton.addActionListener(new ActionListener(){
                 public void actionPerformed(ActionEvent e) {
                    frame.setCursor(waitCursor);
                    Main.generateTimetable();
                    frame.setCursor(defaultCursor);
                    add(timetableButton);
                    add(studentButton);
                    frame.repaint();
                    frame.revalidate();
                }
            });     
            // specialCourseButton.addActionListener(new BackButton(frame, specialCourses));
            // generateTimetableButton.addActionListener(new ActionListener(){
            //     public void actionPerformed(ActionEvent e) {
            //         Main.generateTimetable();
            //     }
            // });
           // add(specialCourseButton);
            add(generateTimetableButton);
        }
    }

    /**
     * Class for displaying the student timetables in a table view 
     */
    class StudentTimetable extends JPanel{
        private  JTable table;
        private DefaultTableModel model= new DefaultTableModel();;
        private JScrollPane pane;
        private final int COURSE_COLUMN_WIDTH  = 100;
        private final int GRADE_COLUMN_WIDTH  = 50;
        private final int STUDENTNAME_COLUMN_WIDTH  = 150;
        private final int GRADE_COLUMN = 1;
        private final int STUDENTNAME_COLUMN = 0;
        private final int STUDENTNUMBER_COLUMN = 2;
        private final int STUDENTNUMBER_COLUMN_WIDTH = 100;
        private final int COURSE_COLUMNS = 11;
        private final int FONT_SIZE = 15;
        private JTextField tableFilter = new JTextField();
        private TableRowSorter<TableModel> rowSorter= new TableRowSorter<>(model);
        private Font font = new Font("Calibri", Font.ITALIC, FONT_SIZE);
        private JButton backbutton = new JButton("Back");
        private JPanel menu;
        private JLabel rowCountLabel = new JLabel("");
        JLabel fullTimetable = new JLabel("");
        JLabel topChoices = new JLabel("");
        JLabel alternateChoices = new JLabel("");
        JLabel percentage = new JLabel("");
      

        /**
         * 
         * @param frame the frame being drawn to
         * @param menu the original menu panel to return to
         */
        public StudentTimetable(JFrame frame, JPanel menu){
            Object[] columns = {"Student Name", "Grade","Student Number", "Sem 1: Period 1", "Sem 1: Period 2", "Sem 1: Period 3", "Sem 1: Period 4", "Sem 2: Period 1", "Sem 2: Period 2", "Sem 2: Period 3", "Sem 2: Period 4" };
            this.table = new JTable(model){ 
                @Override
                public boolean isCellEditable(int row, int column){
                    return false;
                }
            };
            model.setColumnIdentifiers(columns);
            table.setForeground(Color.black);
            table.setRowSorter(rowSorter);
            table.setFont(font);
            table.getTableHeader().setReorderingAllowed(false);
            table.setRowHeight(20);
            table.getTableHeader().setResizingAllowed(false);
            tableFilter.getDocument().addDocumentListener(new SortListener(tableFilter, rowSorter,table));
            tableFilter.setBounds(25,3*(SCREEN_HEIGHT/4) + 25,175,30);
            table.getColumnModel().getColumn(STUDENTNAME_COLUMN).setPreferredWidth(STUDENTNAME_COLUMN_WIDTH);
            table.getColumnModel().getColumn(GRADE_COLUMN).setPreferredWidth(GRADE_COLUMN_WIDTH);
            table.getColumnModel().getColumn(STUDENTNUMBER_COLUMN).setPreferredWidth(STUDENTNUMBER_COLUMN_WIDTH);
            table.setColumnSelectionAllowed(true);
            for(int i = 3; i < COURSE_COLUMNS; i++){
                table.getColumnModel().getColumn(i).setPreferredWidth(COURSE_COLUMN_WIDTH);
            }
            backbutton.addActionListener(new BackButton(frame, menu));
            backbutton.setBounds(25, 3*(SCREEN_HEIGHT/4) + 90, 100,40);
            JLabel searchLabel = new JLabel("Search");
            searchLabel.setForeground(Color.black);
            searchLabel.setBounds(25,3*(SCREEN_HEIGHT/4)+ 5,175,30);
            searchLabel.setFont(font);
            fullTimetable.setBounds(SCREEN_WIDTH/2 + 210, 3*(SCREEN_HEIGHT/4) + 20, 175, 30);
            topChoices.setBounds(SCREEN_WIDTH/2 + 210, 3*(SCREEN_HEIGHT/4) + 35, 175, 30);
            alternateChoices.setBounds(SCREEN_WIDTH/2 + 210, (3*(SCREEN_HEIGHT/4))+ 50, 175, 30);
            percentage.setBounds(SCREEN_WIDTH/2 + 210, (3*(SCREEN_HEIGHT/4)) + 65, 225, 30);
           
            rowCountLabel.setBounds(SCREEN_WIDTH/2 + 210, 3*(SCREEN_HEIGHT/4) + 5, 175, 30);
            pane = new JScrollPane(table);  
            rowSorter.addRowSorterListener(new RowCounter(table, rowCountLabel));
            pane.setBounds(0, 0, SCREEN_WIDTH-3, 3*(SCREEN_HEIGHT/4));
            setLayout(null);     
            add(backbutton);
            add(searchLabel);
            add(tableFilter);
            add(pane);
            add(rowCountLabel);
            add(fullTimetable);
            add(topChoices);
            add(alternateChoices);
            add(percentage);
        }

        /**
         * adds the data from the timetable arraylist to the table
         */
        public void addData(){
            for(Student s: Data.studentMap.values()){
                Object[] data = new Object[12];
                data[0] = s.getName().substring(1, s.getName().length()-1);
                data[1] = s.getGrade();
                data[2] = s.getStudentNumber();
                int counter =3;
                for(int i= 0; i<s.getTimetable().length;i++){
                    data[counter]= s.getTimetable()[i].getCourse();
                    counter++;
                }
                this.model.addRow(data);
            }
            fullTimetable.setText("Full timetables: " + Data.results[0]);
            topChoices.setText("Top Choices fulfilled: " + Data.results[1]);
            alternateChoices.setText("Alternate Choices fulfilled: " + Data.results[2]);
            percentage.setText("Percentage of Courses Filled: " + (int)(((double)Data.results[1] / Data.courseCount) *100) +"%");
            rowCountLabel.setText("Number of rows " + table.getRowCount());
        }
    }

    /**
     * Displays the master timetable in a table view
     */
    class MasterTimetable extends JPanel{
        private  JTable table;
        private DefaultTableModel model= new DefaultTableModel();;
        private JScrollPane pane;
        private JButton backbutton = new JButton("Back");
        private JTextField tableFilter = new JTextField();
        private TableRowSorter<TableModel> rowSorter= new TableRowSorter<>(model);
    
        /**
         * 
         * @param timetable the timetable arraylist
         * @param frame the frame being drawn to
         * @param menu the menu to return to
         */
        public MasterTimetable(JFrame frame, JPanel menu){
            Object[] columns = { "Course", "Room", "Semester", "Period", "Student Count" };
            this.table = new JTable(model){ 
                @Override
                public boolean isCellEditable(int row, int column){
                    return false;
                }
            };
            model.setColumnIdentifiers(columns);
            table.setForeground(Color.black);
            table.setRowSorter(rowSorter);
            table.getTableHeader().setReorderingAllowed(false);
            table.setRowHeight(20);
            table.getTableHeader().setResizingAllowed(false);
            tableFilter.getDocument().addDocumentListener(new SortListener(tableFilter, rowSorter,table));
            tableFilter.setBounds(25,3*(SCREEN_HEIGHT/4) + 25,175,30);
            backbutton.addActionListener(new BackButton(frame, menu));
            backbutton.setBounds(25, 3*(SCREEN_HEIGHT/4) + 90, 100,40);
            JLabel searchLabel = new JLabel("Search");
            pane = new JScrollPane(table);  
            pane.setBounds(0, 0, SCREEN_WIDTH-3, 3*(SCREEN_HEIGHT/4));
            setLayout(null);     
            add(backbutton);
            add(searchLabel);
            add(tableFilter);
            add(pane);   
        }
        
        /**
         * adds the data from the timetable to the table
         * @param timetable the arraylist of class info
         */
        public void addData(){
            Object[] data = new Object[5];
            for(ClassInfo classes: Main.timetable){
                data[0] = classes.getCourse();
                data[1] = classes.getRoom();
                data[2] = (int)classes.getTimeslot()/(Data.NUM_PERIODS/2) +1;
                data[3] = (classes.getTimeslot()) %(Data.NUM_PERIODS/2) + 1;
                data[4] = classes.getStudents().size();
                model.addRow(data);
            }
        }   
    }

    /**
     * The User Interface that allows for students to select their own special courses 
     */
    class SpecialCourseUI extends JPanel{
        private Collection<String> values = Data.courseMap.keySet();
        private String[] courseCode= values.toArray(new String[0]);
        private JCheckBox[] timeslotSelection = new JCheckBox[Data.NUM_PERIODS];
        private JButton updateCourses = new JButton("Update");
        private JButton back = new JButton("Back");
        
        /**
         * Initalizes the special courses
         * @param the frame being drawn
         * @param main menu panel to return to
         */
        public SpecialCourseUI(JFrame frame, JPanel panel){
            Arrays.sort(courseCode);
            JComboBox<String> courseBox = new JComboBox<String>(courseCode);
            courseBox.setBounds(0, 0, 175, 25);
            add(courseBox);
            for(int i = 0; i < timeslotSelection.length; i++){
                int semester = i/(Data.NUM_PERIODS/2) +1;
                int period = i %(Data.NUM_PERIODS/2) + 1;
                timeslotSelection[i] = new JCheckBox("Semester: " + semester  + " " + "Period: " + period );
                timeslotSelection[i].setBounds(500 , 230+ i*10, 175,25);
                add(timeslotSelection[i]);
            }
            updateCourses.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e){
                    ArrayList<Integer> selected = new ArrayList<Integer>();
                    for(int i = 0; i < timeslotSelection.length; i++){
                        if(timeslotSelection[i].isSelected()){
                            selected.add(i);
                        }        
                    }
                    Data.userSpecialCourses.put((String) courseBox.getSelectedItem(), selected);
                }
            });
            add(updateCourses);       
            back.addActionListener(new BackButton(frame, panel));
            add(back);
        }   
    }

    /**
     * Updates the row filter when text has been inputted
     */
    public class SortListener implements DocumentListener{
        private JTextField tableFilter;
        private TableRowSorter<TableModel> rowSorter;
        private JTable table;

        /**
         * Creates new SortListener
         * @param tableFilter the text field 
         * @param rowSorter the row filterthat is updated
         * @param table the table being updated
         */
        public SortListener(JTextField tableFilter, TableRowSorter<TableModel> rowSorter, JTable table){
            this.tableFilter = tableFilter;
            this.rowSorter = rowSorter;
            this.table = table;
        }
        
        @Override
        public void insertUpdate(DocumentEvent e) {
            String text = tableFilter.getText();
            if (text.trim().length() == 0) {
                rowSorter.setRowFilter(null);
            }else if(table.getSelectedColumn() != -1){
                rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, table.getSelectedColumn()));
            } else {
                rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            String text = tableFilter.getText();
            if (text.trim().length() == 0) {
                rowSorter.setRowFilter(null);
            } else if(table.getSelectedColumn() != -1){
                rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, table.getSelectedColumn()));
            } else {
                rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        }
        
        @Override
        public void changedUpdate(DocumentEvent e) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    /**
     * Counts the number of rows within a table
     */
    public class RowCounter implements RowSorterListener{
        JTable table;
        JLabel label;

        /**
         * Creates a new row counter 
         * @param table the table being drawn to
         * @param label the label that is updated
         */
        public RowCounter(JTable table, JLabel label){
            this.table =table;
            this.label  = label;
        }
        
        @Override
        public void sorterChanged(RowSorterEvent e){
            int rowCount = table.getRowCount();
            label.setText("Number of Rows: " + rowCount);
        }
    }

    /**
     * Changes the JPanel when the action occurs
     */
    public class BackButton implements ActionListener{
        private JFrame frame;
        private JPanel panel;
        
        /**
         * Creates new back button
         * @param frame the frame being drawn to
         * @param panel the panel that it is switched to
         */
        public BackButton(JFrame frame, JPanel panel){
            this.frame = frame;
            this.panel = panel;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            frame.setContentPane(panel);
            panel.revalidate();
            panel.repaint();
        }
    }    
}
