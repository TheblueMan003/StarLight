# ==================================================
# void default.zzz_sl_block.462()
# a.k.a default.utils.process_manager.show.170
# ==================================================

scoreboard players set default.utils.process_manager.show._168 tbms.var 1
tellraw @a [{"text": " [ON] ", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"green"},{"translate":"default.int.not_eq_float", "with":[{"text": "green", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"white"}]}]
scoreboard players add default.utils.process_manager.show.running tbms.var 1
