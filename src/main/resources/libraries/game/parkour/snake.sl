package game.parkour.snake

import cmd.entity as entity
import cmd.block as block
import utils.CProcess

/*
enum SnakeBlock{
    white, 
    red, 
    orange, 
    yellow, 
    lime, 
    green, 
    light_blue, 
    aqua, blue, 
    purple, 
    magenta, 
    brown, 
    black, 
    pink, 
    light_gray, 
    gray, 
    cyan, 
    barrier
}
blocktag snake_block{
    minecraft:white_wool,
    minecraft:red_wool,
    minecraft:orange_wool,
    minecraft:yellow_wool,
    minecraft:lime_wool,
    minecraft:green_wool,
    minecraft:blue_wool,
    minecraft:light_blue_wool,
    minecraft:cyan_wool,
    minecraft:blue_wool,
    minecraft:purple_wool,
    minecraft:magenta_wool,
    minecraft:pink_wool,
    minecraft:brown_wool,
    minecraft:black_wool,
    minecraft:light_gray_wool,
    minecraft:gray_wool
}
scoreboard int SnakeID
scoreboard int Speed
scoreboard int Delay
scoreboard int Length
scoreboard SnakeBlock Block1
scoreboard SnakeBlock Block2

entity snakeHead
entity snakeTail

def init(SnakeBlock b1, SnakeBlock b2, int speed, int length){
    SnakeID = 0
    Delay = 0
    Length = length
    Speed = speed
    Block1 = b1
    Block2 = b2
}
def summon(SnakeBlock b1, SnakeBlock b2, int speed, int length){
    snakeHead += entity.summon(marker){
        init(b1, b2, speed, length)
    }
    snakeTail += entity.summon(marker){
        init(b1, b2, speed, length)
    }
}


def setblock(){
    SnakeID = (SnakeID + 1) 
    SnakeID %= 2
    snakeBlock block
    if (SnakeID == 0){
        block = Block1
    }
    if (SnakeID == 1){
        block = Block2
    }
    switch(block){
        SnakeBlock.white -> block.set(~ ~ ~, minecraft:white_wool)
        SnakeBlock.red -> block.set(~ ~ ~, minecraft:red_wool)
        SnakeBlock.orange -> block.set(~ ~ ~, minecraft:orange_wool)
        SnakeBlock.yellow -> block.set(~ ~ ~, minecraft:yellow_wool)
        SnakeBlock.lime -> block.set(~ ~ ~, minecraft:lime_wool)
        SnakeBlock.green -> block.set(~ ~ ~, minecraft:green_wool)
        SnakeBlock.light_blue -> block.set(~ ~ ~, minecraft:light_blue_wool)
        SnakeBlock.cyan -> block.set(~ ~ ~, minecraft:cyan_wool)
        SnakeBlock.blue -> block.set(~ ~ ~, minecraft:blue_wool)
        SnakeBlock.purple -> block.set(~ ~ ~, minecraft:purple_wool)
        SnakeBlock.magenta -> block.set(~ ~ ~, minecraft:magenta_wool)
        SnakeBlock.pink -> block.set(~ ~ ~, minecraft:pink_wool)
        SnakeBlock.gray -> block.set(~ ~ ~, minecraft:gray_wool)
        SnakeBlock.light_gray -> block.set(~ ~ ~, minecraft:light_gray_wool)
        SnakeBlock.black -> block.set(~ ~ ~, minecraft:black_wool)
        SnakeBlock.brown -> block.set(~ ~ ~, minecraft:brown_wool)
        SnakeBlock.barrier -> block.set(~ ~ ~, minecraft:barrier)
    }
}

def copy(){
    val b1 = Block1
    val b2 = Block2
    val speed = Speed
    val length = Length
    int i = SnakeID
    snakeHead += entity.summon(marker){
        init(b1, b2, speed, length)
        SnakeID = i + 1
        setblock()
    }
    snake.main.start()
}

def headMain(){
    with(snakeHead,true){
        Delay += Speed
        if(Delay >= 20){
            Delay  = 0
            bool hasMoved = false
            if (block(~1 ~ ~, minecraft:tripwire) && !hasMoved){
                /tp @s ~1 ~ ~
                at(@s){setblock()}
                hasMoved = true
            }
            
            
            if (block(~-1 ~ ~, minecraft:tripwire) && hasMoved){
                at(~-1 ~ ~){
                    copy()
                }
            }
            if (block(~-1 ~ ~, minecraft:tripwire) && !hasMoved){
                /tp @s ~-1 ~ ~
                at(@s){setblock()}
                hasMoved = true
            }
            
            
            if (block(~ ~1 ~, minecraft:tripwire) && hasMoved){
                at(~ ~1 ~){
                    copy()
                }
            }
            if (block(~ ~1 ~, minecraft:tripwire) && !hasMoved){
                /tp @s ~ ~1 ~
                at(@s){setblock()}
                hasMoved = true
            }
            
            
            if (block(~ ~-1 ~, minecraft:tripwire) && hasMoved){
                at(~ ~-1 ~){
                    copy()
                }
            }
            if (block(~ ~-1 ~, minecraft:tripwire) && !hasMoved){
                /tp @s ~ ~-1 ~
                at(@s){setblock()}
                hasMoved = true
            }
            
            
            if (block(~ ~ ~1, minecraft:tripwire) && hasMoved){
                at(~ ~ ~1){
                    copy()
                }
            }
            if (block(~ ~ ~1, minecraft:tripwire) && !hasMoved){
                /tp @s ~ ~ ~1
                at(@s){setblock()}
                hasMoved = true
            }
            
            
            if (block(~ ~ ~-1, minecraft:tripwire) && hasMoved){
                at(~ ~ ~-1){
                    copy()
                }
            }
            if (block(~ ~ ~-1, minecraft:tripwire) && !hasMoved){
                /tp @s ~ ~ ~-1
                at(@s){setblock()}
                hasMoved = true
            }
            
            
            if(!hasMoved){
                snake.main.stop()
                entity.kill(@s)
            }
        }
    }
}

def rmblock(){
    block.set(tripwire)
    particles.sphere(end_rod,1,0.1,1)
}

def copyTail(){
    snake.main.start()
    int length = Length
    int speed = Speed
    int b1 = Block1
    int b2 = Block2
    int i = SnakeID
    snakeTail += entity.summon(marker){
        init(b1, b2, speed, length)
        SnakeID = i
    }
}

def tailMain(){
    with(snakeTail,true){
        Delay += Speed
        if(Delay >= 20){
            Delay  = 0
            Length--
            if (Length < 0){
                bool hasMoved = false
                if (block(~1 ~ ~, #snake_block) && !hasMoved){
                    /tp @s ~1 ~ ~
                    at(@s){rmblock()}
                    hasMoved = true
                }
                
                if (block(~-1 ~ ~, #snake_block) && hasMoved){
                    at(~-1 ~ ~){
                        copyTail()
                        rmblock()
                    }
                }
                if (block(~-1 ~ ~, #snake_block) && !hasMoved){
                    /tp @s ~-1 ~ ~
                    at(@s){rmblock()}
                    hasMoved = true
                }
                
                
                if (block(~ ~1 ~, #snake_block) && hasMoved){
                    at(~ ~1 ~){
                        copyTail()
                        rmblock()
                    }
                }
                if (block(~ ~1 ~, #snake_block) && !hasMoved){
                    /tp @s ~ ~1 ~
                    at(@s){rmblock()}
                    hasMoved = true
                }
                
                
                if (block(~ ~-1 ~, #snake_block) && hasMoved){
                    at(~ ~-1 ~){
                        copyTail()
                        rmblock()
                    }
                }
                if (block(~ ~-1 ~, #snake_block) && !hasMoved){
                    /tp @s ~ ~-1 ~
                    at(@s){rmblock()}
                    hasMoved = true
                }
                
                if (block(~ ~ ~1, #snake_block) && hasMoved){
                    at(~ ~ ~1){
                        copyTail()
                        rmblock()
                    }
                }
                if (block(~ ~ ~1, #snake_block) && !hasMoved){
                    /tp @s ~ ~ ~1
                    at(@s){rmblock()}
                    hasMoved = true
                }
                
                
                if (block(~ ~ ~-1, #snake_block) && hasMoved){
                    at(~ ~ ~-1){
                        copyTail()
                        rmblock()
                    }
                }
                if (block(~ ~ ~-1, #snake_block) && !hasMoved){
                    /tp @s ~ ~ ~-1
                    at(@s){rmblock()}
                    hasMoved = true
                }
                if(!hasMoved){
                    snake.main.stop()
                    entity.kill(@s)
                }
            }
        }
    }
}

bool enabled
def helper enable(){
    enabled = true
}
def helper disable(){
    enabled = false
}

def @playertick playerticker(){
    enabled:=true
    if (snake.enabled){
        if (__inited == null){
            __inited = true
            GreenSpeed := 4
            GreenLength := 20
            
            RedSpeed := 8
            RedLength := 20
        }
        
        
        bool spawn = false
        int speed
        int length
        if(block(~ ~-0.2 ~ purple_glazed_terracotta)){
            setblock(~ ~-0.2 ~ obsidian)
            /summon marker ~ ~ ~ {Invisible:1,Tags:["limeSnake","rSnake"],NoGravity:1}
            spawn = true
            speed = GreenSpeed
            length = GreenLength
        }
        if(block(~ ~-0.2 ~ red_glazed_terracotta)){
            setblock(~ ~-0.2 ~ obsidian)
            /summon marker ~ ~ ~ {Invisible:1,Tags:["redSnake","rSnake"],NoGravity:1}
            spawn = true
            speed = RedSpeed
            length = RedLength
        }
        if (spawn){
            int rand2 = random.range(0, 13)
            
            at(~ ~-1 ~){
                switch(rand2){
                    0 -> parkour_snake.newSnake(lime, green, speed, length)
                    1 -> parkour_snake.newSnake(blue, cyan, speed, length)
                    2 -> parkour_snake.newSnake(blue, light_blue, speed, length)
                    3 -> parkour_snake.newSnake(yellow, orange, speed, length)
                    4 -> parkour_snake.newSnake(orange, red, speed, length)
                    5 -> parkour_snake.newSnake(magenta, purple, speed, length)
                    6 -> parkour_snake.newSnake(white, black, speed, length)
                    7 -> parkour_snake.newSnake(gray, light_gray, speed, length)
                    8 -> parkour_snake.newSnake(lime, yellow,speed, length)
                    9 -> parkour_snake.newSnake(black, red, speed, length)
                    10 -> parkour_snake.newSnake(black, blue, speed, length)
                    11 -> parkour_snake.newSnake(black, yellow, speed, length)
                    12 -> parkour_snake.newSnake(magenta, pink, speed, length)
                }
            }
            reload.start()
        }
    }
}

CProcess main{
    def main(){
        headMain()
        tailMain()
    }
}


scoreboard int SnakeTime = 0
CProcess reload{
    def main(){
        with(@e[tag=limeSnake,type=marker],true){
            SnakeTime ++
            if (SnakeTime > 125){
                setblock(~ ~-1 ~ purple_glazed_terracotta)
                stop()
                kill(@s)
            }
        }
        with(@e[tag=redSnake,type=marker],true){
            SnakeTime ++
            if (SnakeTime > 125){
                setblock(~ ~-1 ~ red_glazed_terracotta)
                stop()
                kill(@s)
            }
        }
    }
}
*/