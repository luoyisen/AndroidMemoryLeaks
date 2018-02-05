package x.wsxf.androidmemoryleaks;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/**
 * Created by HHX on 2018/2/5.
 */

public class HandlerActivity extends BaseActivity {

    private static final String TAG = "HandlerActivity";
    private View mBtn;
    private String mStringField = "someText";
    public static final int DELAY_IN_MILLIS = 60000;
    private InnerHandler mInnerHandler;
    private InnerStaticHandler mInnerStaticHandler;
    private InnerStaticWeakHandler mInnerStaticWeakHandler;

    @Override
    protected void testLeak() {
        leak0();
//        leak1();
//        leak2();
//        leak3();
    }

    @Override
    protected void initView() {
        mBtn = findViewById(R.id.handlerbutton);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_handler;
    }

    /**
     * 如果handler在处理消息的时候，HandlerActivity已经退出(如果HandlerActivity在启动以后的60秒内退出)，
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

        //// TODO: 2018/2/5 public or private
        public InnerStaticHandler() {
        }

        public InnerStaticHandler(View view) {
            this.view = view;
        }

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
        mInnerStaticHandler = new InnerStaticHandler();
        mInnerStaticHandler.sendMessageDelayed(msg, DELAY_IN_MILLIS);
    }

    private void leak2() {
        Message msg = new Message();
        msg.what = 1;
        msg.obj = "InnerStaticHandler msg";
        mInnerStaticHandler = new InnerStaticHandler(mBtn);
//        mInnerStaticHandler = new InnerStaticHandler(mStringField);//fine without ref to activity
        mInnerStaticHandler.sendMessageDelayed(msg, DELAY_IN_MILLIS);
    }

    //no leak
    private void leak3() {
        Message msg = new Message();
        msg.what = 1;
        msg.obj = "InnerStaticWeakHandler msg";
        mInnerStaticWeakHandler = new InnerStaticWeakHandler(mBtn);
        mInnerStaticWeakHandler.sendMessageDelayed(msg, DELAY_IN_MILLIS);
    }


}
