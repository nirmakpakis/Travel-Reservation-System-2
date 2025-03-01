package Server.RMI;

import Server.Interface.*;
import Server.Common.*;

import java.rmi.NotBoundException;
import java.util.*;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIMiddleware extends ResourceManager {
	private static String s_serverName = "Middleware";
	private static String s_rmiPrefix = "group_31_";
	private static String flights_host = "lab2-18";
	private static String cars_host = "lab2-21";
	private static String rooms_host = "lab2-22";

	// add three different resource managers as attributes
	private static IResourceManager carManager = null;
	private static IResourceManager flightManager = null;
	private static IResourceManager roomManager = null;

	// Transaction Manager
	private static TransactionManager transactionManager = new TransactionManager();

	public RMIMiddleware(String p_name) {
		super(p_name);
		Cleaner cleaner = new Cleaner(transactionManager);
		Thread thread = new Thread(cleaner);
		thread.start();
	}

	// call commit to all servers
	@Override
	public String commit(int xid) throws RemoteException {
		String result = transactionManager.commit(xid);
		return result;
	}

	@Override
	public void abort(int xid) throws RemoteException {
		Set<IResourceManager> activeResources = transactionManager.activeTransactions.get(xid);
		for (IResourceManager rm : activeResources) {
			rm.rewindTransactions(xid);
		}
		transactionManager.abort(xid);
	}

	@Override
	public boolean shutdown() throws RemoteException {
		Boolean result1 = carManager.shutdown();
		Boolean result2 = flightManager.shutdown();
		Boolean result3 = roomManager.shutdown();
		new Thread() {
			@Override
			public void run() {
				System.out.print("The server is shutting down!");
				try {
					sleep(2000);
				} catch (InterruptedException e) {
				}
				System.exit(0);
			}

		}.start();
		return result1 && result2 && result3;
	}

	// start
	public int start() {
		// create a new transaction
		try {
			int xid = transactionManager.start();
			flightManager.rememberState(xid);
			carManager.rememberState(xid);
			roomManager.rememberState(xid);
			return xid;
		} catch (RemoteException e) {
			System.err.println(e.getMessage());
			System.exit(100);
			return -1;
		}
	}

	// calls addFlight on the flightmanager
	@Override
	public boolean addFlight(int xid, int flightNum, int flightSeats, int flightPrice) throws RemoteException {
		transactionManager.updateTime(xid);
		Trace.info("RM::addFlight(" + xid + ", " + flightNum + ", " + flightSeats + ", $" + flightPrice + ") called");
		transactionManager.addResource(xid, flightManager);
		try {
			return flightManager.addFlight(xid, flightNum, flightSeats, flightPrice);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// TODO: add trace infos
	@Override
	public boolean addCars(int xid, String location, int count, int price) throws RemoteException {
		transactionManager.updateTime(xid);
		Trace.info("RM::addCars(" + xid + ", " + location + ", " + count + ", $" + price + ") called");
		transactionManager.addResource(xid, carManager);
		try {
			return carManager.addCars(xid, location, count, price);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean addRooms(int xid, String location, int count, int price) throws RemoteException {
		transactionManager.updateTime(xid);
		Trace.info("RM::addRooms(" + xid + ", " + location + ", " + count + ", $" + price + ") called");
		transactionManager.addResource(xid, roomManager);
		try {
			return roomManager.addRooms(xid, location, count, price);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean deleteFlight(int xid, int flightNum) throws RemoteException {
		transactionManager.updateTime(xid);
		transactionManager.addResource(xid, flightManager);
		try {
			System.out.println("Middleware connected to Flights server to delete flight");

			return flightManager.deleteFlight(xid, flightNum);
		} catch (Exception e) {
			e.printStackTrace();
			return false;

		}
	}

	@Override
	public boolean deleteCars(int xid, String location) throws RemoteException {
		transactionManager.updateTime(xid);
		transactionManager.addResource(xid, carManager);
		try {
			System.out.println("Middleware connected to Cars server to delete cars");
			return carManager.deleteCars(xid, location);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean deleteRooms(int xid, String location) throws RemoteException {
		transactionManager.updateTime(xid);
		transactionManager.addResource(xid, roomManager);
		try {
			System.out.println("Middleware connected to Rooms server to delete rooms");
			return roomManager.deleteRooms(xid, location);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public int queryFlight(int xid, int flightNum) throws RemoteException {
		transactionManager.updateTime(xid);
		transactionManager.addResource(xid, flightManager);
		try {
			System.out.println("Middleware connected to Flights server to query flights.");
			return flightManager.queryFlight(xid, flightNum);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public int queryCars(int xid, String location) throws RemoteException {
		transactionManager.updateTime(xid);
		transactionManager.addResource(xid, carManager);
		try {
			System.out.println("Middleware connected to Cars server to query cars.");
			return carManager.queryCars(xid, location);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public int queryRooms(int xid, String location) throws RemoteException {
		transactionManager.updateTime(xid);
		transactionManager.addResource(xid, roomManager);
		try {
			System.out.println("Middleware connected to Rooms server to query rooms.");
			return roomManager.queryRooms(xid, location);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public int queryFlightPrice(int xid, int flightNum) throws RemoteException {
		transactionManager.updateTime(xid);
		transactionManager.addResource(xid, flightManager);
		try {
			System.out.println("Middleware connected to Flights server to query flight price.");
			return flightManager.queryFlightPrice(xid, flightNum);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public int queryCarsPrice(int xid, String location) throws RemoteException {
		transactionManager.updateTime(xid);
		transactionManager.addResource(xid, carManager);
		try {
			System.out.println("Middleware connected to Cars server to query cars price.");
			return carManager.queryCarsPrice(xid, location);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public int queryRoomsPrice(int xid, String location) throws RemoteException {
		transactionManager.updateTime(xid);
		transactionManager.addResource(xid, roomManager);
		try {
			System.out.println("Middleware connected to Rooms server to query rooms price.");
			return roomManager.queryRoomsPrice(xid, location);

		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public boolean reserveFlight(int xid, int customerID, int flightNum) throws RemoteException {
		// Read customer object if it exists (and read lock it)
		transactionManager.updateTime(xid);
		transactionManager.addResource(xid, flightManager);
		try {
			Customer customer = (Customer) readData(xid, Customer.getKey(customerID));
			if (customer == null) {
				Trace.warn("RM::reserveItem(" + xid + ", " + customerID + ")  failed--customer doesn't exist");
				return false;
			} else {
				boolean b = flightManager.reserveFlight(xid, customerID, flightNum);
				if (b) {
					// if item was successfully reserved, reserve it for customer
					customer.reserve(Flight.getKey(flightNum), String.valueOf(flightNum),
							flightManager.queryFlightPrice(xid, flightNum));
					writeData(xid, customer.getKey(), customer);
					return true;

				}
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean reserveCar(int xid, int customerID, String location) throws RemoteException {
		transactionManager.updateTime(xid);
		transactionManager.addResource(xid, carManager);
		try {
			// Read customer object if it exists (and read lock it)
			Customer customer = (Customer) readData(xid, Customer.getKey(customerID));
			if (customer == null) {
				Trace.warn("RM::reserveItem(" + xid + ", " + customerID + ")  failed--customer doesn't exist");
				return false;
			} else {
				boolean b = carManager.reserveCar(xid, customerID, location);
				if (b) {
					customer.reserve(Car.getKey(location), location, carManager.queryCarsPrice(xid, location));
					writeData(xid, customer.getKey(), customer);
					return true;

				}
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean reserveRoom(int xid, int customerID, String location) throws RemoteException {
		// Read customer object if it exists (and read lock it)
		transactionManager.updateTime(xid);
		transactionManager.addResource(xid, roomManager);
		try {
			Customer customer = (Customer) readData(xid, Customer.getKey(customerID));
			if (customer == null) {
				Trace.warn("RM::reserveItem(" + xid + ", " + customerID + ")  failed--customer doesn't exist");
				return false;
			} else {
				boolean b = roomManager.reserveRoom(xid, customerID, location);
				if (b) {
					customer.reserve(Room.getKey(location), location, roomManager.queryRoomsPrice(xid, location));
					writeData(xid, customer.getKey(), customer);
					return true;

				}
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// BIG BUG: NUMBER OF ITEMS RESERVED OF A RESERVABLE ITEM CANNOT BE REDUCED FROM
	// THE MIDDLEWARE
	@Override
	public boolean deleteCustomer(int xid, int customerID) throws RemoteException {
		transactionManager.updateTime(xid);
		Trace.info("RM::deleteCustomer(" + xid + ", " + customerID + ") called");
		Customer customer = (Customer) readData(xid, Customer.getKey(customerID));
		if (customer == null) {
			Trace.warn("RM::deleteCustomer(" + xid + ", " + customerID + ") failed--customer doesn't exist");
			return false;
		} else {
			try {
				// Increase the reserved numbers of all reservable items which the customer
				// reserved.
				boolean boo = true;
				RMHashMap reservations = customer.getReservations();
				for (String reservedKey : reservations.keySet()) {
					ReservedItem reserveditem = customer.getReservedItem(reservedKey);
					Trace.info("RM::deleteCustomer(" + xid + ", " + customerID + ") has reserved "
							+ reserveditem.getKey() + " " + reserveditem.getCount() + " times");
					if (reservedKey.contains("flight")) {
						String[] result = reservedKey.split("-");
						int flightNum = Integer.parseInt(result[1]);
						boo = flightManager.addFlight(xid, flightNum, reserveditem.getCount(), 0) && boo;
					} else if (reservedKey.contains("car")) {
						String[] result = reservedKey.split("-");
						String location = result[1];
						boo &= carManager.addCars(xid, location, reserveditem.getCount(), 0);
					} else if (reservedKey.contains("room")) {
						String[] result = reservedKey.split("-");
						String location = result[1];
						boo &= roomManager.addRooms(xid, location, reserveditem.getCount(), 0);
					}

					Trace.info("RM::deleteCustomer(" + xid + ", " + customerID + ") has reserved "
							+ reserveditem.getKey() + " " + reserveditem.getCount() + " times");

				}

				// Remove the customer from the storage
				if (boo) {
					removeData(xid, customer.getKey());
					Trace.info("RM::deleteCustomer(" + xid + ", " + customerID + ") succeeded");
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	@Override
	public boolean bundle(int xid, int customerId, Vector<String> flightNumbers, String location, boolean car,
			boolean room) throws RemoteException {
		transactionManager.updateTime(xid);
		transactionManager.addResource(xid, carManager);
		transactionManager.addResource(xid, flightManager);
		transactionManager.addResource(xid, roomManager);
		try {
			Customer customer = (Customer) readData(xid, Customer.getKey(customerId));
			if (customer == null) {
				Trace.warn("RM::reserveItem(" + xid + ", " + customerId + ")  failed--customer doesn't exist");
				return false;
			}
			boolean reservable = false;
			for (int index = 0; index < flightNumbers.size(); index++) {
				reservable = (flightManager.queryFlight(xid, Integer.valueOf(flightNumbers.get(index))) > 0);
				if (!reservable) {
					break;
				}

			}

			if (reservable) {
				for (int index = 0; index < flightNumbers.size(); index++) {
					reserveFlight(xid, customerId, Integer.valueOf(flightNumbers.get(index)));
				}

				if (car && carManager.queryCars(xid, location) > 0) {
					reserveCar(xid, customerId, location);
				}

				if (room && roomManager.queryRooms(xid, location) > 0) {
					reserveRoom(xid, customerId, location);
				}

				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public static IResourceManager connectServer(String server, int port, String name) {
		try {
			boolean first = true;
			while (true) {
				try {
					Registry registry = LocateRegistry.getRegistry(server, port);
					IResourceManager resourceManager = (IResourceManager) registry.lookup(s_rmiPrefix + name);
					System.out.println("Connected to '" + name + "' server [" + server + ":" + port + "/" + s_rmiPrefix
							+ name + "]");
					return resourceManager;
				} catch (NotBoundException | RemoteException e) {
					if (first) {
						System.out.println("Waiting for '" + name + "' server [" + server + ":" + port + "/"
								+ s_rmiPrefix + name + "]");
						first = false;
					}
				}
				Thread.sleep(500);
			}
		} catch (Exception e) {
			System.err.println((char) 27 + "[31;1mServer exception: " + (char) 27 + "[0mUncaught exception");
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	// connect three servers to middleware
	public static void connectServers() {
		// all connect to same registry
		flightManager = connectServer(flights_host, Const.REGISTRY_PORT, Const.FLIGHT_SERVER_NAME);
		carManager = connectServer(cars_host, Const.REGISTRY_PORT, Const.CAR_SERVER_NAME);
		roomManager = connectServer(rooms_host, Const.REGISTRY_PORT, Const.ROOM_SERVER_NAME);
	}

	public static void main(String args[]) {
		if (args.length >= 3) {
			flights_host = args[0];
			cars_host = args[1];
			rooms_host = args[2];
		}

		// Create the RMI server entry
		try {
			// Create a new Server object
			RMIMiddleware middlewareServer = new RMIMiddleware(s_serverName);
			connectServers();

			// Dynamically generate the stub (client proxy)
			IResourceManager resourceManager = (IResourceManager) UnicastRemoteObject.exportObject(middlewareServer, 0);

			// Bind the remote object's stub in the registry
			Registry l_registry;
			try {
				l_registry = LocateRegistry.createRegistry(3099);
			} catch (RemoteException e) {
				l_registry = LocateRegistry.getRegistry(3099);
			}
			final Registry registry = l_registry;
			registry.rebind(s_rmiPrefix + s_serverName, resourceManager);

			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					try {
						registry.unbind(s_rmiPrefix + s_serverName);
						System.out.println("'" + s_serverName + "' resource manager unbound");
					} catch (Exception e) {
						System.err
								.println((char) 27 + "[31;1mServer exception: " + (char) 27 + "[0mUncaught exception");
						e.printStackTrace();
					}
				}
			});
			System.out.println("'" + s_serverName + "' resource manager server ready and bound to '" + s_rmiPrefix
					+ s_serverName + "'");
		} catch (Exception e) {
			System.err.println((char) 27 + "[31;1mServer exception: " + (char) 27 + "[0mUncaught exception");
			e.printStackTrace();
			System.exit(1);
		}

		// Create and install a security manager
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
	}
}
