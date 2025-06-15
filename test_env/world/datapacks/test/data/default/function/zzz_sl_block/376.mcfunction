# ==================================================
# void default.zzz_sl_block.376()
# a.k.a default.standard.string.reverse.1
# ==================================================

data modify storage default.standard.string.reverse._0._0 json set string storage default.standard.string.reverse.source json 0 1
data modify storage default.standard.string.concat._0 json.a set from storage default.standard.string.reverse._0._0 json
data modify storage default.standard.string.concat._0 json.b set from storage default.standard.string.reverse.ret json
function default:standard/string/concat with storage default.standard.string.concat._0 json
data modify storage default.standard.string.reverse._0._0 json set string storage default.standard.string.concat._ret json
data modify storage default.standard.string.reverse.ret json set string storage default.standard.string.reverse._0._0 json
data modify storage default.standard.string.reverse.source json set string storage default.standard.string.reverse.source json 1
scoreboard players add default.standard.string.reverse.c tbms.var 1
execute if score default.standard.string.reverse.c tbms.var matches ..999 unless data storage default.standard.string.reverse.source {json:""} run function default:zzz_sl_block/376
