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
import android.hardware.SensorManager;
import android.support.annotation.NonNull;

/**
 * Custom exception corresponding to a failure of a {@link SensorManager} to
 * listen to a {@link Sensor} due to a lack of system support or other
 * unknown reasons.
 */
public class SensorException extends Exception {

    public SensorException() {
        super("Sensor not available");
    }

    public SensorException(@NonNull Sensor sensor) {
        super("Sensor not available: " + sensor.getName());
    }

}
