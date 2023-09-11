# Libraries
This is the repo for the main compiler. The Libraries are located here: https://github.com/TheblueMan003/StarLightLibraries/
# Notation
Here are some notion used in this ReadMe:

`<value>` needed value

`[value]` optional value

`value*` repeated value (usually splitted by a commat)

# How to use
Use the following command in a cmd prompt or a terminal to open the compiler:
`java -jar <file>`
## Command
Once the compiler is open you will have access to the following command.
- `help`: Show the list of available command
- `new`: Create a new project in the current directory
- `build <path_to/build.slconf>`: Compile & Generate the packs. By default, `build java` build a Java Datapack & `build bedrock` build a bedrock Behaviorpack
- `install <library> [version]`: Install a library into the local project. If version is not specified, the latest version will be installed. Note that standard libraries are downloaded automatically when needed.
- `update <library>`: Update library to latest version.

## build arguments
An alternative is to use the following command:
`java -jar <jarfile> build <path_to/build.slconf>`

# Feature
## Variables
Variables can be declared with 
```
int a
int b, c
```
Variables can be declared and assigned 
```
float a = 10
float b, c = 10
float d, e = (10, 11)
```
Variables can be assigned 
```
a = 10
a, b = 10
d, e = (10, 11)
```

### Scoreboard
Scoreboard can be created by adding the `scoreboard` modifier befor the type when creating a variable.
```
scoreboard int myScore
```
Scoreboard variable can be either referred with their name or with a selector followed by a dot and their name.
```
with(@a){
    myScore = 0
}
@p.myScore = 1
```

### Private, Protected, Public
Access modifiers like `private`, `protected` & `public` can be added before the type when creating a variable.
```
private int a
```
Private ban the access to the variable from from another context.
Protected allow the variable to be accessed anywhere.
Public allow the variable to be accessed anywhere & are exported to the interface for other datapack.
By default variable start with `protected`.

### Lazy Variable
Variable that are mark as `lazy` will have there value computed during the compilation.
```
lazy int a = 5
a += 10
int b = a
```
Will result into the following code:
```
int b = 15
```
If value of the variable cannot be computed during the compilation the global expression will be keep:
```
int f
lazy int a = 5 * f
int b = a
```
Will result into the following code:
```
int f
int b = 5 * f
```
This can be useful for string and json operation at compile time.
### Const
Variable can be marked as constant with the `const` keyword. This will prevent reassignement of the variable. 
```
const int a = 0
```
Note that this only prevent the variable from being reassign within its block, meaning that it can be reassign when a function is called multiple time.
### val, var
If you don't want to use type for your variable you can use the `val` and `var` keyword.
```
val a = 4
var b = 5
```
`val` is equivalent to `const <type>`. The type of the variable is infered by the right expression. The type cannot change after that.

### Attributes
Attributes can be added to function to specify thing to the compiler.
```
[criterion="minecraft.used:minecraft.carrot_on_a_stick"] scoreboard int a
```
Here is a list of used attributes by the compiler:
- `criterion`: specify the criterion when other than dummy. (JAVA Only)
- `name`: force the name of the variable
- `versionSpecific`: Append the version to the name of the variable
- `tag`: force the tag use by the variable (for variable of type entity)


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

### T => T
Store non lazy function (e.i. void=>void)

Supported operation:
* `=`: The variable to have the variable of function
* `()`: Call the contained function


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
You can test if an entity belong to the variable with the following syntax:
```
entity set
if (@s in set){

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

The compiler allow a special expression to test condition on multiple entities at once.
The following code will only say hi if value is true for all player.
```
if (value for all in @a){
    /say hi
}
```
The following code will only say hi if value is true for any player.
```
if (value for any in @a){
    /say hi
}
```
The following code will only say hi if value is false for all player.
```
if (value for none in @a){
    /say hi
}
```

### Switch
A more simplier way of having multiple if is to use a switch statement:
```
int a
switch(a){
    0 -> do_stuff()
    1 -> {
        do_other_stuff()
    }
    2 -> {
        /say hi
    }
    3..4 -> {
        /say hi
    }
}
```
The switch statement will build a tree if the number of cases if big enough. (20 by defaut)
In addition to that switch also support tupple value.
```
(int, int) a
switch(a){
    (0,0) -> do_stuff1()
    (0,1) -> do_stuff2()
    (1,0) -> do_stuff2()
    (1,1) -> do_stuff2()
}
```
In that case it will build nested trees.

You can also an auto generated switch with the following format:
```
int value
switch(value for t in 0..10){
    t -> print(t)
}
```
Note: This is used to precompute operation that cannot be computed in Minecraft. Example: String concatenation with a number, Getting value from lazy json variable, etc.

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
Instructions can be executed **at** a position with:
```
at(~ ~1 ~){
    do_stuff()
}
Instructions can also be executed in a ranged position:
```
at({~, ~0..10, ~}){
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
// as @a at @s if a == 0
with(@a, true, a == 0){
    do_stuff()
}
// as @a if a == 0
with(@a, false, a == 0){
    do_stuff()
}
with(@e){
    do_stuff()
}
else{
    do_stuff_if_no_entity()
}
```

