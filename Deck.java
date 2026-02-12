import java.util.ArrayList;
import java.util.Collections;
public class Deck{
    public ArrayList<Card> cards;
    public Deck(){
        cards = new ArrayList<Card>();
        for(int n = 1; n <= 13; n++){
            for(int k = -1; k >= -4; k--){
                cards.add(new Card(n, k));
            }
        }
    }
    public void shuffle(){
        Collections.shuffle(cards);
    }
    public Card draw(){
        Card out = cards.get(0);
        cards.remove(0);
        return(out);
    }
    public void add(Card input){
        cards.add(input);
    }
    
    public String toString(){
		return cards.toString();
	}
}
