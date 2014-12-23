package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.FileMap;

public class DatabaseFileDescriptor {
    public int bucket;
    public int file;

    public DatabaseFileDescriptor(int bucket, int file) {
        this.bucket = bucket;
        this.file = file;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        DatabaseFileDescriptor other = (DatabaseFileDescriptor) obj;

        if (bucket != other.bucket) {
            return false;
        }
        if (file != other.file) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = bucket;
        result = 47 * result + file;
        return result;
    }
}
