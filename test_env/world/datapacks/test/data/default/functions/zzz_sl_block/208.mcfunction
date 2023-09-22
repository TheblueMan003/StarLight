# ==================================================
# void default.zzz_sl_block.208()
# a.k.a default.int.neq_float_delta.stop.1
# ==================================================

tellraw @a [{"text": "[PASSED] ", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"green"},{"translate":"default.int.neq_float_delta"}]
scoreboard players add default.test.__pass__ tbms.var 1
scoreboard players add default.test.__total__ tbms.var 1
function default:test/-test-runner/next
scoreboard players set default.int.neq_float_delta.enabled tbms.var 0
scoreboard players set default.int.neq_float_delta.callback tbms.var 0
