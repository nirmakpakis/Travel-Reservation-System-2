package Server.RMI;

import Server.Common.*;
import Server.Interface.*;
import java.rmi.RemoteException;

import java.util.*;

public class TransactionManager {
	static int xid_generator = 100;

	// transcaction directory
	HashMap<Integer, Set<IResourceManager>> activeTransactions = new HashMap<>();

	private static synchronized void incrementXid() {
		xid_generator++;
	}

	public int start() {
		int xid = xid_generator;
		TransactionManager.incrementXid();

		HashSet<IResourceManager> resources = new HashSet<>();
		activeTransactions.put(xid, resources);
		return xid;
	}

	public void addResource(int xid, IResourceManager resource) {
		// resource.backupRM(xid);
		activeTransactions.get(xid).add(resource);
	}

	public void commit(int xid) throws RemoteException {
		for (IResourceManager resource : activeTransactions.get(xid)) {
			resource.commit(xid);
		}
		activeTransactions.remove(xid);
	}

	public void abort(int xid) throws RemoteException {
		for (IResourceManager resource : activeTransactions.get(xid)) {
			resource.abort(xid);
		}
		activeTransactions.remove(xid);
	}
}