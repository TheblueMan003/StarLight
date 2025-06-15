# ==================================================
# void default.zzz_sl_block.361()
# a.k.a default.standard.string.trimRight.1
# ==================================================

data modify storage default.standard.string.slice.source json set string storage default.standard.string.trim-right.source json
scoreboard players set default.standard.string.slice.start tbms.var 0
scoreboard players set default.standard.string.slice.end tbms.var -1
function default:standard/string/slice
data modify storage default.standard.string.trim-right.source json set string storage default.standard.string.slice._ret json
scoreboard players add default.standard.string.trimRight.c tbms.var 1
scoreboard players set default.standard.string.trimRight._3 tbms.var 0
execute unless data storage default.standard.string.trim-right.source {json:""} run function default:zzz_sl_block/360
execute unless score default.standard.string.trimRight._3 tbms.var matches 0 run function default:zzz_sl_block/361
