/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package learningtracker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.text.Text;

/**
 *
 * @author Admin
 */
public class MainPageController implements Initializable {

    int seconds = 0;
    int second = 0;
    int minus = 0;
    int hours = 0;
    boolean running = false;

    public Label workLb = null;

    @FXML
    private Button stopBt;

    @FXML
    private Label hourLb;

    @FXML
    private Button resetBt;

    @FXML
    private ChoiceBox<String> workChoiceB;

    @FXML
    private Label secondLb;

    @FXML
    private Button viewWorkBt;


    @FXML
    private Button addWorkBt;

    @FXML
    private Label minusLb;

    @FXML
    private Button startBt;

//    NumberAxis xAsis = new NumberAxis(0, 24, 1);
//    NumberAxis yAsis = new NumberAxis(0, 31, 1);
    double[] dataForChart;

    @FXML
    private LineChart<String, Number> trackerGraph;

    ArrayList<String> workList = new ArrayList<>();

    /**
     * tên của file chứa nhãn công việc
     *
     */
    public final String workLabelFile = "workLabel.txt";
    @FXML
    private Label workLb1;

    public void generalChartData() {
	this.dataForChart = new double[31];
	Calendar cal = Calendar.getInstance();
	int month = 1 + cal.get(Calendar.MONTH);

	for (int i = 0; i < 31; i++) {
	    int hoursOfDate = 0;
	    String filename = String.valueOf(i + 1) + "_" + String.valueOf(month) + ".txt";
	    System.out.println(filename);
	    File recordOfDate = new File(filename);
	    if (recordOfDate.exists()) {

		try {
		    Scanner reader = new Scanner(recordOfDate);
		    while (reader.hasNextLine()) {
			String data = reader.nextLine();
			String[] split = data.split(",");
			hoursOfDate += Integer.valueOf(split[0]);
		    }
		    this.dataForChart[i] = (hoursOfDate / 36000);
		    System.out.println(dataForChart[i]);

		} catch (FileNotFoundException ex) {
		    Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
		}
	    } else {
		this.dataForChart[i] = 0;
	    }
	}
    }

    public void labelChartData(String label) {
	this.dataForChart = new double[31];
	Calendar cal = Calendar.getInstance();
	int month = 1 + cal.get(Calendar.MONTH);

	for (int i = 0; i < 31; i++) {
	    double hoursOfDate = 0;
	    String filename = String.valueOf(i + 1) + "_" + String.valueOf(month) + ".txt";
	    System.out.println(filename);
	    File recordOfDate = new File(filename);
	    if (recordOfDate.exists()) {
		try {
		    Scanner reader = new Scanner(recordOfDate);
		    while (reader.hasNextLine()) {
			String data = reader.nextLine();
			String[] split = data.split(",");
			if (split[1].equals(label)) {
			    System.out.println("phat hien co hoc tap_" + split[0]);
			    hoursOfDate += Integer.valueOf(split[0]);
			}
		    }
		    this.dataForChart[i] = hoursOfDate / 36000;
		    System.out.println(dataForChart[i]);

		} catch (FileNotFoundException ex) {
		    Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
		}
	    } else {
		this.dataForChart[i] = 0;
	    }
	}
    }

    public void initGeneralChart() {
	generalChartData();
	trackerGraph.setTitle("Thời lượng đã học trong ngày");
	trackerGraph.getData().clear();
	XYChart.Series<String, Number> series = new XYChart.Series<>();
	for (int i = 0; i < 31; i++) {
	    series.getData().add(new XYChart.Data<>(String.valueOf(i + 1), dataForChart[i]));
	}
	series.setName("Tổng");
	this.trackerGraph.getData().add(series);
    }

    public void initLabelChart(String label) {
	labelChartData(label);
	this.trackerGraph.setTitle("Thời lượng dành cho " + label);
	this.trackerGraph.getData().clear();
	XYChart.Series<String, Number> labelSeries = new XYChart.Series<>();
	for (int i = 0; i < 31; i++) {
	    labelSeries.getData().add(new XYChart.Data<>(String.valueOf(i + 1), dataForChart[i]));
	}
	labelSeries.setName(label);
	this.trackerGraph.getData().add(labelSeries);
    }

    public void initWorkLabel() {

	try {
	    File myObj = new File(workLabelFile);
	    if (myObj.createNewFile()) {
		workChoiceB.getItems().add("");
	    } else {
		try (Scanner myReader = new Scanner(myObj)) {
		    while (myReader.hasNextLine()) {
			String data = myReader.nextLine();
			workList.add(data);
			workChoiceB.getItems().add(data);
		    }
		}
	    }
	} catch (IOException e) {
	}

    }

