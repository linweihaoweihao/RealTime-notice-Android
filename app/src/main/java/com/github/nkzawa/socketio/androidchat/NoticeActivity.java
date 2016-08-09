package com.github.nkzawa.socketio.androidchat;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A login screen that offers login via username.
 */
public class NoticeActivity extends Activity {

    private TextView textView;

    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();
        // Set up the login form.
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        textView  = (TextView)findViewById(R.id.message);
        // 绑定监听消息 key - Emitter （key对应服务端发送的key 一一对应的）
        mSocket.on("message", onMessage);  // mSocket.emit("new message", message); 可以实现发送消息
        mSocket.connect(); // 实现连接
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出 取消监听
        mSocket.off("message", onMessage);
    }

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            NoticeActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(NoticeActivity.this.getApplicationContext(),
                            R.string.error_connect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            NoticeActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String message;
                    try {
                        message = data.getString("message");
                    }catch (JSONException e) {
                        return;
                    }
                    textView.setText(message.toString());
                }
            });
        }
    };
}



