# ==================================================
# void default.zzz_sl_block.430()
# a.k.a default.utils.process_manager.show.95
# ==================================================

scoreboard players set default.utils.process_manager.show._91 tbms.var 1
tellraw @a [{"text": " [OFF] ", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"red"},{"translate":"default.int.ternary_operator_nested", "with":[{"text": "red", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"white"}]}]
scoreboard players add default.utils.process_manager.show.off tbms.var 1