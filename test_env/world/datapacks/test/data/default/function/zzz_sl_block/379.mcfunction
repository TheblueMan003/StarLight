# ==================================================
# void default.zzz_sl_block.379()
# a.k.a default.standard.string.multiply._0.1
# ==================================================

data modify storage default.standard.string.concat._0 json.a set from storage default.standard.string.multiply.result json
data modify storage default.standard.string.concat._0 json.b set from storage default.standard.string.multiply.value json
function default:standard/string/concat with storage default.standard.string.concat._0 json
data modify storage default.standard.string.multiply.result json set string storage default.standard.string.concat._ret json
scoreboard players add default.standard.string.multiply._0.i tbms.var 1
execute if score default.standard.string.multiply._0.i tbms.var < default.standard.string.multiply.count tbms.var run function default:zzz_sl_block/379
