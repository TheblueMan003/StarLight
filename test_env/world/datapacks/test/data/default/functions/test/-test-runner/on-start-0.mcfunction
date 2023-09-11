tellraw @a [{"text": "Test started", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"white"}]
scoreboard players set default.test.TestRunner.index tbms.var -1
scoreboard players set default.test.TestRunner.running tbms.var 0
scoreboard players add default.test.TestRunner.index tbms.var 1
scoreboard players set default.test.TestRunner.running tbms.var 0
execute if score default.test.TestRunner.index tbms.var matches 0 unless score default.test.TestRunner.enabled tbms.var matches 0 run function default:zzz_sl_block/2
