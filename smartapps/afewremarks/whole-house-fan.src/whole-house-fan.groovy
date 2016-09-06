/**
 *  Whole House Fan
 *
 *  Copyright 2014 Brian Steere
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
definition(
    name: "Whole House Fan",
    namespace: "afewremarks",
    author: "Brian Steere & Mark West",
    description: "Toggle a whole house fan (switch) when: Outside is cooler than inside, Inside is above x temp, Thermostat is off",
    category: "Green Living",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Developers/whole-house-fan.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Developers/whole-house-fan%402x.png"
)


preferences {
	section("Outdoor") {
		input "outTemp", "capability.temperatureMeasurement", title: "Outdoor Thermometer", required: true
	}
    
    section("Indoor") {
    	input "inTemp", "capability.temperatureMeasurement", title: "Indoor Thermometer", required: true
        input "minTemp", "number", title: "Fan on temp diff", required: true
        input "offTemp", "number", title: "Fan off temp diff", required: true
        input "fans", "capability.switch", title: "Vent Fan", multiple: true, required: true
    }
    
    section("Windows/Doors") {
    	paragraph "[Optional] Only turn on the fan if at least one of these is open"
        input "checkContacts", "enum", title: "Check windows/doors", options: ['Yes', 'No'], required: true 
    	input "contacts", "capability.contactSensor", title: "Windows/Doors", multiple: true, required: false
    }
    
    section("Notifications") {
    	input "sendPushMessage", "enum", title: "Send a push notification?", metadata: [values: ["Yes", "No"]], required: false
    	input "phone", "phone", title: "Send a Text Message?", required: false
  }
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	state.fanRunning = false;
    
    //state.hot = false;
    
    subscribe(outTemp, "temperature", "checkThings");
    subscribe(inTemp, "temperature", "checkThings");
    subscribe(contacts, "contact", "checkThings");
    
}

def inthot(){
	//hot = state.hot;
    hot = false;
    
}

def wasHot(){
	
	hot = true;
    return hot;
}

def notHot(){
	hot = false;
    return hot;
}

def checkThings(evt) {
	
	def outsideTemp = settings.outTemp.currentTemperature
    def insideTemp = settings.inTemp.currentTemperature
    def somethingOpen = settings.checkContacts == 'No' || settings.contacts?.find { it.currentContact == 'open' }
    
    log.debug "Inside: $insideTemp, Outside: $outsideTemp, Thermostat: $thermostatMode, Something Open: $somethingOpen"
    
    def shouldRun;
    def tempDiff = insideTemp - outsideTemp;
    inthot()
    if(tempDiff > settings.minTemp){
    shouldRun = true;
    log.debug "It's Hot"
    wasHot()
    }
    else if(tempDiff < settings.offTemp && hot) {
    shouldRun = true;
    log.debug "Cooling down"
    }
    else{
    shouldRun = false;
    notHot()
    log.debug "It's Cool"
    }
   // state.hot = hot;
/*    
    def shouldRun = true;
    
    if(insideTemp < outsideTemp) {
    	log.debug "Not running due to insideTemp > outdoorTemp"
    	shouldRun = false;
    }
    
    if(insideTemp < settings.minTemp) {
    	log.debug "Not running due to insideTemp < minTemp"
    	shouldRun = false;
    }
    
    if(!somethingOpen) {
    	log.debug "Not running due to nothing open"
        shouldRun = false
    }
    */
    if(shouldRun && !state.fanRunning) {
    	fans.on();
        state.fanRunning = true;
        log.debug "Fan On"
       // sendPush("Turn on the house fan now.")
    } else if(!shouldRun && state.fanRunning) {
    	fans.off();
        state.fanRunning = false;
        log.debug "Fan Off"
       // sendPush("Turn off the house fan now.")
    }
}