public class Target{
Player player;
int index;

	public Target(Player player, int index){
		this.player = player;
		this.index = index;
	}
	
	public Target(){
		player = null;
		index = -1;
	}
	
	public Card GetCard(){
		if(player != null){
			if(index >= 0 && index < player.hand.size()) return player.hand.get(index);
		}
		return null;
	}
}
