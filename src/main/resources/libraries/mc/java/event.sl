package mc.java.event

lazy void=>void eventCall(string $advancement, void=>void func3){
    def __call__(){
        lazy val name = Compiler.getProjectName()
        func3()
        Compiler.insert($name, name){
            /advancement revoke @s only $name:events/$advancement
        }
    }
    return __call__
}

def lazy eventCreateAdvancment(string $name, json criteria, void=>void func){
    lazy val name = Compiler.getProjectName()
    Compiler.insert($proj, name){
        jsonfile $proj.advancements.events.$name{
            "criteria": criteria,
            "rewards": {
                "function": func
            }
        }
    }
}
lazy var advancementID = 0
def lazy eventCreate(json criteria, void=>void func1){
    lazy string name = "event_" +  advancementID
    lazy void=>void func = eventCall(name, func1)
    eventCreateAdvancment(name, criteria, func)
    advancementID++
}



def lazy onBeeNestDestroy(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:bee_nest_destroyed",
                "conditions": {}
            }
        },func)
}

def lazy onBeeNestDestroy(int nb, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:bee_nest_destroyed",
                "conditions": {"num_bees_inside": nb}
            }
        },func)
}

def lazy onBredAnimals(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:bred_animals",
                "conditions": {}
            }
        },func)
}

def lazy onBredAnimals(mcobject type, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:bred_animals",
                "conditions": {
                    "parent": [
                        {
                            "condition": "minecraft:entity_properties",
                            "entity": "this",
                            "predicate": {
                                "type": type
                            }
                        }
                        ]
                }
            }
        },func)
}

def lazy onBredAnimals(mcobject type, json nbt1, json nbt2, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:bred_animals",
                "conditions": {
                    "parent": [
                        {
                            "condition": "minecraft:entity_properties",
                            "entity": "this",
                            "predicate": {
                                "type": type,
                                "nbt": nbt1
                            }
                        }
                        ],
                    "partner": [
                        {
                            "condition": "minecraft:entity_properties",
                            "entity": "this",
                            "predicate": {
                                "nbt": nbt2
                            }
                        }
                        ]
                }
            }
        },func)
}


def lazy onDimensionChanged(void=>void func){
    eventCreate(changed_dimension,{
            "requirement": {
                "trigger": "minecraft:changed_dimension",
                "conditions": {}
            }
        },func)
}
def lazy onDimensionChanged(mcobject dest, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:changed_dimension",
                "conditions": {
                    "to": dest
                }
            }
        },func)
}
def lazy onDimensionChanged(mcobject fro, mcobject dest, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:changed_dimension",
                "conditions": {
                    "from": fro,
                    "to": dest
                }
            }
        },func)
}
def lazy onBrewPotion(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:brewed_potion",
                "conditions": {}
            }
        },func)
}
def lazy onBrewPotion(mcobject effect, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:brewed_potion",
                "conditions": {
                    "potion": effect
                }
            }
        },func)
}

def lazy onTick(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:tick",
                "conditions": {}
            }
        },func)
}


def lazy onChanneledLightning(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:channeled_lightning",
                "conditions": {}
            }
        },func)
}
def lazy onChanneledLightning(mcobject entity, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:channeled_lightning",
                "conditions": {
                    "victims": [
                        {
                            "type": entity
                        }
                        ]
                }
            }
        },func)
}
def lazy onChanneledLightning(mcobject entity, json nbt, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:channeled_lightning",
                "conditions": {
                    "victims": [
                        {
                            "type": entity,
                            "nbt": nbt
                        }
                        ]
                }
            }
        },func)
}
        
def lazy onConstructBeacon(void=>void func){
    eventCreate({
    "requirement": {
      "trigger": "minecraft:construct_beacon",
      "conditions": {}
    }
  },func)
}

def lazy onConstructBeacon(int lvl, void=>void func){
    eventCreate({
    "requirement": {
      "trigger": "minecraft:construct_beacon",
      "conditions": {"level": lvl}
    }
  },func)
}

def lazy onConsumeItem(void=>void func){
    eventCreate(consume_item,{
    "requirement": {
      "trigger": "minecraft:consume_item",
      "conditions": {}
    }
  },func)
}
def lazy onConsumeItem(mcobject $item, void=>void func){
    eventCreate({
    "requirement": {
      "trigger": "minecraft:consume_item",
      "conditions": {
      "item": {
          "items": [
            item
          ]
        }
      }
    }
  },func)
}
def lazy onConsumeItem(mcobject item, json nbt,void=>void func){
    eventCreate({
    "requirement": {
      "trigger": "minecraft:consume_item",
      "conditions": {
      "item": {
          "items": [
            item
            ],
            "nbt": nbt
        }
      }
    }
  },func)
}

