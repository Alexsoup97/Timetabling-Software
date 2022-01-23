import javax.swing.JTable;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JPanel;

public class UserInterface {
    private JFrame frame;

    
    
    public UserInterface(ArrayList<ClassInfo> timetable){
        frame = new JFrame();
        MasterTimetable MasterTimetablePane = new MasterTimetable(timetable);
        MasterTimetablePane.setOpaque(true);
        frame.setContentPane(MasterTimetablePane);
        frame.setVisible(true);
        frame.setSize(900, 900);
        frame.setLocationRelativeTo(null); //start the frame in the center of the screen
        frame.setResizable(true);
        

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
}
