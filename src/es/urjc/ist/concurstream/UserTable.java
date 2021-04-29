package es.urjc.ist.concurstream;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;


public class UserTable implements Serializable {
private ConcurrentHashMap<String, String> schedule;

public UserTable()
{
    this.schedule = new ConcurrentHashMap<>();   
    
    this.schedule.put("Bruce Wayne", "ConcurrentPlayList0");
    this.schedule.put("Clark Kent", "ConcurrentPlayList0");
    this.schedule.put("Diana Prince", "ConcurrentPlayList0");
    this.schedule.put("Peter Parker", "ConcurrentPlayList1");    
    this.schedule.put("Steve Rogers", "ConcurrentPlayList1"); 
}


public void añadirUser(String user, String playlist)
{
	if(this.schedule.get(user) != null)
	{
		System.out.println("Usuario ya registrado, prueba con otro");		
	}
	else
		this.schedule.put(user, playlist); 
}


public void cambiarValor(String key,String status) {
    this.schedule.replace(key,status);
}

public String getValor(String key){
    return this.schedule.get(key);
}


public ConcurrentHashMap<String, String> getSchedule(){return this.schedule;}

}