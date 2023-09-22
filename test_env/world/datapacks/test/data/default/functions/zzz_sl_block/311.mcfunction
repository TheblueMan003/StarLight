# ==================================================
# void default.zzz_sl_block.311()
# a.k.a default.int.sub_value.onStop-0._0.5
# ==================================================

tellraw @a [{"text": "[FAILLED] ", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"red"},{"translate":"default.int.sub_value"}]

scoreboard players add default.test.__fail__ tbms.var 1
