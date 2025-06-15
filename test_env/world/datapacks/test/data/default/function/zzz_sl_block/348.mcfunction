# ==================================================
# void default.zzz_sl_block.348()
# a.k.a default.standard.string.split.ret.remove._0.1
# ==================================================

execute store result storage default.standard.string.split.ret.get._0 json.key int 1.00000 run scoreboard players get default.standard.string.split.ret.remove._0.i tbms.var
function default:zzz_sl_block/329 with storage default.standard.string.split.ret.get._0 json
data modify storage default.standard.string.split.ret.remove._0._0.v json set string storage default.standard.string.split.ret.get._ret json
data modify storage default.standard.string.equals.source json set string storage default.standard.string.split.ret.remove._0._0.v json
data modify storage default.standard.string.equals.value json set string storage default.standard.string.split.ret.remove.value json
function default:standard/string/equals
execute if score default.standard.string.equals._ret tbms.var matches 0 run data modify storage default.standard.string.split.ret.remove.tmp json append from storage default.standard.string.split.ret.remove._0._0.v json
scoreboard players add default.standard.string.split.ret.remove._0.i tbms.var 1
execute store result score default.standard.string.split.ret.size._ret tbms.var run data get storage default.standard.string.split.ret.data json
scoreboard players operation default.standard.string.split.ret.remove._0._2 tbms.var = default.standard.string.split.ret.size._ret tbms.var
execute if score default.standard.string.split.ret.remove._0.i tbms.var < default.standard.string.split.ret.remove._0._2 tbms.var run function default:zzz_sl_block/348
