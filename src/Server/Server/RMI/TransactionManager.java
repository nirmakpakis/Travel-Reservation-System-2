package Server.RMI;

import Server.Common.*;

import java.util.*;

public class TransactionManager {
	static int xid_generator = 100;

	// transcaction directory
	HashMap<Integer, Set<ResourceManager>> activeTransactions = new HashMap<>();

	public TransactionManager(){

	}

	public int start() {
		int xid = xid_generator;
		xid_generator++;

		HashSet<ResourceManager> resources = new HashSet<>();
		activeTransactions.put(xid, resources);
		return xid;
	}

	public void addResource(int xid, ResourceManager resource) {
		resource.backupRM(xid);
		activeTransactions.get(xid).add(resource);
	}

	public void commit(int xid) {
		for(ResourceManager resource: activeTransactions.get(xid)) {
			resource.commit(xid);
		}
		activeTransactions.remove(xid);
	}

	public void abort(int xid) {
		for(ResourceManager resource: activeTransactions.get(xid)) {
			resource.abort(xid);
		}
		activeTransactions.remove(xid);
	}
}