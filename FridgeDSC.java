package sample;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class FridgeDSC {

	// the date format we will be using across the application
	public static final String DATE_FORMAT = "dd/MM/yyyy";

	/*
		FREEZER, // freezing cold
		MEAT, // MEAT cold
		COOLING, // general fridge area
		CRISPER // veg and fruits section

		note: Enums are implicitly public static final
	*/
public enum SECTION {
		FREEZER,
		MEAT,
		COOLING,
		CRISPER
	};

	private static Connection connection;
	private static Statement statement;
	private static PreparedStatement preparedStatement;

	public static void connect() throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver");


			/* TODO 1-01 - TO COMPLETE ****************************************
			 * change the value of the string for the following 3 lines:
			 * - url
			 * - user
			 * - password
			 */

			String url = "jdbc:mysql://localhost:3306/fridgedb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
			String user = "root";
			String password = "vishnu125";
			/*
			String url = "jdbc:mysql://localhost:3306/fridgedb";
			String user = "root";
			String password = "1234";
			 */

			connection = DriverManager.getConnection(url, user, password);
			statement = connection.createStatement();
  		} catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}		
	}

	public static void disconnect() throws SQLException {
		if(preparedStatement != null) preparedStatement.close();
		if(statement != null) statement.close();
		if(connection != null) connection.close();
	}



	public Item searchItem(String name) throws Exception {
		String queryString = "SELECT * FROM item WHERE name = ?";
		preparedStatement = connection.prepareStatement(queryString);
		preparedStatement.setString(1,name);
		ResultSet rs = preparedStatement.executeQuery();


		/* TODO 1-02 - TO COMPLETE ****************************************
		 * - preparedStatement to add argument name to the queryString
		 * - resultSet to execute the preparedStatement query
		 * - iterate through the resultSet result
		 */	


		Item item = null;

		if (rs.next()) { // i.e. the item exists

              String name1 = rs.getString(1);
              Integer expires = Integer.parseInt(rs.getString(2));
              boolean expiry= false;
              if(expires ==1)
			  {
			  	expiry = true;
			  }

              item  = new Item(name1,expiry);

			/* TODO 1-03 - TO COMPLETE ****************************************
			 * - if resultSet has result, get data and create an Item instance
			 */		

		}

		return item;
	}

	public Grocery searchGrocery(int id) throws Exception {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
		String queryString = "SELECT * FROM grocery WHERE id = ?";
		preparedStatement = connection.prepareStatement(queryString);
		preparedStatement.setString(1,id+"");
		ResultSet rs = preparedStatement.executeQuery();

		/* TODO 1-04 - TO COMPLETE ****************************************
		 * - preparedStatement to add argument name to the queryString
		 * - resultSet to execute the preparedStatement query
		 * - iterate through the resultSet result
		 */	


		Grocery grocery = null;

		if (rs.next()) { // i.e. the grocery exists

			int id1 = Integer.parseInt(rs.getString(1));
			String itemName = rs.getString(2);
			LocalDate date = LocalDate.parse(rs.getString(3),dtf);
            int quantity = Integer.parseInt(rs.getString(4));
            String section = rs.getString(5);
            Item item = searchItem(itemName);

            grocery = new Grocery(id1,item,date,quantity,SECTION.valueOf(section));

			/* TODO 1-05 - TO COMPLETE ****************************************
			 * - if resultSet has result, get data and create a Grocery instance
			 * - making sure that the item name from grocery exists in 
			 *   item table (use searchItem method)
			 * - pay attention about parsing the date string to LocalDate
			 */	

		}

		return grocery;
	}

	public List<Item> getAllItems() throws Exception {
		String queryString = "SELECT * FROM item";
		statement = connection.createStatement();
		ResultSet rs = statement.executeQuery(queryString);

		/* TODO 1-06 - TO COMPLETE ****************************************
		 * - resultSet to execute the statement query
		 */
		List<Item> items = new ArrayList<Item>();
		Item item =null;
		while (rs.next())
		{
			String name1 = rs.getString(1);
			Integer expires = Integer.parseInt(rs.getString(2));
			boolean expiry= false;
			if(expires ==1)
			{
				expiry = true;
			}

			item  = new Item(name1,expiry);
			items.add(item);

		}

		/* TODO 1-07 - TO COMPLETE ****************************************
		 * - iterate through the resultSet result, create intance of Item
		 *   and add to list items
		 */	

		return items;
	}

	public List<Grocery> getAllGroceries() throws Exception {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
		String queryString = "SELECT * FROM grocery";
		statement = connection.createStatement();
		ResultSet rs = statement.executeQuery(queryString);


		/* TODO 1-08 - TO COMPLETE ****************************************
		 * - resultSet to execute the statement query
		 */	

		List<Grocery> groceries = new ArrayList<Grocery>();
		Grocery grocery =null;
		while(rs.next())
		{
			int id = Integer.parseInt(rs.getString(1));
			String itemName = rs.getString(2);
			LocalDate date = LocalDate.parse(rs.getString(3),dtf);
			int quantity = Integer.parseInt(rs.getString(4));
			String section = rs.getString(5);
			Item item = searchItem(itemName);

			grocery = new Grocery(id,item,date,quantity,SECTION.valueOf(section));
			groceries.add(grocery);
		}

		/* TODO 1-09 - TO COMPLETE ****************************************
		 * - iterate through the resultSet result, create intance of Item
		 *   and add to list items
		 * - making sure that the item name from each grocery exists in 
		 *   item table (use searchItem method)
		 * - pay attention about parsing the date string to LocalDate
		 */	


		return groceries;
	}


	public int addGrocery(String name, int quantity, SECTION section) throws Exception {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
		LocalDate date = LocalDate.now();
		String dateStr = date.format(dtf);
		if (searchItem(name) ==null)
		{
			throw new Exception ("Item doesnot exist");
		}
		// NOTE: should we check if itemName (argument name) exists in item table?
		//		--> adding a groceries with a non-existing item name should through an exception

		String command = "INSERT INTO grocery(itemName,date,quantity,section) VALUES( ?, ?, ?, ?)";
		PreparedStatement p = connection.prepareStatement(command);
		p.setString(1,name);
		p.setString(2,dateStr);
		p.setString(3,Integer.toString(quantity));
		p.setString(4,section.toString());
        p.executeUpdate();
		/* TODO 1-10 - TO COMPLETE ****************************************
		 * - preparedStatement to add arguments to the queryString
		 * - resultSet to executeUpdate the preparedStatement query
		 */

		// retrieving & returning last inserted record id
		ResultSet rs = statement.executeQuery("SELECT LAST_INSERT_ID()");
		rs.next();
		int newId = rs.getInt(1);

		return newId;		
	}

	public Grocery useGrocery(int id) throws Exception {

		/* TODO 1-11 - TO COMPLETE ****************************************
		 * - search grocery by id
		 * - check if has quantity is greater one; if not throw exception
		 *   with adequate error message
		 */
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
		Grocery g = searchGrocery(id);
        if (g.getQuantity() <2)
		{
			throw new Exception("There is only 1 " + g.getItemName() + " brought on " + g.getDate().format(dtf)+ " -Use Delete Instead");
		}

		String queryString =
				"UPDATE grocery " +
						"SET quantity = quantity - 1 " +
						"WHERE quantity > 1 " +
						"AND id = " + id + ";";

        statement = connection.createStatement();
        statement.executeUpdate(queryString);

        return searchGrocery(id);


		/* TODO 1-12 - TO COMPLETE ****************************************
		 * - statement execute update on queryString
		 * - should the update affect a row search grocery by id and
		 *   return it; else throw exception with adequate error message
		 *
		 * NOTE: method should return instance of grocery
		 */

	}

	public int removeGrocery(int id) throws Exception {
		String queryString = "DELETE FROM grocery WHERE id = " + id + ";";
		Grocery g = searchGrocery(id);
		if(g==null)
		{
			throw new Exception("Grocery doesnot exist");

		}

		statement = connection.createStatement();
		return statement.executeUpdate(queryString);

		/* TODO 1-13 - TO COMPLETE ****************************************
		 * - search grocery by id
		 * - if grocery exists, statement execute update on queryString
		 *   return the value value of that statement execute update
		 * - if grocery does not exist, throw exception with adequate
		 *   error message
		 *
		 * NOTE: method should return int: the return value of a
		 *		 stetement.executeUpdate(...) on a DELETE query
		 */

	}



	// STATIC HELPERS -------------------------------------------------------

	public static long calcDaysAgo(LocalDate date) {
    	return Math.abs(Duration.between(LocalDate.now().atStartOfDay(), date.atStartOfDay()).toDays());
	}

	public static String calcDaysAgoStr(LocalDate date) {
    	String formattedDaysAgo;
    	long diff = calcDaysAgo(date);

    	if (diff == 0)
    		formattedDaysAgo = "today";
    	else if (diff == 1)
    		formattedDaysAgo = "yesterday";
    	else formattedDaysAgo = diff + " days ago";	

    	return formattedDaysAgo;			
	}

	// To perform some quick tests	
	/*public static void main(String[] args) throws Exception {
		FridgeDSC myFridgeDSC = new FridgeDSC();

		myFridgeDSC.connect();

		System.out.println("\nSYSTEM:\n");

		System.out.println("\n\nshowing all of each:");
		System.out.println(myFridgeDSC.getAllItems());
		System.out.println(myFridgeDSC.getAllGroceries());

		//int addedId = myFridgeDSC.addGrocery("Milk", 40, SECTION.COOLING);
		//System.out.println("added: " + addedId);
		//System.out.println("deleting " + (addedId - 1) + ": " + (myFridgeDSC.removeGrocery(addedId - 1) > 0 ? "DONE" : "FAILED"));
		//System.out.println("using " + (addedId) + ": " + myFridgeDSC.useGrocery(addedId));
		//System.out.println(myFridgeDSC.searchGrocery(addedId));

		myFridgeDSC.disconnect();
	}

	 */
	
}