def lazy onUsingItem(void=>void func){
    eventCreate({
    "requirement": {
      "trigger": "minecraft:using_item",
      "conditions": {}
    }
  },func)
}
def lazy onUsingItem(mcobject item, void=>void func){
    eventCreate({
    "requirement": {
      "trigger": "minecraft:using_item",
      "conditions": {
      "item": {
          "items": [
          item
          ]
        }
      }
    }
  },func)
}
def lazy onUsingItem(mcobject item, json nbt, void=>void func){
    eventCreate({
    "requirement": {
      "trigger": "minecraft:using_item",
      "conditions": {
      "item": {
          "items": [
          item
            ],
            "nbt": nbt
        }
      }
    }
  },func)
}

def lazy onUsedTotem(void=>void func){
    eventCreate({
    "requirement": {
      "trigger": "minecraft:used_totem",
      "conditions": {}
    }
  },func)
}
def lazy onUsedTotem(mcobject item, void=>void func){
    eventCreate({
    "requirement": {
      "trigger": "minecraft:used_totem",
      "conditions": {
      "item": {
          "items": [
          item
          ]
        }
      }
    }
  },func)
}
def lazy onUsedTotem(mcobject item, mcobject nbt, void=>void func){
    eventCreate({
    "requirement": {
      "trigger": "minecraft:used_totem",
      "conditions": {
      "item": {
          "items": [
          item
            ],
            "nbt": nbt
        }
      }
    }
  },func)
}

def lazy onVillagerTrade(void=>void func){
    eventCreate({
    "requirement": {
      "trigger": "minecraft:villager_trade",
      "conditions": {}
    }
  },func)
}
def lazy onVillagerTrade(mcobject item, void=>void func){
    eventCreate({
    "requirement": {
      "trigger": "minecraft:villager_trade",
      "conditions": {
      "item": {
          "items": [
          item
          ]
        }
      }
    }
  },func)
}
def lazy onVillagerTrade(mcobject item, json nbt, void=>void func){
    eventCreate({
    "requirement": {
      "trigger": "minecraft:villager_trade",
      "conditions": {
      "item": {
          "items": [
          item
            ],
            "nbt": nbt
        }
      }
    }
  },func)
}


def lazy onCuredZombieVillager(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:cured_zombie_villager",
                "conditions": {}
            }
        },func)
}

def lazy onCuredZombieVillager(json nbt, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:cured_zombie_villager",
                "conditions": {
                    "zombie": {
                        "nbt": nbt
                    }
                }
            }
        },func)
}

def lazy onEffect(mcobject $effect, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:effects_changed",
                "conditions": {
                    "effects": {
                        "$effect": {}
                    }
                }
            }
        },func)
}

def lazy onEffect(mcobject $effect, int amplifier, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:effects_changed",
                "conditions": {
                    "effects": {
                        "$effect": {
                            "amplifier": amplifier
                        }
                    }
                }
            }
        },func)
}

def lazy onEffect(mcobject $effect, int amplifier, int duration, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:effects_changed",
                "conditions": {
                    "effects": {
                        "$effect": {
                            "amplifier": amplifier,
                            "duration": duration
                        }
                    }
                }
            }
        },func)
}


def lazy onEnchant(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:enchanted_item",
                "conditions": {}
            }
        },func)
}

def lazy onEnchant(mcobject item, void=>void func){
    events__.eventCreate({
            "requirement": {
                "trigger": "minecraft:enchanted_item",
                "conditions": {
                    "item": {
                        "items": [
                            item
                            ]
                    }
                }
            }
        },func)
}
def lazy onEnchant(mcobject item, json nbt, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:enchanted_item",
                "conditions": {
                    "item": {
                        "items": [
                            item
                            ],
                        "nbt": nbt
                    }
                }
            }
        },func)
}

def lazy onEnterBlock(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:enter_block",
                "conditions": {}
            }
        },func)
}
def lazy onEnterBlock(mcobject block, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:enter_block",
                "conditions": {
                    "block": block
                }
            }
        },func)
}

