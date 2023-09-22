# ==================================================
# void default.zzz_sl_block.56()
# a.k.a default.int.ternary_operator_integer_result.onStop-0._0.5
# ==================================================

tellraw @a [{"text": "[FAILLED] ", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"red"},{"translate":"default.int.ternary_operator_integer_result"}]

scoreboard players add default.test.__fail__ tbms.var 1
