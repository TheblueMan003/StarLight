# ==================================================
# void default.zzz_sl_block.364()
# a.k.a default.standard.string.trimLeft.1
# ==================================================

data modify storage default.standard.string.trim-left.source json set string storage default.standard.string.trim-left.source json 1
scoreboard players add default.standard.string.trimLeft.c tbms.var 1
scoreboard players set default.standard.string.trimLeft._3 tbms.var 0
execute unless data storage default.standard.string.trim-left.source {json:""} run function default:zzz_sl_block/363
execute unless score default.standard.string.trimLeft._3 tbms.var matches 0 run function default:zzz_sl_block/364
