package util;

public class Pair<A, B> {

	public A first;
	public B second;
	
	public Pair(A first, B second) {
		this.first = first;
		this.second = second;
	}

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Pair
            && this.first.equals(((Pair) obj).first) 
            && this.second.equals(((Pair) obj).second);
    }

    @Override
    public int hashCode() {
        return 1013 * (this.first.hashCode()) ^ 1009 * (this.second.hashCode());
    }
}
