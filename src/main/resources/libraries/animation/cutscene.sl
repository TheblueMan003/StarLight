package animation.Cutscene

import utils.Process
import mc.pointer as pointer
import cmd.entity as entity
import cmd.gamemode as gm

def private lazy teleportFront(int $distance){
    /tp @s ^ ^ ^$distance
}
def private lazy teleportHere(){
   /tp @s ~ ~ ~ ~ ~
}
template Cutscene{
    entity entities
    lazy int buildScene = 0
    lazy bool _groupped = false
    
    int scene
    int tick
    entity camera
    bool endOfSegment = false
    
    """
    Start the cutscene for the entities `e`
    """
    def lazy start(entity e){
        entities = e
        init()
        with(e)gm.spectator()
    }
    
    """
    Init the cutscene
    """
    def init(){
        with(camera)./kill
        at(@p)camera = pointer.newPointer()
        scene = 0
        tick = 0
        endOfSegment = true
    }

    """
    Start the next segment of the cutscene
    """
    def nextSegment(){
        endOfSegment = true
        scene++
        tick = 0
    }

    """
    Call the function `fct` when this segment is reach and goes directly to the next
    """
    def lazy event(void=>void fct){
        lazy val myScene = buildScene
        if (!_groupped){
            buildScene ++
        }
        if (myScene == scene){
            if (endOfSegment){
                fct()
                nextSegment()
            }
        }
    }
    
    """
    Wait for the function `fct` to return true then goes to the next
    """
    def lazy waitFor(int=>bool fct){
        lazy val myScene = buildScene
        if (!_groupped){
            buildScene ++
        }
        if (myScene == scene){
            tick++
            bool res = fct(tick)
            if (res){
                nextSegment()
            }
            at(camera){
                with(entities)teleportHere()
            }
        }
    }

    """
    Wait for the function `fct` to return true then goes to the next
    """
    def lazy waitFor(mcposition target, int=>bool fct){
        lazy val myScene = buildScene
        if (!_groupped){
            buildScene ++
        }
        if (myScene == scene){
            tick++
            bool res = fct(tick)
            if (res){
                nextSegment()
            }
            at(camera)facing(target){
                with(entities)teleportHere()
            }
        }
    }

    """
    Wait for the function `fct` to return true then goes to the next
    """
    def lazy waitFor(mcposition position, mcposition target, int=>bool fct){
        lazy val myScene = buildScene
        if (!_groupped){
            buildScene ++
        }
        if (myScene == scene){
            tick++
            bool res = fct(tick)
            if (res){
                nextSegment()
            }
            at(position)facing(target){
                with(camera)teleportHere()
                with(entities)teleportHere()
            }
        }
    }

    """
    Wait for `time` ticks
    """
    def lazy waitTime(int time, int=>void fct = null){
        lazy val myScene = buildScene
        if (!_groupped){
            buildScene ++
        }
        if (myScene == scene){
            tick++
            fct(tick)
            if (tick > time){
                nextSegment()
            }
            at(camera){
                with(entities)teleportHere()
            }
        }
    }

    """
    Wait for `time` ticks
    """
    def lazy waitTime(int time, mcposition target, int=>void fct = null){
        lazy val myScene = buildScene
        if (!_groupped){
            buildScene ++
        }
        if (myScene == scene){
            tick++
            fct(tick)
            if (tick > time){
                nextSegment()
            }
            at(camera)facing(target){
                with(entities)teleportHere()
            }
        }
    }

    """
    Wait for `time` ticks
    """
    def lazy waitTime(int time, mcposition position, mcposition target, int=>void fct){
        lazy val myScene = buildScene
        if (!_groupped){
            buildScene ++
        }
        if (myScene == scene){
            tick++
            fct(tick)
            if (tick > time){
                nextSegment()
            }
            at(position)facing(target){
                with(camera)teleportHere()
                with(entities)teleportHere()
            }
        }
    }


    """
    Move a fictive entity from `pos1` to `pos2`. The camera is located at `target` and look at the entity.
    """
    def lazy pan(float speed, mcposition pos1, mcposition pos2, mcposition target, int=>void whileActive = null){
        lazy val myScene = buildScene
        if (!_groupped){
            buildScene ++
        }
        if (myScene == scene){
            if (endOfSegment){
                endOfSegment = false
                as(camera)at(pos1)./tp @s ~ ~ ~
            }
            whileActive(tick)
            tick++
            with(camera,true){
                facing(pos2){
                    teleportFront(speed)
                }
                at(pos2)if(@s[distance=..speed])nextSegment()
            }
            at(target)facing(camera){
                with(entities){
                   teleportHere()
                }
            }
        }
    }

    """
    Move a fictive entity from current camera pos to `pos2`. The camera is located at `target` and look at the entity.
    """
    def lazy pan(float speed, mcposition pos2, mcposition target, int=>void whileActive = null){
        lazy val myScene = buildScene
        if (!_groupped){
            buildScene ++
        }
        if (myScene == scene){
            if (endOfSegment){
                endOfSegment = false
            }
            whileActive(tick)
            tick++
            with(camera,true){
                facing(pos2){
                    teleportFront(speed)
                }
                lazy val speed2 = -speed*2
                at(pos2)if(@s[distance=..speed])nextSegment()
            }
            at(target)facing(camera){
                with(entities){
                   teleportHere()
                }
            }
        }
    }

    """
    Follow a fictive entity from `pos1` to `pos2` shifted by `delta`
    """
    def lazy track(float speed, mcposition pos1, mcposition pos2, mcposition delta, int=>void whileActive = null){
        lazy val myScene = buildScene
        if (!_groupped){
            buildScene ++
        }
        if (myScene == scene){
            if (endOfSegment){
                endOfSegment = false
                as(camera)at(pos1)./tp @s ~ ~ ~
            }
            whileActive(tick)
            tick++
            with(camera,true){
                facing(pos2){
                    teleportFront(speed)
                }
                at(@s)at(delta)facing(camera){
                    with(entities){
                       teleportHere()
                    }
                }
                at(pos2)if(@s[distance=..speed])nextSegment()
            }
        }
    }

    """
    Follow a fictive entity from current camera pos to `pos2` shifted by `delta`
    """
    def lazy track(float speed, mcposition pos2, mcposition delta, int=>void whileActive = null){
        lazy val myScene = buildScene
        if (!_groupped){
            buildScene ++
        }
        if (myScene == scene){
            if (endOfSegment){
                endOfSegment = false
            }
            whileActive(tick)
            tick++
            with(camera,true){
                facing(pos2){
                    teleportFront(speed)
                }
                at(@s)at(delta)facing(camera){
                    with(entities){
                       teleportHere()
                    }
                }
                at(pos2)if(@s[distance=..speed])nextSegment()
            }
        }
    }

    """
    Move the camera from `pos1` to `pos2` while facing `target`
    """
    def lazy linear(float speed, mcposition pos1, mcposition pos2, mcposition target, int=>void whileActive = null){
        lazy val myScene = buildScene
        if (!_groupped){
            buildScene ++
        }
        if (myScene == scene){
            if (endOfSegment){
                endOfSegment = false
                as(camera)at(pos1)./tp @s ~ ~ ~
            }
            whileActive(tick)
            tick++
            with(camera,true){
                facing(pos2){
                    teleportFront(speed)
                }
                at(@s)facing(target){
                    with(entities){
                       teleportHere()
                    }
                }
                at(pos2)if(@s[distance=..speed])nextSegment()
            }
        }
    }

    """
    Move the camera from current camera pos to `pos2` while facing `target`
    """
    def lazy linear(float speed, mcposition pos2, mcposition target, int=>void whileActive = null){
        lazy val myScene = buildScene
        if (!_groupped){
            buildScene ++
        }
        if (myScene == scene){
            if (endOfSegment){
                endOfSegment = false
            }
            whileActive(tick)
            tick++
            with(camera,true){
                facing(pos2){
                    teleportFront(speed)
                }
                at(@s)facing(target){
                    with(entities){
                       teleportHere()
                    }
                }
                at(pos2)if(@s[distance=..speed])nextSegment()
            }
        }
    }
    
    """
    Group multiple instruction into one segment.
    """
    def lazy group(void=>void fct){
        _groupped = true
        fct()
        _groupped = false
        buildScene ++
    }
}