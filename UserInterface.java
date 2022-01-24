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
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.util.HashMap;

public class UserInterface {
  

    
    
    public UserInterface(ArrayList<ClassInfo> timetable){
        JFrame frame = new JFrame();
       
        MasterTimetable masterTimetablePane = new MasterTimetable(timetable);
        StudentTimetable studentPane = new StudentTimetable();
        Menu menu = new Menu(frame, masterTimetablePane, studentPane);
       
        
        masterTimetablePane.setOpaque(true);
        menu.setOpaque(true);
        frame.setContentPane(menu);
        frame.setVisible(true);
        frame.setSize(900, 900);
        frame.setLocationRelativeTo(null); //start the frame in the center of the screen
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        

    }
    class Menu extends JPanel{
        private JButton timetableButton = new JButton("Master Timetable");
        private JButton studentButton = new JButton("Student Timetables");
        
        public Menu(JFrame frame, MasterTimetable masterTimetablePane, StudentTimetable studentPane){
            timetableButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                   frame.setContentPane(masterTimetablePane);
                    masterTimetablePane.revalidate();
                    masterTimetablePane.repaint();
                }
            });

            studentButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                   frame.setContentPane(studentPane);
                    studentPane.revalidate();
                    studentPane.repaint();
                }
            });

           
           
            add(timetableButton);
            add(studentButton);
        }
       


    }

    class StudentTimetable extends JPanel{
        private  JTable table;
        private DefaultTableModel model;
        private JScrollPane pane;

        public StudentTimetable(){
            Object[] columns = { "Student Name", "Grade","Student Number", "Sem 1: Period 1", "Sem 1: Period 2", "Sem 1: Period 3", "Sem 1: Period 4" };

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
            
            // addData(timetable);
            table.setFont(font);
            table.setRowHeight(20);
            pane = new JScrollPane(table);  
            pane.setPreferredSize(new Dimension(1000, 200));
            setLayout(new BorderLayout());         
            add(pane, BorderLayout.CENTER);
        }

        public void addData(){
            Object[] data = new Object[11];
        
            for(Student s: Data.studentMap.values()){
                data[0] = s.getName();
                data[1] = s.getGrade();
                data[2] = s.getStudentNumber();
                int counter =3;
                for(int i= 0; i<s.getTimetable().length;i++){
                    data[counter]= s.getTimetable()[i];
                    counter++;
                }
            
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
            
            // addData(timetable);
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
}
