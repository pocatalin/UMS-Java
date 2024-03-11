package org.ums;

import java.awt.event.*;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.*;

public class profForm extends JFrame {

    public profForm(String fName, String lName, String profID) {
        initComponents();

        DatabaseConnector databaseConnector = new SQLDBConnector();
        helloProfLabel.setText(fName + " " + lName + ",");
        DataFunctionsProfessor profDataPopulator = new DataFunctionsProfessor(databaseConnector, panelCourses);

        btGradesProf.addActionListener(e -> {
            btGradesAdd.setEnabled(true);
            btGradesAdd.setVisible(true);
            btGradesView.setEnabled(true);
            btGradesView.setVisible(true);
            btAttedenceAdd.setVisible(false);
            btAttedenceAdd.setEnabled(false);
            btAttedenceView.setEnabled(false);
            btAttedenceView.setVisible(false);
            btStudents.setVisible(false);
            btStudents.setEnabled(false);
            btPersonal.setEnabled(false);
            btPersonal.setVisible(false);
            panelCourses.removeAll();
            panelCourses.revalidate();
            panelCourses.repaint();

        });
        btAttedenceProf.addActionListener(e -> {
            btGradesAdd.setEnabled(false);
            btGradesAdd.setVisible(false);
            btGradesView.setEnabled(false);
            btGradesView.setVisible(false);
            btAttedenceAdd.setVisible(true);
            btAttedenceAdd.setEnabled(true);
            btAttedenceView.setEnabled(true);
            btAttedenceView.setVisible(true);
            btStudents.setVisible(false);
            btStudents.setEnabled(false);
            btPersonal.setEnabled(false);
            btPersonal.setVisible(false);
            panelCourses.removeAll();
            panelCourses.revalidate();
            panelCourses.repaint();

        });
        btData.addActionListener(e -> {
            btGradesAdd.setEnabled(false);
            btGradesAdd.setVisible(false);
            btGradesView.setEnabled(false);
            btGradesView.setVisible(false);
            btAttedenceAdd.setVisible(false);
            btAttedenceAdd.setEnabled(false);
            btAttedenceView.setEnabled(false);
            btAttedenceView.setVisible(false);
            btStudents.setVisible(true);
            btStudents.setEnabled(true);
            btPersonal.setEnabled(true);
            btPersonal.setVisible(true);


        });
        btStudents.addActionListener(e -> {
            profDataPopulator.populateStudentsFromDatabase(profID);
        });

        btGradesView.addActionListener(e -> profDataPopulator.handleButtonClick(profID, "Grades"));

        btAttedenceView.addActionListener(e -> profDataPopulator.handleButtonClick(profID, "Attendance"));

        btGradesAdd.addActionListener(e -> openAddGrade(profID));

        btAttedenceAdd.addActionListener(e -> openAddAtte(profID));

        btPersonal.addActionListener(e -> {
            profDataPopulator.populateInfoFromDatabase(profID);
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(panelProf);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        btGradesAdd.setEnabled(false);
        btGradesAdd.setVisible(false);
        btGradesView.setEnabled(false);
        btGradesView.setVisible(false);
        btAttedenceAdd.setVisible(false);
        btAttedenceAdd.setEnabled(false);
        btAttedenceView.setEnabled(false);
        btAttedenceView.setVisible(false);
        btStudents.setVisible(false);
        btStudents.setEnabled(false);
        btPersonal.setEnabled(false);
        btPersonal.setVisible(false);
    }

    private void openAddGrade(String profID)
    {
        agForm addgradeForm=new agForm(profID);
        addgradeForm.setVisible(true);
    }
    private void openAddAtte(String profID)
    {
        aAddForm atteform = new aAddForm(profID);
        atteform.setVisible(true);
    }

    private void btGradesProf(ActionEvent e) {
        // TODO add your code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - Pistol Ovidiu Catalin
        panelProf = new JPanel();
        var helloProf = new JLabel();
        helloProfLabel = new JLabel();
        btGradesView = new JButton();
        btGradesAdd = new JButton();
        btAttedenceProf = new JButton();
        btGradesProf = new JButton();
        btData = new JButton();
        btAttedenceAdd = new JButton();
        btAttedenceView = new JButton();
        panelCourses = new JPanel();
        panelAttaProf = new JPanel();
        panelProfInfo = new JPanel();
        btStudents = new JButton();
        btPersonal = new JButton();

        //======== panelProf ========
        {
            panelProf.setPreferredSize(new Dimension(1000, 600));
            panelProf.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(new
            javax.swing.border.EmptyBorder(0,0,0,0), "",javax
            .swing.border.TitledBorder.CENTER,javax.swing.border.TitledBorder.BOTTOM,new java
            .awt.Font("D\u0069al\u006fg",java.awt.Font.BOLD,12),java.awt
            .Color.red),panelProf. getBorder()));panelProf. addPropertyChangeListener(new java.beans.
            PropertyChangeListener(){@Override public void propertyChange(java.beans.PropertyChangeEvent e){if("\u0062or\u0064er".
            equals(e.getPropertyName()))throw new RuntimeException();}});

            //---- helloProf ----
            helloProf.setFont(new Font("JetBrains Mono", Font.BOLD, 18));
            helloProf.setText("Hello ");

            //---- helloProfLabel ----
            helloProfLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 18));
            helloProfLabel.setText("Hello");

            //---- btGradesView ----
            btGradesView.setText("View Grades");
            btGradesView.addActionListener(e -> btGradesProf(e));

            //---- btGradesAdd ----
            btGradesAdd.setText("Add Grade");
            btGradesAdd.addActionListener(e -> btGradesProf(e));

