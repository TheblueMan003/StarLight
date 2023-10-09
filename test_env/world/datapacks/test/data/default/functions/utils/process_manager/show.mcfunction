# ==================================================
# void default.utils.process_manager.show()
# ==================================================
# ==================================================
# Show the list of processes
# ==================================================

tellraw @a [{"text": "===[ Running Processes ]===", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"green"}]
scoreboard players set default.utils.process_manager.show.running tbms.var 0
scoreboard players set default.utils.process_manager.show.off tbms.var 0
scoreboard players set default.utils.process_manager.show.unknown tbms.var 0
scoreboard players set default.utils.process_manager.show.total tbms.var 0
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/ternary_operator_float_result/__count__
scoreboard players set default.utils.process_manager.show._0 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/390
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._0 tbms.var matches 0 run function default:zzz_sl_block/391
execute if score default.utils.process_manager.show._0 tbms.var matches 0 run function default:zzz_sl_block/392
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/not_lt_int/__count__
scoreboard players set default.utils.process_manager.show._7 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/393
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._7 tbms.var matches 0 run function default:zzz_sl_block/394
execute if score default.utils.process_manager.show._7 tbms.var matches 0 run function default:zzz_sl_block/395
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/not_gt_int/__count__
scoreboard players set default.utils.process_manager.show._14 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/396
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._14 tbms.var matches 0 run function default:zzz_sl_block/397
execute if score default.utils.process_manager.show._14 tbms.var matches 0 run function default:zzz_sl_block/398
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/not_neq_int/__count__
scoreboard players set default.utils.process_manager.show._21 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/399
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._21 tbms.var matches 0 run function default:zzz_sl_block/400
execute if score default.utils.process_manager.show._21 tbms.var matches 0 run function default:zzz_sl_block/401
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/not_neq_float/__count__
scoreboard players set default.utils.process_manager.show._28 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/402
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._28 tbms.var matches 0 run function default:zzz_sl_block/403
execute if score default.utils.process_manager.show._28 tbms.var matches 0 run function default:zzz_sl_block/404
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/not_lte_float/__count__
scoreboard players set default.utils.process_manager.show._35 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/405
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._35 tbms.var matches 0 run function default:zzz_sl_block/406
execute if score default.utils.process_manager.show._35 tbms.var matches 0 run function default:zzz_sl_block/407
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/lt_float/__count__
scoreboard players set default.utils.process_manager.show._42 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/408
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._42 tbms.var matches 0 run function default:zzz_sl_block/409
execute if score default.utils.process_manager.show._42 tbms.var matches 0 run function default:zzz_sl_block/410
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/not_addition_variable/__count__
scoreboard players set default.utils.process_manager.show._49 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/411
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._49 tbms.var matches 0 run function default:zzz_sl_block/412
execute if score default.utils.process_manager.show._49 tbms.var matches 0 run function default:zzz_sl_block/413
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/neq_int_fail/__count__
scoreboard players set default.utils.process_manager.show._56 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/414
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._56 tbms.var matches 0 run function default:zzz_sl_block/415
execute if score default.utils.process_manager.show._56 tbms.var matches 0 run function default:zzz_sl_block/416
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/not_lt_float/__count__
scoreboard players set default.utils.process_manager.show._63 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/417
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._63 tbms.var matches 0 run function default:zzz_sl_block/418
execute if score default.utils.process_manager.show._63 tbms.var matches 0 run function default:zzz_sl_block/419
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/ternary_operator_integer_result/__count__
scoreboard players set default.utils.process_manager.show._70 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/420
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._70 tbms.var matches 0 run function default:zzz_sl_block/421
execute if score default.utils.process_manager.show._70 tbms.var matches 0 run function default:zzz_sl_block/422
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/tuple_unpacking_floats/__count__
scoreboard players set default.utils.process_manager.show._77 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/423
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._77 tbms.var matches 0 run function default:zzz_sl_block/424
execute if score default.utils.process_manager.show._77 tbms.var matches 0 run function default:zzz_sl_block/425
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/multi_variable/__count__
scoreboard players set default.utils.process_manager.show._84 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/426
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._84 tbms.var matches 0 run function default:zzz_sl_block/427
execute if score default.utils.process_manager.show._84 tbms.var matches 0 run function default:zzz_sl_block/428
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/ternary_operator_nested/__count__
scoreboard players set default.utils.process_manager.show._91 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/429
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._91 tbms.var matches 0 run function default:zzz_sl_block/430
execute if score default.utils.process_manager.show._91 tbms.var matches 0 run function default:zzz_sl_block/431
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/sub_value/__count__
scoreboard players set default.utils.process_manager.show._98 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/432
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._98 tbms.var matches 0 run function default:zzz_sl_block/433
execute if score default.utils.process_manager.show._98 tbms.var matches 0 run function default:zzz_sl_block/434
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/gt_float/__count__
scoreboard players set default.utils.process_manager.show._105 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/435
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._105 tbms.var matches 0 run function default:zzz_sl_block/436
execute if score default.utils.process_manager.show._105 tbms.var matches 0 run function default:zzz_sl_block/437
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/not_gte_float/__count__
scoreboard players set default.utils.process_manager.show._112 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/438
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._112 tbms.var matches 0 run function default:zzz_sl_block/439
execute if score default.utils.process_manager.show._112 tbms.var matches 0 run function default:zzz_sl_block/440
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/lte_int/__count__
scoreboard players set default.utils.process_manager.show._119 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/441
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._119 tbms.var matches 0 run function default:zzz_sl_block/442
execute if score default.utils.process_manager.show._119 tbms.var matches 0 run function default:zzz_sl_block/443
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/tuple_unpacking_nested_tuples/__count__
scoreboard players set default.utils.process_manager.show._126 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/444
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._126 tbms.var matches 0 run function default:zzz_sl_block/445
execute if score default.utils.process_manager.show._126 tbms.var matches 0 run function default:zzz_sl_block/446
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/gte_float/__count__
scoreboard players set default.utils.process_manager.show._133 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/447
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._133 tbms.var matches 0 run function default:zzz_sl_block/448
execute if score default.utils.process_manager.show._133 tbms.var matches 0 run function default:zzz_sl_block/449
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/not_multi_variable/__count__
scoreboard players set default.utils.process_manager.show._140 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/450
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._140 tbms.var matches 0 run function default:zzz_sl_block/451
execute if score default.utils.process_manager.show._140 tbms.var matches 0 run function default:zzz_sl_block/452
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/not_lte_int/__count__
scoreboard players set default.utils.process_manager.show._147 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/453
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._147 tbms.var matches 0 run function default:zzz_sl_block/454
execute if score default.utils.process_manager.show._147 tbms.var matches 0 run function default:zzz_sl_block/455
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/tuple_unpacking_ints/__count__
scoreboard players set default.utils.process_manager.show._154 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/456
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._154 tbms.var matches 0 run function default:zzz_sl_block/457
execute if score default.utils.process_manager.show._154 tbms.var matches 0 run function default:zzz_sl_block/458
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/not_addition_value/__count__
scoreboard players set default.utils.process_manager.show._161 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/459
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._161 tbms.var matches 0 run function default:zzz_sl_block/460
execute if score default.utils.process_manager.show._161 tbms.var matches 0 run function default:zzz_sl_block/461
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/not_eq_float/__count__
scoreboard players set default.utils.process_manager.show._168 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/462
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._168 tbms.var matches 0 run function default:zzz_sl_block/463
execute if score default.utils.process_manager.show._168 tbms.var matches 0 run function default:zzz_sl_block/464
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/addition_variable/__count__
scoreboard players set default.utils.process_manager.show._175 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/465
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._175 tbms.var matches 0 run function default:zzz_sl_block/466
execute if score default.utils.process_manager.show._175 tbms.var matches 0 run function default:zzz_sl_block/467
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/not_sub_value/__count__
scoreboard players set default.utils.process_manager.show._182 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/468
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._182 tbms.var matches 0 run function default:zzz_sl_block/469
execute if score default.utils.process_manager.show._182 tbms.var matches 0 run function default:zzz_sl_block/470
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/neq_int/__count__
scoreboard players set default.utils.process_manager.show._189 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/471
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._189 tbms.var matches 0 run function default:zzz_sl_block/472
execute if score default.utils.process_manager.show._189 tbms.var matches 0 run function default:zzz_sl_block/473
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/neq_float_delta/__count__
scoreboard players set default.utils.process_manager.show._196 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/474
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._196 tbms.var matches 0 run function default:zzz_sl_block/475
execute if score default.utils.process_manager.show._196 tbms.var matches 0 run function default:zzz_sl_block/476
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/eq_float/__count__
scoreboard players set default.utils.process_manager.show._203 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/477
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._203 tbms.var matches 0 run function default:zzz_sl_block/478
execute if score default.utils.process_manager.show._203 tbms.var matches 0 run function default:zzz_sl_block/479
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/not_gte_int/__count__
scoreboard players set default.utils.process_manager.show._210 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/480
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._210 tbms.var matches 0 run function default:zzz_sl_block/481
execute if score default.utils.process_manager.show._210 tbms.var matches 0 run function default:zzz_sl_block/482
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/tuple_unpacking_mixed_types/__count__
scoreboard players set default.utils.process_manager.show._217 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/483
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._217 tbms.var matches 0 run function default:zzz_sl_block/484
execute if score default.utils.process_manager.show._217 tbms.var matches 0 run function default:zzz_sl_block/485
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/lte_float/__count__
scoreboard players set default.utils.process_manager.show._224 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/486
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._224 tbms.var matches 0 run function default:zzz_sl_block/487
execute if score default.utils.process_manager.show._224 tbms.var matches 0 run function default:zzz_sl_block/488
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/neq_float_fail/__count__
scoreboard players set default.utils.process_manager.show._231 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/489
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._231 tbms.var matches 0 run function default:zzz_sl_block/490
execute if score default.utils.process_manager.show._231 tbms.var matches 0 run function default:zzz_sl_block/491
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/neq_float/__count__
scoreboard players set default.utils.process_manager.show._238 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/492
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._238 tbms.var matches 0 run function default:zzz_sl_block/493
execute if score default.utils.process_manager.show._238 tbms.var matches 0 run function default:zzz_sl_block/494
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/ternary_operator_mixed_result/__count__
scoreboard players set default.utils.process_manager.show._245 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/495
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._245 tbms.var matches 0 run function default:zzz_sl_block/496
execute if score default.utils.process_manager.show._245 tbms.var matches 0 run function default:zzz_sl_block/497
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/addition_value/__count__
scoreboard players set default.utils.process_manager.show._252 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/498
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._252 tbms.var matches 0 run function default:zzz_sl_block/499
execute if score default.utils.process_manager.show._252 tbms.var matches 0 run function default:zzz_sl_block/500
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/lt_int/__count__
scoreboard players set default.utils.process_manager.show._259 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/501
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._259 tbms.var matches 0 run function default:zzz_sl_block/502
execute if score default.utils.process_manager.show._259 tbms.var matches 0 run function default:zzz_sl_block/503
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/not_eq_int/__count__
scoreboard players set default.utils.process_manager.show._266 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/504
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._266 tbms.var matches 0 run function default:zzz_sl_block/505
execute if score default.utils.process_manager.show._266 tbms.var matches 0 run function default:zzz_sl_block/506
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/eq_int/__count__
scoreboard players set default.utils.process_manager.show._273 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/507
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._273 tbms.var matches 0 run function default:zzz_sl_block/508
execute if score default.utils.process_manager.show._273 tbms.var matches 0 run function default:zzz_sl_block/509
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/gt_int/__count__
scoreboard players set default.utils.process_manager.show._280 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/510
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._280 tbms.var matches 0 run function default:zzz_sl_block/511
execute if score default.utils.process_manager.show._280 tbms.var matches 0 run function default:zzz_sl_block/512
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/not_gt_float/__count__
scoreboard players set default.utils.process_manager.show._287 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/513
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._287 tbms.var matches 0 run function default:zzz_sl_block/514
execute if score default.utils.process_manager.show._287 tbms.var matches 0 run function default:zzz_sl_block/515
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:int/gte_int/__count__
scoreboard players set default.utils.process_manager.show._294 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/516
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._294 tbms.var matches 0 run function default:zzz_sl_block/517
execute if score default.utils.process_manager.show._294 tbms.var matches 0 run function default:zzz_sl_block/518
scoreboard players add default.utils.process_manager.show.total tbms.var 1
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:test/-test-runner/__count__
scoreboard players set default.utils.process_manager.show._301 tbms.var 0
execute if score default.utils.process_manager.t_running tbms.var matches 1 run function default:zzz_sl_block/519
execute if score default.utils.process_manager.t_running tbms.var matches 0 if score default.utils.process_manager.show._301 tbms.var matches 0 run function default:zzz_sl_block/520
execute if score default.utils.process_manager.show._301 tbms.var matches 0 run function default:zzz_sl_block/521
scoreboard players add default.utils.process_manager.show.total tbms.var 1
tellraw @a [{"text": "Stats: ", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"white"},{"score": { "name": "default.utils.process_manager.show.running", "objective": "tbms.var"}, "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"green"},{"text": "/", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"white"},{"score": { "name": "default.utils.process_manager.show.total", "objective": "tbms.var"}, "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"green"},{"text": " Running", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"green"},{"text": " - ", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"white"},{"score": { "name": "default.utils.process_manager.show.off", "objective": "tbms.var"}, "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"red"},{"text": "/", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"white"},{"score": { "name": "default.utils.process_manager.show.total", "objective": "tbms.var"}, "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"red"},{"text": " Off", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"red"},{"text": " - ", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"white"},{"score": { "name": "default.utils.process_manager.show.unknown", "objective": "tbms.var"}, "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"yellow"},{"text": "/", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"white"},{"score": { "name": "default.utils.process_manager.show.total", "objective": "tbms.var"}, "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"yellow"},{"text": " Unknown", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"yellow"},{"text": " - ", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"white"}]
