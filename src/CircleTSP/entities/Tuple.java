package CircleTSP.entities;

/**
 * Created by Erik Heller on 13.11.2018.
 */
public class Tuple <S,T> {

    private S first;
    private T second;

    public Tuple(final S firstArg, final T secondArg) {
        this.first = firstArg;
        this.second = secondArg;
    }

    public final S getFirst() {
        return this.first;
    }

    public void setFirst(final S firstArg) {
        this.first = firstArg;
    }

    public final T getSecond() {
        return this.second;
    }

    public void setSecond(final T secondArg) {
        this.second = secondArg;
    }
}
