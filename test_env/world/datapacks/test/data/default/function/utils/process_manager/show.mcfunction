# ==================================================
# void default.utils.process_manager.show()
# ==================================================
# ==================================================
# Show the list of processes
# ==================================================

tellraw @a [{"text": "===[ Running Processes ]===", "bold":false,"obfuscated":false,"strikethrough":false,"underlined":false,"italic":false, "color":"green"}]
scoreboard players set default.utils.process_manager.show.running tbms.var 0
scoreboard players set default.utils.process_manager.show.off tbms.var 0
scoreboard players set default.utils.process_manager.show.unknown tbms.var 0
scoreboard players set default.utils.process_manager.show.total tbms.var 0
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:array/sugar/__count__
scoreboard players set default.utils.process_manager.show._0 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/202
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._0 tbms.var matches 0 run function default:zzz_sl_block/203
execute if score default.utils.process_manager.show._0 tbms.var matches 0 run function default:zzz_sl_block/204
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:array/initer/__count__
scoreboard players set default.utils.process_manager.show._7 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/205
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._7 tbms.var matches 0 run function default:zzz_sl_block/206
execute if score default.utils.process_manager.show._7 tbms.var matches 0 run function default:zzz_sl_block/207
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:array/addition/__count__
scoreboard players set default.utils.process_manager.show._14 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/208
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._14 tbms.var matches 0 run function default:zzz_sl_block/209
execute if score default.utils.process_manager.show._14 tbms.var matches 0 run function default:zzz_sl_block/210
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:test/-test-runner/__count__
scoreboard players set default.utils.process_manager.show._21 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/211
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._21 tbms.var matches 0 run function default:zzz_sl_block/212
execute if score default.utils.process_manager.show._21 tbms.var matches 0 run function default:zzz_sl_block/213
scoreboard players add default.utils.process_manager.show.total tbms.var 1
tellraw @a [{"text": "Stats: ", "bold":false,"obfuscated":false,"strikethrough":false,"underlined":false,"italic":false, "color":"white"},{"score": { "name": "default.utils.process_manager.show.running", "objective": "tbms.var"}, "bold":false,"obfuscated":false,"strikethrough":false,"underlined":false,"italic":false, "color":"green"},{"text": "/", "bold":false,"obfuscated":false,"strikethrough":false,"underlined":false,"italic":false, "color":"white"},{"score": { "name": "default.utils.process_manager.show.total", "objective": "tbms.var"}, "bold":false,"obfuscated":false,"strikethrough":false,"underlined":false,"italic":false, "color":"green"},{"text": " Running", "bold":false,"obfuscated":false,"strikethrough":false,"underlined":false,"italic":false, "color":"green"},{"text": " - ", "bold":false,"obfuscated":false,"strikethrough":false,"underlined":false,"italic":false, "color":"white"},{"score": { "name": "default.utils.process_manager.show.off", "objective": "tbms.var"}, "bold":false,"obfuscated":false,"strikethrough":false,"underlined":false,"italic":false, "color":"red"},{"text": "/", "bold":false,"obfuscated":false,"strikethrough":false,"underlined":false,"italic":false, "color":"white"},{"score": { "name": "default.utils.process_manager.show.total", "objective": "tbms.var"}, "bold":false,"obfuscated":false,"strikethrough":false,"underlined":false,"italic":false, "color":"red"},{"text": " Off", "bold":false,"obfuscated":false,"strikethrough":false,"underlined":false,"italic":false, "color":"red"},{"text": " - ", "bold":false,"obfuscated":false,"strikethrough":false,"underlined":false,"italic":false, "color":"white"},{"score": { "name": "default.utils.process_manager.show.unknown", "objective": "tbms.var"}, "bold":false,"obfuscated":false,"strikethrough":false,"underlined":false,"italic":false, "color":"yellow"},{"text": "/", "bold":false,"obfuscated":false,"strikethrough":false,"underlined":false,"italic":false, "color":"white"},{"score": { "name": "default.utils.process_manager.show.total", "objective": "tbms.var"}, "bold":false,"obfuscated":false,"strikethrough":false,"underlined":false,"italic":false, "color":"yellow"},{"text": " Unknown", "bold":false,"obfuscated":false,"strikethrough":false,"underlined":false,"italic":false, "color":"yellow"},{"text": " - ", "bold":false,"obfuscated":false,"strikethrough":false,"underlined":false,"italic":false, "color":"white"}]
