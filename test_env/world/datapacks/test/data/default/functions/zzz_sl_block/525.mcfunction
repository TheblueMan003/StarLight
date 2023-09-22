# ==================================================
# void default.zzz_sl_block.525()
# a.k.a default.test.__init__
# ==================================================

scoreboard players set default.test.__pass__ tbms.var 0
scoreboard players set default.test.__fail__ tbms.var 0
scoreboard players set default.test.__total__ tbms.var 0
execute unless score default.test.TestRunner.enabled tbms.var = default.test.TestRunner.enabled tbms.var run scoreboard players set default.test.TestRunner.enabled tbms.var 0
execute unless score default.test.TestRunner.crashCount tbms.var = default.test.TestRunner.crashCount tbms.var run scoreboard players set default.test.TestRunner.crashCount tbms.var 0
execute unless score default.test.TestRunner.callback tbms.var = default.test.TestRunner.callback tbms.var run scoreboard players set default.test.TestRunner.callback tbms.var 0
execute unless score default.test.TestRunner.index tbms.var = default.test.TestRunner.index tbms.var run scoreboard players set default.test.TestRunner.index tbms.var 0
execute unless score default.test.TestRunner.running tbms.var = default.test.TestRunner.running tbms.var run scoreboard players set default.test.TestRunner.running tbms.var 0
