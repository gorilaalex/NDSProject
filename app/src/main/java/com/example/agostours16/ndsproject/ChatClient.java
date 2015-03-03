package com.example.agostours16.ndsproject;

import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Agos Tours 16 on 03.03.2015.
 */
public class ChatClient {

    private static final String TAG = "Chat Client";
    private InetAddress mAddress;
    private int PORT;

    private Thread mSendThread;
    private Thread mRecThread;
    private static ChatClient mChatClient;

    private ChatConnection mConnection = ChatConnection.getInstance(new Handler());

    public static ChatClient getInstance(InetAddress address, int port) {
        if (mChatClient == null) {
            mChatClient = new ChatClient(address,port);
        }

        return mChatClient;
    }

    private ChatClient(InetAddress address, int port) {

        Log.d(TAG, "Creating chatClient");
        this.mAddress = address;
        this.PORT = port;

        mSendThread = new Thread(new SendingThread());
        mSendThread.start();
    }

    public void connectToServer(InetAddress address, int port) {
        mChatClient = new ChatClient(address, port);
    }

    public void sendMessage(String msg) {
        if (mChatClient != null) {
            mChatClient.sendMessage(msg);
        }
    }

    class SendingThread implements Runnable {

        BlockingQueue<String> mMessageQueue;
        private int QUEUE_CAPACITY = 10;

        public SendingThread() {
            mMessageQueue = new ArrayBlockingQueue<String>(QUEUE_CAPACITY);
        }

        @Override
        public void run() {
            try {
                if (mConnection.getSocket() == null) {
                    mConnection.setSocket(new Socket(mAddress, PORT));
                    Log.d(TAG, "Client-side socket initialized.");

                } else {
                    Log.d(TAG, "Socket already initialized. skipping!");
                }

                mRecThread = new Thread(new ReceivingThread());
                mRecThread.start();

            } catch (UnknownHostException e) {
                Log.d(TAG, "Initializing socket failed, UHE", e);
            } catch (IOException e) {
                Log.d(TAG, "Initializing socket failed, IOE.", e);
            }

            while (true) {
                try {
                    String msg = mMessageQueue.take();
                    sendMessage(msg);
                } catch (InterruptedException ie) {
                    Log.d(TAG, "Message sending loop interrupted, exiting");
                }
            }
        }
    }

    class ReceivingThread implements Runnable {

        @Override
        public void run() {

            BufferedReader input;
            try {
                input = new BufferedReader(new InputStreamReader(
                        mConnection.getSocket().getInputStream()));
                while (!Thread.currentThread().isInterrupted()) {

                    String messageStr = null;
                    messageStr = input.readLine();
                    if (messageStr != null) {
                        Log.d(TAG, "Read from the stream: " + messageStr);
                        //updateMessages(messageStr, false);
                    } else {
                        Log.d(TAG, "The nulls! The nulls!");
                        break;
                    }
                }
                input.close();

            } catch (IOException e) {
                Log.e(TAG, "Server loop error: ", e);
            }
        }
    }

    public void tearDown() {
        try {
            mConnection.getSocket().close();
        } catch (IOException ioe) {
            Log.e(TAG, "Error when closing server socket.");
        }
    }


}
