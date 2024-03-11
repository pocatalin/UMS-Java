package org.ums;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class menuForm extends JFrame {
    private final DataGetter dataGetter;

    public menuForm(String username, Integer studentID) {
        initComponents();
        DatabaseConnector databaseConnector = new SQLDBConnector();
        DefaultListModel<String> listModel = new DefaultListModel<>();
        DefaultTableModel tableModel = new DefaultTableModel(null, new Object[]{"StudentID", "CourseName", "W1", "W2", "W3", "W4", "W5", "W6", "W7", "W8", "W9", "W10", "W11", "W12", "W13", "W14", "TotalAttendance"});
        dataGetter = new DataFunctionsStudent(
                listModel,
                tableModel,
                databaseConnector,
                panelMenu,
                listMenu,
                table,
                panelStudentInfo,
                panelGrades,
                panelAtta
        );
        helloUserLabel.setText(username + ",");


        btGrades.addActionListener(e -> new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                setComponentVisibility(panelStudentInfo, false);
                setComponentVisibility(listMenu, false);
                setComponentVisibility(table, false);
                setComponentVisibility(panelGrades, true);
                panelGrades.setEnabled(true);
                panelAtta.setEnabled(false);
                panelAtta.setVisible(false);
                populateGradesFromDatabase(studentID);
                return null;
            }
        }.execute());

        btAttedence.addActionListener(e -> new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                setComponentVisibility(panelStudentInfo, false);
                setComponentVisibility(listMenu, false);
                setComponentVisibility(table, true);
                setComponentVisibility(panelGrades, false);
                setComponentVisibility(panelAtta, true);
                panelAtta.setEnabled(true);
                table.setEnabled(true);
                panelGrades.setVisible(false);
                panelGrades.setEnabled(false);
                populateTabledFromDatabase(studentID);
                return null;
            }
        }.execute());

        btInfoStudent.addActionListener(e -> new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                setComponentVisibility(panelStudentInfo, true);
                setComponentVisibility(listMenu, false);
                setComponentVisibility(table, false);
                panelGrades.setEnabled(false);
                panelGrades.setVisible(false);
                panelStudentInfo.setEnabled(true);
                panelAtta.setEnabled(false);
                panelAtta.setVisible(false);
                populateTableFromDatabase(studentID);
                return null;
            }
        }.execute());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(panelMenu);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        com.formdev.flatlaf.FlatDarkLaf.installLafInfo();

    }

    private void setComponentVisibility(JComponent component, boolean visible) {
        SwingUtilities.invokeLater(() -> {
            component.setVisible(visible);
            component.setEnabled(visible);
        });
    }

    private void populateTableFromDatabase(Integer studentID) {
        try {
            dataGetter.populateTableFromDatabase(studentID);
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    private void populateTabledFromDatabase(Integer studentID) {
        try {
            dataGetter.populateTabledFromDatabase(studentID);
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    private void populateGradesFromDatabase(Integer studentID) {
        try {
            dataGetter.populateGradesFromDatabase(studentID);
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    private void handleSQLException(SQLException e) {
        Logger logger = Logger.getLogger(SQLQueries.class.getName());
        logger.log(Level.SEVERE, "SQLException", e);
    }

    public void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - Pistol Ovidiu Catalin
        panelMenu = new JPanel();
        panelStudentInfo = new JPanel();
        var label1 = new JLabel();
        btGrades = new JButton();
        JButton btGrades2 = new JButton();
        btAttedence = new JButton();
        btInfoStudent = new JButton();
        helloUserLabel = new JLabel();
        listMenu = new JScrollPane();
        table = new JTable();
        panelGrades = new JPanel();
        panelAtta = new JPanel();

        //======== panelMenu ========
        {
            panelMenu.setPreferredSize(new Dimension(650, 400));
            dracusamaduplicated(panelMenu);
            panelMenu.setLayout(new FormLayout(
                "[49px,default], left:4dlu, [4px,default], left:4dlu, [493px,default]",
                "default:grow, 3*($lgap, default)"));

            //======== panelStudentInfo ========
            {
                panelStudentInfo.setLayout(new FormLayout(
                    "5*(default, $lcgap), default",
                    "3*(default, $lgap), default"));
            }
            panelMenu.add(panelStudentInfo, new CellConstraints(5, 1, 1, 1, CC.FILL, CC.FILL, new Insets(60, 0, 0, 0)));

            //---- label1 ----
            label1.setFont(new Font("JetBrains Mono", Font.BOLD, 18));
            label1.setText("Hello ");
            panelMenu.add(label1, new CellConstraints(1, 1, 1, 1, CC.CENTER, CC.TOP, new Insets(10, 0, 0, 0)));

            //---- btGrades ----
            btGrades.setText("Grades");
            panelMenu.add(btGrades, new CellConstraints(1, 1, 1, 1, CC.DEFAULT, CC.TOP, new Insets(90, 0, 0, 0)));

            //---- btGrades2 ----
            btGrades2.setText("Grades");
            panelMenu.add(btGrades2, new CellConstraints(1, 1, 1, 3, CC.DEFAULT, CC.TOP, new Insets(90, 0, 0, 0)));

            //---- btAttedence ----
            btAttedence.setText("Attedence");
            panelMenu.add(btAttedence, new CellConstraints(1, 1, 1, 1, CC.DEFAULT, CC.TOP, new Insets(120, 0, 0, 0)));

            //---- btInfoStudent ----
            btInfoStudent.setText("Student Info");
            panelMenu.add(btInfoStudent, new CellConstraints(1, 1, 1, 1, CC.DEFAULT, CC.TOP, new Insets(150, 0, 0, 0)));

            //---- helloUserLabel ----
            helloUserLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 18));
            helloUserLabel.setText("Hello");
            panelMenu.add(helloUserLabel, new CellConstraints(3, 1, 1, 1, CC.CENTER, CC.TOP, new Insets(10, 0, 0, 0)));
            panelMenu.add(listMenu, new CellConstraints(5, 1, 1, 1, CC.FILL, CC.FILL, new Insets(60, 0, 0, 0)));
            panelMenu.add(table, new CellConstraints(5, 5, 1, 1, CC.FILL, CC.FILL, new Insets(60, 0, 0, 0)));

            //======== panelGrades ========
            {
                panelGrades.setLayout(new FormLayout(
                    "default, $lcgap, default",
                    "2*(default, $lgap), default"));
            }
            panelMenu.add(panelGrades, new CellConstraints(5, 1, 1, 1, CC.FILL, CC.FILL, new Insets(60, 0, 0, 0)));

            //======== panelAtta ========
            {
                panelAtta.setLayout(new FormLayout(
                    "default, $lcgap, default",
                    "2*(default, $lgap), default"));
            }
            panelMenu.add(panelAtta, new CellConstraints(5, 1, 1, 1, CC.FILL, CC.FILL, new Insets(60, 0, 0, 0)));
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    static void dracusamaduplicated(JPanel panelMenu) {
        panelMenu.setBorder (new javax. swing. border. CompoundBorder( new javax .swing .border .TitledBorder (new javax. swing. border. EmptyBorder( 0
        , 0, 0, 0) , "JF\u006frmD\u0065sig\u006eer \u0045val\u0075ati\u006fn", javax. swing. border. TitledBorder. CENTER, javax. swing. border. TitledBorder. BOTTOM
        , new Font ("Dia\u006cog" , Font .BOLD ,12 ), Color. red) ,
        panelMenu. getBorder( )) );
        panelMenu. addPropertyChangeListener (e-> {if ("\u0062ord\u0065r" .equals (e .getPropertyName () )) throw new RuntimeException( ); } );
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Evaluation license - Pistol Ovidiu Catalin
    private JPanel panelMenu;
    private JPanel panelStudentInfo;
    private JButton btGrades;
    private JButton btAttedence;
    private JButton btInfoStudent;
    private JLabel helloUserLabel;
    private JScrollPane listMenu;
    private JTable table;
    private JPanel panelGrades;
    private JPanel panelAtta;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
