import java.util.Scanner;
public class HumanPlayer extends Player{

    private Scanner sc;
    public HumanPlayer(String name){
        super(name);
        sc = new Scanner(System.in);
    }
    public void Init_memory(){}
    
	public void SeeDraw(Player actor, Card Forgone){
		System.out.println(actor.name + " pulls a card from the top of the deck");
	}
	
	public void SeePlaceBlind(Player actor, int index, Card discarded){
		System.out.println(actor.name + " puts the card in place " + index + " and puts the " + discarded + " in the discard pile");
	}
	
	public void SeeDecline(Player actor, Card discarded){
		System.out.println(actor.name + " puts the " + discarded + " in the discard pile");
	}
	
	public void SeePullDiscard(Player actor, int index, Card pulled, Card discarded){
		System.out.println(actor.name + " pulls the " + pulled + " from the discard, placing it in place " + index + ", putting the " + discarded + " in the discard pile");
	}
	
	public void SeeCambio(Player actor){
		System.out.println(actor.name + " has called Cambio! Last card, make it count");
	}
	
	public void SeeSwap(Player actor, SwapTarget targets){
		if(targets.source.player == actor){
			System.out.print(actor + " swaps their card " + targets.source.index  + " with ");
		}
		else{
			System.out.print(actor + " swaps " + targets.source.player + "'s card " + targets.source.index + " with ");
		}
		if(actor == targets.target.player){
			System.out.println(" their own card " + targets.target.index);
		}
		else{
			System.out.println(targets.target.player + "'s card " + targets.target.index);
		}
	}
	
	public void SeeReveal(Player actor, Target target, Card revealed){
		if(actor == target.player){
			System.out.println(actor + " has flipped over their own card " + target.index + " revealing the " + revealed);
		}
		else{
			System.out.println(actor + " has flipped over " + target.player + "'s card " + target.index + " revealing the " + revealed);
		}
	}
	
    public int PeekSelf(){
        System.out.println("Which of your cards would you like to see?");
        int lookAt = sc.nextInt();
        PeekSelf(lookAt);
        return lookAt;
    }

    public void PeekCard(Card input){
        System.out.println(input);
    }

    public void PeekSelf(int index){
        System.out.println(hand.get(index));
    }

    public String Choose(Card disc){
        String choice = sc.nextLine().toLowerCase();
        return choice;
    }

    public Target PeekOpponent(){
        System.out.print("Which opponent do you want to spy on? ");
        Player target = roster.get(sc.nextInt());
        int index = -1;
        if(target == this){
            System.out.println("Cheeky fucker, pick someone else");
            return(this.PeekOpponent());
        }
        else{
            System.out.print("Which card? ");
            index = sc.nextInt();
            System.out.println(target.hand.get(index));
        }
        return (new Target(target, index));
    }
    
    public SwapTarget BlindSwap(){
        Player firstplayer = this;
        System.out.print("Your card index: ");
        int firstindex = sc.nextInt();

        System.out.print("Target player: ");
        Player secondplayer = roster.get(sc.nextInt());
        System.out.print("Target card index: ");
        int secondindex = sc.nextInt();
        
        return (new SwapTarget(new Target(this, firstindex), new Target(secondplayer, secondindex)));
        
    }

    public int ChooseDeck(Card input){
        System.out.println("Where do you want to put the " + input.name + "? (D for decline)");
        String userIn = sc.nextLine();
        if(userIn.equals("D")){
            return -1;
        }
        else{
            int index = Integer.parseInt(userIn);
            if(index > hand.size()){
                System.out.println("Not a valid replacement, declining swap");
                return -1;
            }
            return index;
        }
    }
    
    public int ChooseDiscard(Card input){
        int target = ChooseDeck(input);
		if(target == -1){
			System.out.println("Invalid choice, you MUST choose");
		}
		return(ChooseDiscard(input));
	}
    
    public SwapTarget RevealAndSwap(){
        System.out.print("First card player: ");
        Player firstplayer = roster.get(sc.nextInt());
        System.out.print("First card index: ");
        int firstindex = sc.nextInt();

        System.out.print("Second card player: ");
        Player secondplayer = roster.get(sc.nextInt());
        System.out.print("Second card index: ");
        int secondindex = sc.nextInt();
        
        return (new SwapTarget(new Target(firstplayer, firstindex), new Target(secondplayer, secondindex)));
    }
}
