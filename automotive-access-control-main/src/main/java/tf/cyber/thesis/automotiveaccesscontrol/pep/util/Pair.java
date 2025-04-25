package tf.cyber.thesis.automotiveaccesscontrol.pep.util;

public class Pair<X, Y>{
    X first;
    Y second;

    public Pair() {
    }

    public Pair(X first, Y second) {
        this.first = first;
        this.second = second;
    }

    public X getFirst() {
        return first;
    }

    public void setFirst(X first) {
        this.first = first;
    }

    public Y getSecond() {
        return second;
    }

    public void setSecond(Y second) {
        this.second = second;
    }

    public static <X, Y> Pair<X, Y> of(X first, Y second) {
        return new Pair<>(first, second);
    }
}