def lazy onEntityHurtPlayer(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:entity_hurt_player",
                "conditions": {}
            }
        },func)
}
def lazy onEntityHurtPlayer(mcobject ent, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:entity_hurt_player",
                "conditions": {
                    "damage": {
                        "source_entity": {
                            "type": ent
                        }
                    }
                }
            }
        },func)
}
def lazy onEntityHurtPlayer(mcobject ent, json nbt, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:entity_hurt_player",
                "conditions": {
                    "damage": {
                        "source_entity": {
                            "type": ent,
                            "nbt": nbt
                        }
                    }
                }
            }
        },func)
}

def lazy onEntityKillPlayer(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:entity_killed_player",
                "conditions": {}
            }
        },func)
}
def lazy onEntityKillPlayer(mcobject ent, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:entity_killed_player",
                "conditions": {
                    "damage": {
                        "source_entity": {
                            "type":ent
                        }
                    }
                }
            }
        },func)
}
def lazy onEntityKillPlayer(mcobject ent, json nbt, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:entity_killed_player",
                "conditions": {
                    "damage": {
                        "source_entity": {
                            "type": ent,
                            "nbt": nbt
                        }
                    }
                }
            }
        },func)
}

def lazy onFillBucket(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:filled_bucket",
                "conditions": {}
            }
        },func)
}

def lazy onFishingRodHooked(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:fishing_rod_hooked",
                "conditions": {}
            }
        },func)
}
def lazy onFishingRodHooked(mcobject ent, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:fishing_rod_hooked",
                "conditions": {
                    "entity": {
                        "type": ent
                    }
                }
            }
        },func)
}
def lazy onFishingRodHooked(mcobject ent, json nbt,void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:fishing_rod_hooked",
                "conditions": {
                    "entity": {
                        "type": ent,
                        "nbt": nbt
                    }
                }
            }
        },func)
}

def lazy onHeroOfTheVillager(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:hero_of_the_village",
                "conditions": {}
            }
        },func)
}

def lazy onInventoryChanged(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:inventory_changed",
                "conditions": {}
            }
        },func)
}
def lazy onInventoryFull(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:inventory_changed",
                "conditions": {
                    "slots": {
                        "empty": 0
                    }
                }
            }
        },func)
}
def lazy onItemDurabilityChanged(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:item_durability_changed",
                "conditions": {
                }
            }
        },func)
}
def lazy onItemDurabilityChanged(mcobject item, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:item_durability_changed",
                "conditions": {
                    "item": {
                        "items": [
                            item
                            ]
                    }
                }
            }
        },func)
}
def lazy onItemDurabilityChanged(mcobject item, json nbt, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:item_durability_changed",
                "conditions": {
                    "item": {
                        "items": [
                            item
                            ],
                        "nbt": nbt
                    }
                }
            }
        },func)
}

def lazy onItemUsedOnBlock(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:item_used_on_block",
                "conditions": {
                }
            }
        },func)
}
def lazy onItemUsedOnBlock(mcobject item, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:item_used_on_block",
                "conditions": {
                    "item": {
                        "items": [
                            item
                            ]
                    }
                }
            }
        },func)
}
def lazy onItemUsedOnBlock(mcobject item, json nbt, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:item_used_on_block",
                "conditions": {
                    "item": {
                        "items": [
                            item
                            ],
                        "nbt": nbt
                    }
                }
            }
        },func)
}
def lazy onItemUsedOnBlock(mcobject item, json nbt, mcobject block, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:item_used_on_block",
                "conditions": {
                    "item": {
                        "items": [
                            item
                            ],
                        "nbt": nbt
                    },
                    "location": {
                        "block": {
                            "blocks": [
                                block
                                ]
                        }
                    }
                }
            }
        },func)
}
def lazy onBiome(mcobject biome, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:location",
                "conditions": {
                    "location": {
                        "biome": biome
                    }
                }
            }
        },func)
}

def lazy onLevivation(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:levitation",
                "conditions": {}
            }
        },func)
}
def lazy onLightningStrike(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:lightning_strike",
                "conditions": {}
            }
        },func)
}
def lazy onGenerateLoot(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:player_generates_container_loot",
                "conditions": {}
            }
        },func)
}
def lazy onKilledByCrossbow(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:killed_by_crossbow"
            }
        },func)
}
def lazy onPlacedBlock(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:placed_block",
                "conditions": {}
            }
        },func)
}
def lazy onPlacedBlock(mcobject block, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:placed_block",
                "conditions": {
                    "block": block
                }
            }
        },func)
}

