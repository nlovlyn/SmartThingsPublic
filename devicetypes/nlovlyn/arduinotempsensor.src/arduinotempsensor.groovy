/**
 *  arduinoTempSensor
 *
 *  Copyright 2016 Nicholas Lovlyn
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
    definition (name: "arduinoTempSensor", namespace: "nlovlyn", author: "Nicholas Lovlyn") {
		command "on"
		capability "Temperature Measurement"
        //capability "Switch"
       
        
		attribute "temperature","string"
	}

	simulator {
	}
        
        valueTile("temperature", "device.temperature", width: 1, height: 1, inactiveLabel: false) {
        	state("temperature", label: '${currentValue}°F', unit:"F", 
            	backgroundColors: [
                	[value: 31, color: "#153591"],
                	[value: 44, color: "#1e9cbb"],
                	[value: 59, color: "#90d2a7"],
                	[value: 74, color: "#44b621"],
                	[value: 84, color: "#f1d801"],
                	[value: 95, color: "#d04e00"],
                	[value: 96, color: "#bc2323"]
            	]
        	)
    	}
      /*  standardTile("switch", "device.switch", width: 1, height: 1, canChangeIcon: true, canChangeBackground: true) {
			state "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821"
			state "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
		}
        standardTile("greeting", "device.greeting", width: 1, height: 1, canChangeIcon: true, canChangeBackground: true) {
			state "default", label: 'hello', action: "on", icon: "st.switches.switch.off", backgroundColor: "#ccccff"
		} */

        main (["temperature"])
        details(["temperature", "configure", "greeting"/*, "switch"*/])
	}

///*
// parse events into attributes
def parse(String description) {
    def msg = zigbee.parse(description)?.text

    def parts = msg.split(" ")
    def name  = parts.length>0?parts[0].trim():null    
    //def value = parts.length>1?parts[1].trim():null
    def value = parts.length>1?zigbee.parseHATemperatureValue(parts[1].trim(), "", getTemperatureScale()):null
    def unit = getTemperatureScale()

    name = value != "ping" ? name : null
    def result = createEvent(name: name, value: value, unit: unit)
    log.debug result
    return result
//*/

/*
// Parse incoming device messages to generate events
def parse(String description) {
	log.debug "parse description: $description"
//	def name = parseName(description)
    def name = zigbee.parse(description)?.text
//	def value = parseValue(description)
    def value = parts.length>1?zigbee.parseHATemperatureValue(parts[1].trim(), "", getTemperatureScale()):null
//	def unit = name == "temperature" ? getTemperatureScale() : null
    def unit = getTemperatureScale()
	def result = createEvent(name: name, value: value, unit: unit)
	log.debug "Parse returned ${result?.descriptionText}"
	return result
}

private String parseName(String description) {
	if (description?.startsWith("temperature")) {
		return "temperature"
    }
	
}

private String parseValue(String description) {
	if (description?.startsWith("temperature")) {
		return zigbee.parseHATemperatureValue(description, "temperature: ", getTemperatureScale())
	} 
	*/
}
def on() {
    zigbee.smartShield(text: "on").format()
    log.debug "on sent"
}

def off() {
	zigbee.smartShield(text: "off").format()
     log.debug "off sent"
}