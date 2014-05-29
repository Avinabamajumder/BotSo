#!/bin/bash
if [ $# -lt 1 ] 
then  
	echo "Usage: setupWifi.sh wifiSSID wifiPassKey(optional)"
	exit -1
fi

fileName="$HOME/scripts/wpa_supplicant_temp.conf"

echo "ctrl_interface=DIR=/var/run/wpa_supplicant GROUP=netdev" > $fileName
echo "update_config=1" >> $fileName
echo "" >> $fileName
echo "" >> $fileName

if [  $# -eq 1 ]
then
echo "network={" >> $fileName
echo " ssid=\"$1\"" >> $fileName
echo " scan_ssid=1" >> $fileName
echo " key_mgmt=NONE" >> $fileName
echo " priority=100" >> $fileName
echo "}" >> $fileName
fi

if [  $# -eq 2 ]
then
echo "network={" >> $fileName
echo " scan_ssid=1" >> $fileName
echo " proto=RSN WPA" >> $fileName
echo " key_mgmt=WPA-PSK" >> $fileName
echo " pairwise=CCMP TKIP" >> $fileName
echo " group=CCMP TKIP" >> $fileName
echo " ssid=\"$1\"" >> $fileName
echo " psk=\"$2\"" >> $fileName
echo "}" >> $fileName
fi

sudo bash -c "cat $fileName > /etc/wpa_supplicant/wpa_supplicant.conf"
wpa_cli reconfigure
