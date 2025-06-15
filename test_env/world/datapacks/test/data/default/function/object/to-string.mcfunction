# ==================================================
# string default.object.toString()
# ==================================================

scoreboard players set default.zzz_sl_mux.void___to___string.__fct__ tbms.var 0
data modify storage default.object.to-string._ret json set value "object@"
data modify storage default.standard.string.concat._0 json.a set from storage default.object.to-string._ret json
execute store result storage default.standard.string.concat._0 json.b int 1.00000 run scoreboard players get @s default.object.__ref
function default:standard/string/concat with storage default.standard.string.concat._0 json
data modify storage default.object.to-string._ret json set string storage default.standard.string.concat._ret json
