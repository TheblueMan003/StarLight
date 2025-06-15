# ==================================================
# void default.zzz_sl_block.368()
# a.k.a default.standard.string.lastIndexOf.1
# ==================================================

data modify storage default.standard.string.ends-with.source json set string storage default.standard.string.last-index-of.source json
data modify storage default.standard.string.ends-with.value json set string storage default.standard.string.last-index-of.value json
data modify storage default.standard.string.0.inner._0 json.start set from storage default.standard.string.ends-with.source json
function default:zzz_sl_block/377 with storage default.standard.string.0.inner._0 json
data modify storage default.standard.string.equals.value json set string storage default.standard.string.ends-with.value json
function default:standard/string/equals
scoreboard players operation default.standard.string.endsWith._ret tbms.var = default.standard.string.equals._ret tbms.var
execute unless score default.standard.string.endsWith._ret tbms.var matches 0 run scoreboard players operation default.standard.string.lastIndexOf.ret tbms.var = default.standard.string.lastIndexOf.i tbms.var
data modify storage default.standard.string.slice.source json set string storage default.standard.string.last-index-of.source json
scoreboard players set default.standard.string.slice.start tbms.var 0
scoreboard players set default.standard.string.slice.end tbms.var -1
function default:standard/string/slice
data modify storage default.standard.string.last-index-of.source json set string storage default.standard.string.slice._ret json
scoreboard players add default.standard.string.lastIndexOf.i tbms.var 1
execute if score default.standard.string.lastIndexOf.i tbms.var matches ..999 if score default.standard.string.lastIndexOf.ret tbms.var matches -1 unless data storage default.standard.string.last-index-of.source {json:""} run function default:zzz_sl_block/368
