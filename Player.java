import java.util.ArrayList;
public abstract class Player{
	//An abstract for players. 
	//TODO: Introduce independent threads
    public ArrayList<Card> hand;
    public ArrayList<Player> roster;
    public String name;

    public Player(String name){
        this.name = name;
        hand = new ArrayList<Card>();
    }

    public void AddToHand(Card in){
        hand.add(in);
    }

    public int GetTotalScore(){
        int out = 0;
        for(Card card : hand){
            out += card.value;
        }
        return out;
    }
    
    public String toString(){
		String out = name + "\n";
		out += hand.toString();
		return out;
	}    
	abstract void SeeDraw(Player actor, Card Forgone);
	abstract void SeePlaceBlind(Player play, int index, Card discarded);
	abstract void SeeDecline(Player actor, Card discarded);
	abstract void SeePullDiscard(Player actor, int index, Card discarded, Card pulled);
	abstract void SeeCambio(Player actor);
	abstract void SeeSwap(Player actor, SwapTarget targets);
	abstract void SeeReveal(Player actor, Target target, Card revealed);
	abstract void SeeInterject(Player actor, Target target, boolean hit, Card dropped);
	abstract Target CheckInterject(Card discard);
	
    abstract void PeekCard(Card input);
    abstract String Choose(Card disc);
    abstract SwapTarget RevealAndSwap();
    abstract SwapTarget BlindSwap();
    abstract Target PeekOpponent();
    abstract int PeekSelf();
    abstract void PeekSelf(int index);
    abstract int ChooseDeck(Card input);
    abstract int ChooseDiscard(Card input);
    abstract void Init_memory();
}
