package game.score

"""
Return the entities within `selector` that has the biggest `score`
"""
def lazy entity winner(entity selector, int score){
	entity winner
	int previous = int.minValue
	as(selector){
		if (score > previous){
			previous = score
			winner = @s
		}
		else if (score == previous){
			winner += @s
		}
	}
    return winner
}

"""
Return the entities within `selector` that has the smallest `score`
"""
def lazy loser(entity selector, int score){
	entity winner
	int previous = int.maxValue
	as(selector){
		if (score < previous){
			previous = score
			winner = @s
		}
		else if (score == previous){
			winner += @s
		}
	}
}