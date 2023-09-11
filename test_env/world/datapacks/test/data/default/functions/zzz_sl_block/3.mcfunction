schedule function default:test/-test-runner/crash 1 append
schedule clear default:test/-test-runner/crash
scoreboard players set default.test.TestRunner.crashCount tbms.var 0
execute unless score default.test.TestRunner.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/3 1 append
