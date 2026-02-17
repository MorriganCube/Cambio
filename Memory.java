public class Memory{
    Profile parent;
    int index;
    Card card;
    boolean hard;
    
    public Memory(Profile parent, int index, Card card){
        this.parent = parent;
        this.index = index;
        this.card = card;
        this.hard = true;
    }
    
    public Memory(Profile parent, int index, int value){
		this.parent = parent;
		this.index = index;
		this.card = new Card(value);
		this.hard = false;
	}
    
    public String toString(){
		String out = parent.play.name;
		out += " has the ";
		out += card.name;
		out += " at position ";
		out += index;
		return out;
	}
}
