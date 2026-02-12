import java.util.ArrayList;
import java.util.Scanner;
import java.lang.Thread;

public class Cambio{
    static int HandSize = 4;
    static int init_reveal = 2;
    static int sleeptime;

    static boolean Called = false;
    static Deck deck;
    static ArrayList<Card> discard;
    static Card ActiveCard;    
    static Player ActivePlayer;
    static ArrayList<Player> roster;
    static Scanner sc = new Scanner(System.in);
    
    public static void InitializeGame(int humans, int machines){
        deck = new Deck();
        deck.shuffle();
        discard = new ArrayList<Card>();
        roster = new ArrayList<Player>();
        if(humans != 0){
            roster.add(new HumanPlayer("HU-MAN"));
        }
        for(int n = humans; n < machines + humans; n++){
            String inName = "Computer " + n;
            roster.add(new MachinePlayer(inName));
        }
        for(Player play : roster){
            play.roster = roster;
            for(int n = 0; n < HandSize; n++){
                play.AddToHand(deck.draw());
            }
        }
        for(Player play : roster){
            play.Init_memory();
		}
    }
    
	public static void SeeDraw(Player actor){
		for(Player play : roster){
			play.SeeDraw(actor, discard.get(0));
		}
	}
	
	public static void SeePlaceBlind(Player actor, int index){
		for(Player play : roster){
			play.SeePlaceBlind(actor, index, discard.get(0));
		}
	}
	
	public static void SeeDecline(Player actor){
		for(Player play : roster){
			play.SeeDecline(actor, discard.get(0));
		}
	}
	
	public static void SeePullDiscard(Player actor, int index, Card pulled){
		for(Player play : roster){
			play.SeePullDiscard(actor, index, discard.get(0), pulled);
		}
	}
	
	public static void SeeCambio(Player actor){
		for(Player play : roster){
			play.SeeCambio(actor);
		}
	}
	
	public static void SeeSwap(Player actor, SwapTarget targ){
		for(Player play : roster){
			play.SeeSwap(actor, targ);
		}
	}
	
	public static void SeeReveal(Player actor, Target target, Card revealed){
		for(Player play : roster){
			play.SeeReveal(actor, target, revealed);
		}
	}
	
    public static void runGame(){
        int n = 0;
        int turns = 0;
        while(!Called){
            ActivePlayer = roster.get(n);
            Play();
            n++;
            if(n >= roster.size()){
                n = 0;
            }
        }
        Player LastPlayer = ActivePlayer;
        n++;
        if(n >= roster.size()){
            n = 0;
        }
        ActivePlayer = roster.get(n);
        while(ActivePlayer != LastPlayer){
            Play();
            n++;
            if(n >= roster.size()){
                n = 0;
            }
            ActivePlayer = roster.get(n);
        }
        EndGame();
    }

    public static void EndGame(){
        for(Player play : roster){
            System.out.println(play);
            System.out.println("\nTotal Score: " + play.GetTotalScore());
        }
        System.out.println(deck);
    }

    public static void PlayFromDeck(){
		if(deck.cards.size() < 5){
			for(Card card : discard){
				deck.add(card);
			}
			deck.shuffle();
		}
        SeeDraw(ActivePlayer);
        ActiveCard = deck.draw();
        int index = ActivePlayer.ChooseDeck(ActiveCard);
		if(index == -1){
			discard.add(0, ActiveCard);
			SeeDecline(ActivePlayer);
			if(ActiveCard.face == CardFace.Queen){
				RevealAndSwap();
			}
			else if(ActiveCard.face == CardFace.Jack){
				BlindSwap();
			}
			else if(ActiveCard.face == 10 || ActiveCard.face == 9){
				ActivePlayer.PeekOpponent();
			}
			else if(ActiveCard.face == 7 || ActiveCard.face == 8){
				ActivePlayer.PeekSelf();
			}
		}
		else{
			discard.add(0, ActivePlayer.hand.get(index));
			ActivePlayer.hand.set(index, ActiveCard);
			SeePlaceBlind(ActivePlayer, index);
		}
    
    }
    
    public static void RevealAndSwap(){
		SwapTarget target = ActivePlayer.RevealAndSwap();
		SeeSwap(ActivePlayer, target);
		SeeReveal(ActivePlayer, target.source, target.source.player.hand.get(target.source.index));
		SeeReveal(ActivePlayer, target.target, target.target.player.hand.get(target.target.index));
	}
	
	public static void BlindSwap(){
		SwapTarget target = ActivePlayer.RevealAndSwap();
		SeeSwap(ActivePlayer, target);
	}
    
    public static void PlayFromDiscard(){
        int back = ActivePlayer.ChooseDiscard(discard.get(0));
        Card returned = ActivePlayer.hand.get(back);
        Card pulled = discard.get(0);
        ActivePlayer.hand.set(back, pulled);
        discard.set(0, returned);
        SeePullDiscard(ActivePlayer, back, pulled);
    }

    public static void CallCambio(){
        System.out.println(ActivePlayer.name + " CALLED CAMBIO!");
        Called = true;
    }    

    public static void Play(){
        System.out.println();
        String action;
        if(discard.size() > 0){
            action = ActivePlayer.Choose(discard.get(0));
        }
        else{
            action = ActivePlayer.Choose(null);
        }
        if(action.equals("deck")){
            PlayFromDeck();
        }    
        else if(action.equals("discard")){
            PlayFromDiscard();
        }
        else if(action.equals("cambio")){
            CallCambio();
        }
        else{
            Play();
        }
        try{
            Thread.sleep(sleeptime);
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    public static void main(String[] args){
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
        InitializeGame(human, machines);
        int reveal = init_reveal;
        for(Player play : roster){
            for(int n = 0; n < reveal; n++){
                play.PeekSelf(n);
            }
            System.out.println();
        }
        discard.add(null);
        runGame();
        
    }
}
