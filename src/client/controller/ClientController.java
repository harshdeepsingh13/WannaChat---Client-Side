package client.controller;

import client.threads.MyClientThread;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.URL;
import java.util.*;

/**
 * Created by hdsingh2015 on 26-07-2017.
 */
public class ClientController implements Initializable {
    @FXML private Label nameLabel,nameInChat,onlineLabel;
    @FXML private Button broadcastMessageButton,sendButton;
    @FXML private ImageView googleContactImage;
    @FXML private ListView chatListView,listView;
    @FXML private TextArea messageTextArea;
    @FXML private ImageView logoImageView;
    @FXML private GridPane chatSideGridPane;
    @FXML private SplitPane mySplitPane;
    @FXML private BorderPane parentBorderPane;
    @FXML private MenuItem aboutMenuItem, logoutMenuItem;
    @FXML private StackPane parentStackPane;

    private ObservableList<TextFlow> clientsObservableList = FXCollections.observableArrayList();
    private Socket socket;
    private Map<Integer,String> namesMapFromServer;
    private int myIndexOnServer;
    private String nameOfTheUser;
    private boolean giveNametoServer=false;
    private MyClientThread myClientThread=null;
    private CheckBox selectAllCheckBox;
    private CheckBox[] allClientsAvailable;
    private Set<String> choices = new HashSet<>();
    private Map<Integer,ObservableList<TextFlow>> chatsMap = new HashMap<>();
    private KeyCombination keyCombCrtlEnter = new KeyCodeCombination(KeyCode.ENTER);
    private Label onlineLabel1 = new Label("No one online.");
    private AnchorPane myAnchorPane;
    private GridPane myGridPane;
    private ImageView mainLogoImageView = new ImageView();
    private AnchorPane paneLogo = new AnchorPane();
    private AnchorPane paneLabel = new AnchorPane();
    private HBox hBox = new HBox();
    private Properties properties = new Properties();
    private Stage myStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            parentBorderPane.requestFocus();
            chatListView.setFocusTraversable(true);
            chatListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            onlineLabel1.setAlignment(Pos.CENTER);
            onlineLabel1.setContentDisplay(ContentDisplay.CENTER);
            onlineLabel1.setTextAlignment(TextAlignment.CENTER);
            onlineLabel1.setFont(Font.font("System",FontPosture.ITALIC,13.0));
            hBox.getChildren().add(onlineLabel1);
            HBox.setMargin(onlineLabel1,new Insets(0,10,0,10));
            paneLabel.getChildren().add(hBox);
            paneLabel.setMaxWidth(150.0);
            paneLabel.setMinWidth(150.0);
            myAnchorPane = (AnchorPane) mySplitPane.getItems().remove(0);
            mySplitPane.prefWidthProperty().bind(parentBorderPane.prefWidthProperty());
            mySplitPane.getItems().add(0,paneLabel);
            myGridPane = (GridPane) mySplitPane.getItems().remove(1);
            mainLogoImageView.setImage(new Image("client/resources/images/Wanna_Chat_all-01.png"));
            mainLogoImageView.setOpacity(0.33);
            mainLogoImageView.setPreserveRatio(true);
            mainLogoImageView.setSmooth(true);
            mainLogoImageView.setFitHeight(390.0);
            mainLogoImageView.setFitWidth(447.0);
            mainLogoImageView.setLayoutX(60.0);
            mainLogoImageView.setLayoutY(35.0);
            mainLogoImageView.setPickOnBounds(true);
            paneLogo.getChildren().add(mainLogoImageView);
            mySplitPane.getItems().add(1,paneLogo);
            googleContactImage.setImage(new Image("client/resources/images/ic_account_circle_black_48dp.png"));
            logoImageView.setImage(new Image("client/resources/images/Wanna_Chat_all-01.png"));
            listView.setItems(clientsObservableList);
            if(FXCollections.observableArrayList() == listView.getItems()) System.out.println("clientsObservableList is true.");
            else System.out.println("false");
            broadcastMessageButton.setDisable(true);
            boolean flag=true;
            while(true){
                TextInputDialog textInputDialog = new TextInputDialog();
                textInputDialog.setTitle("Login");
                textInputDialog.setHeaderText("Enter your name to Login");
                textInputDialog.setContentText("Please enter your name (dont begin with space):");
                textInputDialog.getDialogPane().lookupButton(ButtonType.CANCEL).setDisable(true);
                Stage stage = (Stage) textInputDialog.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image("client/resources/images/Wanna_Chat_logo-01.png"));
                Optional<String> result = textInputDialog.showAndWait();
                System.out.println("cancel check");
                if(flag)
                {
                    ArrayList<Object> arrayListFromServer = makeConnection();
                    namesMapFromServer = (Map<Integer, String>) arrayListFromServer.get(0);
                    myIndexOnServer = (int) arrayListFromServer.get(1);
                    System.out.println("myIndexOnServer: " + myIndexOnServer);
                    flag=false;
                }
                if(result.isPresent())
                {
                    System.out.println("Okay");
                    if(!namesMapFromServer.isEmpty())
                    {
                        nameOfTheUser = result.get();
                        Character ch = nameOfTheUser.charAt(0);
                        System.out.println("ch: " + ch);
                        if(namesMapFromServer.containsValue(nameOfTheUser) || Character.isWhitespace(ch))
                        {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Name already exists");
                            alert.setHeaderText(null);
                            alert.setContentText("The name you entered already exists or it is incorrect, try another one.\nHint:\tYou can use alphanumeric name(or username).");
                            Stage stage1 = (Stage) alert.getDialogPane().getScene().getWindow();
                            stage1.getIcons().add(new Image("client/resources/images/Wanna_Chat_logo-01.png"));
                            alert.showAndWait();
                        }
                        else
                        {
                            nameLabel.setText("Hello, " + nameOfTheUser + "!!\t");
                            giveNametoServer = true;
                            break;
                        }
                    }
                    else
                    {
                        nameOfTheUser = result.get();
                        Character ch = nameOfTheUser.charAt(0);
                        System.out.println("ch: " + ch);
                        if(Character.isWhitespace(ch))
                        {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Name already exists");
                            alert.setHeaderText(null);
                            alert.setContentText("The name you entered already exists or it is incorrect, try another one.\nHint:\tYou can use alphanumeric name(or username).");
                            Stage stage1 = (Stage) alert.getDialogPane().getScene().getWindow();
                            stage1.getIcons().add(new Image("client/resources/images/Wanna_Chat_logo-01.png"));
                            alert.showAndWait();
                        }
                        else {
                            nameOfTheUser = result.get();
                            nameLabel.setText("Hello, " + nameOfTheUser + "!!\t");
                            giveNametoServer = true;
                            break;
                        }
                    }
                }
                else
                {
                    System.out.println("Canceled");
                    DataOutputStream d = new DataOutputStream(socket.getOutputStream());
                    d.writeUTF("1 " + myIndexOnServer); d.flush();
                    System.exit(0);
                }

            }
            myClientThread = new MyClientThread(this);
            myClientThread.start();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ConnectException e)
        {
            aboutMenuItem.setDisable(true);
            logoutMenuItem.setDisable(true);
            Label label = new Label();
            label.setText("Server Error...");
            VBox tempContainer = new VBox(label);
            tempContainer.setAlignment(Pos.CENTER);
            label.setStyle("-fx-font-size: 35px; -fx-font-weight: bold; -fx-text-fill: RED;");
            parentBorderPane.setDisable(true);
            parentBorderPane.setStyle("-fx-opacity: 0.3");
            parentStackPane.getChildren().add(tempContainer);
            PauseTransition pauseTransition = new PauseTransition(Duration.seconds(4));
            pauseTransition.play();
            pauseTransition.setOnFinished(event1 ->{
                System.exit(0);
                    }
            );

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("aboutMenuItem onAction");
        aboutMenuItem.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("/client/resources/images/Wanna_Chat_logo-01.png"));
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/client/resources/styles/dialogStyle.css").toExternalForm());
            alert.getDialogPane().getStyleClass().add("dialogStyle");
            alert.setTitle("About");
            alert.setHeaderText("About WannaChat Client");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/client/resources/others/About.txt")));

            StringBuffer stringBuffer = new StringBuffer();
            String s;
            try {
                while((s=bufferedReader.readLine())!=null)
                {
                    stringBuffer.append(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            s = stringBuffer.toString();
            alert.setContentText("WannaChat - Client\n" + s);
            alert.getButtonTypes().remove(ButtonType.CANCEL);
            alert.showAndWait();
        });
        logoutMenuItem.setOnAction(event -> {
            ProgressIndicator progressIndicator = new ProgressIndicator();
            VBox progressContainer = new VBox(progressIndicator);
            progressContainer.setAlignment(Pos.CENTER);
            parentBorderPane.setDisable(true);
            parentBorderPane.setStyle("-fx-opacity: 0.3");
            parentStackPane.getChildren().add(progressContainer);
            Thread thread = new Thread(() -> {
                try {
                    Thread.currentThread().sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Stage is Closing");
                removingClient();
                try {
                    Thread.currentThread().sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private ArrayList<Object> makeConnection() throws ClassNotFoundException, IOException, ConnectException {
        socket = new Socket("localhost",2000);
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        return (ArrayList<Object>) objectInputStream.readObject();
    }

    public boolean isGiveNametoServer() {
        return giveNametoServer;
    }

    public String getNameOfTheUser() {
        return nameOfTheUser;
    }

    public Socket getSocket() {
        return socket;
    }

    public int getMyIndexOnServer() {
        return myIndexOnServer;
    }

    public ObservableList<TextFlow> getClientsObservableList() {
        return clientsObservableList;
    }

    public Map<Integer, ObservableList<TextFlow>> getChatsMap() {
        return chatsMap;
    }

    public void addToClientsObservableList(Object o)
    {
        clientsObservableList.add(new TextFlow(new Text((String) o)));
    }

    public ListView getChatListView() {
        return chatListView;
    }

    public ListView getListView() {
        return listView;
    }

    public void setMyStage(Stage myStage) {
        this.myStage = myStage;
    }

    public Stage getMyStage() {
        return myStage;
    }

    public void removingClient() {

        try {
            myClientThread.getMyClientChattingThread().closeThisClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public void refreshOnlineLabel()
    {
        int no = clientsObservableList.size();
        if(no<=0){
            onlineLabel.setText("No one online."); broadcastMessageButton.setDisable(true);
            mySplitPane.getItems().clear();
            mySplitPane.getItems().add(0,paneLabel);
            mySplitPane.getItems().add(1,paneLogo);
        }
        else if (no==1){
            onlineLabel.setText("1 person online.");broadcastMessageButton.setDisable(false);
            setMySplitPaneBack();
        }
        else {
            onlineLabel.setText(no + " people online.");broadcastMessageButton.setDisable(false);
            setMySplitPaneBack();
        }

    }
    public void broadcastButtonPressed(ActionEvent event) {
        System.out.println("Event triggered: " + event);
        //for custom dialog box https://youtu.be/BweQ7SCNRBA
        Dialog<ArrayList<String>> dialog = new Dialog<>();
        dialog.setTitle("Broadcast Message");
        dialog.setHeaderText("Select recipients.");
        ButtonType nextButton = new ButtonType("Next", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(nextButton,ButtonType.CANCEL);
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("client/resources/images/Wanna_Chat_logo-01.png"));
        selectAllCheckBox = new CheckBox("Select All");
        selectAllCheckBox.setOnAction(event1 -> handleBroadcastButtonCheckClickEvent(event1));
        VBox checkBoxes = new VBox(5,selectAllCheckBox);
        Map<Integer,String> myMap = myClientThread.getMyClientListHandlingThread().provideNameMapToBroadcastButton();

        allClientsAvailable = new CheckBox[myMap.size() - 1];
        int i = 0;
        for(Map.Entry<Integer,String> m:myMap.entrySet())
        {
            String name = m.getValue();
            if(!name.equalsIgnoreCase(nameOfTheUser))
            {
                if(i<allClientsAvailable.length)
                {
                    allClientsAvailable[i] = new CheckBox(name);
                    allClientsAvailable[i].setOnAction(event1 -> handleBroadcastButtonCheckClickEvent(event1));
                    checkBoxes.getChildren().add(allClientsAvailable[i]);
                    i++;
                }
            }
        }

        dialog.getDialogPane().setContent(checkBoxes);
        dialog.setResultConverter(dialogButton->{
            if(dialogButton==nextButton)
            {
                ArrayList<String> myResult = new ArrayList<>(choices);
                return myResult;
            }
            return null;
        });
        Optional<ArrayList<String>>result = dialog.showAndWait();
        ArrayList<String> nameOfTheRecipients = null;
        if(result.isPresent())
        {

            nameOfTheRecipients = result.get();
            TextInputDialog enterMessage = new TextInputDialog();
            enterMessage.setTitle("Enter Message");
            enterMessage.setHeaderText("Enter the message you want to broadcast\t");
            enterMessage.setContentText("(Press ENTER to send the message)");
            Stage stage1 = (Stage) enterMessage.getDialogPane().getScene().getWindow();
            stage1.getIcons().add(new Image("client/resources/images/Wanna_Chat_logo-01.png"));
            Button changeTextToSend = (Button) enterMessage.getDialogPane().lookupButton(ButtonType.OK);
            changeTextToSend.setText("Send");
            Optional<String> resultMessage = enterMessage.showAndWait();
            if(resultMessage.isPresent())
            {
                String message = resultMessage.get();
                message.trim();
                System.out.println("message: " + message + "nameOfTheRecipients: " + nameOfTheRecipients + "myMap: " + myMap);
                myClientThread.getMyClientChattingThread().messageSendingToServer(nameOfTheRecipients,message,myMap);
            }

        }
    }

    private void handleBroadcastButtonCheckClickEvent(ActionEvent event1) {

        if(selectAllCheckBox.isSelected())
        {

            for(CheckBox i:allClientsAvailable) {
                i.setSelected(true);
                choices.add(i.getText());
                i.setDisable(true);
            }
        }
        else
        {
            for(CheckBox i:allClientsAvailable)
            {
                i.setDisable(false);
            }
            selectAllCheckBox.setSelected(false);
            for(CheckBox i:allClientsAvailable)
            {
                if(i.isSelected())
                {
                    choices.add(i.getText());
                }
                if(!i.isSelected())
                {
                    if(choices.contains(i.getText())) choices.remove(i.getText());
                }
            }
        }
    }

    private Integer getKeyUsingValue(String s, Map<Integer, String> myMap) {
        Integer myIndex = null;
        for(Map.Entry<Integer,String> m:myMap.entrySet())
        {
            if(m.getValue().equals(s))
            {
                myIndex=m.getKey();
                return myIndex;
            }
        }
        return myIndex;
    }
    private Integer getKeyUsingValue(ObservableList<TextFlow> o, Map<Integer, ObservableList<TextFlow>> myMap) {
        Integer myIndex = null;
        for(Map.Entry<Integer,ObservableList<TextFlow>> m:myMap.entrySet())
        {
            if(m.getValue()== o)
            {
                myIndex=m.getKey();
                return myIndex;
            }
        }
        return myIndex;
    }

    public void clientsListItemClicked(MouseEvent mouseEvent) {
        System.out.println("Event triggered.");
        if(mouseEvent.getClickCount()==2)
        {
            TextFlow  nameClicked = (TextFlow) listView.getSelectionModel().getSelectedItem();
            String nameClickedStr = getStringFromTextFlow(nameClicked);
            System.out.println("Mouse clicked twice at: " + nameClickedStr);
            if (nameClicked != null) {
                if(mySplitPane.getItems().get(1) == paneLogo)
                {
                    mySplitPane.getItems().remove(1);
                    mySplitPane.getItems().add(1,myGridPane);
                }
                messageTextArea.requestFocus();
                nameInChat.setText(nameClickedStr);
                TextFlow tempTextFlow = (TextFlow) listView.getSelectionModel().getSelectedItem();
                int tempIndex = listView.getSelectionModel().getSelectedIndex();
                System.out.println("tempIndex: " + tempIndex);
                Text tempText = (Text) tempTextFlow.getChildren().get(0);
                tempText.setStyle("-fx-font-weight: normal");
                tempText.setFill(Color.BLACK);
                tempTextFlow.getChildren().remove(0); tempTextFlow.getChildren().add(0,tempText);
                listView.getItems().remove(tempIndex);
                listView.getItems().add(tempIndex,tempTextFlow);
                listView.getSelectionModel().select(tempIndex);
                Map<Integer,String> map = myClientThread.getMyClientListHandlingThread().getNameMapFromServer();
                chatListView.setItems(chatsMap.get(getKeyUsingValue(nameClickedStr,map)));
            }
        }
    }

    public void sendButtonClickedListener(ActionEvent event) {
        System.out.println("mySplitPane.getItems(): " + mySplitPane.getItems());
        if(messageTextArea.getText()!=null && !messageTextArea.getText().isEmpty())
        {
            String message = messageTextArea.getText();
            message = message.trim();
            Integer indexOfRecipient = null;
            indexOfRecipient= getKeyUsingValue(getStringFromTextFlow((TextFlow) listView.getSelectionModel().getSelectedItem()),myClientThread.getMyClientListHandlingThread().provideNameMapToBroadcastButton());
            System.out.println("indexOfRecipient: " + indexOfRecipient);
            String nameOfTheRecipient = myClientThread.getMyClientListHandlingThread().provideNameMapToBroadcastButton().get(indexOfRecipient);
            myClientThread.getMyClientChattingThread().messageSendingToServer(nameOfTheRecipient,message);
            messageTextArea.clear();
        }

    }

    public String getStringFromTextFlow(TextFlow tf)
    {
        StringBuilder tempStringBuilder = new StringBuilder();
        for(Node n:tf.getChildren())
        {
            if(n instanceof Text)
            {
                tempStringBuilder.append(((Text) n).getText());
            }
        }
        return tempStringBuilder.toString();
    }

    public void messageTextAreaKeyPressed(KeyEvent keyEvent) {
        if(keyCombCrtlEnter.match(keyEvent))
        {
            sendButton.fire();
            messageTextArea.setText("");
        }
    }
    public void setMySplitPaneBack()
    {
        System.out.println("setMySplitPaneBack: " + mySplitPane.getItems().get(0));
        if(mySplitPane.getItems().get(0).equals(paneLabel)) {
            System.out.println("setMySplitPaneBack " + " true .");
            mySplitPane.getItems().remove(0);
            mySplitPane.getItems().add(0, myAnchorPane);

        }
    }

    public void clientOfflinePutLogo()
    {
        mySplitPane.getItems().remove(1);
        mySplitPane.getItems().add(1,paneLogo);
    }

}
