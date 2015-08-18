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

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;

import junit.framework.TestCase;

import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import rx.observers.TestSubscriber;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("ConstantConditions")
public class RxSensorManagerTest extends TestCase {

    public void testSensorListenerUnregistered() throws Exception {
        SensorManager sensorManager = mockSensorManager();
        RxSensorManager rxSensorManager = new RxSensorManager(sensorManager);

        TestSubscriber<SensorEvent> subscriber = new TestSubscriber<>();
        rxSensorManager.listenToSensor(null, 0).subscribe(subscriber);
        subscriber.unsubscribe();

        verify(sensorManager).unregisterListener(any(SensorEventListener.class));
    }

    public void testNoSensorEventsObservable() throws Exception {
        SensorManager sensorManager = mockSensorManager(0);
        RxSensorManager rxSensorManager = new RxSensorManager(sensorManager);

        TestSubscriber<SensorEvent> subscriber = new TestSubscriber<>();
        rxSensorManager.listenToSensor(null, 0).subscribe(subscriber);

        subscriber.assertValueCount(0);
        subscriber.unsubscribe();
    }

    public void testSingleSensorEventObservable() throws Exception {
        SensorManager sensorManager = mockSensorManager(1);
        RxSensorManager rxSensorManager = new RxSensorManager(sensorManager);

        TestSubscriber<SensorEvent> subscriber = new TestSubscriber<>();
        rxSensorManager.listenToSensor(null, 0).subscribe(subscriber);

        subscriber.assertValueCount(1);
        subscriber.unsubscribe();
    }

    public void testMultipleSensorEventsObservable() throws Exception {
        SensorManager sensorManager = mockSensorManager(3);
        RxSensorManager rxSensorManager = new RxSensorManager(sensorManager);

        TestSubscriber<SensorEvent> subscriber = new TestSubscriber<>();
        rxSensorManager.listenToSensor(null, 0).subscribe(subscriber);

        subscriber.assertValueCount(3);
        subscriber.unsubscribe();
    }

    private SensorManager mockSensorManager() {
        return mockSensorManager(0);
    }

    private SensorManager mockSensorManager(final int events) {
        SensorManager sensorManager = mock(SensorManager.class);

        final ArgumentCaptor<SensorEventListener> argument = ArgumentCaptor.forClass(SensorEventListener.class);
        when(sensorManager.registerListener(argument.capture(), any(Sensor.class), anyInt(), any(Handler.class)))
                .thenAnswer(new Answer() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        for (int i = 0; i < events; i++) {
                            argument.getValue().onSensorChanged(mockSensorEvent());
                        }
                        return null;
                    }
                });

        return sensorManager;
    }

    private SensorEvent mockSensorEvent() {
        return mock(SensorEvent.class);
    }

}