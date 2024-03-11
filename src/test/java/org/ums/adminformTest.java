package org.ums;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class adminformTest {

    @Test
    void testDatabaseConnection() {
        adminform adminFormm = new adminform();

        try {
            assertNotNull(adminFormm.databaseConnector.getConnection());
            System.out.println("Test Result: Database Connection Successful");
        } catch (SQLException e) {
            System.out.println("Test Result: Database Connection Failed - " + e.getMessage());
            fail("Database connection failed: " + e.getMessage());
        }
    }

    @Test
    void testFillTable() {
        adminform adminFormm = new adminform();

        JTable mockTable = new JTable();

        try {
            adminFormm.fillTable(mockTable, "Students");

            assertTrue(mockTable.getRowCount() > 0);
            System.out.println("Test Result: fillTable Successful - Rows: " + mockTable.getRowCount());
        } catch (SQLException e) {
            System.out.println("Test Result: fillTable Failed - SQLException: " + e.getMessage());
            fail("SQLException during fillTable: " + e.getMessage());
        }
    }


    @Test
    void testGetNextGroupNumber() {
        adminform adminFormm = new adminform();

        try {
            int nextGroupNumber = adminFormm.getNextGroupNumber();

            assertTrue(nextGroupNumber > 0);
            System.out.println("Test Result: getNextGroupNumber Successful - Next Group Number: " + nextGroupNumber);
        } catch (SQLException e) {
            System.out.println("Test Result: getNextGroupNumber Failed - SQLException: " + e.getMessage());
            fail("SQLException during getNextGroupNumber: " + e.getMessage());
        }
    }
}