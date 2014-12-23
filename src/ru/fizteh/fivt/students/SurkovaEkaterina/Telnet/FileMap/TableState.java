package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.FileMap;

public enum TableState {
    NOT_INITIALIZED {
        @Override
        public void checkOperationCorrect() {
            throw new IllegalStateException("not initialized yet");
        }
    },
    WORKING {
        @Override
        public void checkOperationCorrect() {
            // everything is fine
        }
    },
    CLOSED {
        @Override
        public void checkOperationCorrect() {
            throw new IllegalStateException("already closed");
        }
    };

    public abstract void checkOperationCorrect();
}
