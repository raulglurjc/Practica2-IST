package es.urjc.ist.concurstream;

import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
//Clase Lista de reproduccion (PLAYLIST)
public class Playlist 
{
	//Cada objeto tendra dos atributos, una lista para las peliculas y otra para las series
	//En cierta medida es una extension de la clase Catalogo pero lo he implementado asi.
	//El usuario ira a�adiendo peliculas o series a su playlist pero esto lo he implementado en la clase Usuario, aqui se le ofrece un metodo addPelicula o addSerie
	//En esta clase, en Playlist, se ofrecen dos metodos para actualizar la informacion de la ultima reproduccion que son addLast_Rep_Pelicula y addLast_Rep_Serie
	public List<String> peliculas;
	//Constructor por defecto en el que se crean las listas de peliculas y series vacias
	public Playlist() 
	{
		this.peliculas = new ArrayList<String>();
		
	}
	//Constructor para crear un objeto con los parametros que se le introducen
	public Playlist(List<String> peliculas) 
	{
		this.peliculas = peliculas;
		
	}
	
	//Get para obtener la lista de peliculas 
	public List<String> getPeliculas() 
	{
		return peliculas;
	}
	//Set para actualizar la lista de peliculas 
	public void setPeliculas(List<String> peliculas) 
	{
		this.peliculas = peliculas;
	}
	
	
	
}
