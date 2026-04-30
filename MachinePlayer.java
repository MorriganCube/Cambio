import java.lang.Math;
import java.util.ArrayList;
import java.util.HashMap;
public class MachinePlayer extends Player{
    
    HashMap<Player, Profile> profiles;
    
    int target_score;
    double SelfConfidence;
    double PeekConfidence;
    double RevealConfidence;
    double DiscPlaceConfidence;
    double GuessConfidence;    
    double BaseConfidence;

    double SwapLoss;
    double TimeLoss;
    
    public MachinePlayer(String name){ //default constructor, based on medium skill level
        super(name);
        target_score = 8;

        double difficulty = 1.0;

        SelfConfidence = difficulty * 1000.0;
        PeekConfidence = difficulty * 100.0;
        RevealConfidence = difficulty * 75.0;
        DiscPlaceConfidence = difficulty * 35.0;
        GuessConfidence = 0.0;
        BaseConfidence = 0.0;
        SwapLoss = 500.0 / difficulty;
        TimeLoss = 50.0 / difficulty;
    }

    public MachinePlayer(String name, double difficulty){ 
		//difficulty is a modifier on recall odds. 
		//Initialize the recall odds values based on the difficulty.
        super(name);
        if(difficulty < 0.1){
            difficulty = 0.1;
        }
        if(difficulty > 2.0){
            difficulty = 2.0;
        }

        SelfConfidence = difficulty * 1000.0;
        PeekConfidence = difficulty * 100.0;
        RevealConfidence = difficulty * 75.0;
        DiscPlaceConfidence = difficulty * 35.0;
        GuessConfidence = 0.0;
        BaseConfidence = 0.0;
        SwapLoss = 500.0 / difficulty;
        TimeLoss = 50.0 / difficulty;
        
    }
    
    public void Init_memory(){
        profiles = new HashMap<Player, Profile>();
        for(Player play : roster){
            Profile newin = new Profile(play, BaseConfidence * (1 + 0.05 * Math.random()));
            newin.InitializeMemory();
			profiles.put(play, newin);
            
		}
	}
    
    public String toString(){
		String out = super.toString();
		out += "\nKnows: ";
		//out += profiles.toString();
		return out;
	}
	
	public void SeeDraw(Player actor, Card Forgone){
		if(Forgone != null){
		profiles.get(actor).GuessWorst = Forgone.value + 1;
	}
	}
	
	public void SeePlaceBlind(Player play, int index, Card discarded){
		ForgetCard(play, index);
	}
	
	public void SeeDecline(Player actor, Card discarded){
		profiles.get(actor).GuessWorst = discarded.value;
		profiles.get(actor).GuessAverage = discarded.value / 2;
		profiles.get(actor).reeval();
	}
	
	public void SeePullDiscard(Player actor, int index, Card discarded, Card pulled){
		profiles.get(actor).GuessWorst = discarded.value;
		profiles.get(actor).GuessAverage = discarded.value / 2;
		profiles.get(actor).reeval();
	}
	
	public void SeeCambio(Player actor){

	}
	
	public void SeeSwap(Player actor, SwapTarget targets){
		Profile SourceProf = profiles.get(targets.source.player);
		int SourceIndex = targets.source.index;
		Profile TargetProf = profiles.get(targets.target.player);
		int TargetIndex = targets.target.index;
		
		Memory hold = SourceProf.memories.get(SourceIndex);
		SourceProf.memories.set(SourceIndex, TargetProf.memories.get(TargetIndex));
		TargetProf.memories.set(TargetIndex, hold);
		
		SourceProf.reeval();
	    TargetProf.reeval();
	}
	
	public void SeeReveal(Player actor, Target target, Card revealed){
		profiles.get(target.player).LearnCard(target.index, revealed, RevealConfidence);
	}
	
	public void SeeInterject(Player actor, Target target, boolean Hit, Card dropped){
		if(Hit){
			if(actor == target.player){
				profiles.get(target.player).SeeDrop(target.index);
			}
			else{
				profiles.get(target.player).ForgetCard(target.index);
			}
		}
		else{
			profiles.get(actor).SeePickup();
		}
	}
	
