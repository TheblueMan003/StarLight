# ==================================================
# string default.standard.string.slice(string source, int start, int end)
# ==================================================
# ==================================================
#     Return a substring of `source` from `start` with `length`.    
# ==================================================

execute store result storage default.standard.string.slice.inner._0 json.start int 1.00000 run scoreboard players get default.standard.string.slice.start tbms.var
execute store result storage default.standard.string.slice.inner._0 json.end int 1.00000 run scoreboard players get default.standard.string.slice.end tbms.var
function default:zzz_sl_block/371 with storage default.standard.string.slice.inner._0 json
