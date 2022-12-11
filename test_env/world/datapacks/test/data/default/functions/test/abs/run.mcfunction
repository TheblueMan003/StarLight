scoreboard players set default.test.abs.run._1 tbms.var 0
function default:test/abs/main
scoreboard players operation default.test.abs.run._0 tbms.var = default.test.abs.main._ret tbms.var
execute unless score default.test.abs.run._0 tbms.var matches 0 run function default/zzz_sl_block/_1
execute if score default.test.abs.run._1 tbms.var matches 0 run tellraw @a [{"text": "[FAILLED] ", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"red"},{"text": "$this", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"white"}]
