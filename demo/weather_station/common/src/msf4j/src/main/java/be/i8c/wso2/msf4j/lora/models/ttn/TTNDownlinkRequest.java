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

package be.i8c.wso2.msf4j.lora.models.ttn;

import be.i8c.wso2.msf4j.lora.models.common.DownlinkRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This is a implementation of interface DownlinkRequest for sending downlink message through The thingsnetwork.
 */
public class TTNDownlinkRequest implements DownlinkRequest
{
    /**
     * Payload of downlink message in String format
     */
    @JsonIgnore
    private String payloadString;
    /**
     * The device id of device which the downlink message is sending to.
     */
    private String deviceId;

    /**
     * port number
     */
    private int port;

    /**
     * is downlink message confirmed?
     */
    private boolean confirmed;

    /**
     * the Base64 encoded payload
     */
    private String payload_raw;

    public TTNDownlinkRequest()
    {

    }

    public TTNDownlinkRequest(String deviceId, String payloadString)
    {
        this.deviceId = deviceId;
        this.payloadString = payloadString;
        this.port = 1;
        this.confirmed = false;
    }

    @JsonIgnore
    public String getPayloadString() {
        return payloadString;
    }

    public void setPayloadString(String payloadString) {
        this.payloadString = payloadString;
    }

    @JsonProperty("dev_id")
    public String getDeviceId() {
        return deviceId;
    }

    @JsonProperty("port")
    public int getPort() {
        return port;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getPayload_raw() {
        return payload_raw;
    }

    public void setPayload_raw(String payload_raw) {
        this.payload_raw = payload_raw;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
