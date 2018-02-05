package x.wsxf.androidmemoryleaks;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by HHX on 2018/2/5.
 */

public abstract class BaseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initView();
        testLeak();
    }

    protected abstract void testLeak();

    protected abstract void initView();

    protected abstract int getLayoutId();
}
