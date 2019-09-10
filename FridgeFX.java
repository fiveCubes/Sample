package sample;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.sql.SQLOutput;
import java.util.*;
import java.io.*;
import javafx.collections.*;
import javafx.collections.transformation.*;
import javafx.scene.control.cell.*;
import javafx.beans.property.*;


public class FridgeFX extends Application {

	// used as ChoiceBox value for filter
	public enum FILTER_COLUMNS {
		ITEM,
		SECTION,
		BOUGHT_DAYS_AGO
	};
	
	// the data source controller
	private FridgeDSC fridgeDSC;
	

	public void init() throws Exception {
		// creating an instance of the data source controller to be used
		// in this application
			fridgeDSC = new FridgeDSC();

		/* TODO 2-01 - TO COMPLETE ****************************************
		 * call the data source controller database connect method
		 * NOTE: that database connect method throws exception
		 */
		try {
			fridgeDSC.connect();
		}
		catch (Exception e)
		{
			System.out.println("Error in connection " + e.toString());
		}
	}

	public void start(Stage stage) throws Exception {

		/* TODO 2-02 - TO COMPLETE ****************************************
		 * - this method is the start method for your application
		 * - set application title
		 * - show the stage
		 */
		build(stage);
		stage.setTitle("What's in my Fridge v1.0");
		stage.show();

		/* TODO 2-03 - TO COMPLETE ****************************************
		 * currentThread uncaught exception handler
		 */

		Thread.currentThread().setUncaughtExceptionHandler((thread,exception) -> {
			System.out.println("Error: " + exception);
		});
	}

