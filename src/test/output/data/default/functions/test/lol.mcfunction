# ==================================================
# void default.test.lol()
# ==================================================

execute unless score default.test.lol.c tbms.var = default.test.lol.c tbms.var run scoreboard players set default.test.lol.c tbms.var 0
say 5
execute store result storage default.test.test._0 json.a int 1.00000 run scoreboard players get default.test.lol.c tbms.var
function default:test/test with storage default.test.test._0 json
