function default:test/test/on-stop
scoreboard players set default.test.test.enabled tbms.var 0
scoreboard players operation default.zzz_sl_mux.void___to___void.__fct__ tbms.var = default.test.test.callback tbms.var
function default:zzz_sl_block/3
scoreboard players set default.test.test.callback tbms.var 0
