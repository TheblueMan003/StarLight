# ==================================================
# void default.zzz_sl_block.335()
# a.k.a default.int.addition_variable.onStop-0._0.5
# ==================================================

tellraw @a [{"text": "[FAILLED] ", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"red"},{"translate":"default.int.addition_variable"}]

scoreboard players add default.test.__fail__ tbms.var 1
