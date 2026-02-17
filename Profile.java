import java.util.ArrayList;
public class Profile{
	Player play;
	ArrayList<Memory> memories;
	int GuessWorst;
	int GuessAverage;
	
	public String toString(){
		return memories.toString();
	}

	public Profile(Player play){
		this.play = play;
		this.GuessWorst = 13;
		this.GuessAverage = 7;
		this.memories = new ArrayList<Memory>();
		for(int n = 0; n < play.hand.size(); n++){
			memories.add(Guess(n));
		}
	}
	
	public void SeeDrop(int index){
		for(int n = 0; n < memories.size(); n++){
			if(memories.get(n).index == index){
				memories.remove(n);
			}
			else if(memories.get(n).index > index){
				memories.get(n).index--;
			}
		}
	}
	
	public void SeePickup(){
		memories.add(Guess(memories.size()));
	}
	
	public Memory Guess(int index){
		Memory out = new Memory(this, index, new Card(GuessAverage));
		return out;
	}
	
	public int GetCard(int index){
		return memories.get(index).card.value;
	}
	
	public int GuessScore(){
		int score = 0;
		for(Memory mem : memories){
			score += mem.card.value;
		}
		return score;
	}
	
	public int GuessBest(){
		int best = -1;
		int val = 20;
		for(int n = 0; n < memories.size(); n++){
			if(memories.get(n).card.value < val){
				val = memories.get(n).card.value;
				best = n;
			}
		}
		return best;
	}
	
	public int GuessWorst(){
		int best = -1;
		int val = -5;
		for(int n = 0; n < memories.size(); n++){
			if(memories.get(n).card.value > val){
				val = memories.get(n).card.value;
				best = n;
			}
		}
		return best;
	}
	
    public void LearnCard(int index, Card card, double Odds){
        if(Math.random() < Odds){
            memories.set(index, new Memory(this, index, card));
        }
    }    
    
    public void LearnCard(int index, int value, double Odds){
        if(Math.random() < Odds){
            memories.set(index, new Memory(this, index, value));
        }
    }
    
    public void ForgetCard(int index){
		memories.set(index, Guess(index));
	}
    
    public void ForgetCard(Card card){
		for(Memory mem : memories){
			if(mem.card == card){
				memories.set(mem.index, Guess(mem.index));
			}
		}
	}
	
	public void reeval(){
		for(int n = 0; n < memories.size(); n++){
			if(!memories.get(n).hard){
				memories.set(n, new Memory(this, n, GuessAverage));
			}
		}
	}

}
