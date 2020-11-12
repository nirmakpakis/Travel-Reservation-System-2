package Server.RMI;

import java.rmi.RemoteException;

public class Cleaner implements Runnable {
	private TransactionManager transactionManager;
	public Cleaner(TransactionManager transactionManager) {
		super();
		this.transactionManager = transactionManager;
	}

	public void run(){
		while(true) {
			long current = System.currentTimeMillis();
			for(int xid: transactionManager.activeTransactionTime.keySet()) {
				long tranTime = transactionManager.activeTransactionTime.get(xid);
				long diff = current - tranTime;
				if(diff > TransactionManager.CLIENT_TIMEOUT) {
					try {
						this.transactionManager.abort(xid);
					}catch(RemoteException remote) {
						System.out.println("The cleaner failed to clean up friendly transaction");
						remote.printStackTrace();
						System.exit(200);
					}
				}
			}
			try {
				Thread.sleep(1000);
			}catch(InterruptedException exception) 
			{
				System.out.println("The cleaner failed to clean up friendly transaction");
				exception.printStackTrace();
				System.exit(101);
			}
		}
	}
}