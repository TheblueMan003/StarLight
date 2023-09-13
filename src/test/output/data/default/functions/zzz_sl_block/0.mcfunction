# ==================================================
# void default.zzz_sl_block.0(()=>void --await_callback--)
# a.k.a default.test.test.--async_while--
# ==================================================

execute if score default.test.test.-setup-loop- tbms.var matches 0 run function default:zzz_sl_block/1
say hi
schedule function default:zzz_sl_block/2 5 append
