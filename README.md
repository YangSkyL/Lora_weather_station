# Weather station
## Introduction
The purpose of this project is to build a weather station by using loRa technology and [WSO2 msf4j](http://wso2.com/products/microservices-framework-for-java/) framework. In this demo we will be monitoring the temperature, humidity, pressure and light. 

LoRa is a new standard fot the IoT, It provides a long range, low power, but also low bandwidth communication between lora devices.

In our demo, we use both loRa network provided by [Proximus](https://www.enco.io/) and our own gateway.

![dashboard](./doc/img/screenshot_kibana.png)

## Architecture

### High level architecture

![architecture](./doc/img/architecture_weather_station.jpg)

* **loRa devices:** This devices measures the environmental data with difference sensors and sends the data over loRa network to the Gateway. 


*  **Gateway:** The gateway form the bridge between devices and the middleware handler. Devices use low power networks like LoRaWAN to connect to the Gateway, while the Gateway uses high bandwidth networks like WiFi, Ethernet or Cellular to connect to Middleware handler. In our demo, it will be either Proximus or Lorank8.


*  **Middleware handler:** Middleware handler is responsible for routing loRa packets between devices and applications.

*  **Lora micro service:** this micro service is to store the loRa packets foward by The Middleware handler and send downlink messages to specific loRa devices. In addition, managing the communication with the middleware handler.

* **Elasticsearch:** The database where the data will be stored. Postgresql can be used too.

* **Kibana:** It provide a dashboard for visualizing and analyzing the data.

### Functional specs

* **Lora microservice**
	* Receving and storing the loRa packets foward by TTN.
	* Sending the downlink message to specific loRa device.
	* Managing the connections TTN back-end.

* **Arduino**

### Technical specs

* **Lora microserivce**
	* Lora microservice is build upon [MSF4j](https://github.com/wso2/msf4j) and [Spring](https://spring.io). 
	* Both HTTP and MQTT protocol can be used for integration with Middleware handler.
	* This microservice uses either Postgresql or Elasticsearch as database.
		* Spring & JPA is used to persist the data to Postgresql database.
		* Elasticsearch java API is used to persist the data to Elasticsearch database.
	* This microservice contains a set of REST API for managing the MQTT- or HTTP client and sending the downlink message to specific lora devices.

* **Arduino**

### <a name="apireferrences">API referrences</a>

##### 1. MQTT client administration 

| **Method** | **HTTP Request** | **Description** |
|---|---|---|
| start  | POST /api/ttn/manage/start  | start the mqtt client of TTN  |
| stop  | POST /api/ttn/manage/stop  | stop the mqtt client of TTN  |

## Installation

* With own gateway: Please check [the install folder]()
* With Proximus Enco: Please check [thie install folder]()