# ==================================================
# void default.zzz_sl_block.347()
# a.k.a default.standard.string.split.ret.removeAt._0.1
# ==================================================

execute store result storage default.standard.string.split.ret.get._0 json.key int 1.00000 run scoreboard players get default.standard.string.split.ret.removeAt._0.i tbms.var
function default:zzz_sl_block/329 with storage default.standard.string.split.ret.get._0 json
data modify storage default.standard.string.split.ret.remove-at._0._0.v json set string storage default.standard.string.split.ret.get._ret json
execute unless score default.standard.string.split.ret.removeAt._0.i tbms.var = default.standard.string.split.ret.removeAt.index tbms.var run data modify storage default.standard.string.split.ret.remove-at.tmp json append from storage default.standard.string.split.ret.remove-at._0._0.v json
scoreboard players add default.standard.string.split.ret.removeAt._0.i tbms.var 1
execute store result score default.standard.string.split.ret.size._ret tbms.var run data get storage default.standard.string.split.ret.data json
scoreboard players operation default.standard.string.split.ret.removeAt._0._2 tbms.var = default.standard.string.split.ret.size._ret tbms.var
execute if score default.standard.string.split.ret.removeAt._0.i tbms.var < default.standard.string.split.ret.removeAt._0._2 tbms.var run function default:zzz_sl_block/347
