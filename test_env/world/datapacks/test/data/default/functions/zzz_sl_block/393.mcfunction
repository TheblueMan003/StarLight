# ==================================================
# void default.zzz_sl_block.393()
# a.k.a default.utils.process_manager.show.9
# ==================================================

scoreboard players set default.utils.process_manager.show._7 tbms.var 1
tellraw @a [{"text": " [ON] ", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"green"},{"translate":"default.int.not_lt_int", "with":[{"text": "green", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"white"}]}]
scoreboard players add default.utils.process_manager.show.running tbms.var 1
