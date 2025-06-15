# ==================================================
# void default.zzz_sl_block.346()
# a.k.a default.standard.string.split.ret.removeAll._0.1
# ==================================================

execute store result storage default.standard.string.split.ret.get._0 json.key int 1.00000 run scoreboard players get default.standard.string.split.ret.removeAll._0.i tbms.var
function default:zzz_sl_block/329 with storage default.standard.string.split.ret.get._0 json
data modify storage default.standard.string.split.ret.remove-all._0._0.v json set string storage default.standard.string.split.ret.get._ret json
data modify storage default.zzz_sl_mux.string___to___-boolean.a_0 json set string storage default.standard.string.split.ret.remove-all._0._0.v json
execute if score default.zzz_sl_mux.string___to___Boolean._ret tbms.var matches 0 run data modify storage default.standard.string.split.ret.remove-all.tmp json append from storage default.standard.string.split.ret.remove-all._0._0.v json
scoreboard players add default.standard.string.split.ret.removeAll._0.i tbms.var 1
execute store result score default.standard.string.split.ret.size._ret tbms.var run data get storage default.standard.string.split.ret.data json
scoreboard players operation default.standard.string.split.ret.removeAll._0._2 tbms.var = default.standard.string.split.ret.size._ret tbms.var
execute if score default.standard.string.split.ret.removeAll._0.i tbms.var < default.standard.string.split.ret.removeAll._0._2 tbms.var run function default:zzz_sl_block/346
