# ==================================================
# default.standard.collections.List.List default.standard.string.split(string source, string value)
# ==================================================
# ==================================================
#     Split a string into an array of strings.    
# ==================================================

data modify storage default.standard.string.split.ret.data json set value []
data modify storage default.standard.string.split.current json set value ""
scoreboard players set default.standard.string.split.c tbms.var 0
data modify storage default.standard.string.length.value json set string storage default.standard.string.split.value json
execute store result score default.standard.string.length._ret tbms.var run data get storage default.standard.string.length.value json
scoreboard players operation default.standard.string.split.l tbms.var = default.standard.string.length._ret tbms.var
execute if score default.standard.string.split.c tbms.var matches ..999 unless data storage default.standard.string.split.source {json:""} run function default:zzz_sl_block/338
data modify storage default.standard.string.split.ret.add.value json set string storage default.standard.string.split.current json
data modify storage default.standard.string.split.ret.data json append from storage default.standard.string.split.ret.add.value json
data modify storage default.standard.string.split._ret.data json set from storage default.standard.string.split.ret.data json
data modify storage default.standard.string.split._ret.get._0 json set from storage default.standard.string.split.ret.get._0 json
