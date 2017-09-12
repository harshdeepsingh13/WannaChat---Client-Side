package client;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by hdsingh2015 on 26-07-2017.
 */
public class MyClientListHandlingThread extends Thread{
    private MyClientThread myClientThread;
    private boolean exitThis=false;
    private Map<Integer,String> nameMapFromServer=null;
    private ArrayList<Object> allDataArrayListFromServer;
    private Integer lastClosedClientIndex;

    public MyClientListHandlingThread(MyClientThread myClientThread) {
        super("MyClientListHandlingThread");
        this.myClientThread = myClientThread;
    }

    @Override
    public void run() {
//        super.run();
        while(!exitThis) {
            Boolean sendingMessageInfo = false;
            try {
                ObjectInputStream inputStream = new ObjectInputStream(myClientThread.getClientController().getSocket().getInputStream());

//                nameMapFromServer = (Map<Integer, String>) inputStream.readObject();
                allDataArrayListFromServer = (ArrayList<Object>) inputStream.readObject();
                nameMapFromServer = (Map<Integer, String>) allDataArrayListFromServer.get(0);

                lastClosedClientIndex = (Integer) allDataArrayListFromServer.get(1);
                System.out.println("lastClosedClientIndex: " + lastClosedClientIndex);
                sendingMessageInfo = (Boolean) allDataArrayListFromServer.get(2);
//                inputStream.close();
                System.out.println("Got Map From server: " + nameMapFromServer + " sendingMessageInfo: " + sendingMessageInfo);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (!sendingMessageInfo) {
                Platform.runLater(() -> myClientThread.getClientController().getClientsObservableList().clear());

                if(lastClosedClientIndex!=null)
                {
                    System.out.println("lastClosedClientIndex");
                    System.out.println(myClientThread.getClientController().getChatListView().getItems());
                    if(myClientThread.getClientController().getChatsMap().get(lastClosedClientIndex) == myClientThread.getClientController().getChatListView().getItems())
                    {
                        System.out.println("equality true");
                        Platform.runLater(() -> {
                            myClientThread.getClientController().getChatListView().setItems(null);
                            myClientThread.getClientController().clientOfflinePutLogo();
                        }
                        );

                    }
                }

                for (Map.Entry<Integer, String> myMap : nameMapFromServer.entrySet()) {
                    String name = myMap.getValue();
                    if (!name.equalsIgnoreCase(myClientThread.getClientController().getNameOfTheUser())) {
                        Platform.runLater(() -> myClientThread.getClientController().addToClientsObservableList(name));
                    }
                    if(!myClientThread.getClientController().getChatsMap().containsKey(myMap.getKey()) && !name.equalsIgnoreCase(myClientThread.getClientController().getNameOfTheUser()))
                    {
                        System.out.println("and true!");
                        ObservableList<TextFlow> tempObservableList = FXCollections.observableArrayList();
                        myClientThread.getClientController().getChatsMap().put(myMap.getKey(),tempObservableList);
                        System.out.println("chatsMap size: " + myClientThread.getClientController().getChatsMap().size());
                    }
                }
                Platform.runLater(() -> myClientThread.getClientController().refreshOnlineLabel());
            }
            else
            {
                int sendersIndex = (int) allDataArrayListFromServer.get(3);
                if(sendersIndex!=myClientThread.getClientController().getMyIndexOnServer()) {
                    String message = (String) allDataArrayListFromServer.get(4);
                    System.out.println("sendersIndex: " + sendersIndex + " message: " + message);
                    myClientThread.getMyClientChattingThread().getMyClientReadingThread().gotMessage(sendersIndex, message);
                }
            }
        }
    }

    public Map<Integer,String> provideNameMapToBroadcastButton()
    {
        return nameMapFromServer;
    }

    public Map<Integer, String> getNameMapFromServer() {
        return nameMapFromServer;
    }
}
