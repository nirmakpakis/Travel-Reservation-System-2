package Server.RMI;

import Server.Interface.*;
import java.rmi.RemoteException;
import java.util.*;

public class TransactionManager {
	private static int xid_generator = 100;
	public static long CLIENT_TIMEOUT = 30000;

	// transcaction directory
	HashMap<Integer, Set<IResourceManager>> activeTransactions = new HashMap<>();
	// transaction timestamp
	HashMap<Integer, Long> activeTransactionTime = new HashMap<>();

	public TransactionManager() {
		super();
	}

	private static synchronized void incrementXid() {
		xid_generator++;
	}

	public int start() {
		int xid = xid_generator;
		TransactionManager.incrementXid();

		HashSet<IResourceManager> resources = new HashSet<>();
		activeTransactions.put(xid, resources);
		activeTransactionTime.put(xid, System.currentTimeMillis());
		return xid;
	}

	public void addResource(int xid, IResourceManager resource) {
		// resource.backupRM(xid);
		activeTransactions.get(xid).add(resource);
	}

	public String commit(int xid) throws RemoteException {
		if (!activeTransactions.containsKey(xid)) {
			System.out.println("The transaction is already timedout! It is aborted!");
			return "The transaction is already timedout!";
		}
		for (IResourceManager resource : activeTransactions.get(xid)) {
			resource.commit(xid);
		}
		activeTransactions.remove(xid);
		activeTransactionTime.remove(xid);
		return "The transaction is commited!!!";
	}

	public void abort(int xid) throws RemoteException {
		if (!activeTransactions.containsKey(xid)) {
			System.out.println("The transaction is already timedout!");
			return;
		}

		for (IResourceManager resource : activeTransactions.get(xid)) {
			resource.abort(xid);
		}
		activeTransactions.remove(xid);
		activeTransactionTime.remove(xid);
	}

	public void updateTime(int xid) {
		this.activeTransactionTime.put(xid, System.currentTimeMillis());
	}
}