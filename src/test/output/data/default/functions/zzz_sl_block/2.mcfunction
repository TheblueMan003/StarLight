function default:a/test/on-stop
scoreboard players set default.a.test.enabled tbms.var 0
scoreboard players operation default.zzz_sl_mux.void___to___void.__fct__ tbms.var = default.a.test.callback tbms.var
function default:zzz_sl_block/0
scoreboard players set default.a.test.callback tbms.var 0
