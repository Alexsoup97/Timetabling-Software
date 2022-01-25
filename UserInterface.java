import javax.swing.JTable;
import java.util.ArrayList;
import java.util.Set;
import javax.swing.JTable;
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
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.util.HashMap;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import java.awt.BorderLayout;
import javax.swing.JCheckBox;
import javax.swing.event.RowSorterListener;
import javax.swing.event.RowSorterEvent;

public class UserInterface {
    private final int SCREEN_WIDTH = 900;
    private final int SCREEN_HEIGHT = 900;
  
  
    public UserInterface(ArrayList<ClassInfo> timetable){
        JFrame frame = new JFrame();

       Menu menu = new Menu(frame, timetable);

  
        // SpecialCourseUI specialCourses = new SpecialCourseUI();
        // frame.setContentPane(specialCourses);
       menu.setOpaque(true);
       frame.setContentPane(menu);
        frame.setVisible(true);
        frame.setSize(SCREEN_WIDTH,SCREEN_HEIGHT);
        frame.setLocationRelativeTo(null); //start the frame in the center of the screen
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        

    }
    class Menu extends JPanel{
        private JButton timetableButton = new JButton("Master Timetable");
        private JButton studentButton = new JButton("Student Timetables");
     
        
        public Menu(JFrame frame, ArrayList<ClassInfo> timetable){
            
            MasterTimetable masterTimetablePane = new MasterTimetable(timetable, frame, this);
            StudentTimetable studentPane = new StudentTimetable(frame, this);    
            masterTimetablePane.setOpaque(true);
            timetableButton.addActionListener(new BackButton(frame, masterTimetablePane));
            studentButton.addActionListener(new BackButton(frame, studentPane));     
    
            add(timetableButton);
            add(studentButton);
        }

    }

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
        private JTextField tableFilter = new JTextField();
        private TableRowSorter<TableModel> rowSorter= new TableRowSorter<>(model);
        private Font font = new Font("Calibri", Font.ITALIC, 15);
        private JButton backbutton = new JButton("Back");
        private JPanel menu;
        private JLabel rowCountLabel;
      
        

