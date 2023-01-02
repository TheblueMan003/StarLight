# template utils.cprocess.CProcess
Represent a task that run along side the main tick function.This process will only stop if `stop` is called as many time as `start` was called.Function to Override:- main: Main function that is repeated as long as the process is running- onStart: callback when the process is star. Cannot be called if the process was is running- onStop: callback when the process is stop. Cannot be called if the process was not running

## loading void utils.cprocess.reload()
Restart the process on load. (JAVA Only)

## void utils.cprocess.crash()
Detect maxCommandChainLength extended, and stop process if more than 10 in a row

## void utils.cprocess.start()
Start the process

## @process.main void utils.cprocess.run()
Main loop for the process (JAVA Only)

## ticking void utils.cprocess.mainLoop()
Main loop for the process (Bedrock Only)

## void utils.cprocess.stop()
Stop the process

## void utils.cprocess.waitFor(void=>void fct)
- void=>void fct

Add a callback for when the process stop

## @process.count void utils.cprocess.__count__()
Count the number of active process

## @process.stop void utils.cprocess.stopall()
Stop the process

## void utils.cprocess.onStop()


## void utils.cprocess.onStart()





