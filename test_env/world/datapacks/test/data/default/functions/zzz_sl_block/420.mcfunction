# ==================================================
# void default.zzz_sl_block.420()
# a.k.a default.utils.process_manager.show.72
# ==================================================

scoreboard players set default.utils.process_manager.show._70 tbms.var 1
tellraw @a [{"text": " [ON] ", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"green"},{"translate":"default.int.ternary_operator_integer_result", "with":[{"text": "green", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"white"}]}]
scoreboard players add default.utils.process_manager.show.running tbms.var 1