            //---- btAttedenceProf ----
            btAttedenceProf.setText("Attedence");

            //---- btGradesProf ----
            btGradesProf.setText("Grades");
            btGradesProf.setPreferredSize(new Dimension(40, 26));
            btGradesProf.addActionListener(e -> btGradesProf(e));

            //---- btData ----
            btData.setText("Data");

            //---- btAttedenceAdd ----
            btAttedenceAdd.setText("Attedence Add");

            //---- btAttedenceView ----
            btAttedenceView.setText("Attedence View");

            //======== panelCourses ========
            {
                panelCourses.setLayout(new FormLayout(
                    "11*(default, $lcgap), default",
                    "2*(default, $lgap), default"));
            }

            //======== panelAttaProf ========
            {
                panelAttaProf.setLayout(new FormLayout(
                    "default, $lcgap, default",
                    "2*(default, $lgap), default"));
            }

            //======== panelProfInfo ========
            {
                panelProfInfo.setLayout(new FormLayout(
                    "default, $lcgap, default",
                    "2*(default, $lgap), default"));
            }

            //---- btStudents ----
            btStudents.setText("Students");

            //---- btPersonal ----
            btPersonal.setText("Personal Info");

            GroupLayout panelProfLayout = new GroupLayout(panelProf);
            panelProf.setLayout(panelProfLayout);
            panelProfLayout.setHorizontalGroup(
                    panelProfLayout.createParallelGroup()
                            .addGroup(panelProfLayout.createSequentialGroup()
                                    .addComponent(panelCourses, GroupLayout.PREFERRED_SIZE, 645, GroupLayout.PREFERRED_SIZE)
                                    .addGap(1, 1, 1)
                                    .addGroup(panelProfLayout.createParallelGroup()
                                            .addComponent(panelAttaProf, GroupLayout.PREFERRED_SIZE, 493, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(panelProfInfo, GroupLayout.PREFERRED_SIZE, 493, GroupLayout.PREFERRED_SIZE)))
                            .addGroup(panelProfLayout.createSequentialGroup()
                                    .addGap(10, 10, 10)
                                    .addGroup(panelProfLayout.createParallelGroup()
                                            .addGroup(panelProfLayout.createSequentialGroup()
                                                    .addComponent(helloProf)
                                                    .addGap(23, 23, 23)
                                                    .addComponent(helloProfLabel))
                                            .addComponent(btGradesView, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
                                            .addGroup(panelProfLayout.createSequentialGroup()
                                                    .addGroup(panelProfLayout.createParallelGroup()
                                                            .addGroup(panelProfLayout.createSequentialGroup()
                                                                    .addComponent(btGradesProf, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                                    .addGap(10, 10, 10)
                                                                    .addComponent(btAttedenceProf)
                                                                    .addGap(14, 14, 14)
                                                                    .addComponent(btData))
                                                            .addComponent(btGradesAdd, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
                                                            .addGroup(panelProfLayout.createSequentialGroup()
                                                                    .addGap(90, 90, 90)
                                                                    .addGroup(panelProfLayout.createParallelGroup()
                                                                            .addComponent(btAttedenceAdd)
                                                                            .addComponent(btAttedenceView))))
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(panelProfLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                            .addComponent(btData, GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                                                            .addComponent(btStudents, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                            .addComponent(btPersonal, GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))))
                                    .addContainerGap())
            );

            panelProfLayout.setVerticalGroup(
                    panelProfLayout.createParallelGroup()
                            .addGroup(panelProfLayout.createSequentialGroup()
                                    .addGap(5, 5, 5)
                                    .addGroup(panelProfLayout.createParallelGroup()
                                            .addComponent(helloProf)
                                            .addComponent(helloProfLabel))
                                    .addGap(16, 16, 16)
                                    .addGroup(panelProfLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                            .addComponent(btAttedenceProf, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addGroup(panelProfLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(btData, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(btStudents))
                                            .addComponent(btGradesProf, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE))
                                    .addGap(6, 6, 6)
                                    .addGroup(panelProfLayout.createParallelGroup()
                                            .addGroup(panelProfLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(btAttedenceAdd)
                                                    .addComponent(btPersonal))
                                            .addComponent(btGradesAdd))
                                    .addGap(6, 6, 6)
                                    .addGroup(panelProfLayout.createParallelGroup()
                                            .addComponent(btGradesView)
                                            .addComponent(btAttedenceView))
                                    .addGroup(panelProfLayout.createParallelGroup()
                                            .addGroup(panelProfLayout.createSequentialGroup()
                                                    .addGap(77, 77, 77)
                                                    .addGroup(panelProfLayout.createParallelGroup()
                                                            .addComponent(panelAttaProf, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(panelProfInfo, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE))
                                                    .addGap(99, 99, 99))
                                            .addGroup(GroupLayout.Alignment.TRAILING, panelProfLayout.createSequentialGroup()
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(panelCourses, GroupLayout.PREFERRED_SIZE, 211, GroupLayout.PREFERRED_SIZE)
                                                    .addContainerGap())))
            );
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Evaluation license - Pistol Ovidiu Catalin
    private JPanel panelProf;
    private JLabel helloProfLabel;
    private JButton btGradesView;
    private JButton btGradesAdd;
    private JButton btAttedenceProf;
    private JButton btGradesProf;
    private JButton btData;
    private JButton btAttedenceAdd;
    private JButton btAttedenceView;
    private JPanel panelCourses;
    private JPanel panelAttaProf;
    private JPanel panelProfInfo;
    private JButton btStudents;
    private JButton btPersonal;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
