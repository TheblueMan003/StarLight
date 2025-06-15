# ==================================================
# void default.zzz_sl_block.367()
# a.k.a default.standard.string.toUpper.1
# ==================================================

data modify storage default.standard.char.to-upper.c json set string storage default.standard.string.to-upper.source json 0 1
data modify storage default.standard.char.to-upper._0.ret json set string storage default.standard.char.to-upper.c json
data modify storage default.standard.char.to-upper._0._0 json set string storage default.standard.char.to-upper.c json
execute if data storage default.standard.char.to-upper._0._0 {json:"a"} run data modify storage default.standard.char.to-upper._0.ret json set value "A"
execute if data storage default.standard.char.to-upper._0._0 {json:"b"} run data modify storage default.standard.char.to-upper._0.ret json set value "B"
execute if data storage default.standard.char.to-upper._0._0 {json:"c"} run data modify storage default.standard.char.to-upper._0.ret json set value "C"
execute if data storage default.standard.char.to-upper._0._0 {json:"d"} run data modify storage default.standard.char.to-upper._0.ret json set value "D"
execute if data storage default.standard.char.to-upper._0._0 {json:"e"} run data modify storage default.standard.char.to-upper._0.ret json set value "E"
execute if data storage default.standard.char.to-upper._0._0 {json:"f"} run data modify storage default.standard.char.to-upper._0.ret json set value "F"
execute if data storage default.standard.char.to-upper._0._0 {json:"g"} run data modify storage default.standard.char.to-upper._0.ret json set value "G"
execute if data storage default.standard.char.to-upper._0._0 {json:"h"} run data modify storage default.standard.char.to-upper._0.ret json set value "H"
execute if data storage default.standard.char.to-upper._0._0 {json:"i"} run data modify storage default.standard.char.to-upper._0.ret json set value "I"
execute if data storage default.standard.char.to-upper._0._0 {json:"j"} run data modify storage default.standard.char.to-upper._0.ret json set value "J"
execute if data storage default.standard.char.to-upper._0._0 {json:"k"} run data modify storage default.standard.char.to-upper._0.ret json set value "K"
execute if data storage default.standard.char.to-upper._0._0 {json:"l"} run data modify storage default.standard.char.to-upper._0.ret json set value "L"
execute if data storage default.standard.char.to-upper._0._0 {json:"m"} run data modify storage default.standard.char.to-upper._0.ret json set value "M"
execute if data storage default.standard.char.to-upper._0._0 {json:"n"} run data modify storage default.standard.char.to-upper._0.ret json set value "N"
execute if data storage default.standard.char.to-upper._0._0 {json:"o"} run data modify storage default.standard.char.to-upper._0.ret json set value "O"
execute if data storage default.standard.char.to-upper._0._0 {json:"p"} run data modify storage default.standard.char.to-upper._0.ret json set value "P"
execute if data storage default.standard.char.to-upper._0._0 {json:"q"} run data modify storage default.standard.char.to-upper._0.ret json set value "Q"
execute if data storage default.standard.char.to-upper._0._0 {json:"r"} run data modify storage default.standard.char.to-upper._0.ret json set value "R"
execute if data storage default.standard.char.to-upper._0._0 {json:"s"} run data modify storage default.standard.char.to-upper._0.ret json set value "S"
execute if data storage default.standard.char.to-upper._0._0 {json:"t"} run data modify storage default.standard.char.to-upper._0.ret json set value "T"
execute if data storage default.standard.char.to-upper._0._0 {json:"u"} run data modify storage default.standard.char.to-upper._0.ret json set value "U"
execute if data storage default.standard.char.to-upper._0._0 {json:"v"} run data modify storage default.standard.char.to-upper._0.ret json set value "V"
execute if data storage default.standard.char.to-upper._0._0 {json:"w"} run data modify storage default.standard.char.to-upper._0.ret json set value "W"
execute if data storage default.standard.char.to-upper._0._0 {json:"x"} run data modify storage default.standard.char.to-upper._0.ret json set value "X"
execute if data storage default.standard.char.to-upper._0._0 {json:"y"} run data modify storage default.standard.char.to-upper._0.ret json set value "Y"
execute if data storage default.standard.char.to-upper._0._0 {json:"z"} run data modify storage default.standard.char.to-upper._0.ret json set value "Z"
data modify storage default.standard.char.to-upper._ret json set string storage default.standard.char.to-upper._0.ret json
data modify storage default.standard.string.to-upper._0._0 json set string storage default.standard.char.to-upper._ret json
data modify storage default.standard.string.concat._0 json.a set from storage default.standard.string.to-upper.ret json
data modify storage default.standard.string.concat._0 json.b set from storage default.standard.string.to-upper._0._0 json
function default:standard/string/concat with storage default.standard.string.concat._0 json
data modify storage default.standard.string.to-upper.ret json set string storage default.standard.string.concat._ret json
data modify storage default.standard.string.to-upper.source json set string storage default.standard.string.to-upper.source json 1
scoreboard players add default.standard.string.toUpper.c tbms.var 1
execute if score default.standard.string.toUpper.c tbms.var matches ..999 unless data storage default.standard.string.to-upper.source {json:""} run function default:zzz_sl_block/367
