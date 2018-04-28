package util;

public class Pair<A, B> {

	public A first;
	public B second;
	
	public Pair(A first, B second) {
		this.first = first;
		this.second = second;
	}

    @Override 
    public int hashCode() {
        1013 * (this.first.hashCode()) ^ 1009 * (this.second.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instance of Pair<A,B> 
            && ((Pair<A,B>) obj).first.equals(this.first)
            && ((Pair<A,B>) obj).second.equals(this.second);
    }
}
