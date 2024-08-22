package com.sun.httpsample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;


/**
 * @author sunxiaoyun
 * <p>
 * 公共容器activity, 直接装载fragment显示
 */
public class ContainerActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_fragemnt_container);
        setFragment(savedInstanceState == null);
    }

    private void setFragment(boolean isCreate) {
        Bundle bundle = getIntent().getBundleExtra("data");
        String fragName = getIntent().getStringExtra("frag");

        if (fragName == null) {
            throw new NullPointerException("frag at intent is null");
        }

        if (isCreate) {
            Fragment fragment = Fragment.instantiate(this, fragName, bundle);
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment, fragName).commit();
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragName);
            getSupportFragmentManager().beginTransaction().show(fragment).commit();
        }
    }

    public static void start(Activity context, Class clazz, @Nullable Bundle args) {
        Intent intent = new Intent(context, ContainerActivity.class);
        intent.putExtra("data", args);
        intent.putExtra("frag", clazz.getName());
        context.startActivity(intent, args);
    }
}
