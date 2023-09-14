# ==================================================
# void default.test.test()
# ==================================================

execute unless score default.test.test.a tbms.var = default.test.test.a tbms.var run scoreboard players set default.test.test.a tbms.var 0
scoreboard players set default.test.test._1 tbms.var 0
execute unless score default.test.test.a tbms.var matches 0 run function default:zzz_sl_block/0
execute if score default.test.test._1 tbms.var matches 0 run scoreboard players set default.test.test._0 tbms.var 1
execute unless score default.test.test._0 tbms.var matches 0 run tellraw @a [{"text": "a && value() is true", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"white"}]
