package client;

import javafx.collections.ObservableList;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by hdsingh2015 on 26-07-2017.
 */
public class MyClientChattingThread extends Thread{
    private MyClientThread myClientThread;
    private DataOutputStream dataOutputStream;
    private Socket socket;
    private MyClientReadingThread myClientReadingThread = null;

    public MyClientReadingThread getMyClientReadingThread() {
        return myClientReadingThread;
    }

    public Socket getSocket() {
        return socket;
    }

    public MyClientThread getMyClientThread() {
        return myClientThread;
    }

    public MyClientChattingThread(MyClientThread myClientThread) {
        super("MyClientChattingThread");
        this.myClientThread = myClientThread;
        socket = myClientThread.getClientController().getSocket();
        try {
            dataOutputStream = new DataOutputStream(myClientThread.getClientController().getSocket().getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
//        super.run();

        myClientReadingThread = new MyClientReadingThread(this);
        myClientReadingThread.start();
    }

    public void closeThisClient() throws IOException {
        int index = getMyClientThread().getClientController().getMyIndexOnServer();
        dataOutputStream.writeUTF("1 " + index);
        dataOutputStream.flush();
    }

    public void messageSendingToServer(ArrayList<String> recipients, String message, Map<Integer, String> myMap)
    {
        String myRecipients = "";
        System.out.println("recipients: " + recipients);
        for(String s:recipients)
        {
            myRecipients = myRecipients + s + " ";
            if(myMap.containsValue(s))
            {
                Integer in = getKeyUsingValue(s,myMap);
                System.out.println("key: " + in);
                TextFlow tf = new TextFlow();
                Text t1 = new Text("You" + ":\t");
                t1.setStyle("-fx-font-weight: bold");
                Text t2 = new Text(message);
                tf.getChildren().addAll(t1,t2);

                ObservableList<TextFlow> o = myClientThread.getClientController().getChatsMap().get(in);
                o.add(tf);
            }
        }
        try {
            dataOutputStream.writeUTF("2\n" + message + "\n" + myRecipients);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void messageSendingToServer(String recipient, String message)
    {
//        String myRecipients = "";
//        System.out.println("recipients: " + recipients);
//        for(String s:recipients)
//        {
//            myRecipients = myRecipients + s + " ";
//            if(myMap.containsValue(s))
//            {
//                Integer in = getKeyUsingValue(s,myMap);
//                System.out.println("key: " + in);
//                TextFlow tf = new TextFlow();
//                Text t1 = new Text("You" + ":\t");
//                t1.setStyle("-fx-font-weight: bold");
//                Text t2 = new Text(message);
//                tf.getChildren().addAll(t1,t2);
//
//                ObservableList<TextFlow> o = myClientThread.getClientController().getChatsMap().get(in);
//                o.add(tf);
//            }
//        }
        Integer in = getKeyUsingValue(recipient,myClientThread.getMyClientListHandlingThread().provideNameMapToBroadcastButton());
        System.out.println("key: " + in);
        TextFlow tf = new TextFlow();
        Text t1 = new Text("You" + ":\t");
        t1.setStyle("-fx-font-weight: bold");
        Text t2 = new Text(message.trim());
        tf.getChildren().addAll(t1,t2);

        ObservableList<TextFlow> o = myClientThread.getClientController().getChatsMap().get(in);
        o.add(tf);
        try {
            dataOutputStream.writeUTF("2\n" + message + "\n" + recipient);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
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
}
