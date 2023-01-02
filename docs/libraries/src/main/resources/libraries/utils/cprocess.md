List()


# CProcess
Represent a task that run along side the main tick function.This process will only stop if `stop` is called as many time as `start` was called.Function to Override:- main: Main function that is repeated as long as the process is running- onStart: callback when the process is star. Cannot be called if the process was is running- onStop: callback when the process is stop. Cannot be called if the process was not running


## reload

Restart the process on load. (JAVA Only)
## crash

Detect maxCommandChainLength extended, and stop process if more than 10 in a row
## start

Start the process
## run

Main loop for the process (JAVA Only)List()

## mainLoop

Main loop for the process (Bedrock Only)List()
## stop

Stop the process
## waitFor
- void=>void fct
Add a callback for when the process stop
## __count__

Count the number of active process
## stopall

Stop the process
## onStop


## onStart


