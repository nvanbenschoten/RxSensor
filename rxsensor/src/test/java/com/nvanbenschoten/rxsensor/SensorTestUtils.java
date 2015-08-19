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

import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SensorTestUtils {

    public static final int GOOD_SENSOR = 0;
    public static final int BAD_SENSOR = 1;

    public static SensorManager mockSensorManager(final int events) {
        return mockSensorManager(events, true);
    }

    public static SensorManager mockBadSensorManager() {
        return mockSensorManager(0, false);
    }

    private static SensorManager mockSensorManager(final int events, final boolean result) {
        SensorManager sensorManager = mock(SensorManager.class);

        when(sensorManager.getDefaultSensor(GOOD_SENSOR)).thenReturn(mockSensor());
        when(sensorManager.getDefaultSensor(BAD_SENSOR)).thenReturn(null);

        final ArgumentCaptor<SensorEventListener> argument = ArgumentCaptor.forClass(SensorEventListener.class);
        when(sensorManager.registerListener(argument.capture(), any(Sensor.class), anyInt(), any(Handler.class)))
                .thenAnswer(new Answer<Boolean>() {
                    @Override
                    public Boolean answer(InvocationOnMock invocation) throws Throwable {
                        for (int i = 0; i < events; i++) {
                            argument.getValue().onSensorChanged(mockSensorEvent());
                        }
                        return result;
                    }
                });

        return sensorManager;
    }

    private static Sensor mockSensor() {
        return mock(Sensor.class);
    }

    private static SensorEvent mockSensorEvent() {
        return mock(SensorEvent.class);
    }

}
