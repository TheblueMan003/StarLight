package math.time

"""
Struct to represent time
"""
struct Time{
    int tick
    def __init__(){
        tick = 0
    }
    def __init__(int t){
        tick = t
    }
    def __init__(int h, int m, int s){
        tick = ((h*60+m)*60+s)*20
    }
    def __init__(int h, int m, int s, int t){
        tick = ((h*60+m)*60+s)*20+t
    }
    lazy __add__(int i){
        tick+=i
    }
    lazy __add__(Time other){
        tick += other.tick
    }
    lazy __sub__(int i){
        tick-=i
    }
    lazy __sub__(Time other){
        tick -= other.tick
    }
    lazy __set__(int i){
        tick = i
    }
    lazy __set__(Time other){
        tick = other.tick
    }
    lazy __mult__(int i){
        tick*=i
    }
    lazy __mult__(Time other){
        tick*=other.tick
    }
    lazy __div__(int i){
        tick/=i
    }
    lazy __div__(Time other){
        tick/=other.tick
    }
    lazy __mod__(int i){
        tick%=i
    }
    lazy __mod__(Time other){
        tick%=other.tick
    }
    lazy bool __smaller__(int i){
        if (tick < i){return true}
        if (tick >= i){return false}
    }
    lazy bool __smaller__(Time other){
        if (tick < other.tick){return true}
        if (tick >= other.tick){return false}
    }
    lazy bool __smaller_or_equals__(int i){
        if (tick <= i){return true}
        if (tick > i){return false}
    }
    lazy bool __smaller_or_equals__(Time other){
        if (tick <= other.tick){return true}
        if (tick > other.tick){return false}
    }
    lazy bool __bigger__(int i){
        if (tick > i){return true}
        if (tick <= i){return false}
    }
    lazy bool __bigger__(Time other){
        if (tick > other.tick){return true}
        if (tick <= other.tick){return false}
    }
    lazy bool __bigger_or_equals__(int i){
        if (tick >= i){return true}
        if (tick < i){return false}
    }
    lazy bool __bigger_or_equals__(Time other){
        if (tick >= other.tick){return true}
        if (tick < other.tick){return false}
    }

    """
    Return the total amount of milliseconds
    """
    lazy int inMilliseconds(){
        return (tick*5)
    }

    """
    Return the milliseconds part of the time
    """
    lazy int getMilliseconds(){
        return (inMilliSeconds()%100)
    }

    """
    Return the total amount of seconds
    """
    lazy int inSeconds(){
        return (tick/20)
    }
    """
    Return the ticks part of the time
    """
    lazy int getTick(){
        return (tick%20)
    }

    """
    Return the seconds part of the time
    """
    lazy int getSeconds(){
        return (inSeconds()%60)
    }

    """
    Return the total amount of minutes
    """
    lazy int inMinutes(){
        return (tick/(20*60))
    }

    """
    Return the minutes part of the time
    """
    lazy int getMinutes(){
        return (inMinutes()%60)
    }

    """
    Return the total amount of hours
    """
    lazy int inHours(){
        return (tick/(20*60*60))
    }

    """
    Return the hours part of the time
    """
    lazy int getHours(){
        return (inHours()%24)
    }

    """
    Return the total amount of days
    """
    lazy int inDay(){
        return (tick/(20*60*60*24))
    }

    """
    Return the days part of the time
    """
    lazy int getDay(){
        return (inDay())
    }

    lazy rawjson __toJson__(){
        int h = tick/(20*60*60)
        int m = (tick/(20*60))%60
        int s = (tick/20)%60
        int ms = tick*5
        return(h,":",m,":",s,".",ms)
    }
}