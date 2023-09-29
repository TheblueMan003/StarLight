# ==================================================
# void default.array.test()
# ==================================================

data modify storage default.array.test.a json set value "hello"
data modify storage default.array.test.b json set string storage default.array.test.a json 0 2
data modify storage default.__string_concat__._0 json.a set from storage default.array.test.b json
data modify storage default.__string_concat__._0 json.b set value "world"
function default:__string_concat__ with storage default.__string_concat__._0 json
data modify storage default.array.test.b json set string storage default.__string_concat__._ret json
