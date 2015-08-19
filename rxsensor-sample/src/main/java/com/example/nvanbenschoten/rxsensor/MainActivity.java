/*
 * Copyright (C) 2015 Nathan VanBenschoten.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.nvanbenschoten.rxsensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.nvanbenschoten.rxsensor.RxSensorManager;
import com.trello.rxlifecycle.components.RxActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class MainActivity extends RxActivity {

    private RxSensorManager mRxSensorManager;

    @Bind(R.id.sensor_switch) Switch mSensorSwitch;
    @Bind(R.id.sensor_data_0) TextView mSensorData0;
    @Bind(R.id.sensor_data_1) TextView mSensorData1;
    @Bind(R.id.sensor_data_2) TextView mSensorData2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Prepare sensor objects
        setupSensorManager();

        // Set up Rx Chain
        RxCompoundButton.checkedChanges(mSensorSwitch)
                .doOnNext(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean checked) {
                        if (checked) {
                            showData();
                        } else {
                            hideData();
                        }
                    }
                })
                .switchMap(new Func1<Boolean, Observable<? extends SensorEvent>>() {
                    @Override
                    public Observable<? extends SensorEvent> call(Boolean checked) {
                        if (checked) {
                            return mRxSensorManager.observe(Sensor.TYPE_ACCELEROMETER,
                                                            SensorManager.SENSOR_DELAY_NORMAL);
                        } else {
                            return Observable.empty();
                        }
                    }
                })
                .compose(this.<SensorEvent>bindToLifecycle())
                .subscribe(new Action1<SensorEvent>() {
                    @Override
                    public void call(SensorEvent sensorEvent) {
                        mSensorData0.setText(Float.toString(sensorEvent.values[0]));
                        mSensorData1.setText(Float.toString(sensorEvent.values[1]));
                        mSensorData2.setText(Float.toString(sensorEvent.values[2]));
                    }
                });
    }

    private void setupSensorManager() {
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mRxSensorManager = new RxSensorManager(sensorManager);
    }

    private void showData() {
        mSensorData0.setVisibility(View.VISIBLE);
        mSensorData1.setVisibility(View.VISIBLE);
        mSensorData2.setVisibility(View.VISIBLE);
    }

    private void hideData() {
        mSensorData0.setText(null);
        mSensorData1.setText(null);
        mSensorData2.setText(null);
        mSensorData0.setVisibility(View.GONE);
        mSensorData1.setVisibility(View.GONE);
        mSensorData2.setVisibility(View.GONE);
    }

}
