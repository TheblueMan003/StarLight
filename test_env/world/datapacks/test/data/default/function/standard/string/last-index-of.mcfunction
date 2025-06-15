# ==================================================
# int default.standard.string.lastIndexOf(string source, string value)
# ==================================================
# ==================================================
#     Return the index of `value` in `source` starting from the end.    
# ==================================================

scoreboard players set default.standard.string.lastIndexOf.ret tbms.var -1
data modify storage default.standard.string.length.value json set string storage default.standard.string.last-index-of.source json
execute store result score default.standard.string.length._ret tbms.var run data get storage default.standard.string.length.value json
scoreboard players operation default.standard.string.lastIndexOf.i tbms.var = default.standard.string.length._ret tbms.var
data modify storage default.standard.string.length.value json set string storage default.standard.string.last-index-of.value json
execute store result score default.standard.string.length._ret tbms.var run data get storage default.standard.string.length.value json
scoreboard players operation default.standard.string.lastIndexOf.i tbms.var -= default.standard.string.length._ret tbms.var
execute if score default.standard.string.lastIndexOf.i tbms.var matches ..999 if score default.standard.string.lastIndexOf.ret tbms.var matches -1 unless data storage default.standard.string.last-index-of.source {json:""} run function default:zzz_sl_block/368
