package x.wsxf.androidmemoryleaks;

import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/**
 * Created by HHX on 2018/2/5.
 */

public class HandlerActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "HandlerActivity";
    private View mBtn0;
    private View mBtn1;
    private View mBtn2;
    private View mBtn3;
    private View mBtn4;
    private String mStringField = "someText";
    public static final int DELAY_IN_MILLIS = 30000;
    private InnerHandler mInnerHandler;
    private InnerStaticHandler mInnerStaticHandler;
    private InnerStaticWeakHandler mInnerStaticWeakHandler;

    @Override
    protected void testLeak() {
    }

    @Override
    protected void initView() {
        mBtn0 = findViewById(R.id.handlerbutton0);
        mBtn0.setOnClickListener(this);
        mBtn1 = findViewById(R.id.handlerbutton1);
        mBtn1.setOnClickListener(this);
        mBtn2 = findViewById(R.id.handlerbutton2);
        mBtn2.setOnClickListener(this);
        mBtn3 = findViewById(R.id.handlerbutton3);
        mBtn3.setOnClickListener(this);
        mBtn4 = findViewById(R.id.handlerbutton4);
        mBtn4.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_handler;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.handlerbutton0:
                leak0();//leaked 62kb
                break;
            case R.id.handlerbutton1:
                leak1();//no leak
                break;
            case R.id.handlerbutton2:
                leak2();//leaked 5.2kb
                break;
            case R.id.handlerbutton3:
                leak3();//no leak
                break;
            case R.id.handlerbutton4:
                leak4();//no leak
                break;
        }
    }

    /**
     * 如果handler在处理消息的时候，HandlerActivity已经退出(如果HandlerActivity在启动以后的30秒内退出)，
     * 由于此时Handler持有HandlerActivity的强引用，将导致HandlerAcitivty所占内存不能释放，造成HandlerActivity内存泄露
     */
    //leak
    private class InnerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e(TAG, "InnerHandler:handleMessage");
        }
    }

    private static class InnerStaticHandler extends Handler {
        private View view;
        private String field;

        //用这个构造方法不会内存泄露
        private InnerStaticHandler() {
        }

        //用这个构造方法会导致内存泄露，因为Activity退出以后，它包含的View所占用的内存也应该释放掉，但是因为Handler的消息还未执行完毕，持有HandlerActivity的引用，导致HandlerAcivity不能释放内存(有可能只是该View所占用的内存无法释放)
        private InnerStaticHandler(View view) {
            this.view = view;
        }

        //用这个构造方法不会导致内存泄露，因为string不是Activity引用
        public InnerStaticHandler(String field) {
            this.field = field;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e(TAG, "InnerStaticHandler:handleMessage");

        }
    }

    private static class InnerStaticWeakHandler extends Handler {
        private WeakReference<View> viewWeakReference;

        public InnerStaticWeakHandler(View view) {
            viewWeakReference = new WeakReference<View>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e(TAG, "InnerStaticWeakHandler:handleMessage");

        }
    }


    private void leak0() {
        Message msg = new Message();
        msg.what = 0;
        msg.obj = "InnerHandler msg";
        mInnerHandler = new InnerHandler();
        mInnerHandler.sendMessageDelayed(msg, DELAY_IN_MILLIS);
    }

    //static no leak
    private void leak1() {
        Message msg = new Message();
        msg.what = 1;
        msg.obj = "InnerStaticHandler msg";
        mInnerStaticHandler = new InnerStaticHandler();//静态handler不会造成内存泄露
        mInnerStaticHandler.sendMessageDelayed(msg, DELAY_IN_MILLIS);
    }

    private void leak2() {
        Message msg = new Message();
        msg.what = 1;
        msg.obj = "InnerStaticHandler msg";
        mInnerStaticHandler = new InnerStaticHandler(mBtn1);//传入button的构造函数
        mInnerStaticHandler.sendMessageDelayed(msg, DELAY_IN_MILLIS);
    }

    private void leak3() {
        Message msg = new Message();
        msg.what = 1;
        msg.obj = "InnerStaticHandler msg";
        mInnerStaticHandler = new InnerStaticHandler(mStringField);//传入String的构造函数
        mInnerStaticHandler.sendMessageDelayed(msg, DELAY_IN_MILLIS);
    }

    private void leak4() {
        Message msg = new Message();
        msg.what = 1;
        msg.obj = "InnerStaticWeakHandler msg";
        mInnerStaticWeakHandler = new InnerStaticWeakHandler(mBtn2);//持有button的弱引用
        mInnerStaticWeakHandler.sendMessageDelayed(msg, DELAY_IN_MILLIS);
    }

}
