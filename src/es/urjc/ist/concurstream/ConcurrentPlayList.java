package es.urjc.ist.concurstream;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class ConcurrentPlayList implements Serializable  {
private ConcurrentHashMap<String, Playlist> schedule;

public ConcurrentPlayList()//Constructor por defecto que genera 2 playlist, ConcurrentPlayList0/1 cada una con sus respectivas listas de peliculas.
{
    this.schedule = new ConcurrentHashMap<>();   
    
    List<String> peliculas0 = new ArrayList<String>();
    List<String> peliculas1 = new ArrayList<String>();;
    peliculas0.add("SpiderMan");
    peliculas0.add("El hombre de acero");
    peliculas1.add("Gru");
    peliculas1.add("Los increibles");
    Playlist playlist0 = new Playlist(peliculas0);
    Playlist playlist1 = new Playlist(peliculas1);
    this.schedule.put("ConcurrentPlayList0", playlist0);
    this.schedule.put("ConcurrentPlayList1", playlist1);
}


public void añadirConcurrentPlaylist(String playlist, Playlist lista)//Añadir una nueva fila a la base de datos
//Si la playlist ya está registrada se informará de ello, si no, se reflejará en el mapa
{
	if(this.schedule.get(playlist) != null)
	{
		System.out.println("Playlist ya registrada, prueba con otra");		
	}
	else
		this.schedule.put(playlist, lista); 
}
public void añadirPelicula(String pelicula, String playlist)//Añadir pelicula a una playlist del mapa de la base de datos
{
	Playlist lista = this.schedule.get(playlist);
	lista.getPeliculas().add(pelicula);
	this.schedule.replace(playlist,lista);
	
}
public void eliminarPelicula(int seleccion, String playlist)//Eliminar pelicula de una playlist del mapa de la base de datos
{
	Playlist lista = this.schedule.get(playlist);
	lista.getPeliculas().remove(seleccion-1);
	this.schedule.replace(playlist,lista);
	
}

public ConcurrentHashMap<String, Playlist> getSchedule(){return this.schedule;}

}



