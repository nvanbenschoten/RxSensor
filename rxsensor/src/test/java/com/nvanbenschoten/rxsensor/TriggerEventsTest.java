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
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.Build.VERSION_CODES;

import junit.framework.TestCase;

import rx.observers.TestSubscriber;

import static com.nvanbenschoten.rxsensor.SensorTestUtils.BAD_SENSOR;
import static com.nvanbenschoten.rxsensor.SensorTestUtils.GOOD_SENSOR;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

@TargetApi(VERSION_CODES.JELLY_BEAN_MR2)
@SuppressWarnings({"ConstantConditions", "ThrowableResultOfMethodCallIgnored"})
public class TriggerEventsTest extends TestCase {

    public void testTriggerEventsObservable() throws Exception {
        SensorManager sensorManager = SensorTestUtils.mockSensorManager();
        RxSensorManager rxSensorManager = new RxSensorManager(sensorManager);

        TestSubscriber<TriggerEvent> subscriber = new TestSubscriber<>();
        rxSensorManager.observeTrigger(GOOD_SENSOR).subscribe(subscriber);

        subscriber.assertValueCount(1);
        subscriber.unsubscribe();
    }

    public void testTriggerListenerUnregistered() throws Exception {
        SensorManager sensorManager = SensorTestUtils.mockSensorManager();
        RxSensorManager rxSensorManager = new RxSensorManager(sensorManager);

        TestSubscriber<TriggerEvent> subscriber = new TestSubscriber<>();
        rxSensorManager.observeTrigger(GOOD_SENSOR).subscribe(subscriber);
        subscriber.unsubscribe();

        verify(sensorManager).cancelTriggerSensor(any(TriggerEventListener.class), any(Sensor.class));
    }

    public void testTriggerArgumentError() throws Exception {
        SensorManager sensorManager = SensorTestUtils.mockSensorManager();
        RxSensorManager rxSensorManager = new RxSensorManager(sensorManager);

        TestSubscriber<TriggerEvent> subscriber = new TestSubscriber<>();
        rxSensorManager.observeTrigger(BAD_SENSOR).subscribe(subscriber);

        assertEquals(subscriber.getOnErrorEvents().size(), 1);
        assertTrue(subscriber.getOnErrorEvents().get(0) instanceof SensorException);
    }

    public void testTriggerRequestError() throws Exception {
        SensorManager sensorManager = SensorTestUtils.mockBadSensorManager();
        RxSensorManager rxSensorManager = new RxSensorManager(sensorManager);

        TestSubscriber<TriggerEvent> subscriber = new TestSubscriber<>();
        rxSensorManager.observeTrigger(BAD_SENSOR).subscribe(subscriber);

        assertEquals(subscriber.getOnErrorEvents().size(), 1);
        assertTrue(subscriber.getOnErrorEvents().get(0) instanceof SensorException);
    }

}