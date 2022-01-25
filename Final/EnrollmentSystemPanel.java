import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Timer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import java.util.ArrayList;
import java.io.IOException;


/**
 * [EnrollmentSystemPanel.java]
 * Displays the GUI that allows users to add students to the club
 * @author Alex, Nicholas, Samson
 * @version 1.0 Oct 14, 2021
**/
public class EnrollmentSystemPanel {

    private static ArrayList<Student> studentList = new ArrayList <Student>();
    private static JTable table;
    private static DefaultTableModel model;
    private static JScrollPane pane;
    private static JTextField textName = new JTextField();
    private static JTextField textID = new JTextField();
    private static JTextField textGrade = new JTextField();
    private static JTextField friendPrefOne = new JTextField();
    private static JTextField friendPrefTwo = new JTextField();
    private static JTextField friendPrefThree = new JTextField();
    private static String[] groups = {"intro", "contest", "web"};
    private static JComboBox<String> group = new JComboBox<String>(groups);
    private static JLabel errorMessage = new JLabel();

    /**
     * SystemManager constructor
     */
    public EnrollmentSystemPanel() {
          Object[] columns = { "Name", "ID", "Grade", "Friends", "Group"};

          model = new DefaultTableModel();
          table = new JTable(model){
              @Override
              public boolean isCellEditable(int row, int column){
                  return false;
              }
          };
          model.setColumnIdentifiers(columns);
          table.setForeground(Color.black);
          Font font = new Font("Calibri", Font.ITALIC, 15);
          table.setFont(font);
          table.setRowHeight(20);
          pane = new JScrollPane(table);           
          pane.setBounds(0, 0, 900, 200);
    }

    /** generateJTable
     * generates the jtable on the enrollment system panel
     */
    public void generateJTable() {
        JFrame frame = new JFrame();

        JButton btnAdd = new JButton("Add");
        JButton btnDelete = new JButton("Delete");
        JButton btnUpdate = new JButton("Update");
        JButton btnBack = new JButton("Back");

        JLabel name = new JLabel("Name");
        JLabel ID = new JLabel("Student ID");
        JLabel grade = new JLabel("Grade");
        JLabel friends = new JLabel("Friends (Student ID)");
        JLabel groupText = new JLabel("Group");
       
        errorMessage.setForeground(Color.red);
        errorMessage.setFont(new Font("Calibri", Font.BOLD, 25));
        errorMessage.setBounds(460, 400, 420, 70);
        name.setForeground(Color.black);
        name.setFont(new Font("Calibri", Font.ITALIC, 15));
        name.setBounds(50, 205, 300, 25);
        ID.setForeground(Color.black);
        ID.setFont(new Font("Calibri", Font.ITALIC, 15));
        ID.setBounds(50, 260, 250, 25);
        grade.setForeground(Color.black);
        grade.setFont(new Font("Calibri", Font.ITALIC, 15));
        grade.setBounds(50, 315, 300, 25);
        friends.setForeground(Color.black);
        friends.setFont(new Font("Calibri", Font.ITALIC, 15));
        friends.setBounds(260, 205, 300, 25);
        groupText.setForeground(Color.black);
        groupText.setFont(new Font("Calibri", Font.ITALIC, 15));
        groupText.setBounds(460, 205, 300, 25);
    
        textName.setBounds(50, 230, 150, 25);
        textID.setBounds(50, 285, 150, 25);
        textGrade.setBounds(50, 340, 150, 25);
        friendPrefOne.setBounds(260, 230, 150, 25);
        friendPrefTwo.setBounds(260, 285, 150, 25);
        friendPrefThree.setBounds(260, 340, 150, 25);
        group.setBounds(460, 230, 175, 25);

        btnBack.setBounds(50, 400, 100, 25);
        btnAdd.setBounds(690, 230, 100, 25);
        btnUpdate.setBounds(690, 285, 100, 25);
        btnDelete.setBounds(690, 340, 100, 25);

        frame.setLayout(null);
        frame.add(pane);
        frame.add(friendPrefOne);
        frame.add(friendPrefTwo);
        frame.add(friendPrefThree);
        frame.add(group);
    
        frame.add(textName);
        frame.add(textID);
        frame.add(textGrade);
        frame.add(name);
        frame.add(ID);
        frame.add(grade);
        frame.add(friends);
        frame.add(groupText);
        frame.add(errorMessage);

        frame.add(btnAdd);
        frame.add(btnDelete);
        frame.add(btnUpdate);
        frame.add(btnBack);
       
        //button add action listener
        btnAdd.addActionListener(new ActionListener() {
            /**
             * actionPerformed 
             * checks all interactions when a key is pressed
             * @param event, ActionEvent
            */
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTable(-1);
            }
        });