	public void build(Stage stage) throws Exception {

		// Create table data (an observable list of objects)
		ObservableList<Grocery> tableData = FXCollections.observableArrayList(fridgeDSC.getAllGroceries());

		// Define table columns
		TableColumn<Grocery, Integer> idColumn = new TableColumn<Grocery, Integer>("Id");
		TableColumn<Grocery, String> itemNameColumn = new TableColumn<Grocery, String>("Item");
		TableColumn<Grocery, Integer> quantityColumn = new TableColumn<Grocery, Integer>("QTY");
		TableColumn<Grocery, String> sectionColumn = new TableColumn<Grocery, String>("Section");
		TableColumn<Grocery, String> daysAgoColumn = new TableColumn<Grocery, String>("Bought");
		
		/* TODO 2-04 - TO COMPLETE ****************************************
		 * for each column defined, call their setCellValueFactory method 
		 * using an instance of PropertyValueFactory
		 */

		idColumn.setCellValueFactory( new PropertyValueFactory<Grocery,Integer>("id"));
		itemNameColumn.setCellValueFactory( new PropertyValueFactory<Grocery, String>("ItemName"));
		quantityColumn.setCellValueFactory( new PropertyValueFactory<Grocery, Integer>("Quantity"));
		sectionColumn.setCellValueFactory( new PropertyValueFactory<Grocery, String>("Section"));
		daysAgoColumn.setCellValueFactory( new PropertyValueFactory<Grocery, String>("DaysAgo"));

		// Create the table view and add table columns to it
		TableView<Grocery> tv = new TableView<Grocery>();

		/* TODO 2-05 - TO COMPLETE ****************************************
		 * add table columns to the table view create above
		 */

		tv.getColumns().add(idColumn);
		tv.getColumns().add(itemNameColumn);
		tv.getColumns().add(quantityColumn);
		tv.getColumns().add(sectionColumn);
		tv.getColumns().add(daysAgoColumn);


		//	Attach table data to the table view
		//tv.setItems(tableData);


		/* TODO 2-06 - TO COMPLETE ****************************************
		 * set minimum and maximum width to the table view and each columns
		 */
		 tv.setMinWidth(500);
		 tv.setMaxWidth(800);
		 quantityColumn.setMinWidth(50);


		/* TODO 2-07 - TO COMPLETE ****************************************
		 * call data source controller get all groceries method to add
		 * all groceries to table data observable list
		 */
	

		// =====================================================
		// ADD the remaining UI elements
		// NOTE: the order of the following TODO items can be 
		// 		 changed to satisfy your UI implementation goals
		// =====================================================

		/* TODO 2-08 - TO COMPLETE ****************************************
		 * filter container - part 1
		 * add all filter related UI elements you identified
		 */

		// top filter elements
		TextField filterTextField = new TextField();
		Label filterLabel = new Label("Filter By");
		ChoiceBox<String> filterchoice = new ChoiceBox<>();
		filterchoice.getItems().addAll("ITEM","SECTION","BOUGHT DAYS AGO");
		filterchoice.setValue("ITEM");

		CheckBox filtercb = new CheckBox();
		filtercb.setDisable(true);
		Label checkBoxLabel = new Label ("Show Expire Only");
		checkBoxLabel.setDisable(true);
		HBox topRow = new HBox(filterTextField,filterLabel,filterchoice,filtercb,checkBoxLabel);
		topRow.setSpacing(5);


		/* TODO 2-09 - TO COMPLETE ****************************************
		 * filter container - part 2:
		 * - addListener to the "Filter By" ChoiceBox to clear the filter
		 *   text field vlaue and to enable the "Show Expire Only" CheckBox
		 *   if "BOUGHT_DAYS_AGO" is selected
		 */


        //listener when filter by check box is selected
		filterchoice.getSelectionModel().selectedItemProperty().addListener((observableValue,oldValue,newValue) -> {
			filterTextField.clear();
			filterTextField.requestFocus();
			if (newValue.equalsIgnoreCase("BOUGHT DAYS AGO"))
			{
				checkBoxLabel.setDisable(false);
				filtercb.setDisable(false);
			}
			else
			{
				checkBoxLabel.setDisable(true);
				filtercb.setDisable(true);

			}
		});

		/* TODO 2-10 - TO COMPLETE ****************************************
		 * filter container - part 2:
		 * - addListener to the "Filter By" ChoiceBox to clear and set focus
		 *   to the filter text field and to enable the "Show Expire Only"
		 *   CheckBox if "BOUGHT_DAYS_AGO" is selected
		 *
		 * - setOnAction on the "Show Expire Only" Checkbox to clear and
		 *   set focus to the filter text field
		 */

		filtercb.setOnAction(event -> {
			filterTextField.clear();
			filterTextField.requestFocus();
		});


		/* TODO 2-11 - TO COMPLETE ****************************************
		 * filter container - part 3:
		 * - create a filtered list
		 * - create a sorted list from the filtered list
		 * - bind comparators of sorted list with that of table view
		 * - set items of table view to be sorted list
		 * - set a change listener to text field to set the filter predicate
		 *   of filtered list
		 */
		FilteredList<Grocery> filterList = new FilteredList<>(tableData, p -> true);
		SortedList<Grocery> sortedList = new SortedList<>(filterList);
		sortedList.comparatorProperty().bind(tv.comparatorProperty());
		tv.setItems(sortedList);




		filterTextField.textProperty().addListener((observableValue,oldValue,newValue) ->
				{

					filterList.setPredicate(grocerydata ->{
							try{
					{
						if (newValue == null || newValue.isEmpty()) {

							return true;
						}

						String filterString = newValue.toUpperCase();
						if (filterchoice.getValue().equalsIgnoreCase("item"))
						{
							if (grocerydata.getItemName().toUpperCase().contains(filterString))
							{
								return true;
							} else
								{
								return false;
							    }
						}
						else if (filterchoice.getValue().equalsIgnoreCase("section"))
						{
							if(grocerydata.getSection().toString().contains(filterString)) {
								return true;
							}
							else
							{
								return false;
							}
						}
						else if (filterchoice.getValue().equalsIgnoreCase("Bought days ago"))
						{
							int daysAgo = grocerydata.getDaysAgo().equalsIgnoreCase("today")? 0:
									Integer.parseInt(grocerydata.getDaysAgo().substring(0,grocerydata.getDaysAgo().indexOf(" ")));
							if(filtercb.isSelected())
							{
								if(daysAgo >= Integer.parseInt(filterTextField.getText()) && grocerydata.getItem().canExpire())
								{
									return true;
								}
								else
								{
									return false;
								}


							}
							else
							{
								if(daysAgo >= Integer.parseInt(filterTextField.getText()))
								{
									return true;
								}
								else
								{
									return false;
								}
							}

						}
						else
						{
							return false;
						}


					}}
							catch(Exception e)
					{
						return false;
					}});
				});





		/* TODO 2-12 - TO COMPLETE ****************************************
		 * ACTION buttons: ADD, UPDATE ONE, DELETE
		 * - ADD button sets the add UI elements to visible;
		 *   NOTE: the add input controls and container may have to be
		 * 		   defined before these action controls & container(s)
		 * - UPDATE ONE and DELETE buttons action need to check if a
		 *   table view row has been selected first before doing their
		 *   action; hint: should you also use an Alert confirmation?
		 */

		//buttons
		Button addBtn = new Button("Add");
		Button updateBtn = new Button("Update");
		Button deletBtn = new Button("Delete");
		HBox buttonBox = new HBox (addBtn,updateBtn,deletBtn);
		buttonBox.setSpacing(5);

		//Add controls
		//Labels for Add items
		Label itemLabel = new Label("Item");
		Label sectionLabel = new Label("Section");
		Label quantityLabel = new Label ("Quantity");

		ComboBox<Item> itemAdd = new ComboBox<Item>();
		itemAdd.getItems().addAll(fridgeDSC.getAllItems());

		ChoiceBox<FridgeDSC.SECTION> sectionAdd = new ChoiceBox<>();
		sectionAdd.getItems().addAll(FridgeDSC.SECTION.values());

		Button clearBtn = new Button("Clear");
		Button saveBtn = new Button("Save");

		TextField quantityAdd = new TextField();
		GridPane addGrid = new GridPane();
		addGrid.setPadding(new Insets(10,10,10,10));
		addGrid.setVgap(5);
		addGrid.setHgap(5);
		addGrid.add(itemLabel,0,0);
		addGrid.add(itemAdd,0,1);
		addGrid.add(sectionLabel,1,0);
		addGrid.add(sectionAdd,1,1);
		addGrid.add(quantityLabel,2,0);
		addGrid.add(quantityAdd,2,1);


		HBox saveBox = new HBox (clearBtn,saveBtn);
		saveBox.setSpacing(5);
		saveBox.setAlignment(Pos.CENTER);

		VBox hiddenAddBox = new VBox(addGrid,saveBox);
		hiddenAddBox.setVisible(false);
		addBtn.setOnAction(e -> hiddenAddBox.setVisible(true));


		saveBtn.setOnAction(e->
		{
			try {
				if ((itemAdd.getValue().getName()!= null) && sectionAdd.getValue()!=null)
				{
					System.out.println(quantityAdd.getText()!=null);
					if(!quantityAdd.getText().isEmpty()){
						Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
						alert.setContentText("Are you sure to add the item?");
						Optional<ButtonType> result = alert.showAndWait();
						if (result.isPresent()) {
							if (result.get() == ButtonType.OK) {
								fridgeDSC.addGrocery(itemAdd.getValue().getName(), Integer.parseInt(quantityAdd.getText()), sectionAdd.getValue());
								tableData.clear();
								tableData.addAll(fridgeDSC.getAllGroceries());
								Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
								infoAlert.setContentText("Item successfully added to the Fridge");
								infoAlert.showAndWait();
								itemAdd.getSelectionModel().clearSelection();
								sectionAdd.getSelectionModel().clearSelection();
								quantityAdd.clear();
								tv.getSelectionModel().clearSelection();
								hiddenAddBox.setVisible(false);
							}

						}
					}
					else
					{
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setContentText("Quantity cannot be empty Please add a Quantity value");
						Optional<ButtonType> result = alert.showAndWait();
					}
				}
				else
				{
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Section cannot be empty Please add a section");
					Optional<ButtonType> result = alert.showAndWait();
				}

			}
			catch (NullPointerException empty)
			{
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setHeaderText(empty.toString());
				alert.setContentText("Item Value Cannot be Empty Please add an Item");
				Optional<ButtonType> result = alert.showAndWait();

			}
			catch (NumberFormatException n)
			{
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setHeaderText(n.toString());
				alert.setContentText(" Please add a number to quantity");
				Optional<ButtonType> result = alert.showAndWait();

			}
			catch(Exception ex)
			{
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setHeaderText(ex.toString());
				//alert.setContentText(" Please add a number to quantity");
				Optional<ButtonType> result = alert.showAndWait();
			}
		});

		//clear button
		clearBtn.setOnAction(e-> {
			itemAdd.getSelectionModel().clearSelection();
			sectionAdd.getSelectionModel().clearSelection();
			quantityAdd.clear();
		});

		//update button

		updateBtn.setOnAction(e ->
		{
			hiddenAddBox.setVisible(false);
			Grocery g = tv.getSelectionModel().getSelectedItem();
			if (g !=null) {
				try {
					Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
					alert.setContentText("Are you sure? You want to Update the selected Grocery?");
					Optional<ButtonType> result = alert.showAndWait();
					if (result.isPresent()) {
						if (result.get() == ButtonType.OK) {
							fridgeDSC.useGrocery(g.getId());
							tableData.clear();
							tableData.addAll(fridgeDSC.getAllGroceries());
							Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
							infoAlert.setContentText(" Item Id " + g.getId() + ": " + g.getItemName() + " quantity is reduced from " + g.getQuantity() + " to " +(g.getQuantity()-1) );
							infoAlert.showAndWait();
							tv.getSelectionModel().clearSelection();
						}
					}

				} catch (Exception exception) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					//alert.setHeaderText(e.toString());
					alert.setContentText(exception.toString());
					Optional<ButtonType> result = alert.showAndWait();

				}
			}
			else
			{
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setContentText("Please select a row from table to update");
				Optional<ButtonType> result = alert.showAndWait();
			}

		});