def lazy onHurtEntity(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:player_hurt_entity",
                "conditions": {}
            }
        },func)
}
def lazy onHurtEntity(mcobject ent,void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:player_hurt_entity",
                "conditions": {
                    "entity": [
                        {
                            "condition": "minecraft:entity_properties",
                            "predicate": {
                                "type": ent
                            }
                        }
                        ]
                }
            }
        },func)
}
def lazy onHurtEntity(int $ent, int $nbt, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:player_hurt_entity",
                "conditions": {
                    "entity": [
                        {
                            "condition": "minecraft:entity_properties",
                            "predicate": {
                                "type": ent,
                                "nbt": nbt
                            }
                        }
                        ]
                }
            }
        },func)
}

def lazy onInteractWithEntity(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:player_interacted_with_entity",
                "conditions": {}
            }
        },func)
}
def lazy onInteractWithEntity(mcobject ent, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:player_interacted_with_entity",
                "conditions": {
                    "entity": [
                        {
                            "condition": "minecraft:entity_properties",
                            "predicate": {
                                "type": ent
                            }
                        }
                        ]
                }
            }
        },func)
}
def lazy onInteractWithEntity(mcobject ent, json nbt, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:player_interacted_with_entity",
                "conditions": {
                    "entity": [
                        {
                            "condition": "minecraft:entity_properties",
                            "predicate": {
                                "type": ent,
                                "nbt": nbt
                            }
                        }
                        ]
                }
            }
        },func)
}

def lazy onKillEntity(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:player_killed_entity",
                "conditions": {}
            }
        },func)
}
def lazy onKillEntity(mcobject ent, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:player_killed_entity",
                "conditions": {
                    "entity": [
                        {
                            "condition": "minecraft:entity_properties",
                            "predicate": {
                                "type": ent
                            }
                        }
                        ]
                }
            }
        },func)
}
def lazy onKillEntity(mcobject ent, json nbt, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:player_killed_entity",
                "conditions": {
                    "entity": [
                        {
                            "condition": "minecraft:entity_properties",
                            "predicate": {
                                "type": ent,
                                "nbt": nbt
                            }
                        }
                        ]
                }
            }
        },func)
}

def lazy onSummonEntity(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:summoned_entity",
                "conditions": {}
            }
        },func)
}
def lazy onSummonEntity(mcobject ent, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:summoned_entity",
                "conditions": {
                    "entity": [
                        {
                            "condition": "minecraft:entity_properties",
                            "predicate": {
                                "type": ent
                            }
                        }
                        ]
                }
            }
        },func)
}
def lazy onSummonEntity(mcobject ent, json nbt, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:summoned_entity",
                "conditions": {
                    "entity": [
                        {
                            "condition": "minecraft:entity_properties",
                            "predicate": {
                                "type": ent,
                                "nbt": nbt
                            }
                        }
                        ]
                }
            }
        },func)
}


def lazy onTameEntity(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:tame_animal",
                "conditions": {}
            }
        },func)
}
def lazy onTameEntity(mcobject ent, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:tame_animal",
                "conditions": {
                    "entity": [
                        {
                            "condition": "minecraft:entity_properties",
                            "predicate": {
                                "type": ent
                            }
                        }
                        ]
                }
            }
        },func)
}
def lazy onTameEntity(mcobject ent, json nbt, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:tame_animal",
                "conditions": {
                    "entity": [
                        {
                            "condition": "minecraft:entity_properties",
                            "predicate": {
                                "type": ent,
                                "nbt": nbt
                            }
                        }
                        ]
                }
            }
        },func)
}

def lazy onReciepeUnlock(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:recipe_unlocked",
                "conditions": {}
            }
        },func)
}
def lazy onReciepeUnlock(string reciepe, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:recipe_unlocked",
                "conditions": {
                    "recipe": reciepe
                }
            }
        },func)
}
def lazy onShotCrossbow(void=>void func){
    eventCreate(shot_crossbow,{
            "requirement": {
                "trigger": "minecraft:shot_crossbow",
                "conditions": {
                }
            }
        },func)
}
def lazy onShotCrossbow(mcobject item, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:shot_crossbow",
                "conditions": {
                    "item": {
                        "items": [
                            item
                            ]
                    }
                }
            }
        },func)
}
def lazy onShotCrossbow(mcobject item, json nbt,void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:shot_crossbow",
                "conditions": {
                    "item": {
                        "items": [
                            item
                            ],
                        "nbt": nbt
                    }
                }
            }
        },func)
}
def lazy onSleep(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:slept_in_bed",
                "conditions": {}
            }
        },func)
}
def lazy onSlideDownBlock(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:slide_down_block",
                "conditions": {
                }
            }
        },func)
}
def lazy onSlideDownBlock(mcobject block, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:slide_down_block",
                "conditions": {
                    "block": block
                }
            }
        },func)
}
def lazy onRide(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:started_riding",
                "conditions": {}
            }
        },func)
}

