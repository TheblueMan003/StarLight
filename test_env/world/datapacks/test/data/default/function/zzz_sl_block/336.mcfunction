# ==================================================
# void default.zzz_sl_block.336()
# a.k.a default.standard.string.split._0.3
# ==================================================

scoreboard players set default.standard.string.split._0._1 tbms.var 1
data modify storage default.standard.string.split.ret.add.value json set string storage default.standard.string.split.current json
data modify storage default.standard.string.split.ret.data json append from storage default.standard.string.split.ret.add.value json
data modify storage default.standard.string.split.current json set value ""
scoreboard players set default.standard.string.split._0._2._0.i tbms.var 0
execute if score default.standard.string.split._0._2._0.i tbms.var < default.standard.string.split.l tbms.var run function default:zzz_sl_block/335
