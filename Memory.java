public class Memory{
    Profile parent;
    int index;
    Card card;
    boolean hard;
    double confidence; 
    //A confidence stat. When it hits zero it is forgotten. 
    //If it's below 100 it is likely to be misremembered.
    
    public Memory(Profile parent, int index, Card card, double confidence){
        this.parent = parent;
        this.index = index;
        this.card = card;
        this.hard = true;
        this.confidence = confidence;
    }
    
    public Memory(Profile parent, int index, int value, double confidence){
		this.parent = parent;
		this.index = index;
		this.card = new Card(value);
		this.hard = false;
        this.confidence = confidence;
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
