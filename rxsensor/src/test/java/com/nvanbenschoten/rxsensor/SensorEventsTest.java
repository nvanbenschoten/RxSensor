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

import junit.framework.TestCase;

import rx.observers.TestSubscriber;

import static com.nvanbenschoten.rxsensor.SensorTestUtils.BAD_SENSOR;
import static com.nvanbenschoten.rxsensor.SensorTestUtils.GOOD_SENSOR;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

@SuppressWarnings({"ConstantConditions", "ThrowableResultOfMethodCallIgnored"})
public class SensorEventsTest extends TestCase {

    public void testNoSensorEventsObservable() throws Exception {
        SensorManager sensorManager = SensorTestUtils.mockSensorManager(0);
        RxSensorManager rxSensorManager = new RxSensorManager(sensorManager);

        TestSubscriber<SensorEvent> subscriber = new TestSubscriber<>();
        rxSensorManager.observeSensor(GOOD_SENSOR, 0).subscribe(subscriber);

        subscriber.assertValueCount(0);
        subscriber.unsubscribe();
    }

    public void testSingleSensorEventObservable() throws Exception {
        SensorManager sensorManager = SensorTestUtils.mockSensorManager(1);
        RxSensorManager rxSensorManager = new RxSensorManager(sensorManager);

        TestSubscriber<SensorEvent> subscriber = new TestSubscriber<>();
        rxSensorManager.observeSensor(GOOD_SENSOR, 0).subscribe(subscriber);

        subscriber.assertValueCount(1);
        subscriber.unsubscribe();
    }

    public void testMultipleSensorEventsObservable() throws Exception {
        SensorManager sensorManager = SensorTestUtils.mockSensorManager(3);
        RxSensorManager rxSensorManager = new RxSensorManager(sensorManager);

        TestSubscriber<SensorEvent> subscriber = new TestSubscriber<>();
        rxSensorManager.observeSensor(GOOD_SENSOR, 0).subscribe(subscriber);

        subscriber.assertValueCount(3);
        subscriber.unsubscribe();
    }

    public void testSensorListenerUnregistered() throws Exception {
        SensorManager sensorManager = SensorTestUtils.mockSensorManager();
        RxSensorManager rxSensorManager = new RxSensorManager(sensorManager);

        TestSubscriber<SensorEvent> subscriber = new TestSubscriber<>();
        rxSensorManager.observeSensor(GOOD_SENSOR, 0).subscribe(subscriber);
        subscriber.unsubscribe();

        verify(sensorManager).unregisterListener(any(SensorEventListener.class), any(Sensor.class));
    }

    public void testSensorArgumentError() throws Exception {
        SensorManager sensorManager = SensorTestUtils.mockSensorManager();
        RxSensorManager rxSensorManager = new RxSensorManager(sensorManager);

        TestSubscriber<SensorEvent> subscriber = new TestSubscriber<>();
        rxSensorManager.observeSensor(BAD_SENSOR, 0).subscribe(subscriber);

        assertEquals(subscriber.getOnErrorEvents().size(), 1);
        assertTrue(subscriber.getOnErrorEvents().get(0) instanceof SensorException);
    }

    public void testSensorRegistrationError() throws Exception {
        SensorManager sensorManager = SensorTestUtils.mockBadSensorManager();
        RxSensorManager rxSensorManager = new RxSensorManager(sensorManager);

        TestSubscriber<SensorEvent> subscriber = new TestSubscriber<>();
        rxSensorManager.observeSensor(GOOD_SENSOR, 0).subscribe(subscriber);

        assertEquals(subscriber.getOnErrorEvents().size(), 1);
        assertTrue(subscriber.getOnErrorEvents().get(0) instanceof SensorException);
    }

}