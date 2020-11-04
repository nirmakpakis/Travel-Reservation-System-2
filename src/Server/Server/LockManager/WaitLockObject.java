package Server.LockManager;

public class WaitLockObject extends DataLockObject
{
	public Thread m_thread = null;

	// The data members inherited are 
	// TransactionObject:: public int m_xid;
	// TransactionLockObject:: public String m_data;
	// TransactionLockObject:: public int m_lockType;

	WaitLockObject()
	{
		super();
		m_thread = null;
	}

	WaitLockObject(int xid, String data, LockType lockType)
	{
		super(xid, data, lockType);
		m_thread = null;
	}

	WaitLockObject(int xid, String data, LockType lockType, Thread thread)
	{
		super(xid, data, lockType);
		m_thread = thread;
	}

	public Thread getThread()
	{
		return m_thread;
	}
}
