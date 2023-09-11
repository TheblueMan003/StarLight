scoreboard players set default.test.__pass__ tbms.var 0
scoreboard players set default.test.__fail__ tbms.var 0
scoreboard players set default.test.__total__ tbms.var 0
execute unless score default.test.TestRunner.enabled tbms.var = default.test.TestRunner.enabled tbms.var run scoreboard players set default.test.TestRunner.enabled tbms.var 0
execute if score default.test.TestRunner.enabled tbms.var matches 0 run function default:zzz_sl_block/6
