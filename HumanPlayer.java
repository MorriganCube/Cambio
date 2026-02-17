import java.util.Scanner;
import java.util.concurrent.*;
public class HumanPlayer extends Player{

    private Scanner sc;
    public HumanPlayer(String name){
        super(name);
        sc = new Scanner(System.in);
    }
    
    public void Init_memory(){
		}
    
	public void SeeDraw(Player actor, Card Forgone){
		System.out.println(actor.name + " pulls a card from the top of the deck");
	}
	
	public void SeePlaceBlind(Player actor, int index, Card discarded){
		System.out.println(actor.name + " puts the card in place " + index + " and puts the " + discarded + " in the discard pile");
	}
	
	public void SeeDecline(Player actor, Card discarded){
		System.out.println(actor.name + " puts the " + discarded + " in the discard pile");
	}
	
	public void SeePullDiscard(Player actor, int index, Card discarded, Card pulled){
		System.out.println(actor.name + " pulls the " + pulled + " from the discard, placing it in place " + index + ", putting the " + discarded + " in the discard pile");
	}
	
	public void SeeCambio(Player actor){
		System.out.println(actor.name + " has called Cambio! Last card, make it count");
	}
	
	public void SeeSwap(Player actor, SwapTarget targets){
		if(targets.source.player == actor){
			System.out.print(actor.name + " swaps their card " + targets.source.index  + " with ");
		}
		else{
			System.out.print(actor.name + " swaps " + targets.source.player.name + "'s card " + targets.source.index + " with ");
		}
		if(actor == targets.target.player){
			System.out.println(" their own card " + targets.target.index);
		}
		else{
			System.out.println(targets.target.player.name + "'s card " + targets.target.index);
		}
	}
	
	public void SeeReveal(Player actor, Target target, Card revealed){
		if(actor == target.player){
			System.out.println(actor.name + " has flipped over their own card " + target.index + " revealing the " + revealed);
		}
		else{
			System.out.println(actor.name + " has flipped over " + target.player.name + "'s card " + target.index + " revealing the " + revealed);
		}
	}
	
	public void SeeInterject(Player actor, Target target, boolean Hit, Card dropped){
		if(Hit){
			System.out.println(". It's a hit!");
		}
		else{
			System.out.println(". It's a miss! They gain a new card");
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
		System.out.println("Your turn: ");
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
		return(ChooseDiscard(input));
		}
		else return target;
	}
	
	public Target CheckInterject(Card disc){
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<Target> future = executor.submit(() -> {
			if(sc.hasNextInt()){
			int target_player = -1;
			int target_card = -1;
			Player play;
			
			target_player = sc.nextInt();
			target_card = sc.nextInt();
			
			if(target_player < 0 || target_player >= roster.size()){
				return null;
			}
			else{
				play = roster.get(target_player);
			}
			if(target_card < 0 || target_card >= play.hand.size()){
				return null;
			}
			else{
				return(new Target(play, target_card));
			}
		}
		else return null;
		});
		
		try {
			return future.get(5000, TimeUnit.MILLISECONDS);
		} catch (TimeoutException e) {
			future.cancel(true);
			return null;
		} catch (Exception e){
			e.printStackTrace();
			System.exit(0);
		} finally {
			//sc.close();
			this.sc = new Scanner(System.in);
			executor.shutdown();
		}
		return null;
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