(Java Only)
To execute commands with the attacker, controller, leasher, origin, owner, passengers, target or vehicle of an entity, you can use the following selectors:
`@attacker`, `@controller`, `@leasher`, `@origin`, `@owner`, `@passengers`, `@target` and `@vehicle`
```
with(@attacker){
    // do stuff
}
```

To execute on top of an height map you can use the following selectors:
`@world_surface`, `@motion_blocking`, `@motion_blocking_no_leaves`, and `@ocean_floor`
```
at(@world_surface){
    // do stuff
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

### Lambda
Lambda can be created with the following syntax:
```
int=>int fct = (a)=> {return a}
```

### Function Tags
Function can also belong to a tag by adding an name prefixed by `@`:
```
def @tagExample test(){

}
```
If the tag doesn't exist before it will be created.
Tags allow you to call all the function that belong to it by calling the tag name:
```
@tagExample()
```
Note that tags are global across the code context.

### Attributes
Attributes can be added to function to specify thing to the compiler.
```
def [tag.order=10] @tick test(){

}
```
Here is a list of used attributes by the compiler:
- `tag.order`: (float or int) Order for which it will run when call by a tag. Lower number are call first.
- `compile.order`: (float or int) Order for the function is compile. Lower number are compiled first.
- `inline`: (bool) Make that lazy function call is not on a sub context. It allow to make the inner state of the function visible outside the call.

## Import
Library can be imported with the following syntax:
```
import math

int a = math.abs(5)
```
Package can also be given alias uppon importation:
```
import math as mt

int a = mt.abs(5)
```
Object can be be directly imported from a library:
```
from math.vector3 import Vector3

Vector3 vec
```
Imported object can also be given alias uppon importation:
```
from math.vector3 import Vector3 as v3

v3 vec
```
You can also import everything from a package:
```
from math.vector3 import _

Vector3 vec
```

## JSON File
Jsonfile can be added with the following construct:
```
jsonfile advancements.name{
    <json content>
}
```

Jsonfile can also take a lazy variable containing a json object:
```
lazy json jsonObj = {
    "a": 0
}
jsonfile advancements.name jsonObj
```

Jsonfile can also take a mix of json object and json variable:
```
lazy int a = 0
jsonfile advancements.name{
    "a": a
}
```

### Attributes
Attributes can be added to jsonfile to specify thing to the compiler.
```
[java_rp=true] jsonfile models.block.stone{

}
```
Here is a list of used attributes by the compiler:
- `java_rp`: (bool) Add the file to the Java Resources Pack
- `bedrock_rp`: (bool) Add the file to the Bedrock Resources Pack

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
        ]
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
* `__mul__`: `*=`
* `__div__`: `/=`
* `__mod__`: `%=`
* `__and__`: `&=`
* `__or__`: `|=`
* `__lt__`: `<`
* `__le__`: `<=`
* `__eq__`: `==`
* `__ne__`: `!=`
* `__ge__`: `>=`
* `__gt__`: `>`
* `__init__`: Constructor

Structs can also be extended. They will recieve all the methode & variable from the parent struct.
```
struct vector4 extends vector3{
    int a
}
```

### Generics 
Struct can also accept type parameters with the following syntax:
```
struct Struct<T>{
    T vari

    def __init__(T value){
        vari = value
    }
}

Struct<int> example = new Struct<int>(0)
```

## Classes
Classes can be define with the following syntax:
```
class cow{
    int health
}
```

Class also support operator overloading with the same syntax as structs.

### Generics 
Class can also accept type parameters with the following syntax:
```
class Class<T>{
    T vari

    def __init__(T value){
        vari = value
    }
}

