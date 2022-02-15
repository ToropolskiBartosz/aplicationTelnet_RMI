import java.rmi.Remote;
import java.rmi.RemoteException;
public interface RMIInterface extends Remote { 
    public String myCommand(String name,int client_id) throws RemoteException;
    public int handshake() throws RemoteException;
}




