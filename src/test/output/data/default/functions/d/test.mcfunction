scoreboard players set default.d.lol.a tbms.var 1
function default:d/lol
scoreboard players operation default.d.test.b tbms.var = default.d.lol._ret tbms.var
tellraw @a [{"score": { "name": "default.d.test.b", "objective": "tbms.var"}, "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"white"}]
scoreboard players set default.d.lol.a tbms.var 0
function default:d/lol
scoreboard players operation default.d.test.c tbms.var = default.d.lol._ret tbms.var
tellraw @a [{"score": { "name": "default.d.test.c", "objective": "tbms.var"}, "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"white"}]
