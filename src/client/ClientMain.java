package client;/**
 * Created by hdsingh2015 on 26-07-2017.
 */

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class ClientMain extends Application {
    private ClientController clientController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        System.out.println("ClientMain Started");
//        Parent root = null;
//        try {
//            root = FXMLLoader.load(getClass().getResource("ClientGUI.fxml"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("clientgui.fxml"));
        Parent root = fxmlLoader.load();
        clientController = fxmlLoader.getController();
        System.out.println("ClientController;\t" + clientController);

        primaryStage.setTitle("Wanna Chat!!");
        primaryStage.setScene(new Scene(root));
        primaryStage.sizeToScene();
        primaryStage.getIcons().add(new Image("client/Wanna_Chat_logo-01.png"));
        primaryStage.setResizable(false);
        primaryStage.show();
        clientController.setMyStage(primaryStage);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.out.println("Stage is Closing");
                clientController.removingClient();
            }
        });


    }

//    @Override
//    public void stop() throws Exception {
////        super.stop();
//        System.out.println("Stage is stopping");
//        clientController.removingClient();
//    }
}
