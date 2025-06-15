# ==================================================
# void default.zzz_sl_block.335()
# a.k.a default.standard.string.split._0._2._0.1
# ==================================================

data modify storage default.standard.string.split.source json set string storage default.standard.string.split.source json 1
scoreboard players add default.standard.string.split._0._2._0.i tbms.var 1
execute if score default.standard.string.split._0._2._0.i tbms.var < default.standard.string.split.l tbms.var run function default:zzz_sl_block/335
