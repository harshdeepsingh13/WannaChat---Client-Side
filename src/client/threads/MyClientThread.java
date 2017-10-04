package client.threads;

import client.controller.ClientController;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by hdsingh2015 on 26-07-2017.
 */
public class MyClientThread extends Thread{
    private ClientController clientController;
    private MyClientChattingThread myClientChattingThread = null;
    private MyClientListHandlingThread myClientListHandlingThread = null;
    public MyClientThread(ClientController clientController) {
        super("MyClientThread");
        this.clientController = clientController;
    }

    public MyClientListHandlingThread getMyClientListHandlingThread() {
        return myClientListHandlingThread;
    }

    public MyClientChattingThread getMyClientChattingThread() {
        return myClientChattingThread;
    }

    public ClientController getClientController() {
        return clientController;
    }

    @Override
    public void run() {
//        super.run();
        if (clientController.isGiveNametoServer()){
            try {
                giveNameToTheServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        myClientChattingThread = new MyClientChattingThread(this);
        myClientChattingThread.start();


        myClientListHandlingThread = new MyClientListHandlingThread(this);
        myClientListHandlingThread.start();
    }

    private void giveNameToTheServer() throws IOException {

        DataOutputStream dataOutputStream = new DataOutputStream(clientController.getSocket().getOutputStream());
        dataOutputStream.writeBoolean(true);
        dataOutputStream.writeUTF(clientController.getNameOfTheUser());
        dataOutputStream.flush();

    }
}
