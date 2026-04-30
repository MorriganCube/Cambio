import java.util.ArrayList;
public class Profile{
	Player play;
	ArrayList<Memory> memories;
	int GuessWorst;
	int GuessAverage;
	double attention;

	public String toString(){
		return memories.toString();
	}

	public Profile(Player play, double attention){
		this.play = play;
		this.GuessWorst = 13;
		this.GuessAverage = 7;
        this.attention = attention;
		this.memories = new ArrayList<Memory>();
		
	}

    public void InitializeMemory(){
        for(int n = 0; n < play.hand.size(); n++){
			memories.add(GuessFromAverage(n));
		}
    }   
	    
	public void SeeDrop(int index){
		for(int n = 0; n < memories.size(); n++){
			if(memories.get(n).index == index){
				memories.remove(n);
				memories.get(n).index--;
			}
			else if(memories.get(n).index > index){
				memories.get(n).index--;
				//memories.get(n).confidence -= 50.0;
			}
		}
	}
	
	public void SeePickup(){
		memories.add(GuessFromAverage(memories.size()));
	}
	
	public Memory GuessFromAverage(int index){
		Memory out = new Memory(this, index, new Card(GuessAverage), 0.0);
		return out;
	}
	
	public int GuessFromIndex(int index){
		return(GuessFromMemory(memories.get(index)));

	}

	public int GuessFromMemory(Memory mem){
		int out = mem.Recall();
		if(out == -100) return GuessAverage;
		else return out;
	}

	public int GuessFaceFromIndex(int index){
		return(memories.get(index).RecallFace());
	}

	public int GuessFaceFromMemory(Memory mem){
		return(mem.RecallFace());
	}
	
	public int GuessScore(){
		int score = 0;
		for(Memory mem : memories){
			score += GuessFromMemory(mem);
		}
		return score;
	}
	
	public int GuessBest(){
		int best = -1;
		int val = 20;
		for(int n = 0; n < memories.size(); n++){
			int guess = GuessFromIndex(n);
			if(guess < val){
				val = guess;
				best = n;
			}
		}
		return best;
	}
	
	public int GuessWorst(){
		int best = -1;
		int val = -5;
		for(int n = 0; n < memories.size(); n++){
			int guess = GuessFromIndex(n);
			if(guess > val){
				val = guess;
				best = n;
			}
		}
		return best;
	}
	
    public void LearnCard(int index, Card cards, double confidence){
		//System.out.println(confidence);
		Memory out = new Memory(this, index, cards, confidence);
		//System.out.println(out);
		memories.set(index, out);
    }    
    
    public void ForgetCard(int index){
		memories.set(index, GuessFromAverage(index));
	}
    
    public void ForgetCard(Card card){
		for(Memory mem : memories){
			if(mem.card == card){
				memories.set(mem.index, GuessFromAverage(mem.index));
			}
		}
	}
	
	public void reeval(){
		for(int n = 0; n < memories.size(); n++){
			if(memories.get(n).confidence <= 25){
				memories.set(n, new Memory(this, n, GuessAverage, 1000.0)); //TODO: Replace
			}
		}
	}

}
