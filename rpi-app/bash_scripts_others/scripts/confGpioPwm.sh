#!/bin/bash

gpio mode 1 pwm
gpio pwm-ms
gpio pwmc 384
gpio pwmr 1000
gpio pwm 1 70
sleep .3
gpio pwm 1 0