        //button delete action listener
        btnDelete.addActionListener(new ActionListener() {
            /**
             * actionPerformed 
             * checks all interactions when a key is pressed
             * @param event, ActionEvent
            */
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = table.getSelectedRow();

                if (i >= 0) {
                    removeStudent(i);
                    textName.setText("");
                    textID.setText("");
                    textGrade.setText("");
                    friendPrefOne.setText("");
                    friendPrefTwo.setText("");
                    friendPrefThree.setText("");
                } else {
                    errorMessage.setText("Please select a valid row");
                    clearErrorMessage(errorMessage);
                }
            }
        });

        // mouse listener
        table.addMouseListener(new MouseAdapter() {
            @Override
            /**
             * mouseClicked 
             * checks all interactions when a mouse is clicked
             * @param event, MouseEvent
            */
            public void mouseClicked(MouseEvent e) {
                int[] updatedFriends;
                int i = table.getSelectedRow();

                textName.setText(getTableValue(i, 0));
                textID.setText(getTableValue(i, 1));
                textGrade.setText(getTableValue(i, 2)); 
                group.setSelectedItem(getTableValue(i,4));

                updatedFriends = studentList.get(i).getFriendPreferences();
                if(updatedFriends[0] == -1){
                    friendPrefOne.setText("");
                }else {
                    friendPrefOne.setText(Integer.toString(updatedFriends[0]));
                }
                if(updatedFriends[1] == -1){
                    friendPrefTwo.setText("");
                }else{
                    friendPrefTwo.setText(Integer.toString(updatedFriends[1]));
                }
                if(updatedFriends[2] == -1){
                    friendPrefThree.setText("");
                }else{
                    friendPrefThree.setText(Integer.toString(updatedFriends[2]));
                }
            }
        });

        //button update action listener
        btnUpdate.addActionListener(new ActionListener() {
            /**
             * actionPerformed 
             * checks all interactions when a key is pressed
             * @param event, ActionEvent
            */
            @Override           
            public void actionPerformed(ActionEvent e) {
                int i = table.getSelectedRow();

                if (i >= 0) {
                    updateTable(i);
                } else {
                    errorMessage.setText("Please select a valid row");
                    clearErrorMessage(errorMessage);
                }
            }
        });

        //button back action listener
        btnBack.addActionListener(new ActionListener() {
            /**
             * actionPerformed 
             * checks all interactions when a key is pressed
             * @param event, ActionEvent
            */
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        frame.setSize(900, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    /**clearErrorMessage
     * Removes the Jlabel after 2.5 seconds
     * @param errorMessage the display label
     */
    public static void clearErrorMessage(JLabel errorMessage) {
        Timer timer = new Timer(2500, new ActionListener() {
            /**
             * actionPerformed 
             * checks all interactions when a key is pressed
             * @param event, ActionEvent
            */
            @Override
            public void actionPerformed(ActionEvent e) {
                errorMessage.setText("");
            }
        });
        timer.start();
        timer.setRepeats(false);    
    }

    /** getTableValue
     * returns the value at the specified location 
     * @param i the row of the table
     * @param j the column of the table
     * @return String the value in the table
     */
    public static String getTableValue(int i, int j){
        return model.getValueAt(i, j).toString();
    }

    /** removeStudent
     * removes student from the table and arraylist
     * @param i student being removed
     */
    public static void removeStudent(int i) {
        studentList.remove(i);
        model.removeRow(i); 
    }

    /** updateStudent
     * updates a student in the arraylist and table
     * @param i the student being updated
     * @param data an object array of the updated values
     * @param friends an array of the friend prefrence id's
     */
    public static void updateStudent(int i, Object[] data, int[] friends) {
        String name = (String) data[0];
        int id = (int) data[1];
        int grade = (int) data[2];
        String group = (String) data[4];

        studentList.set(i, new Student(name, id, grade, friends, group));

        model.setValueAt(data[0], i, 0);
        model.setValueAt(data[1], i, 1);
        model.setValueAt(data[2], i, 2);
        model.setValueAt(data[3], i, 3);
        model.setValueAt(data[4], i, 4);
    }

    /** addStudent
     * adds the student to the arraylist and table
     * @param data,an object array of the student values
     * @param friends,an array of the friend prefrence id's
     */
    public static void addStudent(Object[] data, int[] friends) {  
        model.addRow(data);

        String name = (String)data[0];
        int id = (int) data[1];
        int grade = (int) data[2];
        String group = (String) data[4];    

        studentList.add(new Student(name, id, grade, friends, group));
    }
 
    /**getStudentList
     * gets the student arrayList
     * @return ArrayList<Student>
     */
    public ArrayList<Student> getStudentList(){
        return studentList;
    }

    /**updateTable
     * updates the table with the inputs provided
     * @param i position of the studnet on the jtable
     */
    public static void updateTable(int i){
        Object data[] = new Object[5];

        try{
            int[] friends = new int[3];
            String friendList = ""; 

            if(!friendPrefOne.getText().equals("")){
                friends[0] = Integer.valueOf(friendPrefOne.getText());
            }else{
                friends[0] = -1;
            }
            if(!friendPrefTwo.getText().equals("")){
                friends[1] = Integer.valueOf(friendPrefTwo.getText());
            }else{
                friends[1] = -1;
            }
            if(!friendPrefThree.getText().equals("")){
                friends[2] = Integer.valueOf(friendPrefThree.getText());
            }else{
                friends[2] = -1;
            }

            data[0] = textName.getText();
            data[1] = Integer.valueOf(textID.getText());

            if(Integer.valueOf(textGrade.getText()) < 9 || Integer.valueOf(textGrade.getText()) > 12){
                throw new IOException();
            }else{
                data[2] = Integer.valueOf(textGrade.getText());
            }
            if (!(friendPrefOne.getText().equals("")  && friendPrefTwo.getText().equals("") && friendPrefThree.getText().equals("") )){
                friendList = String.join(",",(String)friendPrefOne.getText() ,(String)friendPrefTwo.getText(),(String)friendPrefThree.getText());
                //checking commas at the front
                while (friendList.charAt(0) == ','){
                    friendList = friendList.substring(1,friendList.length());
                }
                //checking commas at the end
                while (friendList.charAt(friendList.length()-1) ==','){
                    friendList = friendList.substring(0,friendList.length()-1);
                }
                //checking commas in the middle
                if ( friendList.charAt(friendList.indexOf(",")+1) == ','){
                    friendList= friendList.substring(0,friendList.indexOf(",")+1) + friendList.substring(friendList.indexOf(",")+2,friendList.length()); 
                }
            }
            
            data[3] = friendList;
            data[4] = group.getSelectedItem(); 
            textName.setText("");
            textID.setText("");
            textGrade.setText("");
            friendPrefOne.setText("");
            friendPrefTwo.setText("");
            friendPrefThree.setText("");

            if(i == -1){
                addStudent(data, friends);
            }else{
                updateStudent(i, data, friends);
            }
        }catch (NumberFormatException a) {
            errorMessage.setText("Inccorect Inputs");
            clearErrorMessage(errorMessage);
        }catch(IOException b){
            errorMessage.setText("Invalid Grade");
            clearErrorMessage(errorMessage);
        }
    }
}