	public Target CheckInterject(Card discard){
		Target targ = CheckMemory(discard);
		return targ;
	}
	
	public Target CheckMemory(Card search){

        //System.out.println(this.name);
		for(Player play : roster){
			Profile prof = profiles.get(play);
			for(Memory mem : prof.memories){
                //System.out.println(mem);
				if(prof.GuessFaceFromMemory(mem) == search.face && mem.confidence >= 150.0){
					return(new Target(prof.play, mem.index));
				}
			}
		}
		return null;
	}
	
    public String Choose(Card disc){
        Profile profile = profiles.get(this);
        int total = profile.GuessScore();
        if(disc == null){
            return "deck";
        }
        else if(disc.value <= profile.memories.get(profile.GuessBest()).card.value){
			return "discard";
		}
        if(total < target_score){
            return "cambio";
        }
        return "deck";
    }

    public void LearnCard(Player player, int index, Card cards, double confidence){
        profiles.get(player).LearnCard(index, cards, confidence);
    }
    
    public void ForgetCard(Player player, int index){
        profiles.get(player).ForgetCard(index);
    }

    public void ForgetCard(Card card){
        for(Player play : roster){
			Profile prof = profiles.get(play);
			prof.ForgetCard(card);
			
        }
    }

    public Player GuessBestOpp(){
        int val = 1000;
        Player target = this;
        for(Player play : roster){
            if(play != this && play.hand.size() > 0){
				Profile prof = profiles.get(play);
				int score = prof.GuessScore();
				if(score < val){
					target = play;
					val = score;
				}
			}
        }
        return target;
    }

    public Player GuessBestPlayer(){
        int val = 1000;
        Player target = this;
        for(Player play : roster){
			Profile prof = profiles.get(play);
			int score = prof.GuessScore();
			if(score < val){
				target = prof.play;
				val = score;
			}
		}
        return target;
    }

    public Player GuessWorstPlayer(){
        int val = -10;
        Player target = this;
        for(Player play : roster){
			Profile prof = profiles.get(play);
			int score = prof.GuessScore();
			if(score > val){
				target = play;
				val = score;
			}
		}
        return target;
    }
    
    public int PeekSelf(){
        for(int n = 0; n < hand.size(); n++){
            boolean learned = false;
            for(int k = 0; k < hand.size(); k++){
                Memory mem = profiles.get(this).memories.get(k);
                if(mem.card.equals(hand.get(n))){
                    learned = true;
                }
            }
            if(!learned){
                PeekSelf(n);
                return n;
            }
        }
        return -1;
    }
    
    public void PeekSelf(int index){
        System.out.println(name + " looks at their card " + index);
        LearnCard(this, index, hand.get(index), SelfConfidence);
    }

    public Target PeekOpponent(){
        int playIndex = (int)(Math.random() * roster.size());
        System.out.println(playIndex);
        Player play = roster.get(playIndex);
        if(play == this){
            return PeekOpponent();
        }
        else{
            int cardIndex = (int)(Math.random() * play.hand.size());
            System.out.println(name + " looks at " + play.name + "'s card " + cardIndex);
            LearnCard(play, cardIndex, play.hand.get(cardIndex), PeekConfidence);
			return(new Target(play, cardIndex));
        } 
    }
    
    public SwapTarget BlindSwap(){
        
        Player opp = GuessBestOpp();
        int desired = profiles.get(opp).GuessBest();
        int garbo = profiles.get(this).GuessWorst();
        
        return (new SwapTarget(new Target(this, garbo), new Target(opp, desired)));
    }

    public int ChooseDeck(Card input){
        Profile profile = profiles.get(this);
        int best = profile.GuessBest();
        if(input.value < profile.GuessFromIndex(best)){
            return(best);
        }
        return -1;
        
    }
    
    public int ChooseDiscard(Card input){
        Profile profile = profiles.get(this);
        int best = profile.GuessBest();
        return best;
        
    }
    
    public SwapTarget RevealAndSwap(){
        Player opp = GuessBestOpp();
        int desired = profiles.get(opp).GuessBest();
        int garbo = profiles.get(this).GuessWorst();
        
        return (new SwapTarget(new Target(this, garbo), new Target(opp, desired)));
    }
}
