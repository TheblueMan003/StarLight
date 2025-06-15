# ==================================================
# void default.zzz_sl_block.163()
# a.k.a default.array.addition.onStop-0._0.5
# ==================================================

tellraw @a [{"text": "[FAILLED] ", "bold":false,"obfuscated":false,"strikethrough":false,"underlined":false,"italic":false, "color":"red"},{"translate":"default.array.addition"}]

scoreboard players add default.test.__fail__ tbms.var 1
