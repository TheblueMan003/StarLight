schedule function default:a/test/crash 1 append
execute unless score default.a.test.enabled tbms.var matches 0 run function default:a/test/main
schedule clear default:a/test/crash
scoreboard players set default.a.test.crashCount tbms.var 0
