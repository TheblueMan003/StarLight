# ==================================================
# void default.zzz_sl_block.378()
# a.k.a default.standard.string.contains.1
# ==================================================

data modify storage default.standard.string.starts-with.source json set string storage default.standard.string.contains.source json
data modify storage default.standard.string.starts-with.value json set string storage default.standard.string.contains.value json
function default:standard/string/starts-with
execute unless score default.standard.string.startsWith._ret tbms.var matches 0 run scoreboard players set default.standard.string.contains.ret tbms.var 1
data modify storage default.standard.string.contains.source json set string storage default.standard.string.contains.source json 1
scoreboard players add default.standard.string.contains.c tbms.var 1
execute if score default.standard.string.contains.c tbms.var matches ..999 if score default.standard.string.contains.ret tbms.var matches 0 unless data storage default.standard.string.contains.source {json:""} run function default:zzz_sl_block/378
