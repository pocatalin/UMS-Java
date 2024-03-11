package org.ums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class logInTest {

    @Test
    void testLoginActionWithValidProfessorCredentials() {
        logIn loginInstance = new logIn();

        loginInstance.tbUser.setText("1");
        loginInstance.pfPassword.setText("2");

        loginInstance.loginAction();

        System.out.println("Test Result: " + (loginInstance.isVisible() ? "Login Successful" : "Login Failed"));
    }

    @Test
    void testLoginActionWithInvalidCredentials() {
        logIn loginInstance = new logIn();

        loginInstance.tbUser.setText("InvalidUser");
        loginInstance.pfPassword.setText("InvalidPassword");

        loginInstance.loginAction();

        System.out.println("Test Result: " + (loginInstance.isVisible() + "Login Failed"));
    }

    @Test
    void testLoginActionWithEmptyCredentials() {
        logIn loginInstance = new logIn();

        loginInstance.tbUser.setText("");
        loginInstance.pfPassword.setText("");

        loginInstance.loginAction();


        System.out.println("Test Result: " + (loginInstance.isVisible() + "Login Failed"));
    }

    @Test
    void testDatabaseConnection() {
        logIn loginInstance = new logIn();

        boolean isConnected = loginInstance.testDatabaseConnection();

        System.out.println("Test Result: Database Connection " + (isConnected ? "Successful" : "Failed"));
        assertTrue(isConnected);
    }
}
