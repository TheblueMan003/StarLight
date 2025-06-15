# ==================================================
# void default.zzz_sl_block.369()
# a.k.a default.standard.string.indexOf.1
# ==================================================

data modify storage default.standard.string.starts-with.source json set string storage default.standard.string.index-of.source json
data modify storage default.standard.string.starts-with.value json set string storage default.standard.string.index-of.value json
function default:standard/string/starts-with
execute unless score default.standard.string.startsWith._ret tbms.var matches 0 run scoreboard players operation default.standard.string.indexOf.ret tbms.var = default.standard.string.indexOf.i tbms.var
data modify storage default.standard.string.index-of.source json set string storage default.standard.string.index-of.source json 1
scoreboard players add default.standard.string.indexOf.i tbms.var 1
execute if score default.standard.string.indexOf.i tbms.var matches ..999 if score default.standard.string.indexOf.ret tbms.var matches -1 unless data storage default.standard.string.index-of.source {json:""} run function default:zzz_sl_block/369
