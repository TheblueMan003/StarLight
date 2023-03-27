schedule function default:test/test/crash 1 append
execute unless score default.test.test.enabled tbms.var matches 0 run function default:test/test/main
schedule clear default:test/test/crash
scoreboard players set default.test.test.crashCount tbms.var 0
