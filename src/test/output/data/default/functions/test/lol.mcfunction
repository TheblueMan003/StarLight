# ==================================================
# void default.test.lol(int a)
# ==================================================

data modify storage default.test._0 json.a set value 5
function default:test/bar with storage default.test._0 json
