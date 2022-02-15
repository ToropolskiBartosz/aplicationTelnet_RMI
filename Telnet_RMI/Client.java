import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;


public class Client {

    private static RMIInterface look_up;

    public static void main(String[] args)
            throws MalformedURLException, RemoteException, NotBoundException {
        try{
            Scanner myObj = new Scanner(System.in);
            //uzyskanie referencji do obiektu 
            look_up = (RMIInterface) Naming.lookup("//127.0.0.1:40000/RMIInterface");
            int myId = look_up.handshake();

            for(;;){
                    System.out.print("$ ");
                    String command = myObj.nextLine();
                    //Zdalne wywolywanie funkcji
                    String response = look_up.myCommand(command,myId);
                    System.out.println("---------------------------------------\n" + response);
                    if(response.equals("FINISH")){
                        break;
                    }

            }
            System.exit(0);
        }catch(MalformedURLException e){
            System.err.println("Niepoprawny adres URL RMI --> "+e);
        }
        catch (RemoteException e) {
            System.err.println("Problem z komunikacja--> "+e);
        }
        catch (NotBoundException e) {
            System.err.println("Nie znaleziono takiego obiektu --> "+e);
        }
    }
}





