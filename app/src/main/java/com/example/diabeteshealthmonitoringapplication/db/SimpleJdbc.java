package com.example.diabeteshealthmonitoringapplication.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SimpleJdbc {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        //load the java database driver
        Class.forName("com.mysql.jdbc.Driver");
        System.out.println("Driver loaded");

        //connect to the dataBase						pathName, 								DBName, pswd
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/martin", "root", "1234");
        System.out.println("DataBase connected");

        //create a statement
        Statement statement = connection.createStatement();

        //Execute statement
        //ResultSet resultSet=statement.executeQuery("Select Firstname, LastName from student where lastName"+"=Wainaina");
        ResultSet resultSet = statement.executeQuery("Select * from student");

        //Iterate through the result and print the student names
        while (resultSet.next()) {
            System.out.println(resultSet.getString(1) + "\t" + resultSet.getString(2) + "\t" + resultSet.getString(3));
        }

        //close the connection
        connection.close();
    }

}
