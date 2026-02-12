import java.lang.Math;
import java.util.ArrayList;
import java.util.HashMap;
public class MachinePlayer extends Player{
    static double base_recall_odds_good = 0.15;
    static double base_recall_odds_mid = 0.55;
    static double base_recall_odds_bad = .85;

    int memory_cap;
    double recall_odds_good;
	double recall_odds_mid;
	double recall_odds_bad;
    double learn_odds;
    double forget_odds;

    
    HashMap<Player, Profile> profiles;
    
    int target_score;
    
    public MachinePlayer(String name){
        super(name);
        memory_cap = 10;
        recall_odds_good = 1.0 - base_recall_odds_good;
        recall_odds_mid = .45;
        recall_odds_bad = .15;
        learn_odds = .75;
        forget_odds = .5;
        target_score = 8;
    }

    public MachinePlayer(String name, double difficulty){ //difficulty is a modifier on recall odds. 
        super(name);
        if(difficulty < 0.1){
            difficulty = 0.1;
        }
        if(difficulty > 2.0){
            difficulty = 2.0;
        }
        memory_cap = (int)(10 * difficulty);
        recall_odds_good = 1.0 - (.15 / difficulty);
        recall_odds_mid = 1.0 - (.55 / difficulty);
        recall_odds_bad = 1.0 - (.85 / difficulty);
        if(recall_odds_good < 0.25){
            recall_odds_good = .25;
        }
        if(recall_odds_mid < .10){
            recall_odds_mid = .10;
        }
        if(recall_odds_bad < 0.0){
            recall_odds_bad = 0.0;
        }
    }
    
    public void Init_memory(){
        profiles = new HashMap<Player, Profile>();
        for(Player play : roster){
			profiles.put(play, new Profile(play));
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
	
	public void SeePullDiscard(Player actor, int index, Card pulled, Card discarded){

	}
	
	public void SeeCambio(Player actor){

	}
	
	public void SeeSwap(Player actor, SwapTarget targets){

	}
	
	public void SeeReveal(Player actor, Target target, Card revealed){

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

    public void PeekCard(Card input){
    }

    public void LearnCard(Player player, int index, Card card, double Odds){
        profiles.get(player).LearnCard(index, card, Odds);
    }
    
    public void LearnCard(Player player, int index, int value, double Odds){
		profiles.get(player).LearnCard(index, value, Odds);
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
            if(play != this){
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
        LearnCard(this, index, hand.get(index), recall_odds_good);
    }

    public Target PeekOpponent(){
        int playIndex = (int)(Math.random() * roster.size());
        System.out.println(playIndex);
        Player play = roster.get(playIndex);
        if(play == this){
            return PeekOpponent();
        }
        else{
            int cardIndex = (int)(Math.random() * roster.size());
            System.out.println(name + " looks at " + play.name + "'s card " + cardIndex);
            LearnCard(play, cardIndex, play.hand.get(cardIndex), recall_odds_mid);
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
        if(input.value < profile.GetCard(best)){
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
