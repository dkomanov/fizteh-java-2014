package ru.fizteh.fivt.students.AndrewFedorov.filemap;

public class Utility {
    /**
     * Handles an occured exception.<br/>
     * Equivalent to handleError(null, message, true);
     * 
     * @param message
     *            message that can be reported to user and is written to log.
     */
    static void handleError(String message) {
	handleError(null, message, true);
    }

    /**
     * Handles an occured exception.
     * 
     * @param exc
     *            occured exception. If null, an {@link Exception} is
     *            constructed via {@link Exception#Exception(String)}.
     * @param message
     *            message that can be reported to user and is written to log.
     * @param reportToUser
     *            if true, message is printed to {@link System#err}.
     */
    static void handleError(Throwable exc, String message, boolean reportToUser) {
	if (reportToUser) {
	    System.err.println(message);
	}
	Log.log(Commands.class, exc, message);
	if (exc == null) {
	    exc = new Exception(message);
	}
	throw new HandledException(exc);
    }

    static byte[] insertArray(byte[] source, int sourceOffset, int sourceSize,
	    byte[] target, int targetOffset) {
	if (sourceSize + targetOffset > target.length) {
	    int newLength = Math.max(target.length * 2, sourceSize
		    + targetOffset);
	    if (newLength < 0) {
		newLength = Integer.MAX_VALUE;
	    }
	    if (newLength <= target.length) {
		throw new RuntimeException("Cannot allocate such big array");
	    }

	    byte[] newTarget = new byte[newLength];
	    System.arraycopy(target, 0, newTarget, 0, targetOffset);
	    target = newTarget;
	}

	System.arraycopy(source, sourceOffset, target, targetOffset, sourceSize);
	return target;
    }

    static String simplifyClassName(String name) {
	name = name.substring(name.lastIndexOf('.') + 1);
	name = name.substring(name.lastIndexOf('$') + 1);
	name = name.toLowerCase();
	return name;
    }

}
