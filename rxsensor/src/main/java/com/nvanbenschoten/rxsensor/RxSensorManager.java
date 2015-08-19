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
package com.nvanbenschoten.rxsensor;

import android.annotation.TargetApi;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/**
 * A lightweight wrapper around {@link SensorManager} which allows for continuously observing
 * the {@link SensorEvent} data.
 */
public final class RxSensorManager {

    private final Handler mSensorListenerHandler;
    private final SensorManager mSensorManager;

    public RxSensorManager(@NonNull SensorManager sensorManager) {
        mSensorListenerHandler = new Handler(Looper.getMainLooper());
        mSensorManager = sensorManager;
    }

    /**
     * Create an observable which will notify subscribers with a {@link SensorEvent}. Sensor
     * data will continue to be polled until the Observable's subscription is unsubscribed from.
     *
     * @see SensorManager#registerListener(SensorEventListener, Sensor, int)
     */
    @CheckResult
    public Observable<SensorEvent> observe(final int sensorType,
                                           final int samplingPeriodUs) {
        return observe(sensorType, samplingPeriodUs, 0);
    }

    /**
     * Create an observable which will notify subscribers with a {@link SensorEvent}. Sensor
     * data will continue to be polled until the Observable's subscription is unsubscribed from.
     *
     * @see SensorManager#registerListener(SensorEventListener, Sensor, int, int)
     */
    @CheckResult
    public Observable<SensorEvent> observe(final int sensorType,
                                           final int samplingPeriodUs,
                                           final int maxReportLatencyUs) {
        OnSubscribe<SensorEvent> subscribe = new OnSubscribe<SensorEvent>() {
            @TargetApi(VERSION_CODES.KITKAT)
            @Override
            public void call(final Subscriber<? super SensorEvent> subscriber) {
                // Determine Sensor to use
                Sensor sensor = mSensorManager.getDefaultSensor(sensorType);
                if (sensor == null) {
                    subscriber.onError(new SensorException());
                    subscriber.onCompleted();
                    return;
                }

                // Create SensorEventListener that publishes onSensorChanged events to subscriber
                final SensorEventListener listener = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        subscriber.onNext(event);
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
                };

                // Attempt to register listener to SensorManager
                boolean success;
                if (Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                    success = mSensorManager.registerListener(listener, sensor, samplingPeriodUs,
                            maxReportLatencyUs, mSensorListenerHandler);
                } else {
                    success = mSensorManager.registerListener(listener, sensor, samplingPeriodUs,
                            mSensorListenerHandler);
                }
                if (!success) {
                    subscriber.onError(new SensorException());
                    subscriber.onCompleted();
                    return;
                }

                // Set action on un-subscribe
                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        mSensorManager.unregisterListener(listener);
                    }
                }));
            }
        };
        return Observable.create(subscribe)
                .lift(BackpressureBufferLastOperator.<SensorEvent>instance());
    }

}
