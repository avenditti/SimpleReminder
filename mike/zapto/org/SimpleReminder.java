package mike.zapto.org;
/*
 * Author: Alex Venditti
 * This is a simple program to keep track of daily tasks
 *
 *
 */
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SimpleReminder extends Application implements Runnable{

	ArrayList<ListElement> listElements;

	public static void main(String[] args) throws Exception {
		new Thread(new SimpleReminder()).start();
	}

	@Override
	public void start(Stage stage) throws Exception {
		VBox root = new VBox();
		HBox hbox = new HBox();
		Button load = new Button("Load");
		Button newList = new Button("Create New List");
		TextArea textField = new TextArea();
		root.getChildren().add(new Label(
				  "Add each entry on its own line in the format name,description\n"
				+ "Make sure that each entry is on a single line\n"
				+ "Descriptions are added as a tooltip, to view them hover over the name of the entry\n"
				));
		hbox.getChildren().add(newList);
		hbox.getChildren().add(load);
		root.getChildren().add(textField);
		root.getChildren().add(hbox);
		hbox.setAlignment(Pos.TOP_CENTER);
		hbox.setSpacing(20);
		root.setAlignment(Pos.CENTER);
		root.setSpacing(20);
		textField.setPrefHeight(350);
		stage.setScene(new Scene(root,500,500));
		stage.show();

		load.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				stage.close();
				showList(load());
			}
		});

		newList.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				Scanner scan = new Scanner(textField.getText());
				listElements = new ArrayList<ListElement>();
				while(scan.hasNextLine()) {
					String entry = scan.nextLine() + ",No Description";
					String[] list = entry.split(",");
					if(list.length>=2) {
						listElements.add(new ListElement(list[0],list[1]));
					} else {
						System.out.println("Invalid Entry: " + entry);
					}
				}
				save();
				scan.close();
				stage.close();
				showList(listElements);
			}
		});

	}

	@SuppressWarnings("unchecked")
	private ArrayList<ListElement> load() {
		listElements = new ArrayList<ListElement>();
		try {
			Object temp;
			FileInputStream fos = new FileInputStream("list.data");
			ObjectInputStream oos = new ObjectInputStream (fos);
			temp = oos.readObject();
			if(temp instanceof ArrayList<?>) {
				listElements = (ArrayList<ListElement>) temp;
			}
			oos.close();
			return listElements;
		} catch(IOException | ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Corrupt list, try new list");
			listElements.add(new ListElement("Error Importing List",""));
			return listElements;
		}
	}

	private void showList(ArrayList<ListElement> listElements) {
		Stage list = new Stage();
		ScrollPane sp = new ScrollPane();
		int size = listElements.size()*50;
		if(size > 800)	size = 800;
		VBox root = new VBox();
		for(ListElement l : listElements) {
			Line line = new Line();
			root.getChildren().add(l.getListEntry());
			root.getChildren().add(line);
		}
		list.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				save();
			}
		});
		root.setPrefWidth(380);
		sp.setContent(root);
		list.setScene(new Scene(sp, 400, size));
		list.show();
	}

	private void save() {
		try {
			FileOutputStream fos = new FileOutputStream("list.data");
			ObjectOutputStream oos = new ObjectOutputStream (fos);
			oos.writeObject(listElements);
			oos.close();
		} catch(IOException e) {
			e.printStackTrace();
			System.out.println("Error Saving");
		}
	}

	@Override
	public void run() {
		SimpleReminder.launch();
	}
}

@SuppressWarnings("serial")
class ListElement implements Serializable{

	private boolean isDone;
	private String name;
	private String tooltip;

	public ListElement(String name, String tooltipText) {
		this.name = name;
		this.tooltip = tooltipText;
		isDone = false;

	}

	public HBox getListEntry() {
		CheckBox isDoneCheckBox = new CheckBox(" is Finished?");
		Label name = new Label(this.name);
		HBox hbox = new HBox();
		isDoneCheckBox.setSelected(isDone);
		name.setTooltip(new Tooltip(tooltip));
		if(isDone)	isDoneCheckBox.setSelected(true);
		isDoneCheckBox.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(!isDoneCheckBox.isSelected()) {
					isDone = false;
					hbox.setStyle("-fx-background-color: #ff0000");
				} else {
					isDone = true;
					hbox.setStyle("-fx-background-color: #808080");
				}
			}
		});
		hbox.getChildren().add(name);
		hbox.getChildren().add(isDoneCheckBox);
		if(isDone) {
			hbox.setStyle("-fx-background-color: #808080");
		} else {
			hbox.setStyle("-fx-background-color: #ff0000");
		}
		hbox.setSpacing(30);
		name.setPrefHeight(50);
		isDoneCheckBox.setPrefHeight(50);
		return hbox;
	}

}
