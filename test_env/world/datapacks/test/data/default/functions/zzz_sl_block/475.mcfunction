# ==================================================
# void default.zzz_sl_block.475()
# a.k.a default.utils.process_manager.show.200
# ==================================================

scoreboard players set default.utils.process_manager.show._196 tbms.var 1
tellraw @a [{"text": " [OFF] ", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"red"},{"translate":"default.int.neq_float_delta", "with":[{"text": "red", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"white"}]}]
scoreboard players add default.utils.process_manager.show.off tbms.var 1