    public void createFile(String filename) {
	try {
	    File myObj = new File(filename);
	    myObj.createNewFile();
	} catch (IOException ex) {
	    Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public void writeToFile(String filename, String data) {
	createFile(filename);
	FileWriter myWriter;
	try {
	    myWriter = new FileWriter(filename, true);
	    myWriter.write(data + "\n");
	    myWriter.close();
	} catch (IOException ex) {
	    Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
	initWorkLabel();
	initGeneralChart();
	this.workChoiceB.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
	    @Override
	    public void changed(ObservableValue<? extends Number> observableValue, Number oldvalue, Number newValue) {
		workLb.setText(workChoiceB.getItems().get(newValue.intValue()));
	    }
	});
    }

    public void setDataToWatch(int second, int minus, int hours) {
	secondLb.setText(String.format("%02d", second));
	minusLb.setText(String.format("%02d", minus));
	hourLb.setText(String.format("%02d", hours));
    }

    @FXML
    void onStartBt(ActionEvent event) {
	if (workLb.getText().equals("")) {
	    Alert chooseWorkLb = new Alert(Alert.AlertType.INFORMATION);
	    chooseWorkLb.setTitle("Chọn công việc sắp làm");
	    chooseWorkLb.setHeaderText("Bạn chưa chọn công việc sắp làm, vui lòng chọn.");
	    Optional<ButtonType> result = chooseWorkLb.showAndWait();
	    return;
	}
	if (this.running == false) {
	    this.running = true;
	} else {
	    saveCurrentWork();
	    resetWatch();
	}
	Thread t = new Thread() {
	    @Override
	    public void run() {
		while (running) {
		    try {
			Thread.sleep(1000);
			seconds++;
			second = seconds % 60;
			minus = seconds / (3600);
			hours = seconds / (216000);
			Platform.runLater(() -> {
			    setDataToWatch(second, minus, hours);
			});
		    } catch (InterruptedException ex) {
			Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
		    }
		}
	    }
	};
	t.start();
    }

    @FXML
    void onStopBt(ActionEvent event) {

	if (stopBt.getText().equals("Dừng")) {
	    stopBt.setText("Tiếp tục");
	    this.running = false;
	} else {
	    this.running = false;
	}
    }

    public void saveCurrentWork() {
	this.running = false;
	Alert confirmSaveWorkAlert = new Alert(Alert.AlertType.CONFIRMATION);
	confirmSaveWorkAlert.setTitle("Reset phiên làm việc");
	confirmSaveWorkAlert.setHeaderText("Bạn có muốn lưu lại phiên làm việc này không?");
	confirmSaveWorkAlert.setContentText(String.format("Bạn đã làm việc trong %02d:%02d:%02d", hours, minus, second));
	Optional<ButtonType> result = confirmSaveWorkAlert.showAndWait();
	if (result.get() == ButtonType.OK) {
//	    System.out.print("save this work");
	    // lấy thời gian để tạo tên file
	    Calendar cal = Calendar.getInstance();
	    int dayofmonth = cal.get(Calendar.DAY_OF_MONTH);
	    int monthofyear = cal.get(Calendar.MONTH) + 1;
	    String montheo = String.valueOf(monthofyear);
	    String dateo = String.valueOf(dayofmonth);
//	    System.out.print(dateo + "/"+montheo);
	    // ghi dữ liệu vào file
	    String dateData = dateo + "_" + montheo + ".txt";
	    String dataToWrite = String.valueOf(seconds) + "," + workLb.getText();
	    writeToFile(dateData, dataToWrite);
	}
    }

    public void resetWatch() {
	seconds = 0;
	second = 0;
	minus = 0;
	hours = 0;
	setDataToWatch(second, minus, hours);
    }

    @FXML
    void onResetBt(ActionEvent event) {
	if (this.running == true) {
	    saveCurrentWork();
	}
	resetWatch();
    }

    void onChooseWorkBt(ActionEvent event) {
	workLb.setText(workChoiceB.getValue());
	System.out.print(workLb);
    }

    @FXML
    void onAddWorkBt(ActionEvent event) {
	// tạo input dialog 
	TextInputDialog inputWorkLb = new TextInputDialog();
	inputWorkLb.setTitle("Thêm nhãn công việc");
	inputWorkLb.setHeaderText("Xin vui lòng nhập vào nhãn công việc bạn muốn thêm");
	Optional<String> showAndWait = inputWorkLb.showAndWait();
	System.out.println("Bạn đã nhập vào: " + showAndWait.get());
	workChoiceB.getItems().add(showAndWait.get());// add label mới vào choice box 
	writeToFile(this.workLabelFile, showAndWait.get());//ghi label mới vào file
    }

    @FXML
    void onViewWorkBt(ActionEvent event) {
	if (workLb.getText().equals("")) {
	    initGeneralChart();
	} else {
	    initLabelChart(workLb.getText());
	}
    }

}
