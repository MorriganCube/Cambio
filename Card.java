import java.util.ArrayList;
public class Card{
    int face;
    int suit;
    int value;
    String name;
    boolean hard;

    public String toString(){
        return name;
    }
    public Card(){ //avoid using
        face = -1;
        suit = -1;
        value = 0;
        name = "Dummy Card";
    }
    
    public Card(int value){ //Approximate Card Value (good for memory)
		face = value;
		suit = -1;
		this.value = value;
		name = "Unknown Card with value approximately " + value;
	}
		

    public Card(int face, int suit){ //Create an Actual Card
        this.face = face;
        this.suit = suit;
        this.CalcVal();
        this.CalcName();
    }
	
	public void CalcVal(){ //Calculate the value of a card based on its face
        if(face <= 10){
            this.value = this.face;
        }
        else if(face == CardFace.King){
            if(suit == CardFace.Diamonds || suit == CardFace.Hearts){ 
                this.value = -1;
            }
        }
        else{
            this.value = 10;
        }
	}
	
	public void CalcName(){
		if(face == 0){
			this.name = "Joker of ";
		}
        if(face == 1){
            this.name = "Ace of ";
        }
        else if(face <= 10){
            this.name = face + " of ";
        }
        else if(face == CardFace.Jack){
            this.name = "Jack of ";
        }
        else if(face == CardFace.Queen){
            this.name = "Queen of ";
            this.value = 10;
        }
        else if(face == CardFace.King){
            this.name = "King of ";
        }
        
        if(suit == CardFace.Spades){
            name += "Spades";
        }
        else if(suit == CardFace.Clubs){
            name += "Clubs";
        }
        else if(suit == CardFace.Diamonds){
            name += "Diamonds";
        }
        else if(suit == CardFace.Hearts){
            name += "Hearts";
        }
	}
	
	
}

