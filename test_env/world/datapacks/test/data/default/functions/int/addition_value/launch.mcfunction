# ==================================================
# void default.int.addition_value.launch()
# ==================================================

execute unless score default.int.addition_value.enabled tbms.var = default.int.addition_value.enabled tbms.var run scoreboard players set default.int.addition_value.enabled tbms.var 0
execute if score default.int.addition_value.enabled tbms.var matches 0 run function default:zzz_sl_block/323
