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
    def lazy __add__(int i){
        tick+=i
    }
    def lazy __add__(Time other){
        tick += other.tick
    }
    def lazy __sub__(int i){
        tick-=i
    }
    def lazy __sub__(Time other){
        tick -= other.tick
    }
    def lazy __set__(int i){
        tick = i
    }
    def lazy __set__(Time other){
        tick = other.tick
    }
    def lazy __mult__(int i){
        tick*=i
    }
    def lazy __mult__(Time other){
        tick*=other.tick
    }
    def lazy __div__(int i){
        tick/=i
    }
    def lazy __div__(Time other){
        tick/=other.tick
    }
    def lazy __mod__(int i){
        tick%=i
    }
    def lazy __mod__(Time other){
        tick%=other.tick
    }
    lazy bool __lt__(int i){
        return tick < i
    }
    lazy bool __lt__(Time other){
        return (tick < other.tick)
    }
    lazy bool __le__(int i){
        return (tick <= i)
    }
    lazy bool __le__(Time other){
        return (tick <= other.tick)
    }
    lazy bool __gt__(int i){
        return (tick > i)
    }
    lazy bool __gt__(Time other){
        return (tick > other.tick)
    }
    lazy bool __ge__(int i){
        return (tick >= i)
    }
    lazy bool __ge__(Time other){
        return (tick >= other.tick)
    }
    lazy bool __eq__(int i){
        return i == tick
    }
    lazy bool __eq__(Time other){
        return other.tick == tick
    }
    lazy bool __ne__(int i){
        return i != tick
    }
    lazy bool __ne__(Time other){
        return other.tick != tick
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