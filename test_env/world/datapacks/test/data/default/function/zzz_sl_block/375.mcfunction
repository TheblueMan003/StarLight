# ==================================================
# void default.zzz_sl_block.375()
# a.k.a default.standard.string.replace.1
# ==================================================

scoreboard players set default.standard.string.replace._0._1 tbms.var 0
data modify storage default.standard.string.starts-with.source json set string storage default.standard.string.replace.source json
data modify storage default.standard.string.starts-with.value json set string storage default.standard.string.replace.value json
function default:standard/string/starts-with
execute unless score default.standard.string.startsWith._ret tbms.var matches 0 run function default:zzz_sl_block/373
execute if score default.standard.string.replace._0._1 tbms.var matches 0 run function default:zzz_sl_block/374
scoreboard players add default.standard.string.replace.c tbms.var 1
execute if score default.standard.string.replace.c tbms.var matches ..999 unless data storage default.standard.string.replace.source {json:""} run function default:zzz_sl_block/375
