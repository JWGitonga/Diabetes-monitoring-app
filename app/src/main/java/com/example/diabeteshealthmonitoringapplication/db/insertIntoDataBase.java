package com.example.diabeteshealthmonitoringapplication.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class insertIntoDataBase {
	public static void main(String[] args) {
		String URL="jdbc:mysql://localhost:3306/martin";
		String userName="root";
		String password="1234";
		
		try {
			//create a connection to the database
			Connection connection=DriverManager.getConnection(URL,userName, password);
			System.out.println("connection to the database was successful");
			
			//insert values into the database
			String sql="INSERT INTO customer(firstname, lastname) VALUES(?, ?)";
			//create a statement
			PreparedStatement preparedStatement=connection.prepareStatement(sql);
			preparedStatement.setString(1, "Daniel");
			preparedStatement.setString(2, "Wanjala");
			
			int rows= preparedStatement.executeUpdate();
			if(rows>0) {
				System.out.println("1 row updated successful");
			}
			//close the statement
			preparedStatement.close();
			
			//close the connection
			connection.close();
		}
		catch(SQLException ex) {
			ex.printStackTrace();
			System.out.println("could not connect to the database");
		}
	}

}
