package ru.fizteh.fivt.students.dsalnikov.storable;

import java.util.stream.IntStream;

public enum WorkStatus {
    WORKING(1),
    NOT_INITIALIZED(-1),
    CLOSED(0);

    private int state;

    private WorkStatus(int stateNumber) throws IllegalStateException {
        if (IntStream.range(-1, 2).noneMatch(n -> n == stateNumber)) {
            throw new IllegalStateException("container workstatus state getting problem: provided state: " + stateNumber);
        } else {
            state = stateNumber;
        }
    }

    public void setState(int stateNumber) throws IllegalStateException {
        if (IntStream.range(-1, 2).noneMatch(n -> n == stateNumber)) {
            throw new IllegalStateException("container workstatus state setting problem: provided stateNumber " + stateNumber);
        } else {
            state = stateNumber;
        }
    }

    public void canBeSafelyUsed() throws IllegalStateException {
        if (state == -1) {
            throw new IllegalStateException("container workstatus error: container isn't initialized");
        }
        if (state == 0) {
            throw new IllegalStateException("container workstatus: error: container is closed");
        }
    }

    public void canBeClosed() throws IllegalStateException {
        if (state == -1) {
            throw new IllegalStateException("container workstatus error: container isn't initialized");
        }
    }
}
