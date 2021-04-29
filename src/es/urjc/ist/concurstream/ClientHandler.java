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
        try {
            line = scanner.nextLine();
        } catch (NoSuchElementException e) {
            line = null;
        }
        if(this.bd.getValor(line)==null)
        {
        	out.println(String.format("El usuario \"%s\" no existe en la bd, lo hemos añadido con referencia a ConcurrentPlayList1", line));
        	bd.añadirUser(line, "ConcurrentPlayList1"); 
        }
        	else
        	out.println("Si existe en la bd, tiene referencia a "+bd.getSchedule().get(line)+"\n");
        
        
        String user = line;
        String playlist = bd.getSchedule().get(user);
        
        
        while(true)
        {
     // nextLine() se bloquea hasta recibir una nueva línea del cliente
        line = null;
        try {
            line = scanner.nextLine();
        } catch (NoSuchElementException e) {
            line = null;
        }
        if(line.equals("exit"))
        	break;
        if(line.equals("1"))
        {
        	String contenido="";
        	out.println(String.format("Su playlist tiene nombre \"%s\"",playlist));
        	out.println(String.format("Hay %d peliculas", playlists.getSchedule().get(playlist).getPeliculas().size()));
        	out.println("\t Contenido:");
        	
        	for(int i = 0; i<playlists.getSchedule().get(playlist).getPeliculas().size(); i++)
        	{
        		out.println("\t\t * "+playlists.getSchedule().get(playlist).getPeliculas().get(i));
        		
                	
        	}
        	out.println(contenido+"%%%");
        }
        if(line.equals("2"))
        {
        	String linea = null;
            try {
                linea = scanner.nextLine();
            } catch (NoSuchElementException e) {
                linea = null;
            }
            playlists.añadirPelicula(linea, playlist);
            Thread.sleep(2000);
            
        }
        if(line.equals("3"))
        {
        	
        	out.println("Eliminar pelicula de "+bd.getSchedule().get(user));
        	for(int i = 0; i<playlists.getSchedule().get(playlist).getPeliculas().size(); i++)
        	{
        		out.println("\t * "+playlists.getSchedule().get(playlist).getPeliculas().get(i));
        		
        	}
        	out.println("Seleccione el numero de la pelicula a eliminar: %%%");
        	
        	String ss = null;
            try {
                ss = scanner.nextLine();
            } catch (NoSuchElementException e) {
                ss = null;
            }
            
            int seleccion = Integer.parseInt(ss);
            playlists.eliminarPelicula(seleccion, playlist);
            Thread.sleep(2000);
            
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
