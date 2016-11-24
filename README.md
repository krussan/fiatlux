# fiatlux

A service for home automation using a Tellstick.

## Setup

1. Get a tellstick dongle
2. Get a raspberry pi (or any other computer)
3. Download and compile the telldusd core as described 
here: http://elinux.org/R-Pi_Tellstick_core
4. Set up your receivers in /etc/tellstick.conf.
5. clone the repo and do "mvn package"
6. start the service using "run.sh <port>". the port number being the 
port that the android app uses to communicate with the service on the 
local network.
7. Download the app and install it on your android phone
8. In the app setup the IP and port number to the service

## Scheduling

Fiatlux service uses a crontab like syntax when defining the scheduling.
Look in the file cron-example for examples and configuration regarding 
scheduling.


