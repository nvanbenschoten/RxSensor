# RxSensor [ ![Download](https://api.bintray.com/packages/nvanbenschoten/maven/rxsensor/images/download.svg) ](https://bintray.com/nvanbenschoten/maven/rxsensor/_latestVersion)
A lightweight wrapper around SensorManager which introduces reactive stream semantics to Sensor and Trigger data.

## Usage

To use RxSensor, an `RxSensorManger` object must be constructed from a standard SensorManager.

```java
SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

RxSensorManager rxSensorManager = new RxSensorManager(sensorManager);
```

#### observeSensor()

This reactive implementation of a SensorManager exposes methods to listen to a specific `Sensor`
as an `Observable<SensorEvent>` stream. These methods correspond roughly to SensorManager's various
supported [registerListener](http://developer.android.com/reference/android/hardware/SensorManager.html#registerListener(android.hardware.SensorEventListener, android.hardware.Sensor, int, int))
methods.

```java
Observable<SensorEvent> sensorObservable =
        rxSensorManager.observeSensor(Sensor.TYPE_ACCELEROMETER, SensorManager.SENSOR_DELAY_NORMAL);
sensorObservable.subscribe(new Action1<SensorEvent>() {
    @Override public void call(SensorEvent event) {
        // TODO react to event...
    }
});
```

#### observeTrigger()

In addition to observing `SensorEvent` streams, `RxSensorManager` also provides the functionality to
observe a `TriggerEvent`. This method roughly corresponds to SensorManager's
[requestTriggerSensor](http://developer.android.com/reference/android/hardware/SensorManager.html#requestTriggerSensor(android.hardware.TriggerEventListener, android.hardware.Sensor))
method.

**NOTE:** While this library targets **API 9** and above, TriggerEvents are only supported on
**API 18** and above. This means that observeSensor also targets API 18 and will throw an exception
if used on a low API level.

```java
Observable<TriggerEvent> triggerObservable =
        rxSensorManager.observeTrigger(Sensor.TYPE_SIGNIFICANT_MOTION);
triggerObservable.subscribe(new Action1<TriggerEvent>() {
    @Override public void call(TriggerEvent event) {
        // TODO react to event...
    }
});
```

## Download

```groovy
compile 'com.nvanbenschoten.rxsensor:rxsensor:1.1.1'
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