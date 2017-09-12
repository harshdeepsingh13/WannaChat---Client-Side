package client;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.DataInputStream;
import java.net.Socket;

/**
 * Created by hdsingh2015 on 27-07-2017.
 */
public class MyClientReadingThread extends Thread {
    private MyClientChattingThread myClientChattingThread;
    private DataInputStream dataInputStream;
    private Socket socket;
    private AudioClip audioClip = new AudioClip(getClass().getResource("alertTone.wav").toString());

    public MyClientReadingThread(MyClientChattingThread myClientChattingThread) {
        super("MyClientReadingThread");
        this.myClientChattingThread = myClientChattingThread;
        socket = myClientChattingThread.getSocket();
        //            dataInputStream = new DataInputStream(myClientChattingThread.getSocket().getInputStream());
    }

    @Override
    public void run() {
//        super.run();
        while(true) {
            //                byte messageInfoByte[] = new byte[10000];
//                dataInputStream.read(messageInfoByte);
//                String messageInfo = new String(messageInfoByte);
//                System.out.println(messageInfo);
//            String messageInfo="";
//                if(dataInputStream.readBoolean())
//                {
//                    messageInfo = dataInputStream.readUTF();
////

        }
    }

    public void gotMessage(int sendersIndex, String message)
    {
        System.out.println("got, Senders Index: " + sendersIndex);
        TextFlow tf = new TextFlow();
        String sendersName = myClientChattingThread.getMyClientThread().getMyClientListHandlingThread().getNameMapFromServer().get(sendersIndex);
        Text t1 = new Text(sendersName + ":\t");
        t1.setStyle("-fx-font-weight: bold");
        Text t2 = new Text(message);
        tf.getChildren().addAll(t1,t2);
        System.out.println("sendersIndex:" + myClientChattingThread.getMyClientThread().getClientController().getChatsMap().get(sendersIndex));
        ObservableList<TextFlow>tempObservableList = myClientChattingThread.getMyClientThread().getClientController().getChatsMap().get(sendersIndex);
        Platform.runLater(() -> myClientChattingThread.getMyClientThread().getClientController().getListView().requestFocus());
//        myClientChattingThread.getMyClientThread().getClientController().getListView().requestFocus();
        if(myClientChattingThread.getMyClientThread().getClientController().getListView().getSelectionModel().getSelectedItem() != sendersName)
        {
//            int tempIndex = myClientChattingThread.getMyClientThread().getClientController().getListView().getItems().indexOf(new TextFlow(new Text(sendersName)));
            int tempIndex = listViewTextFlowObservableListContainsValue(sendersName);
            System.out.println("tempIndex: " + tempIndex);
            Platform.runLater(() -> {
                System.out.println("Requesting");

                TextFlow tempTextFlow = (TextFlow) myClientChattingThread.getMyClientThread().getClientController().getListView().getItems().get(tempIndex);
                System.out.println("Hey: Hey: " + tempTextFlow.getChildren().get(0));
                Text newText = new Text(sendersName);
                newText.setStyle("-fx-font-weight: bold");
//                newText.setStyle("-fx-font-color: crimson");
                newText.setFill(Color.RED);
                tempTextFlow.getChildren().remove(0);
                tempTextFlow.getChildren().add(0,newText);
                boolean flag=false;
                if(myClientChattingThread.getMyClientThread().getClientController().getListView().getSelectionModel().isSelected(tempIndex))
                    flag=true;
                TextFlow tf2 = (TextFlow) myClientChattingThread.getMyClientThread().getClientController().getListView().getItems().remove(tempIndex);
                myClientChattingThread.getMyClientThread().getClientController().getListView().getItems().add(tempIndex,tempTextFlow);
                myClientChattingThread.getMyClientThread().getClientController().getListView().getFocusModel().focus(tempIndex);
                if(flag) myClientChattingThread.getMyClientThread().getClientController().getListView().getSelectionModel().select(tempIndex);
                audioClip.play(1.0);
                myClientChattingThread.getMyClientThread().getClientController().getMyStage().requestFocus();
            });
//            myClientChattingThread.getMyClientThread().getClientController().getListView().getFocusModel().focus(sendersIndex);
        }
        Platform.runLater(() -> tempObservableList.add(tf));

//        String name = myClientChattingThread.getMyClientThread().getMyClientListHandlingThread().provideNameMapToBroadcastButton().get(sendersIndex);





    }

    private int listViewTextFlowObservableListContainsValue(String sendersName) {
        ObservableList<TextFlow> t = myClientChattingThread.getMyClientThread().getClientController().getListView().getItems();
        for(int i = 0;i<t.size();i++)
        {
            TextFlow f = t.get(i);
            String s = myClientChattingThread.getMyClientThread().getClientController().getStringFromTextFlow(f);
            System.out.println("s: " + s);
            if(s.equalsIgnoreCase(sendersName)) {
                System.out.println("True True!!!! " + i);
                return i;}
        }
        return -1;
    }

}
