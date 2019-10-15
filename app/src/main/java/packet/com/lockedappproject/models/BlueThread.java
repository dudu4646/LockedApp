package packet.com.lockedappproject.models;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BlueThread extends Thread {

    private static final String TAG = "BlueThread";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private final BluetoothSocket socket;
    private final InputStream inStream;
    private final OutputStream outStream;
    private final Activity activity;
    private byte[] buffer;
    private ThreadCB cb;
    private int status;

    public BlueThread(BluetoothDevice device, ThreadCB cb) {
        this.cb = cb;
        activity = (Activity) cb;
        BluetoothSocket tmp = null;
        InputStream inTmp = null;
        OutputStream outTmp = null;
        try {
            tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            inTmp = tmp.getInputStream();
            outTmp = tmp.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "BlueThread: failed to establish connection", e);
        }
        socket = tmp;
        inStream = inTmp;
        outStream = outTmp;
        System.out.println("bThread created!!!");
    }

    public void run() {
        try {
            final Activity activity = (Activity) cb;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cb.updateUi(ThreadCB.CONNECTING, "");
                }
            });
            socket.connect();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cb.updateUi(ThreadCB.CONNECTED, "");
                }
            });

            while (true) {
                try {
                    int numBytes;
                    numBytes = inStream.read();
                    System.out.println("testing ---> total length = " + numBytes);
                    buffer = new byte[numBytes];
                    for (int i = 0; i < numBytes; i++)
                        buffer[i] = (byte) inStream.read();
                    System.out.println("testing ---> msg = " + new String(buffer));

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cb.updateUi(ThreadCB.GET_LID, new String(buffer));
                        }
                    });

                } catch (IOException e) {
                    System.out.println("testing ---> read loop ended");
                    break;
                }
            }
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException ex) {
                Log.e(TAG, "failed to close socket ", ex);
            }
        } finally {
            cancel();
        }
    }

    public void write(byte[] msg, int sts) {
        try {
            status = sts;
            System.out.println("testing ---> status = " + sts + ", msg: " + new String(msg));
            outStream.write(msg);
        } catch (IOException e) {
            System.out.println("failed to send msg");
            cancel();
        }
    }

    public void cancel() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cb.updateUi(ThreadCB.DISCONNECT, "");
            }
        });
        System.out.println("testing ---> cancel called");
        try {
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface ThreadCB {
        static int DISCONNECT = -1;
        static int CONNECTING = 0;
        static int CONNECTED = 1;
        static int GET_LID = 2;
        static int GET_SSID = 3;
        static int GET_PASS = 4;
        static int SET_SSID = 5;
        static int SET_PASS = 6;

        void updateUi(int status, String msg);
    }
}

/*
activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cb.updateUi(ThreadCB.CONNECTED,1,1,"");
                }
            });
 */

