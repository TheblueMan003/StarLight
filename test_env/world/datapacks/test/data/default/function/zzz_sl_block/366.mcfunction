# ==================================================
# void default.zzz_sl_block.366()
# a.k.a default.standard.string.toLower.1
# ==================================================

data modify storage default.standard.char.to-lower.c json set string storage default.standard.string.to-lower.source json 0 1
data modify storage default.standard.char.to-lower._0.ret json set string storage default.standard.char.to-lower.c json
data modify storage default.standard.char.to-lower._0._0 json set string storage default.standard.char.to-lower.c json
execute if data storage default.standard.char.to-lower._0._0 {json:"A"} run data modify storage default.standard.char.to-lower._0.ret json set value "a"
execute if data storage default.standard.char.to-lower._0._0 {json:"B"} run data modify storage default.standard.char.to-lower._0.ret json set value "b"
execute if data storage default.standard.char.to-lower._0._0 {json:"C"} run data modify storage default.standard.char.to-lower._0.ret json set value "c"
execute if data storage default.standard.char.to-lower._0._0 {json:"D"} run data modify storage default.standard.char.to-lower._0.ret json set value "d"
execute if data storage default.standard.char.to-lower._0._0 {json:"E"} run data modify storage default.standard.char.to-lower._0.ret json set value "e"
execute if data storage default.standard.char.to-lower._0._0 {json:"F"} run data modify storage default.standard.char.to-lower._0.ret json set value "f"
execute if data storage default.standard.char.to-lower._0._0 {json:"G"} run data modify storage default.standard.char.to-lower._0.ret json set value "g"
execute if data storage default.standard.char.to-lower._0._0 {json:"H"} run data modify storage default.standard.char.to-lower._0.ret json set value "h"
execute if data storage default.standard.char.to-lower._0._0 {json:"I"} run data modify storage default.standard.char.to-lower._0.ret json set value "i"
execute if data storage default.standard.char.to-lower._0._0 {json:"J"} run data modify storage default.standard.char.to-lower._0.ret json set value "j"
execute if data storage default.standard.char.to-lower._0._0 {json:"K"} run data modify storage default.standard.char.to-lower._0.ret json set value "k"
execute if data storage default.standard.char.to-lower._0._0 {json:"L"} run data modify storage default.standard.char.to-lower._0.ret json set value "l"
execute if data storage default.standard.char.to-lower._0._0 {json:"M"} run data modify storage default.standard.char.to-lower._0.ret json set value "m"
execute if data storage default.standard.char.to-lower._0._0 {json:"N"} run data modify storage default.standard.char.to-lower._0.ret json set value "n"
execute if data storage default.standard.char.to-lower._0._0 {json:"O"} run data modify storage default.standard.char.to-lower._0.ret json set value "o"
execute if data storage default.standard.char.to-lower._0._0 {json:"P"} run data modify storage default.standard.char.to-lower._0.ret json set value "p"
execute if data storage default.standard.char.to-lower._0._0 {json:"Q"} run data modify storage default.standard.char.to-lower._0.ret json set value "q"
execute if data storage default.standard.char.to-lower._0._0 {json:"R"} run data modify storage default.standard.char.to-lower._0.ret json set value "r"
execute if data storage default.standard.char.to-lower._0._0 {json:"S"} run data modify storage default.standard.char.to-lower._0.ret json set value "s"
execute if data storage default.standard.char.to-lower._0._0 {json:"T"} run data modify storage default.standard.char.to-lower._0.ret json set value "t"
execute if data storage default.standard.char.to-lower._0._0 {json:"U"} run data modify storage default.standard.char.to-lower._0.ret json set value "u"
execute if data storage default.standard.char.to-lower._0._0 {json:"V"} run data modify storage default.standard.char.to-lower._0.ret json set value "v"
execute if data storage default.standard.char.to-lower._0._0 {json:"W"} run data modify storage default.standard.char.to-lower._0.ret json set value "w"
execute if data storage default.standard.char.to-lower._0._0 {json:"X"} run data modify storage default.standard.char.to-lower._0.ret json set value "x"
execute if data storage default.standard.char.to-lower._0._0 {json:"Y"} run data modify storage default.standard.char.to-lower._0.ret json set value "y"
execute if data storage default.standard.char.to-lower._0._0 {json:"Z"} run data modify storage default.standard.char.to-lower._0.ret json set value "z"
data modify storage default.standard.char.to-lower._ret json set string storage default.standard.char.to-lower._0.ret json
data modify storage default.standard.string.to-lower._0._0 json set string storage default.standard.char.to-lower._ret json
data modify storage default.standard.string.concat._0 json.a set from storage default.standard.string.to-lower.ret json
data modify storage default.standard.string.concat._0 json.b set from storage default.standard.string.to-lower._0._0 json
function default:standard/string/concat with storage default.standard.string.concat._0 json
data modify storage default.standard.string.to-lower.ret json set string storage default.standard.string.concat._ret json
data modify storage default.standard.string.to-lower.source json set string storage default.standard.string.to-lower.source json 1
scoreboard players add default.standard.string.toLower.c tbms.var 1
execute if score default.standard.string.toLower.c tbms.var matches ..999 unless data storage default.standard.string.to-lower.source {json:""} run function default:zzz_sl_block/366
