#!/bin/bash

# to be added to the startup

sudo modprobe wire
sudo modprobe w1-gpio
sudo modprobe w1-therm 
#getting the temp data
cat /sys/bus/w1/devices/`cat /sys/bus/w1/devices/w1_bus_master1/w1_master_slaves`/w1_slave

