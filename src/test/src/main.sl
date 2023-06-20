package fruit

import game.Room
import cmd.entity as entity

lazy json example = {
    "name": "level1",
    "objects": [
        {
            "type": "player",
            "position": [0, 0, 0],
            "rotation": [0, 0],
            "scores": {
                "score1": 0,
                "score2": 0
            },
            "functions":[
                "test"
            ],
            "tags": [
                "test"
            ],
            "events": [
                "skin"
            ],
            "destroy": "test"
        }
    ]
}

template Level{
    scoreboard void=>void destroyer
    entity objects
    public lazy void summon(json value){
        Compiler.insert(($x, $y, $z), (value["position"][0], value["position"][1], value["position"][2])){
            at($x $y $z){
                objects += entity.summon(value["type"]){
                    if ("scores" in value){
                        foreach(key in value["scores"]){
                            Compiler.insert($key, key){
                                key = value["scores"][key];
                            }
                        }
                    }
                    if ("functions" in value){
                        foreach(function in value["functions"]){
                            Compiler.insert($function, function){
                                $function();
                            }
                        }
                    }
                    if ("tags" in value){
                        foreach(tag in value["tags"]){
                            Compiler.insert($tag, tag){
                                /tag add @s $tag
                            }
                        }
                    }
                    if ("rotation" in value){
                        Compiler.insert(($x, $y), (value["rotation"][0], value["rotation"][1])){
                            /tp @s ~ ~ ~ $x $y
                        }
                    }
                    if ("events" in value){
                        foreach(event in value["events"]){
                            Compiler.insert($event, event){
                                /event entity @s $event
                            }
                        }
                    }
                    if ("destroy" in value){
                        Compiler.insert($destroy, value["destroy"]){
                            destroyer = $destroy
                        }
                    }
                }
            }
        }
    }
    public lazy void summonAll(json value){
        foreach(object in value["objects"]){
            summon(object)
        }
    }
    public void onStart(){
    }
    public void onStop(){
    }
    public void start(){
        onStart()
    }
    public void stop(){
        onStop()
        with(objects,true){
            destroyer()
            /kill
        }
    }
}

template AreaLevel<sx, sy, sz, ex, ey, ez> extends Level{
    def lazy parentStart(){
        start()
    }
    def lazy parentStop(){
        stop()
    }
    def @templates.parent.room.tick __room_tick__(){
        @templates.room.tick()
    }
    Room room{
        init(sx, sy, sz, ex, ey, ez)
        def onStart(){
            parentStart()
        }
        def onStop(){
            parentStop()
        }
    }
}

fruit.AreaLevel<0,0,0,10,10,10> test{
    public void onStart(){
        /say hi
        summon({"type":"minecraft:cow", "position":[0, 0, 0]})
    }
}

test.start()