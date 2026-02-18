import java.util.ArrayList;
import java.util.Scanner;
import java.lang.Thread;
import java.util.Collections;

public class Cambio{
    int HandSize = 4;
    int init_reveal = 2;
    int sleeptime;

    boolean Called = false;
    Deck deck;
    ArrayList<Card> discard;
    Card ActiveCard;    
    Player ActivePlayer;
    ArrayList<Player> roster;
    Scanner sc = new Scanner(System.in);
    
    //InitializeGame sets up the game. 
    //Humans determines how many human players there are, usually either zero or one
    //Machines determines how many machine players there are, any number less than or equal to 10.
    
    public Cambio(){
        discard = new ArrayList<Card>();
        roster = new ArrayList<Player>();
        deck = new Deck();
        for(int n = 0; n < 4; n++){
            String inName = "Computer " + n;
            roster.add(new MachinePlayer(inName));
        }
	}
    
    public Cambio(int humans, int machines){
        discard = new ArrayList<Card>();
        roster = new ArrayList<Player>();
        deck = new Deck();
        if(humans != 0){
            roster.add(new HumanPlayer("HU-MAN"));
        }
        for(int n = humans; n < machines + humans; n++){
            String inName = "Computer " + n;
            roster.add(new MachinePlayer(inName));
        }
	}
    
    public void InitializeGame(int humans, int machines){ 
        deck = new Deck();
        deck.shuffle();
        for(Player play : roster){
            play.roster = roster;
            for(int n = 0; n < HandSize; n++){
                play.AddToHand(deck.draw());
            }
        }
        for(Player play : roster){
            play.Init_memory();
		}
        int reveal = init_reveal;
        for(Player play : roster){
            for(int n = 0; n < reveal; n++){
                play.PeekSelf(n);
            }
            System.out.println();
        }
        discard.add(null);
    }
    
    //The see block of methods is used to communicate to the players what has happened.
    
    
	public void SeeDraw(Player actor){ //Shows that the player has taken a card from the deck
		for(Player play : roster){
			play.SeeDraw(actor, discard.get(0));
		}
	}
	
	public void SeePlaceBlind(Player actor, int index){ //Shows that the player has kept the card from the deck
		for(Player play : roster){
			play.SeePlaceBlind(actor, index, discard.get(0));
		}
	}
	
	public void SeeDecline(Player actor){ //Shows that the player has declined the card from the deck
		for(Player play : roster){
			play.SeeDecline(actor, discard.get(0));
		}
	}
	
	public void SeePullDiscard(Player actor, int index, Card pulled){ //Shows that the player has taked a card from the discard. Shows where they put the discarded card
		for(Player play : roster){
			play.SeePullDiscard(actor, index, discard.get(0), pulled);
		}
	}
	
	public void SeeCambio(Player actor){ //Shows that a player has called Cambio
		for(Player play : roster){
			play.SeeCambio(actor);
		}
	}
	
	public void SeeSwap(Player actor, SwapTarget targ){ //Shows that the positions of two cards have swapped
		for(Player play : roster){
			play.SeeSwap(actor, targ);
		}
	}
	
	public void SeeReveal(Player actor, Target target, Card revealed){ //Shows that a card has been revealed
		for(Player play : roster){
			play.SeeReveal(actor, target, revealed);
		}
	}
	
	public void SeeInterject(Player actor, Target revealed, boolean hit, Card discarded){ //Shows that a player has interjected, putting a card in the discard from someone's hand
		SeeReveal(actor, revealed, discarded);
		for(Player play : roster){
			play.SeeInterject(actor, revealed, hit, discarded);
		}
	}
	
	public void CheckInterject(){ 
		//Gets called every time the top card on the discard gets changed.
		
		// Generates a random ordering to give each player a random chance of getting the interject first. 
		// TODO: When fully independently threaded players happen, give each a random time.
		ArrayList<Player> temproster = (ArrayList<Player>) roster.clone();
		Collections.shuffle(temproster);
		
		for(Player play : temproster){
			Target check = play.CheckInterject(discard.get(0)); //Check to see if that player wants to interject
			if(check != null){
				Card dropped = check.player.hand.get(check.index); 
				if(dropped.face == discard.get(0).face){ //Check if the dropped card matches the discard
					discard.add(0, dropped); 
					check.player.hand.remove(dropped);
					SeeInterject(play, check, true, dropped);
					CheckInterject(); //The discard has changed, so we check again
					return;
				}
				else{
					SeeInterject(play, check, false, dropped);
					play.hand.add(deck.draw());
				}
			}
		}
	}
	
