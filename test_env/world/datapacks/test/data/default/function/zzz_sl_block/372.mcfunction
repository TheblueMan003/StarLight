# ==================================================
# void default.zzz_sl_block.372()
# a.k.a default.standard.string.replace._0._2._0.1
# ==================================================

data modify storage default.standard.string.replace.source json set string storage default.standard.string.replace.source json 1
scoreboard players add default.standard.string.replace._0._2._0.i tbms.var 1
data modify storage default.standard.string.length.value json set string storage default.standard.string.replace.value json
execute store result score default.standard.string.length._ret tbms.var run data get storage default.standard.string.length.value json
scoreboard players operation default.standard.string.replace._0._2._0._2 tbms.var = default.standard.string.length._ret tbms.var
execute if score default.standard.string.replace._0._2._0.i tbms.var < default.standard.string.replace._0._2._0._2 tbms.var run function default:zzz_sl_block/372
