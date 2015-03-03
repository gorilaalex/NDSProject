package com.example.agostours16.ndsproject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Agos Tours 16 on 03.03.2015.
 */
public class ChatServer {
    private static final String TAG = "Chat Server";
    ServerSocket mServerSocket = null;
    Thread mThread = null;
    private ChatConnection mConnection = ChatConnection.getInstance(new Handler());

    public ChatServer(Handler handler) {
        mThread = new Thread(new ServerThread());
        mThread.start();
    }

    public void tearDown() {
        mThread.interrupt();
        try {
            mServerSocket.close();
        } catch (IOException ioe) {
            Log.e(TAG, "Error when closing server socket.");
        }
    }


    public static synchronized void updateMessages(String msg, boolean local) {
        Log.e(TAG, "Updating message: " + msg);

        if (local) {
            msg = "me: " + msg;
        } else {
            msg = "them: " + msg;
        }

        Bundle messageBundle = new Bundle();
        messageBundle.putString("msg", msg);

        Message message = new Message();
        message.setData(messageBundle);
        //mUpdateHandler.sendMessage(message);
    }

    public void sendMessage(String msg) {
        try {
            Socket socket = mConnection.getSocket();
            if (socket == null) {
                Log.d(TAG, "Socket is null, wtf?");
            } else if (socket.getOutputStream() == null) {
                Log.d(TAG, "Socket output stream is null, wtf?");
            }

            PrintWriter out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(getSocket().getOutputStream())), true);
            out.println(msg);
            out.flush();
            updateMessages(msg, true);
        } catch (UnknownHostException e) {
            Log.d(TAG, "Unknown Host", e);
        } catch (IOException e) {
            Log.d(TAG, "I/O Exception", e);
        } catch (Exception e) {
            Log.d(TAG, "Error3", e);
        }
        Log.d(TAG, "Client sent message: " + msg);
    }

    class ServerThread implements Runnable {

        @Override
        public void run() {

            try {
                // Since discovery will happen via Nsd, we don't need to care which port is
                // used.  Just grab an available one  and advertise it via Nsd.
              //  mServerSocket = new ServerSocket(0);
                mConnection.setLocalPort(mServerSocket.getLocalPort());

                while (!Thread.currentThread().isInterrupted()) {
                    Log.d(TAG, "ServerSocket Created, awaiting connection");
                 //   mConnection.setSocket(mServerSocket.accept());
                    Log.d(TAG, "Connected.");
                   // if (mChatClient == null) {
                     //   int port = mSocket.getPort();
                       // InetAddress address = mSocket.getInetAddress();
                        //connectToServer(address, port);
                    }

            } catch (IOException e) {
                Log.e(TAG, "Error creating ServerSocket: ", e);
                e.printStackTrace();
            }
        }
    }
}
