# client-sensor
Signal Strength Detector Client


Status
------
All the code is kind of experimental so that we can get started. Idea is to explore how to get sensor data and this part is done. 
1. Basic UI skeleton code is ready. It needs further fine tuning.
2. Added code which measure signal strength  - right now gets only LTE signal strength
3. Added code which tracks the location

To do
-----
1. To place toggle button in middle and change the text
2. To develop a switch case kind of statement based on network type (GSM, CDMA, LTE etc) to collect relevant parameters. 
3. Location service is not completely working. Needs little investigation
4. Need to take a decision - There are independent APIs for location tracking and signal strength change tracking. How to optimally combine these two to send the correct update to server
5. There are battery saving mode tracking for location. This needs to be explored further
6. When user turns off the tracking button, app should unsubscribe from location and signal strength services.
7. Just a software architecture view point - it is better to have looper thread and let location and signal strength tracker callback functions post to this looper thread. So only looper thread knows last known location and signal strength, so it can send them together to server
8. Need to have intelligent code which keeps track of previous signal strength. Because, as per my observation, system is giving callback even when there is no signal strength change.
