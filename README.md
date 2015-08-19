# RxSensor [ ![Download](https://api.bintray.com/packages/nvanbenschoten/maven/rxsensor/images/download.svg) ](https://bintray.com/nvanbenschoten/maven/rxsensor/_latestVersion)
A lightweight wrapper around SensorManager which introduces reactive stream semantics to Sensor data.

## Usage

To use RxSensor, an `RxSensorManger` object must be constructed from a standard SensorManager.

```java
SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

RxSensorManager rxSensorManager = new RxSensorManager(sensorManager);
```

This reactive implementation of a SensorManager exposes methods to listen to a specific `Sensor`
as an `Observable<SensorEvent>` stream. These methods correspond roughly to SensorManager's various
supported [registerListener](http://developer.android.com/reference/android/hardware/SensorManager.html#registerListener(android.hardware.SensorEventListener, android.hardware.Sensor, int, int))
methods.

```java
Observable<SensorEvent> sensorObservable =
        rxSensorManager.observe(Sensor.TYPE_ACCELEROMETER, SensorManager.SENSOR_DELAY_NORMAL);
sensorObservable.subscribe(new Action1<SensorEvent>() {
    @Override public void call(SensorEvent event) {
        // TODO react to event...
    }
});
```

## Download

```groovy
compile 'com.nvanbenschoten.rxsensor:rxsensor:1.0.1'
```

## License

    Copyright 2015 Nathan VanBenschoten

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.