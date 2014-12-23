package ru.fizteh.fivt.students.surin;

public class Pair<FirstType extends Comparable<FirstType>, SecondType>
        implements Comparable<Pair<FirstType, SecondType>> {
    public FirstType first;
    public SecondType second;
    Pair(FirstType first, SecondType second) {
        this.first  = first;
        this.second = second;
    }

    @Override
    public int compareTo(Pair<FirstType, SecondType> fsscPair) {
        if (first.compareTo(fsscPair.first) == 0) {
            return second.hashCode() - fsscPair.hashCode();
        }
        return first.compareTo(fsscPair.first);
    }
}
