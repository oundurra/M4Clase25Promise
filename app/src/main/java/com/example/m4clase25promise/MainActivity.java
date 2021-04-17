package com.example.m4clase25promise;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.ProgressCallback;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final int NUMBER_OF_CPUS = Runtime.getRuntime().availableProcessors();
    private static final String TAG = "MainActivity";
    private ExecutorService mExecutorService;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mExecutorService = Executors.newFixedThreadPool(NUMBER_OF_CPUS);
        textView = (TextView) findViewById(R.id.tvMensaje);
        //doWorkInBackground();

        doWorkInBackground()
                .then(new DoneCallback<String>() {
                    @Override
                    public void onDone(String result) {
                        Log.i(TAG, "then() on thread " + Thread.currentThread().getId());
                    }
                }).progress(new ProgressCallback<Integer>() {
                    @Override
                    public void onProgress(Integer progress) {
                        Log.i(TAG, "Done " + progress + "% of work on thread " + Thread.currentThread().getId());
                    }
                }).done(new DoneCallback<String>() {
                    @Override
                    public void onDone(String result) {
                        Log.i(TAG, "done() on thread " + Thread.currentThread().getId());
                        textView.setText("TERMINADO");
                    }
                }).fail(new FailCallback<Throwable>() {
                    @Override
                    public void onFail(Throwable result) {
                        Log.i(TAG, "fail() on thread " + Thread.currentThread().getId());
                        result.printStackTrace();
                    }
                }).always(new AlwaysCallback<String, Throwable>() {
                    @Override
                    public void onAlways(Promise.State state, String resolved, Throwable rejected) {
                        Log.i(TAG, "always() on thread " + Thread.currentThread().getId());
                    }
                });

    }

    private Promise<String, Throwable, Integer> doWorkInBackground() {
        final DeferredObject<String, Throwable, Integer> deferredObject = new DeferredObject<String, Throwable, Integer>();

        Runnable work = new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i <= 100; i += 20) {
                        Thread.sleep(1000);
                        deferredObject.notify(i);
                    }

                    deferredObject.resolve("Finish!");
                } catch (Throwable ex) {
                    deferredObject.reject(ex);
                }
            }
        };
        mExecutorService.submit(work);

        return deferredObject.promise();
    }

    /*private void doWorkInBackground() {
        Runnable work = new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i <= 100; i += 20) {
                        Thread.sleep(1000);
                        Log.i(TAG, "Done " + i + "% of work on thread " + Thread.currentThread().getId());
                    }
                } catch (Throwable ex) {
                    Log.e(TAG, "Error doing background work", ex);
                }
            }
        };
        mExecutorService.submit(work);*/
}