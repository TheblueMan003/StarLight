# Feature
## Variables
Variables can be declared with 
```
<modifier> <type> <variable1_name>, [variable2_name], [variable3_name] ...
```
Variables can be declared and assigned 
```
<modifier> <type> <variable1_name>, [variable2_name], [variable3_name] ... = <expression>
```
Variables can be assigned 
```
<variable1_name>, [variable2_name], [variable3_name] ... = <expression>
```
If the variable is tuple, then the tuple with be unpack

## Default Types
* int: normal integer
* float: fixed point value (by default keep 3 digits after dot)
* bool: boolean value
* (T, T*): tuple store multiple value. e.i. (float, float, float) position = 0,0,0

## Function
Functions can be declared with 
```
[def] <modifier> <name>([<type> <argument name>])<block or instruction>
```
or with
```
[def] <modifier> <type> <variable1_name>, [variable2_name], [variable3_name] ...
```
run -i G:/005_SoftwareDev/StarLight/src/main/test/test.sl -o G:/005_SoftwareDev/StarLight/src/main/test/output