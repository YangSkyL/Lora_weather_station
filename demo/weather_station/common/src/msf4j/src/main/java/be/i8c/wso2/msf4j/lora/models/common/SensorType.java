/*
  * Copyright (c) 2017, i8c N.V. (Integr8 Consulting; http://www.i8c.be)
  * All Rights Reserved.
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
package be.i8c.wso2.msf4j.lora.models.common;

/**
 * This is a enum which represents all types of sensor.
 * It also holds different the minimum and maximum value for each sensor types.
 */
public enum SensorType {
    Temperature(-50,100,10.0d,"Temperature sensor","temperature_sensor_value"), Light(0,150, 10.0d,"Light sensor","light_sensor_value"),
    Pressure(900,1100, 10.0d,"Pressure sensor","pressure_sensor_value"), Humidity(0,100, 10.0d, "Humidity sensor","humidity_sensor_value"),
    AirQuality(0,1000,10.0d,"air quality","air_quality_value"), BatteryLevel(0,101,1000.0d,"BatteryLevel sensor","batterylevel_sensor_value");

    /**
     * the minimum value which is valid per sensor type.
     */
    private final double min;
    /**
     * the maximum value which is valid per sensor type
     */
    private final double max;
    private final double factor;
    /**
     * This is the description of specified type in json object of proximus
     */
    private String desc;
    private String valueString;
    
    SensorType(double min, double max, double factor, String desc, String valueString)
    {
        this.min = min;
        this.max = max;
        this.factor = factor;
        this.desc = desc;
        this.valueString = valueString;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getFactor() {return factor;}

    public String getDesc() {
        return desc;
    }

    public String getValueString() {
        return valueString;
    }
}
