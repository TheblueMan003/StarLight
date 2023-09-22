# ==================================================
# void default.zzz_sl_block.427()
# a.k.a default.utils.process_manager.show.88
# ==================================================

scoreboard players set default.utils.process_manager.show._84 tbms.var 1
tellraw @a [{"text": " [OFF] ", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"red"},{"translate":"default.int.multi_variable", "with":[{"text": "red", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"white"}]}]
scoreboard players add default.utils.process_manager.show.off tbms.var 1