        public StudentTimetable(JFrame frame, JPanel menu){
            Object[] columns = { "Student Name", "Grade","Student Number", "Sem 1: Period 1", "Sem 1: Period 2", "Sem 1: Period 3", "Sem 1: Period 4", "Sem 2: Period 1", "Sem 2: Period 2", "Sem 2: Period 3", "Sem 2: Period 4" };

    
            this.table = new JTable(model){ 
                @Override
                public boolean isCellEditable(int row, int column){
                    return false;
                }
            };
            model.setColumnIdentifiers(columns);
            table.setForeground(Color.black);

           
            addData();
            table.setRowSorter(rowSorter);
            table.setFont(font);
            //table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            table.getTableHeader().setReorderingAllowed(false);
            table.setRowHeight(20);
            table.getTableHeader().setResizingAllowed(false);

            tableFilter.getDocument().addDocumentListener(new SortListener(tableFilter, rowSorter));
            tableFilter.setBounds(25,(SCREEN_HEIGHT/2) + 25,175,30);

            
            table.getColumnModel().getColumn(STUDENTNAME_COLUMN).setPreferredWidth(STUDENTNAME_COLUMN_WIDTH);
            table.getColumnModel().getColumn(GRADE_COLUMN).setPreferredWidth(GRADE_COLUMN_WIDTH);
            table.getColumnModel().getColumn(STUDENTNUMBER_COLUMN).setPreferredWidth(STUDENTNUMBER_COLUMN_WIDTH);
            for(int i = 3; i < COURSE_COLUMNS; i++){
                table.getColumnModel().getColumn(i).setPreferredWidth(COURSE_COLUMN_WIDTH);
            }

           
            backbutton.addActionListener(new BackButton(frame, menu));
            backbutton.setBounds(25, 700, 75,40);
          
            JLabel searchLabel = new JLabel("Search");
            searchLabel.setForeground(Color.black);
            searchLabel.setBounds(25,(SCREEN_HEIGHT/2) + 5,175,30);
            searchLabel.setFont(font);

            JLabel fullTimetable = new JLabel("Full timetables: " + Data.results[0]);
            JLabel topChoices = new JLabel("Top Choices fulfilled: " + Data.results[1]);
            JLabel alternateChoices = new JLabel("Alternate Choices fulfilled: " + Data.results[2]);
            JLabel percentage = new JLabel("Percentage of Courses Filled: " + Data.results[1] / Data.courseCount);
            fullTimetable.setBounds(SCREEN_WIDTH/2 + 250, (SCREEN_HEIGHT/2) + 20, 175, 30);
            topChoices.setBounds(SCREEN_WIDTH/2 + 250, (SCREEN_HEIGHT/2) + 35, 175, 30);
            alternateChoices.setBounds(SCREEN_WIDTH/2 + 250, (SCREEN_HEIGHT/2) + 50, 175, 30);


            rowCountLabel = new JLabel("Number of rows " + table.getRowCount());
            rowCountLabel.setBounds(SCREEN_WIDTH/2 + 250, (SCREEN_HEIGHT/2) + 5, 175, 30);
            pane = new JScrollPane(table);  
            
            rowSorter.addRowSorterListener(new RowCounter(table, rowCountLabel));
            pane.setBounds(0, 0, SCREEN_WIDTH-3, SCREEN_HEIGHT/2);
            
            setLayout(null);     
            add(backbutton);
            add(searchLabel);
            add(tableFilter);
            add(pane);
            add(rowCountLabel);
            add(fullTimetable);
            add(topChoices);
            add(alternateChoices);
        }

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
      
        }

    }

    class MasterTimetable extends JPanel{
        private  JTable table;
        private DefaultTableModel model= new DefaultTableModel();;
        private JScrollPane pane;
        private JButton backbutton = new JButton("Back");
        private JTextField tableFilter = new JTextField();
        private TableRowSorter<TableModel> rowSorter= new TableRowSorter<>(model);
    

        public MasterTimetable(ArrayList<ClassInfo> timetable, JFrame frame, JPanel menu){
            Object[] columns = { "Course", "Room", "Semester", "Period", "Student Count" };
            
            
            this.table = new JTable(model){ 
                @Override
                public boolean isCellEditable(int row, int column){
                    return false;
                }
            };
            model.setColumnIdentifiers(columns);
            table.setForeground(Color.black);

           
            addData(timetable);
            table.setRowSorter(rowSorter);
        
            table.getTableHeader().setReorderingAllowed(false);
            table.setRowHeight(20);
            table.getTableHeader().setResizingAllowed(false);

            tableFilter.getDocument().addDocumentListener(new SortListener(tableFilter, rowSorter));
            tableFilter.setBounds(25,(SCREEN_HEIGHT/2) + 25,175,30);
           
            backbutton.addActionListener(new BackButton(frame, menu));
            backbutton.setBounds(25, 700, 30,40);
          
            JLabel searchLabel = new JLabel("Search");

            pane = new JScrollPane(table);  
            
            pane.setBounds(0, 0, SCREEN_WIDTH-3, SCREEN_HEIGHT/2);
            
            setLayout(null);     
            add(backbutton);
            add(searchLabel);
            add(tableFilter);
            add(pane);
         
            
        }


        public void addData(ArrayList<ClassInfo> timetable){
            Object[] data = new Object[5];
            for(ClassInfo classes: timetable){
                
                data[0] = classes.getCourse();
                data[1] = classes.getRoom();
                data[2] = (int)classes.getTimeslot()/4 +1;
                data[3] = (classes.getTimeslot()) %4 + 1;
                data[4] = classes.getStudents().size();
                
                model.addRow(data);
            }


        }
        
    }

    class SpecialCourseUI extends JPanel{
        private Collection<String> values = Data.courseMap.keySet();
        private String[] courseCode= values.toArray(new String[0]);
        
        private  JComboBox<String> courseBox = new JComboBox<String>(courseCode);
        private JCheckBox[] timeslotSelection = new JCheckBox[Data.NUM_PERIODS];
        private JButton updateCourses = new JButton("Update");
    
    
        public SpecialCourseUI(){
            courseBox.setBounds(0, 0, 175, 25);
            add(courseBox);
            for(int i = 1; i < timeslotSelection.length-1; i++){
                timeslotSelection[i-1] = new JCheckBox("Semester: " + (int)Math.floor(i/4) + "Period: " + i%4);
                timeslotSelection[i-1].setBounds(500 , 230+ i*10, 175,25);
                add(timeslotSelection[i-1]);
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

                  //  Data.userSpecialCourses.put(courseBox.getSelectedItem(), selected);
                }
            });

           // setLayout(new BorderLayout());
            add(updateCourses);
                

        }

        
    }

    public class SortListener implements DocumentListener{
        private JTextField tableFilter;
        private TableRowSorter<TableModel> rowSorter;

        public SortListener(JTextField tableFilter, TableRowSorter<TableModel> rowSorter){
            this.tableFilter = tableFilter;
            this.rowSorter = rowSorter;

        }
        @Override
        public void insertUpdate(DocumentEvent e) {
            String text = tableFilter.getText();

            if (text.trim().length() == 0) {
                rowSorter.setRowFilter(null);
            } else {
                rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            String text = tableFilter.getText();

            if (text.trim().length() == 0) {
                rowSorter.setRowFilter(null);
            } else {
                rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    public class RowCounter implements RowSorterListener{

        JTable table;
        JLabel label;
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


    public class BackButton implements ActionListener{
        private JFrame frame;
        private JPanel panel;
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