		// delete Button

		deletBtn.setOnAction(e ->
		{
			hiddenAddBox.setVisible(false);
			Grocery g = tv.getSelectionModel().getSelectedItem();
			if ( g !=null) {
				try {
					Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
					alert.setContentText("Are you sure? Delete Selected Grocery?");
					Optional<ButtonType> result = alert.showAndWait();
					if (result.isPresent()) {
						if (result.get() == ButtonType.OK) {
							fridgeDSC.removeGrocery(g.getId());
							tableData.clear();
							tableData.addAll(fridgeDSC.getAllGroceries());
							Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
							infoAlert.setContentText("Item Id " +g.getId() +": " + g.getItemName() + " is deleted from the grocery " );
							infoAlert.showAndWait();
							tv.getSelectionModel().clearSelection();
						}
					}
				}
				catch (Exception exception)
				{
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText(exception.toString());
					Optional<ButtonType> result = alert.showAndWait();

				}
			}
			else
			{
				Alert alert = new Alert(Alert.AlertType.ERROR);
				//alert.setHeaderText(n.toString());
				alert.setContentText("Please select a row from table to delete");
				Optional<ButtonType> result = alert.showAndWait();

			}


		});


		/* TODO 2-13 - TO COMPLETE ****************************************
		 * add input controls and container(s)
		 * - Item will list item data from the data source controller list
		 *   all items method
		 * - Section will list all sections defined in the data source
		 *   controller SECTION enum
		 * - Quantity: a texf field, self descriptive
		 * - CANCEL button: clears all input controls
		 * - SAVE button: sends the new grocery information to the data source
		 *   controller add grocery method; be mindful of exceptions when any
		 *   or all of the input controls are empty upon SAVE button click
		 */




		// =====================================================================
		// SET UP the Stage
		// =====================================================================
		// Create scene and set stage
		VBox root = new VBox(topRow,tv,buttonBox,hiddenAddBox);
		root.setSpacing(5);
		root.setPadding(new Insets(5,5,5,10));

		/* TODO 2-14 - TO COMPLETE ****************************************
		 * - add all your containers, controls to the root
		 */		

		root.setStyle(
			"-fx-font-size: 20;" +
			"-fx-alignment: center;"
		);

		Scene scene = new Scene(root,820,600);
		stage.setScene(scene);
	}

	public void stop() throws Exception {
		try
		{
			FridgeDSC.disconnect();
		}
		catch (Exception exception)
		{
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText(exception.toString());
			Optional<ButtonType> result = alert.showAndWait();
		}

		/* TODO 2-15 - TO COMPLETE ****************************************
		 * call the data source controller database disconnect method
		 * NOTE: that database disconnect method throws exception
		 */				
	}	
}
