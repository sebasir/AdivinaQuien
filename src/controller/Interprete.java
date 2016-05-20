package controller;
import java.util.StringTokenizer;
public class Interprete{
    private Object[] myObject;
    private DBManager DBManager;
    public Interprete(DBManager DBManager){
        this.DBManager=DBManager;
    }
    public Object[] response(String Codex){
        System.out.println(Codex);
        StringTokenizer myTok=new StringTokenizer(Codex,":");
        String comando=myTok.nextToken();
        if(comando.equals("Register")){
            myObject=new Object[]{"Connected",true};
        }
        if(comando.equals("Search")){
            try {
                myObject=new Object[]{"Found",DBManager.searchUser(myTok.nextToken())};                
            } catch (Exception ex) {
                System.out.println("Interprete.response.search: ");
                ex.printStackTrace();
            }
        }
        if(comando.equals("Log"))
            myObject=new Object[]{"Ready","Waiting"};        
        if(comando.equals("Disconnect")){
            myObject=new Object[]{"Disconnected","Disconnected"};
        }
        if(comando.equals("Esperando")){
            myObject=new Object[]{"Ready","Ready"};
        }
        return myObject;
    }
}