# ==================================================
# void default.zzz_sl_block.4()
# a.k.a default.test.TestRunner.run.lambda_0
# ==================================================

schedule function default:test/-test-runner/crash 1 append
execute unless score default.test.TestRunner.enabled tbms.var matches 0 run function default:test/-test-runner/main
schedule clear default:test/-test-runner/crash
scoreboard players set default.test.TestRunner.crashCount tbms.var 0
