# ==================================================
# void default.zzz_sl_block.338()
# a.k.a default.standard.string.split.1
# ==================================================

scoreboard players set default.standard.string.split._0._1 tbms.var 0
data modify storage default.standard.string.starts-with.source json set string storage default.standard.string.split.source json
data modify storage default.standard.string.starts-with.value json set string storage default.standard.string.split.value json
function default:standard/string/starts-with
execute unless score default.standard.string.startsWith._ret tbms.var matches 0 run function default:zzz_sl_block/336
execute if score default.standard.string.split._0._1 tbms.var matches 0 run function default:zzz_sl_block/337
scoreboard players add default.standard.string.split.c tbms.var 1
execute if score default.standard.string.split.c tbms.var matches ..999 unless data storage default.standard.string.split.source {json:""} run function default:zzz_sl_block/338
