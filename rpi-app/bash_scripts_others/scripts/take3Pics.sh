
#!/bin/bash

$HOME/scripts/confGpioPwm.sh
gpio pwm 1 70
gpio pwm 1 40
raspistill -n -t 120 -ISO 800 -w 640 -h 480 -q 30 -o $HOME/store/right.jpg
gpio pwm 1 70
raspistill -n -t 120 -ISO 800 -w 640 -h 480 -q 30 -o $HOME/store/center.jpg
gpio pwm 1 100
raspistill -n -t 120 -ISO 800 -w 640 -h 480 -q 30 -o $HOME/store/left.jpg
gpio pwm 1 70
sleep 0.5
gpio pwm 1 0

