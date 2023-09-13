# ==================================================
# void default.zzz_sl_block.2()
# a.k.a default.test.test.--async_while--.lambda_1
# ==================================================

say wait
scoreboard players remove default.test.test.count tbms.var 1
scoreboard players set default.test.test.--async_while--.lambda_1._0 tbms.var 0
execute if score default.test.test.count tbms.var matches 1.. run function default:zzz_sl_block/3
execute if score default.test.test.--async_while--.lambda_1._0 tbms.var matches 0 run function default:zzz_sl_block/4
execute if score default.test.test.--async_while--.lambda_1._0 tbms.var matches 0 run function default:zzz_sl_block/6
scoreboard players operation default.zzz_sl_mux.void___to___void.__fct__ tbms.var = default.test.test.--async_while--.--await_callback-- tbms.var
function default:zzz_sl_block/5
