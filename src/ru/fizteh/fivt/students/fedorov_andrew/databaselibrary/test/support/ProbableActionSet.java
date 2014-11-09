package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support;

import java.util.LinkedList;

/**
 * Class for convenient probable action set forming and picking random actions from this set.
 * @param <Action>
 * @author phoenix
 */
public class ProbableActionSet<Action> {
    private final LinkedList<ProbableAction> actions;
    private int probCasesSum;

    public ProbableActionSet() {
        actions = new LinkedList<>();
        probCasesSum = 0;
    }

    public static <E extends Enum<E>> ProbableActionSet<E> makeDistributedSet(Class<E> clazz,
                                                                              int... probabilities) {
        E[] enums = clazz.getEnumConstants();
        if (probabilities.length != enums.length) {
            throw new IllegalArgumentException("Probabilities count does not match actions count");
        }

        ProbableActionSet<E> set = new ProbableActionSet<>();
        for (int i = 0, len = enums.length; i < len; i++) {
            set.add(enums[i], probabilities[i]);
        }
        return set;
    }

    public static <E extends Enum<E>> ProbableActionSet<E> makeEquallyDistributedSet(Class<E> clazz) {
        E[] enums = clazz.getEnumConstants();
        ProbableActionSet<E> set = new ProbableActionSet<>();
        for (E en : enums) {
            set.add(en, 1);
        }
        return set;
    }

    public ProbableActionSet<Action> add(Action action, int probCases) {
        if (probCases <= 0) {
            throw new IllegalArgumentException("probCases must be positive integer");
        }

        for (ProbableAction probAction : actions) {
            if (probAction.action.equals(action)) {
                throw new IllegalArgumentException("Action already registered: " + action);
            }
        }
        actions.add(new ProbableAction(action, probCases));
        probCasesSum += probCases;
        return this;
    }

    public Action nextAction() {
        if (probCasesSum == 0) {
            throw new IllegalStateException("No actions have been added");
        }

        int probCase = TestUtils.randInt(1, probCasesSum);
        for (ProbableAction action : actions) {
            if (probCase <= action.probabilityCases) {
                return action.action;
            }
            probCase -= action.probabilityCases;
        }

        throw new IllegalStateException("Could not pick an action");
    }

    private class ProbableAction {
        private final Action action;
        private final int probabilityCases;

        public ProbableAction(Action action, int probabilityCases) {
            this.action = action;
            this.probabilityCases = probabilityCases;
        }
    }
}
