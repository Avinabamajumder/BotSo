
#!/bin/bash

gpio mode 4 output
ic="0"
while [ $ic -lt $3 ]
do	
	gpio write 4 1
	sleep $1
	ic=$[$ic+1]
	gpio write 4 0 
	sleep $2
done

