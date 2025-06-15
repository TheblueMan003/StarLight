# ==================================================
# void default.zzz_sl_block.373()
# a.k.a default.standard.string.replace._0.3
# ==================================================

scoreboard players set default.standard.string.replace._0._1 tbms.var 1
data modify storage default.standard.string.concat._0 json.a set from storage default.standard.string.replace.ret json
data modify storage default.standard.string.concat._0 json.b set from storage default.standard.string.replace.replacement json
function default:standard/string/concat with storage default.standard.string.concat._0 json
data modify storage default.standard.string.replace.ret json set string storage default.standard.string.concat._ret json
scoreboard players set default.standard.string.replace._0._2._0.i tbms.var 0
data modify storage default.standard.string.length.value json set string storage default.standard.string.replace.value json
execute store result score default.standard.string.length._ret tbms.var run data get storage default.standard.string.length.value json
scoreboard players operation default.standard.string.replace._0._2._0._3 tbms.var = default.standard.string.length._ret tbms.var
execute if score default.standard.string.replace._0._2._0.i tbms.var < default.standard.string.replace._0._2._0._3 tbms.var run function default:zzz_sl_block/372
