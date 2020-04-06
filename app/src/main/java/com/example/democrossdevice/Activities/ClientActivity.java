package com.example.democrossdevice.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import static com.example.democrossdevice.Utilities.Constants.*;
import com.app.mg.connectionlibraryandroid.Implementations.ConnectMethods;
import com.example.democrossdevice.Entities.SocketMessage;
import com.example.democrossdevice.InterfaceSocket.WebSocketReceiver;
import com.example.democrossdevice.R;
import com.example.democrossdevice.WebSocket.WebSocketClient;
import com.google.gson.Gson;
import java.util.Date;


public class ClientActivity extends AppCompatActivity implements WebSocketReceiver,GestureDetector.OnGestureListener {

    private String myIpAddress;
    private String myServerIpAddress;
    private ConnectMethods connectMethods = new ConnectMethods();
    private WebSocketClient clientSocket;
    private Gson gson = new Gson();
    private boolean sendMessage = true;
    private boolean connectedBy = false;
    private SocketMessage messageSocket;
    private float downX, downY, upX, upY, deltaX, deltaY, actualX,pastX;
    private DisplayMetrics displayMetrics;
    private int screenWidth;
    private Handler handler;
    private SensorManager accelerometerSensorManager;
    private Sensor accelerometerSensor;
    private SensorEventListener accelerometerSensorListener;
    private long shakeTime = 0;
    private TextView txtSlide;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myServerIpAddress = getIntent().getStringExtra("ipServer");
        txtSlide = findViewById(R.id.txtSwapOrientation);
        myIpAddress = connectMethods.FindMyIpAddress(this);
        displayMetrics = new DisplayMetrics();
        accelerometerSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = accelerometerSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.gestureDetector = new GestureDetector(ClientActivity.this,this  );
        messageSocket = new SocketMessage();
        connectWebSocket();
        handler = new Handler();
        handler.postDelayed(() -> initializeAccelerometerSensor(), 2000);

    }



    private void connectWebSocket()
    {
        clientSocket = new WebSocketClient(connectMethods.GetUriServer(myServerIpAddress, PORT), this);
        clientSocket.connect();
        Toast.makeText(getApplicationContext(),"Connected Client",Toast.LENGTH_SHORT).show();
    }


    private void initializeAccelerometerSensor()
    {
        if (accelerometerSensor == null)
        {
            Toast.makeText(this, "Accelerometer sensor not available", Toast.LENGTH_SHORT).show();
        }else
        {
            accelerometerSensorListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if(connectedBy)
                    {
                        if(event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
                        {
                            if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                            {
                                Toast.makeText(ClientActivity.this, "Accelerometer unreliable", Toast.LENGTH_SHORT).show();
                            }
                            return ;
                        }
                        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                            detectShake(event);
                        }
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };
            accelerometerSensorManager.registerListener(accelerometerSensorListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

        }
    }

    //Function for detection of shaking the device to unpaired with other devices
    private void detectShake(SensorEvent event)
    {
        long now = System.currentTimeMillis();

        if ((now - shakeTime) > SHAKE_WAIT_TIME_MS) {
            shakeTime = now;

            float gX = event.values[0] / SensorManager.GRAVITY_EARTH;
            float gY = event.values[1] / SensorManager.GRAVITY_EARTH;
            float gZ = event.values[2] / SensorManager.GRAVITY_EARTH;

            // gForce will be close to 1 when there is no movement
            double gForce = Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            if (gForce > SHAKE_THRESHOLD) {
                connectedBy = false;
                sendMessageToServer(CONFIRM_UNPAIRING, true, messageSocket.getSender());
                Toast.makeText(this, "Device unpaired with " + messageSocket.getSender(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    //Sending Message
    private void sendMessageToServer(String message, boolean pairing, String receiver)
    {

        if(clientSocket.isOpen())
        {
            if(sendMessage)
            {
                sendMessage=false;
                SocketMessage webSocketMessage = new SocketMessage()
                        .setSender(myIpAddress)
                        .setReceiver(receiver)
                        .setMessage(message)
                        .setPairing(pairing);
                String gsonMessage = gson.toJson(webSocketMessage);
                clientSocket.send(gsonMessage);
            }
            else
            {
                Toast.makeText(this, "Not connected", Toast.LENGTH_SHORT).show();
            }
        }

    }

    // Actions after touching the screen


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break ;
            case MotionEvent.ACTION_UP:
                upX = event.getX();
                upY = event.getY();
                deltaX = upX - downX;
                deltaY = upY - downY;
                if((Math.abs(deltaX) > Math.abs(deltaY)) && !connectedBy) {
                    if (Math.abs(deltaX) > MIN_SWIPE_DISTANCE) {
                        String orientation = "";
                        if ((upX > downX)) {
                            orientation = ORIENTATION.RIGHT.toString();
                        } else if ((upX < downX)) {
                            orientation = ORIENTATION.LEFT.toString();
                        }
                        if (!orientation.isEmpty()){

                            if (messageSocket != null && messageSocket.getSender() == null){
                                sendMessageToServer(SWIPE_ORIENTATION + orientation, true , "");
                            }
                            else if ((Math.abs((new Date()).getTime() - messageSocket.getTime().getTime()) < DELAY_DETECT_PINCH) && !connectedBy){
                                if ((messageSocket.getMessage().startsWith(SWIPE_ORIENTATION)) &&
                                        !(messageSocket.getMessage().substring(SWIPE_ORIENTATION.length()).equals(orientation))){
                                    connectedBy = true;
                                    Toast.makeText(ClientActivity.this, "Device paired with " + messageSocket.getSender(), Toast.LENGTH_SHORT).show();
                                    txtSlide.setText(messageSocket.getSender());
                                    sendMessageToServer(CONFIRM_PAIRING, true, messageSocket.getSender());
                                }
                            }
                        }
                    }
                    else {
                        Toast.makeText(ClientActivity.this,"Swipe not long enough", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
                break;

        }
        return super.onTouchEvent(event);
    }

    //Receiving message from another device
    @Override
    public void onWebSocketMessage(String message) {
        SocketMessage messageReceived = gson.fromJson(message,SocketMessage.class);
        //the message was send from another device
        if(!myIpAddress.equals(messageReceived.getSender()))
        {
            if(messageReceived.isPairing())
            {
                messageSocket = messageReceived;
                if(!connectedBy && CONFIRM_PAIRING.equals(messageReceived.getMessage()))
                {
                    txtSlide.setText(messageSocket.getReceiver());
                    connectedBy = true;

                }
                else if(connectedBy && CONFIRM_UNPAIRING.equals(messageReceived.getMessage()))
                {
                    connectedBy = false;
                    Toast.makeText(this, "Device unpaired with " + messageSocket.getSender(), Toast.LENGTH_SHORT).show();

                }
            }
            else if(connectedBy && myIpAddress.equals(messageReceived.getReceiver()) && messageReceived.getMessage().startsWith(SWIPE_ORIENTATION))
            {
                txtSlide.setText(messageReceived.getMessage());
            }
        }

        sendMessage = true;
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        if(code == 1001 && remote){
            Toast.makeText(this, "SERVER WAS CLOSED", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (clientSocket != null){
            clientSocket.close();
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
