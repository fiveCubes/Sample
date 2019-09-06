package sample;
import javafx.application.Application;
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
		fridgeDSC.connect();

		/* TODO 2-01 - TO COMPLETE ****************************************
		 * call the data source controller database connect method
		 * NOTE: that database connect method throws exception
		 */
	}

	public void start(Stage stage) throws Exception {
        build(stage);
		stage.setTitle("What's in my Fridge v1.0");
		stage.show();

		/* TODO 2-02 - TO COMPLETE ****************************************
		 * - this method is the start method for your application
		 * - set application title
		 * - show the stage
		 */


		/* TODO 2-03 - TO COMPLETE ****************************************
		 * currentThread uncaught exception handler
		 */
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

		tv.getColumns().add(idColumn);
		tv.getColumns().add(itemNameColumn);
		tv.getColumns().add(quantityColumn);
		tv.getColumns().add(sectionColumn);
		tv.getColumns().add(daysAgoColumn);

		/* TODO 2-05 - TO COMPLETE ****************************************
		 * add table columns to the table view create above
		 */


		//	Attach table data to the table view
		//tv.setItems(tableData);


		/* TODO 2-06 - TO COMPLETE ****************************************
		 * set minimum and maximum width to the table view and each columns
		 */


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

		  //buttons
		  Button addBtn = new Button("Add");
		  Button updateBtn = new Button("Update");
		  Button deletBtn = new Button("Delete");
		  HBox buttonBox = new HBox (addBtn,updateBtn,deletBtn);


		  //Add controls
		  ComboBox<Item> itemAdd = new ComboBox<Item>();

		  ChoiceBox<FridgeDSC.SECTION> sectionAdd = new ChoiceBox<>();
		  sectionAdd.getItems().addAll(FridgeDSC.SECTION.values());

		  TextField quantityAdd = new TextField();
		  itemAdd.getItems().addAll(fridgeDSC.getAllItems());
		  HBox addBox = new HBox (itemAdd,sectionAdd,quantityAdd);

		  Button clearBtn = new Button("Clear");
		  Button saveBtn = new Button("Save");

		  HBox saveBox = new HBox (clearBtn,saveBtn);

		  VBox hiddenAddBox = new VBox(addBox,saveBox);
		  hiddenAddBox.setVisible(false);
		  addBtn.setOnAction(e -> hiddenAddBox.setVisible(true));

		  saveBtn.setOnAction(e->
		  {
			  //System.out.println(itemAdd.getValue().getName() + sectionAdd.getValue().toString() + quantityAdd.getText());
			  try {
				  fridgeDSC.addGrocery(itemAdd.getValue().getName(), Integer.parseInt(quantityAdd.getText()), sectionAdd.getValue());
				  tableData.clear();
				  tableData.addAll(fridgeDSC.getAllGroceries());
				  tv.getSelectionModel().clearSelection();
				  hiddenAddBox.setVisible(false);
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
			  	System.out.println(ex.toString() );
			  }
		  });

		  //update button

		updateBtn.setOnAction(e ->
		{
			hiddenAddBox.setVisible(false);
			Grocery g = tv.getSelectionModel().getSelectedItem();
			try {
				fridgeDSC.useGrocery(g.getId());
				tableData.clear();
				tableData.addAll(fridgeDSC.getAllGroceries());
				tv.getSelectionModel().clearSelection();
			}
			catch (Exception exception)
			{
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setContentText(exception.toString());
				Optional<ButtonType> result = alert.showAndWait();
			}

		});

		// delete Button

		deletBtn.setOnAction(e ->
		{
			hiddenAddBox.setVisible(false);
			Grocery g = tv.getSelectionModel().getSelectedItem();
			try {
				Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
				alert.setContentText("Are you sure? Delete Selected Grocery?");
				Optional<ButtonType> result = alert.showAndWait();
				if (result.isPresent())
				{
					if(result.get() == ButtonType.OK)
					{
						fridgeDSC.removeGrocery(g.getId());
						tableData.clear();
						tableData.addAll(fridgeDSC.getAllGroceries());
						tv.getSelectionModel().clearSelection();
					}
				}
			}
			catch (Exception exception)
			{
				System.out.println(exception);
			}

		});




		filterchoice.getSelectionModel().selectedItemProperty().addListener((observableValue,oldValue,newValue) -> {
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

		FilteredList<Grocery> filterList = new FilteredList<>(tableData, p -> true);
		SortedList<Grocery> sortedList = new SortedList<>(filterList);
		sortedList.comparatorProperty().bind(tv.comparatorProperty());
		tv.setItems(sortedList);




		/* TODO 2-09 - TO COMPLETE ****************************************
		 * filter container - part 2:
		 * - addListener to the "Filter By" ChoiceBox to clear the filter
		 *   text field vlaue and to enable the "Show Expire Only" CheckBox
		 *   if "BOUGHT_DAYS_AGO" is selected
		 */

		filterTextField.textProperty().addListener((observableValue,oldValue,newValue) ->
				{
					System.out.println("listener activated");
					filterList.setPredicate(grocerydata ->
					{
						System.out.println(newValue);
						if (newValue == null || newValue.isEmpty()) {
							System.out.println("null value");
							return true;
						}
						String filterString = newValue.toUpperCase();
						System.out.println(filterString);
						if (filterchoice.getValue().equalsIgnoreCase("Item"))
						{
							if (grocerydata.getItemName().toUpperCase().contains(filterString))
							{
								System.out.println("filter applied for items");
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
							if(filtercb.isSelected())
							{
								if(grocerydata.getDaysAgo().contains(filterString) && grocerydata.getItem().canExpire())
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
								if(grocerydata.getDaysAgo().contains(filterString))
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


					});
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

		/* TODO 2-11 - TO COMPLETE ****************************************
		 * filter container - part 3:
		 * - create a filtered list
		 * - create a sorted list from the filtered list
		 * - bind comparators of sorted list with that of table view
		 * - set items of table view to be sorted list
		 * - set a change listener to text field to set the filter predicate
		 *   of filtered list
		 */		


		/* TODO 2-12 - TO COMPLETE ****************************************
		 * ACTION buttons: ADD, UPDATE ONE, DELETE
		 * - ADD button sets the add UI elements to visible;
		 *   NOTE: the add input controls and container may have to be
		 * 		   defined before these action controls & container(s)
		 * - UPDATE ONE and DELETE buttons action need to check if a
		 *   table view row has been selected first before doing their
		 *   action; hint: should you also use an Alert confirmation?
		 */		

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

		/* TODO 2-14 - TO COMPLETE ****************************************
		 * - add all your containers, controls to the root
		 */		

		root.setStyle(
			"-fx-font-size: 20;" +
			"-fx-alignment: center;"
		);

		Scene scene = new Scene(root,700,500);
		stage.setScene(scene);
	}

	public void stop() throws Exception {

		/* TODO 2-15 - TO COMPLETE ****************************************
		 * call the data source controller database disconnect method
		 * NOTE: that database disconnect method throws exception
		 */				
	}	
}