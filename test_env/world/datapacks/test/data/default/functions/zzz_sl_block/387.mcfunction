# ==================================================
# void default.zzz_sl_block.387()
# a.k.a default.test.TestRunner.start.1
# ==================================================

scoreboard players set default.test.TestRunner.enabled tbms.var 1
tellraw @a [{"text": "Test started", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"white"}]
scoreboard players set default.test.TestRunner.index tbms.var -1
scoreboard players set default.test.TestRunner.running tbms.var 0
function default:test/-test-runner/next
execute unless score default.test.TestRunner.enabled tbms.var matches 0 run function default:zzz_sl_block/385