def lazy onVoluntaryExile(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:voluntary_exile",
                "conditions": {
                    "location": {}
                }
            }
        },func)
}

def lazy onLookingAtEntity(mcobject ent, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:tick",
                "conditions": {
                    "player": [
                        {
                            "condition": "minecraft:entity_properties",
                            "entity": "this",
                            "predicate": {
                                "player": {
                                    "looking_at": {
                                        "type": ent
                                    }
                                }
                            }
                        }
                        ]
                }
            }
        },func)
}
def lazy onLookingAtEntity(mcobject ent, json nbt, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:tick",
                "conditions": {
                    "player": [
                        {
                            "condition": "minecraft:entity_properties",
                            "entity": "this",
                            "predicate": {
                                "player": {
                                    "looking_at": {
                                        "type": ent,
                                        "nbt": nbt
                                    }
                                }
                            }
                        }
                        ]
                }
            }
        },func)
}

def lazy onTargetHit(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:target_hit",
                "conditions": {
                }
            }
        },func)
}
def lazy onTargetHit(int power, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:target_hit",
                "conditions": {
                    "player": [],
                    "projectile": [],
                    "signal_strength": power
                }
            }
        },func)
}
def lazy onTargetHit(mcobject proj, int power, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:target_hit",
                "conditions": {
                    "player": [],
                    "projectile": [
                        {
                            "condition": "minecraft:entity_properties",
                            "entity": "this",
                            "predicate": {
                                "type": proj
                            }
                        }
                        ],
                    "signal_strength": power
                }
            }
        },func)
}

def lazy onUsedEnderEye(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:used_ender_eye",
                "conditions": {
                    "player": []
                }
            }
        },func)
}
def lazy onUsedEnderEye(int distance,void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:used_ender_eye",
                "conditions": {
                    "player": [],
                    "distance": distance
                }
            }
        },func)
}
def lazy onUsedEnderEye(int dMin, int dMax, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:used_ender_eye",
                "conditions": {
                    "player": [],
                    "distance": {
                        "min": dMin,
                        "max": dMax
                    }
                }
            }
        },func)
}

def lazy onThrownItemPickupByEntity(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:thrown_item_picked_up_by_entity",
                "conditions": {}
            }
        },func)
}
def lazy onThrownItemPickupByEntity(mcobject ent, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:thrown_item_picked_up_by_entity",
                "conditions": {
                    "player": [],
                    "entity": {
                        "type": ent
                    }
                }
            }
        },func)
}
def lazy onThrownItemPickupByEntity(mcobject ent, mcobject item, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:thrown_item_picked_up_by_entity",
                "conditions": {
                    "player": [],
                    "entity": {
                        "type": ent
                    },
                    "item": {
                        "items": [
                            item
                            ]
                    }
                }
            }
        },func)
}
def lazy onThrownItemPickupByEntity(mcobject ent, json nbt_entity, mcobject item, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:thrown_item_picked_up_by_entity",
                "conditions": {
                    "player": [],
                    "entity": {
                        "type": ent,
                        "nbt": nbt_entity
                    },
                    "item": {
                        "items": [
                            item
                            ]
                    }
                }
            }
        },func)
}
def lazy onThrownItemPickupByEntity(mcobject ent, json nbt_entity, mcobject item, json nbt_item, void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:thrown_item_picked_up_by_entity",
                "conditions": {
                    "player": [],
                    "entity": {
                        "type": ent,
                        "nbt": nbt_entity
                    },
                    "item": {
                        "items": [
                            item
                            ],
                        "nbt": nbt_item
                    }
                }
            }
        },func)
}

def lazy onVillage(void=>void func){
    eventCreate({
            "requirement": {
                "trigger": "minecraft:location",
                "conditions": {
                    "player": [
                        {
                            "condition": "minecraft:entity_properties",
                            "entity": "this",
                            "predicate": {
                                "location": {
                                    "feature": "village"
                                }
                            }
                        }
                        ]
                }
            }
        },func)
}