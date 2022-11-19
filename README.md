# Notation
Here are some notion used in this ReadMe:

`<value>` needed value

`[value]` optional value

`value*` repeated value (usually splitted by a commat)

# How to use
## program argument
-i <list of source files>: Specifify which files to use. Order doesn't matter. If a directory is given, all the files in it will be taken.
-o <output path>: Specifify the output of the datapack

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
[def] <modifier> <name>([<type> <argument name>]*)<block or instruction>
```
or with
```
[def] <modifier> <type> <name>([<type> <argument name>]*)<block or instruction>
```