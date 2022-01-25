import javax.swing.JTable;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
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

public class UserInterface {
    private final int SCREEN_WIDTH = 900;
    private final int SCREEN_HEIGHT = 900;
  
  
    public UserInterface(ArrayList<ClassInfo> timetable){
        JFrame frame = new JFrame();

        Menu menu = new Menu(frame, timetable);
  
       
        

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
            
            MasterTimetable masterTimetablePane = new MasterTimetable(timetable);
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
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            table.getTableHeader().setReorderingAllowed(false);
            
            table.getColumnModel().getColumn(STUDENTNAME_COLUMN).setPreferredWidth(STUDENTNAME_COLUMN_WIDTH);
            table.getColumnModel().getColumn(GRADE_COLUMN).setPreferredWidth(GRADE_COLUMN_WIDTH);
            table.getColumnModel().getColumn(STUDENTNUMBER_COLUMN).setPreferredWidth(STUDENTNUMBER_COLUMN_WIDTH);
            for(int i = 3; i < COURSE_COLUMNS; i++){
                table.getColumnModel().getColumn(i).setPreferredWidth(COURSE_COLUMN_WIDTH);
            }

            tableFilter.getDocument().addDocumentListener(new DocumentListener(){

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

            });

            

            backbutton.addActionListener(new BackButton(frame, menu));
            backbutton.setBounds(25, 700, 30,40);
            

            
            table.setRowHeight(20);
            table.getTableHeader().setResizingAllowed(false);
            JLabel searchLabel = new JLabel("Search");

            searchLabel.setForeground(Color.black);
            searchLabel.setBounds(25,(SCREEN_HEIGHT/2) + 200,175,30);
            searchLabel.setFont(font);
            pane = new JScrollPane(table);  
            
            pane.setBounds(0, 0, SCREEN_WIDTH-3, SCREEN_HEIGHT/2);
            tableFilter.setBounds(25,(SCREEN_HEIGHT/2) + 50,175,30);
            setLayout(null);     
            add(backbutton);
            add(searchLabel);
            add(tableFilter);
            add(pane);
        }

        public void addData(){
            
            for(Student s: Data.studentMap.values()){
                Object[] data = new Object[12];
                data[0] = s.getName().substring(1, s.getName().length()-1);
                data[1] = s.getGrade();
                data[2] = s.getStudentNumber();
                int counter =3;
                for(int i= 0; i<s.getTimetable().length;i++){
                    data[counter]= s.getTimetable()[i];
                    counter++;
                }
                this.model.addRow(data);

            }
      
        }

    }

    class MasterTimetable extends JPanel{
        private  JTable table;
        private DefaultTableModel model;
        private JScrollPane pane;
    

        public MasterTimetable(ArrayList<ClassInfo> timetable){
            Object[] columns = { "Course", "Room", "Period" };

            this.model = new DefaultTableModel();
            this.table = new JTable(model){
                @Override
                public boolean isCellEditable(int row, int column){
                    return false;
                }
            };
            model.setColumnIdentifiers(columns);
            table.setForeground(Color.black);
            Font font = new Font("Calibri", Font.ITALIC, 15);
            
            addData(timetable);
            table.setFont(font);
            table.setRowHeight(20);
            
            
            pane = new JScrollPane(table);           
            pane.setBounds(0, 0, 880, 200);
            add(pane);
            
        }

        public void addData(ArrayList<ClassInfo> timetable){
            Object[] data = new Object[3];
            for(ClassInfo classes: timetable){
                data[0] = classes.getCourse();
                data[1] = classes.getRoom();
                data[2] = classes.getTimeslot();
                this.model.addRow(data);
            }


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
