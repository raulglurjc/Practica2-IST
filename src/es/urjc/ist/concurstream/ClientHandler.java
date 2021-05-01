package es.urjc.ist.concurstream;


import java.io.*;
import java.util.Scanner;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

/**
 * Clase que implementa un gestor de clientes que conectan con
 * nuestro servidor. En este caso, implementa también la interfaz
 * {@link Runnable}, para poder cargar la tarea en un hilo del
 * pool proporcionado por el ExecutorService de EchoServer. 
 * 
 * @author Felipe Ortega
 *
 */
public class ClientHandler implements Callable<String> {
	
private static final String CHARSET_NAME = "UTF-8";
	
	private Socket clientSocket;
	public UserTable bd;
	public ConcurrentPlayList playlists;

	/**
	 * Constructor recibe como parámetro el objeto Socket
	 * para atender a un nuevo cliente.
	 * 
	 * @param clientSocket El Socket para atención del cliente.
	 */
	public ClientHandler(Socket clientSocket, UserTable bd, ConcurrentPlayList playlists) {
		this.clientSocket = clientSocket;
		this.bd = bd;
		this.playlists = playlists;
	}
	
	/**
	 * Método para atención de un cliente que ha conectado con el
	 * servidor. La atención consiste meramente en rebotar hacia el
	 * cliente el mensaje que se ha recibido.
	 * @throws InterruptedException 
	 */
	@Override
	public String call() throws InterruptedException {
		Scanner scanner;
        PrintWriter out;
        
		// Configurar streams de E/S
        try {
            InputStream is = clientSocket.getInputStream();
            scanner = new Scanner(new BufferedInputStream(is), CHARSET_NAME);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        }
        catch (IOException ioe) {
            throw new IllegalArgumentException("Imposible crear " + clientSocket, ioe);
        }

        // nextLine() se bloquea hasta recibir una nueva línea del cliente
        String line = null;
        try {//Con este try catch vamos a esperar a que el cliente introduzca un usuario por teclado, cuando lo haya hecho, lo recibiremos aqui y procederemos a buscarlo en la base de datos para verificar si existe.
            line = scanner.nextLine();
        } catch (NoSuchElementException e) {
            line = null;
        }
        if(this.bd.getValor(line)==null)//Si lo que el cliente ha introducido aparece como null en el mapa de la base de datos significa que no existe, por lo que se creará y se le vinculara con la ConcurrentPlayList1
        {
        	out.println(String.format("El usuario \"%s\" no existe en la bd, lo hemos añadido con referencia a ConcurrentPlayList1", line));
        	bd.añadirUser(line, "ConcurrentPlayList1"); 
        }
        	else // Si el usuario ya existe se informara al cliente de esto y nada mas.
        	out.println("Si existe en la bd, tiene referencia a "+bd.getSchedule().get(line)+"\n");
        
        
        String user = line;// Guardamos el cliente introducido y la playlist a la que está vinculado.
        String playlist = bd.getSchedule().get(user);
        
        //Al igual que en el programa del cliente, tenemos que mantener la sincronia de las cosas que se van ejecutando
        //Vamos a entrar en el bucle infinito del menu que solo se saldrá si el cliente escribe exit por el teclado.
        while(true)
        {
     // nextLine() se bloquea hasta recibir una nueva línea del cliente
        line = null;
        try {
            line = scanner.nextLine();
        } catch (NoSuchElementException e) {
            line = null;
        }
        if(line.equals("exit"))//Si el cliente introduce exit, se saldrá del bucle mediante break y se acabará el programa.
        	break;
        if(line.equals("1"))//Si el cliente introduce un 1, se va a imprimir el nombre de la playlist a la que está vinculado y una lista de las peliculas que en ella se alojan accediendo al mapa de la base de datos que registra las playlist
        {
        	String contenido="";
        	out.println(String.format("Su playlist tiene nombre \"%s\"",playlist));
        	out.println(String.format("Hay %d peliculas", playlists.getSchedule().get(playlist).getPeliculas().size()));
        	out.println("\t Contenido:");
        	
        	for(int i = 0; i<playlists.getSchedule().get(playlist).getPeliculas().size(); i++)
        	{
        		out.println("\t\t * "+playlists.getSchedule().get(playlist).getPeliculas().get(i));
        		
                	
        	}
        	out.println(contenido+"%%%");//Token %%% para identificar cuando se acaba la informacion a enviar al cliente.
        }
        if(line.equals("2"))//Si el cliente introduce un 2 se le solicitará que introduzca por teclado una pelicula para añadir a la lista a la que está vinculado
        {
        	String linea = null;
            try {
                linea = scanner.nextLine();
            } catch (NoSuchElementException e) {
                linea = null;
            }
            playlists.añadirPelicula(linea, playlist);//Uso del metodo añadir pelicula la cual accede al mapa de la base de datos e introduce una nueva instancia.
            Thread.sleep(2000);
            
        }
        if(line.equals("3"))// Si el cliente introduce un 3 significa que va a borrar una pelicula de la playlist. Se le solicitara un listado con todas las peliculas que esta alberga
        {
        	
        	out.println("Eliminar pelicula de "+bd.getSchedule().get(user));
        	for(int i = 0; i<playlists.getSchedule().get(playlist).getPeliculas().size(); i++)
        	{
        		out.println("\t "+(i+1)+"- "+playlists.getSchedule().get(playlist).getPeliculas().get(i));
        		
        	}
        	out.println("%%%");
        	
        	while(true)//Bucle de control de errores, si el numero introducido para seleccionar una de las peliculas de la lista no existe, se continuará solicitando una pelicula valida para ser borrada.
        	{
        	String ss = null;
            try {
                ss = scanner.nextLine();
            } catch (NoSuchElementException e) {
                ss = null;
            }
           
            
            int seleccion = Integer.parseInt(ss);
            if(seleccion!=0 && seleccion<=playlists.getSchedule().get(playlist).getPeliculas().size())
            {
            	out.println("OK");
            	playlists.eliminarPelicula(seleccion, playlist);
                Thread.sleep(2000);
                break;
            }
            else
            {
            	out.println("ERROR");
            	continue;
            }
        	}
            
            
        }
        
        
        
        }
        
        
        // Cerrar streams de E/S y después el socket de atención al cliente
        System.out.println("Cerrando recursos de comuniación...");
        out.close();
        scanner.close();
        try {
        	clientSocket.close();
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
        System.out.println("¡Cliente atendido con éxito!");
        
        // Devolvemos el string recibido al thread principal
        // del servidor
        System.out.println(line);
        return line;

	}

}