    public void runGame(){ //The main game loop
        int n = 0;
        int turns = 0;
        while(!Called){ //The loop ends when someone calls Cambio, moves to secondary loop
            ActivePlayer = roster.get(n);
            Play();
            n++;
            if(n >= roster.size()){
                n = 0;
            }
            turns++;
        }
        Player LastPlayer = ActivePlayer; //Flag the current player, move to the next player
        n++;
        if(n >= roster.size()){
            n = 0;
        }
        ActivePlayer = roster.get(n);
        while(ActivePlayer != LastPlayer){ //one more loop
            Play();
            n++;
            if(n >= roster.size()){
                n = 0;
            }
            ActivePlayer = roster.get(n);
            turns++;
        }
        EndGame();
    }

    public void EndGame(){ //Print out the status of the game
        for(Player play : roster){
            System.out.println(play);
            System.out.println("\nTotal Score: " + play.GetTotalScore());
        }
        System.out.println(deck);
    }

    public void PlayFromDeck(){ 									//A player chooses to play a card from the deck
		if(deck.cards.size() < 5){ 										//Shuffle the discard pile into the deck
			for(Card card : discard){
				deck.add(card);
			}
			deck.shuffle();
		}
        SeeDraw(ActivePlayer); 											//Everyone sees that the player has chosen to do this
        ActiveCard = deck.draw(); 										//The active card is now the top card of the deck
        int index = ActivePlayer.ChooseDeck(ActiveCard); 				//The player designates the index of one of their cards to replace
		if(index == -1){ 												//if they choose not to keep the card, discard it and use its ability
			discard.add(0, ActiveCard); 
			SeeDecline(ActivePlayer);
			if(ActiveCard.face == CardFace.Queen){
				RevealAndSwap();
			}
			else if(ActiveCard.face == CardFace.Jack){
				BlindSwap();
			}
			else if(ActiveCard.face == 10 || ActiveCard.face == 9){		//TODO: replace magic numbers
				ActivePlayer.PeekOpponent();
			}
			else if(ActiveCard.face == 7 || ActiveCard.face == 8){
				ActivePlayer.PeekSelf();
			}
		}
		else{															//If they chose a valid card index, put the active card in that spot and discard the previous card there
			discard.add(0, ActivePlayer.hand.get(index));
			ActivePlayer.hand.set(index, ActiveCard);
			SeePlaceBlind(ActivePlayer, index);
		}
    
    }
    
    public void RevealAndSwap(){									//If they played a Queen, they designate two cards to swap, which are then revealed
		SwapTarget target = ActivePlayer.RevealAndSwap();
		SeeSwap(ActivePlayer, target);
		SeeReveal(ActivePlayer, target.source, target.source.player.hand.get(target.source.index));
		SeeReveal(ActivePlayer, target.target, target.target.player.hand.get(target.target.index));
	}
	
	public void BlindSwap(){										//If they played a Jack, they designate two cards to swap, but they are not revealed
		SwapTarget target = ActivePlayer.RevealAndSwap();
		SeeSwap(ActivePlayer, target);
	}
    
    public void PlayFromDiscard(){								
        int back = ActivePlayer.ChooseDiscard(discard.get(0));			//The player picks one of their cards (They must choose a valid card for this to pass)
        Card returned = ActivePlayer.hand.get(back);					//Swap it with the top of the discard pile
        Card pulled = discard.remove(0);
        ActivePlayer.hand.set(back, pulled);
        discard.add(0, returned);
        SeePullDiscard(ActivePlayer, back, pulled);						//Everyone sees it
    }

    public void CallCambio(){									//Sets the Called flag to true, triggering the end of the cycle
        SeeCambio(ActivePlayer);
        Called = true;
    }    

    public void Play(){											//The active player chooses which action to take
        System.out.println();
        //System.out.println(discard.get(0));
        String action;
        if(discard.size() > 0){											//Sanity clause to make sure nobody tries to pull a null from the discard
            action = ActivePlayer.Choose(discard.get(0));
        }
        else{
            action = ActivePlayer.Choose(null);
        }
        if(action.equals("deck")){										
            PlayFromDeck();
			CheckInterject();
        }    
        else if(action.equals("discard")){
            PlayFromDiscard();
			CheckInterject();
        }
        else if(action.equals("cambio")){
            CallCambio();
        }
        else{
            Play();														//If they pick something other than these three, ask again
        }
        try{
            Thread.sleep(sleeptime);									//Ensure the game doesn't run at lightning speed
        }
        catch (Exception e){											
            System.out.println(e);
        }
    }

    public void main(String[] args){
		int human = 1;
		int machines = 4;
		//if(args.length <= 0){
			//machines = 4;
			//sleeptime = 16;
		//}
		//if(args.length == 1){
			//machines = Integer.parseInt(args[0]);
			//sleeptime = 16;
		//}
		//if(args.length == 2)		{
			//human = Integer.parseInt(args[0]);
			//machines = Integer.parseInt(args[1]);
			//sleeptime = 2000;
	    //}
	    //else{
			//System.out.println("Too many arguments");
			//return;
		//}       
		
		Cambio game = new Cambio(human, machines);
        game.InitializeGame(human, machines);
        game.runGame();
        
    }
}
