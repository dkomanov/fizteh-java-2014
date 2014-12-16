package ru.fizteh.fivt.students.NikolaiKrivchanskii.filemap;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public abstract class SomeStorage<Key, Value> {
    protected HashMap<Key, Value> unchangedOldData;
    protected HashSet<Key> deletedKeys;
    private String name;
    protected String parentDirectory;
    protected boolean doAutoCommit = true;
    private final Lock transactionLock = new ReentrantLock(true);
    
    protected final ThreadLocal<Transactions> transaction = new ThreadLocal<Transactions>() {
        public Transactions initialValue() {
            return new Transactions();
        }
    };
    
    class Transactions {
    	HashMap<Key, Value> currentData;
    	int size;
    	int unsavedChangesCounter;
    	
    	Transactions() {
    		this.currentData = new HashMap<Key, Value>();
    		this.size = 0;
    		this.unsavedChangesCounter = 0;
    	}
    	
    	public void newModification(Key key, Value value) {
    		currentData.put(key, value);
    	}
    	
    	public int saveModifications() {
    		int changesCounter = 0;
    		for(Key key : currentData.keySet()) {
    			Value newOne = currentData.get(key);
    			if(!GlobalUtils.compare(newOne, unchangedOldData.get(key))) {
    				if (newOne != null) {
    					unchangedOldData.put(key, newOne);
    				} else {
    					unchangedOldData.remove(key);
    				}
    				changesCounter++;
    			}
    		}
    		return changesCounter;
    	}
    	
    	public int calculateChangesQuantity() {
    		int changesCounter = 0;
    		for(Key key : currentData.keySet()) {
    			Value newOne = currentData.get(key);
    			if(!GlobalUtils.compare(newOne, unchangedOldData.get(key))) {
    				changesCounter++;
    			}
    		}
    		return changesCounter;
    	}
    	
    	public int calculateSize() {
    		int size = unchangedOldData.size();
    		for(Key key : currentData.keySet()) {
    			Value newOne = currentData.get(key);
    			Value oldOne = unchangedOldData.get(key);
    			if (newOne == null && oldOne != null) {
    				size--;
    			} else if (newOne != null && oldOne == null) {
    				size++;
    			}
    		}
    		return size;
    	}
    	
    	public Value getVal(Key key) {
    		if (currentData.containsKey(key)) {
    			return currentData.get(key);
    		}
    		return unchangedOldData.get(key);
    	}
    	
    	public int getSize() {
    		return unchangedOldData.size() + calculateSize();
    	}
    	
    	public void incrementUnsavedChangesCounter() {
    		unsavedChangesCounter++;
    	}
    	
    	public int getUnsavedChangesCounter() {
    		return unsavedChangesCounter;
    	}
    	
    	public void clear() {
    		currentData.clear();
    		unsavedChangesCounter = 0;
    		size = 0;
    	}
    	
    }
    
    public String getParentDirectory() {
        return parentDirectory;
    }
    
    public void setAutoCommit(boolean status) {
    	doAutoCommit = status;
    }
    
    public boolean getAutoCommit() {
    	return doAutoCommit;
    }
    
    public String getName() {
        return name;
    }
    public int sizeOfStorage() {
        return transaction.get().calculateSize();
    }
    
    public int getChangesCounter() {
        return transaction.get().calculateChangesQuantity();
    }
    
    protected abstract void load() throws IOException;
    protected abstract void save() throws IOException;
    
    public SomeStorage(String dir, String name) {
        this.parentDirectory = dir;
        this.name = name;
        unchangedOldData = new HashMap<Key, Value>();
        setAutoCommit(true);               //change here to change autocommit status
        try {
            load();
        } catch (IOException e) {
        	if (e.getMessage() != "didn't exist" && e.getMessage() != "empty file") {
        	    throw new IllegalArgumentException("invalid file format " + e.getMessage());
        	}
        }
        
    }
    
    public Value getFromStorage(Key key) {
        if (key == null) {
        	throw new IllegalArgumentException ("key cannot be null");
        }
        return transaction.get().getVal(key);
    }
    
    public Value putIntoStorage(Key key, Value value) {
        Value oldVal =  transaction.get().getVal(key);
        transaction.get().newModification(key, value);
        return oldVal;
    }
    
    public Value removeFromStorage(Key key) {
    	if (key == null ) {
        	throw new IllegalArgumentException("key cannot be null");
        }
    	if (getFromStorage(key) == null) {
    		return null;
    	}
        Value oldVal = transaction.get().getVal(key);
        transaction.get().newModification(key, null);
        transaction.get().incrementUnsavedChangesCounter();
        return oldVal;
    }
    
    public int rollbackStorage() {
        int deletedOrAdded = transaction.get().calculateChangesQuantity();
        transaction.get().clear();
        return deletedOrAdded;
    }
    
    public int commitStorage() {
    	try{
    		transactionLock.lock();
        	int commitCount = transaction.get().saveModifications();
        	transaction.get().clear();
        	try {
        		save();
        	} catch (IOException e) {
        		System.err.println("commit error: " + e.getMessage());
        		return 0;
        	}
        	return commitCount;
        } finally {
        	transactionLock.unlock();
        }
    }
    
    void rawPut(Key key, Value value) {
    	unchangedOldData.put(key, value);
    }
    
    Value rawGet(Key key) {
    	return unchangedOldData.get(key);
    }


}
