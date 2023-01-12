schedule function default:a/adas/crash 1t append
execute unless score default.a.adas.enabled tbms.var matches 0 run function default:a/adas/main
schedule clear default:a/adas/crash
scoreboard players set default.a.adas.crashCount tbms.var 0
