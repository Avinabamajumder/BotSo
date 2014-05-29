#!/bin/bash

$HOME/scripts/confGpioPwm.sh

raspivid -n -w 720 -h 405 -fps 30 -t 9000 -b 18000000 -o $HOME/store/sweepVideo.h264  &
ic="70"

while [ $ic -ge 40 ]
do	
	gpio pwm 1 $ic
	sleep 0.05
	ic=$[$ic-1]
done

while [ $ic -le 100 ]
do
        gpio pwm 1 $ic
        sleep 0.05
        ic=$[$ic+1]
done

while [ $ic -ge 70 ]
do
        gpio pwm 1 $ic
        sleep 0.05
        ic=$[$ic-1]
done

sleep 1
avconv -v error -i  $HOME/store/sweepVideo.h264 -y $HOME/store/sweepVideo.mpg 
gpio pwm 1 0