Class<int> example = new Class<int>(0)
```

### Entity for the class
Class use an entity, to store data. By default it use a marker entity but you can change it with the following syntax:
```
class Cow with minecraft:cow for mcjava with minecraft:pig for mcbedrock{
    def __init__(){

    }
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
Template can be "Apply" with the following syntax:
```
example2 foo{
    def bar(){
        test()
    }
}
```

### Generic Template
Template can also accept type parameters with the following syntax:
```
template example<T, U>{
    T vari = U

    def __init__(T value){
        vari = value
    }
}
```
The value can be either a type or a value and can be use in the template body.
When Instantiating the template, the type parameter can be given with the following syntax:
```
example<int, 0> instance
```

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
Every argument of the function will be a lazy variable. If the variable start with a `$` it will be replace literally instead of being replace by it's value.
Every call for function that are not mark as inline is done in a sub context meaning that variable inside it won't be usable after the call.

Note: Lazy function can be recursive, but you must be careful to not create infinite loop, or the compiler will crash.

### Generic Functions
Function can also support type parameters with the following syntax:
```
T function<T>(T a){
    return a
}
int a = function(0)
```
where T is the type parameters.

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
* `@tagExample`: where tagExample is a function tag. Tell if there is at least one function inside the tag when the compiler reach the condition.


### Compile Type Checking
When a lazy variable get assign a value. The type of the value is keep inside the lazy variable.
For instance:
```
lazy float a = 1
```
Will contains an int value.

It is therefor possible to write the following code:
```
if (a is int){
    print("Is int")
}
if (a is float){
    print("Is float")
}
if ("key" in dict){
    print("Is dict")
}
```
This is computed at compile time and not runtime.

## Foreach
The foreach statement allow to iterate over a list of value and generate code for each of them.
```
foreach (i in (0, 1, 2)){
    print(i)
}
```
Will generate:
```
print(0)
print(1)
print(2)
```
Note that forgenerate "copy paste" the code inside the loop for each value. This mean that there is no loop at runtime.

The allowed generators are:
* `<start>..<end>`: Generate a range of number
* `#<blocktag>`: Generate all the block inside the tag
* `@<functiontag>`: Generate all the function inside the tag
* `<json array>`: Generate all the value inside the array
* `<json object>`: Generate all the key inside the object

## Forgenerate
Forgenerate is a legacy syntax that is not recommended to use. You can use foreach instead.
Forgenerate can be used to generate code but work by replacing literal value inside the code. For instance:
```
forgenerate ($i, (0, 1, 2)){
    /say $i
}
```
Will generate:
```
/say 0
/say 1
/say 2
```

However, it is not possible to use the following syntax:
```
forgenerate ($i, (0, 1, 2)){
    a = $i
}
```
In this case the compiler will think that $i is a variable and not a literal value.

## Compiler Function
The compiler offer some function that can be used to generate code. They are: 
* `Compiler.insert(<$name>, <value>){<code>}`: Insert the value inside the code. The value can be used with the syntax `$name`:
```
Compiler.insert($i, i){
    /say $i
}
```
Mutliple value can be insert at the same time:
```
Compiler.insert(($i, $j), (i, j)){
    /say $i $j
}
```
* `Compiler.readJson(<path>)`: Read a json file and return it as a lazy json object
* `Compiler.random()`: Return a random number (At compile time)
* `Compiler.variableExists(<name>)`: Return true if the variable exist
* `Compiler.getTemplateName()`: Return the name of the current template
* `Compiler.sqrt(<value>)`: Return the square root of the value
* `Compiler.pow(<value>, <power>)`: Return the value to the power of the power
* `Compiler.powInt(<value1>, <value2>)`: Return the value1 to the power of the value2
* `Compiler.hash(<value1>)`: Return the hash of the value
* `Compiler.getObjective(<variable>)`: Return the objective of the variable
* `Compiler.getSelector(<variable>)`: Return the selector of the variable
* `Compiler.getContextName()`: Return the name of the current context
* `Compiler.getVariableTag()`: Return the tag of the current variable (for variable of type entity)
* `Compiler.toNBT(<value>)`: Return the nbt of the json value
* `Compiler.getProjectVersionType()`: Return the version type of the project alpha, beta, release
* `Compiler.getProjectVersionMajor()`: Return the major version of the project
* `Compiler.getProjectVersionMinor()`: Return the minor version of the project
* `Compiler.getProjectFullName()`: Return the full name of the project
* `Compiler.getProjectName()`: Return the name of the project
* `Compiler.print()`: Print a message in the console
* `Compiler.replace(<src>, <from>, <to>)`: Replace all the occurence of from by to in src string
* `Compiler.toRadians(<value>)`: Convert the value to radians
* `Compiler.toDegrees(<value>)`: Convert the value to degrees
* `Compiler.makeUnique(<selector>)`: Return a selector that is unique
* `Compiler.interpreterException(<message>)`: Throw an exception with the message in the interpreter
* `Compiler.cmdstore(<variable>){<code>}`: Store the result of the code inside the variable
