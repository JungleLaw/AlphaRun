package cn.alpha.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import butterknife.OnClick;
import cn.alpha.demo.base.AppBaseCompatActivity;
import cn.alpha.demo.hybird.HybirdActivity;
import cn.alpha.demo.imageloader.ImageLoaderActivity;
import cn.alpha.demo.jni.JNIActivity;
import cn.alpha.demo.net.NetActivity;
import cn.alpha.demo.others.OthersActivity;
import cn.alpha.demo.view.ViewsActivity;
import cn.alpha.demo.widget.WidgetsActivity;

public class MainActivity extends AppBaseCompatActivity {

    @Override
    public int setContentViewLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void loadData(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public int[] loadExitAnimation() {
        return null;
    }

    @Override
    public int[] loadEntryAnimation() {
        return null;
    }

//    @OnClick(R.id.btn_db)
//    void gotoDBActivity(View view) {
//        startActivity(new Intent(MainActivity.this, DBActivity.class));
//    }

    @OnClick(R.id.btn_net)
    void gotoNetActivity(View view) {
        startActivity(new Intent(MainActivity.this, NetActivity.class));
    }

    @OnClick(R.id.btn_views)
    void gotoViewsActivity(View view) {
        startActivity(new Intent(MainActivity.this, ViewsActivity.class));
    }

    @OnClick(R.id.btn_widgets)
    void gotoWidgetsActivity(View view) {
        startActivity(new Intent(MainActivity.this, WidgetsActivity.class));
    }

    @OnClick(R.id.btn_jni)
    void gotoJNIActivity(View view) {
        startActivity(new Intent(MainActivity.this, JNIActivity.class));
    }

    @OnClick(R.id.btn_hybird)
    void gotoHybirdActivity(View view) {
        startActivity(new Intent(MainActivity.this, HybirdActivity.class));
    }

    @OnClick(R.id.btn_imageloader)
    void gotoImageLoaderActivity(View view) {
        startActivity(new Intent(MainActivity.this, ImageLoaderActivity.class));
    }

    @OnClick(R.id.btn_others)
    void gotoOthersActivity(View view) {
        startActivity(new Intent(MainActivity.this, OthersActivity.class));
    }
}
