# ==================================================
# void default.zzz_sl_block.8()
# a.k.a default.test.test.a.run.lambda_0
# ==================================================

schedule function default:zzz_sl_block/6 1 append
execute unless score default.test.test.a.enabled tbms.var matches 0 run function default:zzz_sl_block/7
schedule clear default:zzz_sl_block/6
scoreboard players set default.test.test.a.crashCount tbms.var 0
