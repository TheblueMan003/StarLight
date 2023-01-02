# template Process
Represent a task that run along side the main tick function.Function to Override:- main: Main function that is repeated as long as the process is running- onStart: callback when the process is star. Cannot be called if the process was is running- onStop: callback when the process is stop. Cannot be called if the process was not running

## loading void reload()
Restart the process on load. (JAVA Only)

## void crash()
Detect maxCommandChainLength extended, and stop process if more than 10 in a row

## void start()
Start the process

## @process.main void run()
Main loop for the process (JAVA Only)

## ticking void mainLoop()
Main loop for the process (Bedrock Only)

## void stop()
Stop the process

## void waitFor(void=>void fct)
- void=>void fct

Add a callback for when the process stop

## @process.count void __count__()
Count the number of active process

## @process.stop void stopall()
Stop the process

## void onStop()


## void onStart()





