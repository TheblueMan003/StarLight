package mc.java.villager

import cmd.java.data as data

"""
Set the job site of the current villager to the given coordinates in the overworld.
"""
def lazy setJobSite(int x, int y, int z){
    data.set({Brain: {memories: {"minecraft:job_site": {value: {pos: [x, y, z], dimension: "minecraft:overworld"}}}}})
}

"""
Set the job site of the current villager to the given coordinates and dimension.
"""
def lazy setJobSite(int x, int y, int z, mcobject dim){
    data.set({Brain: {memories: {"minecraft:job_site": {value: {pos: [x, y, z], dimension: dim}}}}})
}

"""
Clear the trade offers of the current villager.
"""
def lazy clearTrade(){
    data.set({Offers:{Recipes:[]}})
}

"""
Add a trade offer to the current villager.
"""
def lazy addTrade(mcobject item1, int count1, mcobject res, int countr){
    data.append("Offers.Recipes", {maxUses: 999999, buyB: {id: "minecraft:air", Count: 1b}, buy: {id: item1, Count: count1}, sell: {id: res, Count: countr}})
}

"""
Add a trade offer to the current villager.
"""
def lazy addTrade(int max, mcobject item1, int count1, mcobject res, int countr){
    data.append("Offers.Recipes", {maxUses: max, buyB: {id: "minecraft:air", Count: 1b}, buy: {id: item1, Count: count1b}, sell: {id: res, Count: countr}})
}

"""
Add a trade offer to the current villager.
"""
def lazy addTrade(mcobject item1, int count1,mcobject item2, int count2, mcobject res, int countr){
    data.append("Offers.Recipes", {maxUses: 999999, buyB: {id: item2, Count: count2}, buy: {id: item1, Count: count1}, sell: {id: res, Count: countr}})
}

"""
Add a trade offer to the current villager.
"""
def lazy addTrade(int max, mcobject item1, int count1, mcobject item2, int count2, mcobject res, int countr){
    data.append("Offers.Recipes",{maxUses: max, buyB: {id: item2, Count: count2}, buy: {id: item1, Count: count1}, sell: {id: res, Count: countr}})
}


"""
Set the profession of the current villager.
"""
def lazy setProfession(mcobject prof){
    data.set({VillagerData: {profession: prof}})
}

"""
Set the level of the current villager.
"""
def lazy setLevel(int level){
    data.set({VillagerData: {level: level}})
}

"""
Set the biome of the current villager.
"""
def lazy setBiome(mcobject biome){
    data.set({VillagerData: {type: biome}})
}