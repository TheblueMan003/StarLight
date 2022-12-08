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
### int
Store Normal integer

Supported operation:
* `=`, `+`, `-`, `*`, `/`, `%`


### float
Store Fixed point value with 32 bits (by default keep 3 digits after dot)

Supported operation:
* `=`, `+`, `-`, `*`, `/`, `%`

### bool
Store boolean value

Supported operation:
* `=`, `+`, `-`, `*`, `/`, `%`, `&`, `|`, `&&`, `||`

### entity
Store one or multiple entities

Supported operation:
* `=`: The variable to have the entity
* `&`: Intersection of entities set
* `|`: Union of entities set
* `-`: Difference of entities set

### (T, T*)
Tuple, Store multiple value. e.i. (float, float, float) position = 0,0,0

All operators are supported as long as either the operation can be done on every single value or the right hand side is also a tuple with the same size and the operation can be perform with each corresponding entry.

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
else if(b == 0 && a < 0){
    // b==0 and a < 0
}
else if (b == 1 || a < 0){
    // b ==1 or a < 0
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
[def] <modifier> <name>([<type> <argument name>]*)<block or instruction>
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

## Predicate (JAVA Only)
Predicate can be defined like function but with a json body.
```
predicate example(int count, string itemID){
  "condition": "minecraft:entity_properties",
  "entity": "this",
  "predicate": {
    "equipment": {
      "mainhand": {
        "items": [
          itemID
        ],
        "count": count
      }
    }
  }
}
```
They can only be call inside conditional statements like if, while, etc

## Typedef
Complexe type can be assign to a name with the following syntax:
```
typedef int=>int fct
fct foo = (a)=>{ return a + 1 }
```

## Structs
Data structure can be define with the following syntax:
```
struct vector3{
    int x
    int y
    int z

    def __add__(vector other){
        x += other.x
        y += other.y
        z += other.z
    }
}
```
They can then be used as any other type:
```
vector3 a
vector3 b
a.x = 0
a += b
```
Here is the list of possible operator overloading:
* `__set__`: `=`
* `__add__`: `+=`
* `__sub__`: `-=`
* `__mult__`: `*=`
* `__div__`: `/=`
* `__mod__`: `%=`
* `__and__`: `&=`
* `__or__`: `|=`
* `__init__`: Constructor

Structs can also be extended. They will recieve all the methode & variable from the parent struct.
```
struct vector4 extends vector3{
    int a
}
```

## Classes
Classes can be define with the following syntax:
```
class cow{
    int health
}
```

## Templates
Templates are ways of having static class with inherritance.
```
template example{
    def test(){

    }
}
template example2 extends example{

}
```

```
example2 foo{
    def bar(){
        test()
    }
}
```

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

### "Lazy IF"
When a if statement is compiled, the expression inside it is simplified. If the result value is always true then the condition will disapear from the output code and all the else statement with it. 
```
if (true){
    //do stuff
}
else{
    // crash
}
```
will become
```
//do stuff
```
This can be used to compile according to some compilation settings. Here is a list of meta variable that can be tested:
* `Compiler.isJava`: Tell if the target is MC Java
* `Compiler.isBedrock`: Tell if the target is MC Bedrock
* `Compiler.isDebug`: Used to add extra info in the datapack that can be used to debug