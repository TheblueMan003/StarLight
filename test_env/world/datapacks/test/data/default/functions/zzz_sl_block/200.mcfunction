# ==================================================
# void default.zzz_sl_block.200()
# a.k.a default.int.neq_float_fail.onStop-0._0.5
# ==================================================

tellraw @a [{"text": "[FAILLED] ", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"red"},{"translate":"default.int.neq_float_fail"}]

scoreboard players add default.test.__fail__ tbms.var 1
