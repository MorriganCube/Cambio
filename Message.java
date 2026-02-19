public class Message{
	public Player actor;
	public Target targ1;
	public Target targ2;
	public Card card1;
	public Card card2;
	public int flag = 0;

	public Message(Player actor){ //A player giving a response to indicate their presence
		this.actor = actor;
	}
	
	public Message(int flag){ //The game messages a player
		this.flag = flag;
	}
	
	public Message(int flag, Card discard){ //The game messages a player, including a card
		this.flag = flag;
		this.card1 = discard;
	}
	
	public Message(Player actor, int flag, Target targ){ //A player giving a response designating a card
		this.actor = actor;
		this.targ1 = targ;
		this.flag = flag;
	}
	
	public Message(Player actor, int flag){ //A player giving a response indicating a flag, such as a turn action
		this.actor = actor;
		this.flag = flag;
	}
	
	public Message(Player actor, int flag, Target targ){ //A player giving a response indicating a 
		this.actor = actor;
		this.flag = flag;
		this.targ1 = targ;
	}

}

