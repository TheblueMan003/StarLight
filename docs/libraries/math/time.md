# struct Time
Struct to represent time

## void __init__()


## void __init__(int t)
- int t



## void __init__(int h, int m, int s)
- int h
- int m
- int s



## void __init__(int h, int m, int s, int t)
- int h
- int m
- int s
- int t



## lazy void __add__(int i)
- int i



## lazy void __add__(Time? other)
- Time? other



## lazy void __sub__(int i)
- int i



## lazy void __sub__(Time? other)
- Time? other



## lazy void __set__(int i)
- int i



## lazy void __set__(Time? other)
- Time? other



## lazy void __mult__(int i)
- int i



## lazy void __mult__(Time? other)
- Time? other



## lazy void __div__(int i)
- int i



## lazy void __div__(Time? other)
- Time? other



## lazy void __mod__(int i)
- int i



## lazy void __mod__(Time? other)
- Time? other



## lazy bool __smaller__(int i)
- int i



## lazy bool __smaller__(Time? other)
- Time? other



## lazy bool __smaller_or_equals__(int i)
- int i



## lazy bool __smaller_or_equals__(Time? other)
- Time? other



## lazy bool __bigger__(int i)
- int i



## lazy bool __bigger__(Time? other)
- Time? other



## lazy bool __bigger_or_equals__(int i)
- int i



## lazy bool __bigger_or_equals__(Time? other)
- Time? other



## lazy int inMilliseconds()
Return the total amount of milliseconds

## lazy int getMilliseconds()
Return the milliseconds part of the time

## lazy int inSeconds()
Return the total amount of seconds

## lazy int getTick()
Return the ticks part of the time

## lazy int getSeconds()
Return the seconds part of the time

## lazy int inMinutes()
Return the total amount of minutes

## lazy int getMinutes()
Return the minutes part of the time

## lazy int inHours()
Return the total amount of hours

## lazy int getHours()
Return the hours part of the time

## lazy int inDay()
Return the total amount of days

## lazy int getDay()
Return the days part of the time

## lazy rawjson __toJson__()





