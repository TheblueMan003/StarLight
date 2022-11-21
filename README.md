# Notation
Here are some notion used in this ReadMe:

`<value>` needed value

`[value]` optional value

`value*` repeated value (usually splitted by a commat)

# How to use
java -jar <new|build|compile>
## compile arguments
-i <list of source files>: Specifify which files to use. Order doesn't matter. If a directory is given, all the files in it will be taken.

-o <output path>: Specifify the output of the datapack

-n <top level name>: Namespace name for the datapack

-bedrock: If present export as a Behavor pack

## new arguments
-p <path>: path where to place the project

-n <top level name>: Namespace name for the datapack

## build arguments
java -jar build <path_to/build.slconf>

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
* entity: store multiple entities. (Use a tag)
* json: store a json value. `+=` operator acts as append and `&=` operator acts as merge. This type is dynamicly support only for java datapack. For bedrock, the lazy key word must be used.
* string: TODO
* T[]: TODO
* (T*)=>T: any store dynamicly any non lazy functions when the variable is not lazy or any functions otherwise. The variable can be call with the usual call notation.
* (T, T*): tuple store multiple value. e.i. (float, float, float) position = 0,0,0

## Selectors & Entity
The entity can be assigned a selector. This will result in only the entity selected by the selector to be inside the variable.
```
entity players = @a
```
The selector can have either the java or bedrock syntax.

Sets operations can be performs on the variables of type entity.
```
players += @a
players -= @p
```
In addition variables of type entity and selectors can be used inside if. It will return true if there is at least one entity that match the selector or is in the variable.
```
entity a = @p
if (a && @e[tag=hello]){

}
```

## Flow Controls
### If
The language support `if` with a c like syntax:
```
if (a > 0){

}
else if(b == 0){

}
else{

}
```
If a case is always true if will be remove the other following cases in the output code. If a case is always false it will be removed from the output code.


### Loop
The language also support the following loop: for, while, do while with a c like syntax:
```
for(int a=0;a < 10;a+=1){

}

while(a > 0){

}

do{

}while(a > 0)
```

### Context Manipulation
Instructions can be executed **at** an entity with:
```
at(@a){
    do_stuff()
}
```
Instructions can be executed **as** an entity with:
```
as(@a){
    do_stuff()
}
```
For compatibility with BluePhoenix the following also work:
```
// as @a at @s
with(@a, true, a == 0){
    do_stuff()
}
// as @a
with(@a, false, a == 0){
    do_stuff()
}
```

## Function
Functions can be declared with 
```
def <modifier> <name>([<type> <argument name>]*)<block or instruction>
```
or with
```
[def] <modifier> <type> <name>([<type> <argument name>]*)<block or instruction>
```

Arugment can also have default value:
```
def hello(int a = 0){
}
```

Functions can be called with:
```
function_name(arg1, arg2)
```

Function can also return value:
```
bool test(){
    return true
}
```


Function can be put inside variable:
```
int plus(int a){
    return a + 1
}
int=>int fct = plus
int b = fct(5)
```

Lambda can be created with the syntax:
```
(int, int)=>int fct = (a,b)=> return a + b
```

```
def fct(void=>void arg){
    /say befor
    arg()
    /say after
}
fct(){
    /say hi
}
```
With the latter syntax the lambda is always the rightmost argument of the function; not including optional argument.



## JSON File
Jsonfile can be added with the following construct:
```
jsonfile advancements.name{
    <json content>
}
```


## Struct
Struct allow you to create new type. Note that every time copy a struct to another a copy of the inner states is perform.
```
struct typ{
    int a
    int b
    
    def fct(){
        a += b
    }
}
typ foo
foo.fct()
int bar = typ.foo
```

To overload the operator of the type, the following function can be used:
* `=`: `__set__`
* `+=`: `__add__`
* `-=`: `__sub__`
* `*=`: `__mult__`
* `/=`: `__div__`
* `%=`: `__mod__`
* `&=`: `__and__`
* `|=`: `__or__`

## Metaprogramming
### Lazy Variable
Lazy variable are not exported into the output code. Instead all operation on them are interpreted directly and when they are used, they are replaced by the value interpreted.
```
lazy int a = 0
a += 10*5
int b = a
```
Will be the same as
```
int b = 50
```

This allow to do static string & json manipulation.

### Lazy Functions
Lazy functions are also not exported into the output code. Instead when they are called, there content is replace at the call site.
```
lazy def test(int a){
    int b = a
}
test(0)
```
Will be the same as
```
int b = 0
```
Every call for function that are not mark as inline is done in a sub context meaning that variable inside it won't be usable after the call.
