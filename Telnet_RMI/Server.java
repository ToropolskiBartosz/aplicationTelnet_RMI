import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.net.*;
import java.rmi.registry.*;

public class Server extends UnicastRemoteObject implements RMIInterface{

    private static HashMap<Integer, Client> clients = new HashMap<Integer, Client>();
    private static int idClient = -1;

    protected Server() throws RemoteException { super(); }
    
    //Nadpisanie metoda wykonujaca komendy
    @Override
    public String myCommand(String cmd,int client_id) throws RemoteException{
        String reply = exec(cmd, client_id);
        return reply;
    }
    
    //Nadpisanie metoda rejestrujacej klientow na serwerze  
    //Metoda ta zwraca id jakie klient otrzymal od serwera 
    @Override
    public int handshake() throws RemoteException{
        try {
            String OS = System.getProperty("os.name").toLowerCase();

            Process process; 
            //Sprawdzanie na jakim systemie dziala aplikacja
            //Nastepnie utworzenie procesu 
            if(OS.contains("win")){
                process = Runtime.getRuntime().exec("cmd /c");
            }else{
                process = Runtime.getRuntime().exec("/bin/bash");
            }
            //Utworzenie obliektu klasy Client
            Client client = new Client(process);
            //Dodanie Klienta do listy 
            idClient++;
            clients.put(idClient,client);
            
            return idClient;

        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
            return 1;
        }
    }

    public static void main(String[] args){
        try
        {
            //rejestracja obiektu 
            Server f = new Server();
            LocateRegistry.createRegistry(40000);
            Naming.rebind("//127.0.0.1:40000/RMIInterface", f);
            System.out.println("===== START =====");
        }
        catch (RemoteException re) {
            System.err.println("Blad!! Exception in Telnet --> "+re);
        }
        catch(MalformedURLException e){
            System.err.println("Blad!! Exception -->"+e);
        }
    }

    public String exec(String command, int client_id){

        try {
            System.out.println("Klient z ktorym sie komunikujesz: "+client_id);
            //Pobranie odpowiadniego klienta
            Client client = clients.get(client_id);

            //Przeslanie klienta do procesu   
            client.write.write(command+"\n");
            client.write.flush();

            
            Thread.sleep(400);
            //Pobranie danych z procesu
            String line="";
            while (client.reader.ready()) {
                line += client.reader.readLine()+"\n";
            }

            //Zakonczenie dzialania procesu
            if (client.process.waitFor(10,TimeUnit.MILLISECONDS)) {
                System.out.println("Zakonczenie pracy telneta");
                String succes = "FINISH";
                clients.remove(client_id);
                return succes;
            } else {
                //Przeslanie wyniku do klienta 
                System.out.println("Wykonanie komendy: "+command);
                return line;
            }
        
        } catch (IOException e) {
            String succes = "error";
            return succes;
        } catch (InterruptedException e) {
            String succes = "error";
            return succes;
        }
        
    }

    public class Client{
        private Process process;
        public BufferedWriter write ;
        public BufferedReader reader;

        public Client(Process process){
            this.process = process;
            this.write = new BufferedWriter(
                new OutputStreamWriter(this.process.getOutputStream()));
            this.reader = new BufferedReader(
                new InputStreamReader(this.process.getInputStream())); 
        }

    } 
}





