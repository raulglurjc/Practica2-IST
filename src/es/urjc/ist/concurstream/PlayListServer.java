package es.urjc.ist.concurstream;


import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import es.urjc.ist.concurstream.ClientHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Clase que implementa un servidor concurrente de eco.
 * Se implementa mediante la API de alto nivel para
 * programación concurrente, usando una instancia con
 * interfaz de {@link ExecutorService}, creada mediante
 * un factory method de la clase {@link Executors}.
 * 
 * @author Felipe Ortega
 *
 */
public class PlayListServer {
	
	private static final int NCLIENTES = 1;
	
	private int port; // Puerto al que se ata el servidor
	private ServerSocket serverSocket; // Socket del servidor
	private ExecutorService pool; // Pool de threads para ejecución concurrente
	private List<Future<String>> resultList; // Lista para recoger los valores devueltos
	public static UserTable bd;
	public static ConcurrentPlayList playlists;
	/**
	 * El nuevo constructor de objetos servidor de eco. Permite encapsular
	 * la funcionalidad del servidor en un objeto, en lugar de delegar todo
	 * el código al método main.
	 * 
	 * @param port Número de puerto en que queda escuchando el servidor.
	 * @param nThreads Tamaño del pool de threads para atención concurrente
	 * de clientes.
	 */
	public PlayListServer(int port, int nThreads) {
		this.port = port;
		serverSocket = null;
		this.bd = new UserTable();
		this.playlists = new ConcurrentPlayList();
		/* Creamos un nuevo ExecutorService, usando un factory
         * method de la clase Executors. En concreto, creamos
         * un pool de threads de tamaño fijo con newFixedThreadPool.
         */
        pool = Executors.newFixedThreadPool(nThreads);
	}
	
	/**
	 * Método getter para obtener el puerto al que está atado el servidor.
	 * 
	 * @return int Puerto al que está atado el servidor.
	 */
	public int getPort() {
		return port;
	}
	public List<Future<String>> getResultList() {
		return resultList;
	}
	
	/**
	 * Método para atar el servidor a un puerto, a la espera de clientes.
	 * 
	 * @throws IOException
	 */
	public void bindServer() throws IOException {
		this.serverSocket = new ServerSocket(port);
	}
	
	/**
	 * Método para atender a los clientes que llegan al servidor. Los clientes
	 * son atendidos mediante tareas de la clase ClientHandler, enviadas a un
	 * pool de threads para ejecución concurrente.
	 * 
	 * Tras acabar de aceptar los clientes configurados, el método cierra el pool
	 * de threads y espera a que finalicen las tareas pendients.
	 * 
	 * @param nCli Número de clientes que atiende el servidor antes de finalizar.
	 */
	public void atiendeClientes(int nCli, UserTable bd, ConcurrentPlayList playlists) {
		// Inicializa lista para resultados
		resultList = new ArrayList<Future<String>>(nCli);

		// Esperar a las conexiones de los clientes y procesar
        // Procesa NCLIENTES conexiones entrantes antes de finalizar
        for (int count = 0; count < NCLIENTES; count++) {
        	System.err.println("Esperando nuevo cliente...");
            // La llamada a accept() es bloqueante hasta que llegue un cliente
        	Socket clientSocket = null;
        	try {
        		clientSocket = serverSocket.accept();
        	} catch (IOException ex) {
            	ex.printStackTrace();
            }
            System.err.println("Aceptada nueva conexión de un cliente, procesando...");
            
            // Ahora, aquí lanzamos un nuevo hilo para atención del cliente
            ClientHandler handler = new ClientHandler(clientSocket, bd, playlists);
            
            /* La tarea que atiende al cliente (objeto de clase ClientHandler)
             * ya está lista. Ahora, se envía al pool de threads del ExecutorService
             * para ejecutarla. El resultado se recogerá en un objeto Future, que
             * añadimos a la lista del servidor.
             */
            resultList.add(this.pool.submit(handler));
            
        }
    
	}
	
	/**
	 * Método para cerrara el socket del servidor.
	 * 
	 * @throws IOException
	 */
	public void closeServer() throws IOException {
		serverSocket.close();
	}
	
	/**
	 * Método para cerrar ordendamante las actividades de un
	 * ExecutorService, en dos fases: primero evita que se envíen
	 * nuevas tareas y luego solicita que acaben tareas pendientes
	 * que todavía sigan en ejecución.
	 * 
	 * Tomado del ejemplo incluido en la documentación de Java 11
	 * para la clase ExecutorService:
	 * 
	 * https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ExecutorService.html
	 * 
	 * Se ha modificado para parametrizar la duración de la primera
	 * y segunda fase de espera, como argumentos de entrada al método.
	 * 
	 * @param pool Pool de threads {@link ExecutorService} que queremos cerrar.
	 * @param firstTimeout Timeout en segundos para la primera fase de espera.
	 * @param secondTimeout Timeout en segundos para la segunda fase de espera
	 */
	public void shutdownAndAwaitTermination(int firstTimeout, int secondTimeout) {
		pool.shutdown(); // Evita que se envíen nuevas tareas
		try {
			// Esperamos un poco a que terminen las tareas pendientes
			if (!pool.awaitTermination(firstTimeout, TimeUnit.SECONDS)) {
				System.err.println("No han terminado la tareas. Forzando cierre...");
				pool.shutdownNow(); // Cancela cualquier tarea pendiente en ejecución
				// Espera un poco a que las tareas pendientes respondan
				// a la petición de cancelación
				if (!pool.awaitTermination(secondTimeout, TimeUnit.SECONDS))
					System.err.println("El pool no terminó.");
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancela el pool si el thread actual se interrumpe
			pool.shutdownNow();
			// Preserva el estado de interrupción
			Thread.currentThread().interrupt();
		}
	}

	
	/*
	 *  Método principal
	 */
	public static void main(String[] args) {
		// Crear el objeto servidor
		PlayListServer server = new PlayListServer(15000, NCLIENTES);
		
		
		
        // Ahora atamos el ServerSocket al puerto para recibir peticiones
        // entrantes de clientes
        try {
        	server.bindServer();
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
        System.err.println("Inciado servidor en el puerto " + server.getPort());
        
        // Atendemos a NCLIENTES
        server.atiendeClientes(NCLIENTES, bd, playlists);
        
        /* Ahora, debemos asegurarnos de que todos los hilos creados han finalizado 
         * antes de terminar el programa. De otro modo, el hilo principal acaba y 
         * con ello el proceso completo, matando a todos los hilos creados dentro.
         * Podemos usar el método que sugiere la documentación de Java 11 para la
         * clase ExecutorService, que encapsula dos fases de espera: primero evitando
         * que se envíen nuevas tareas, y luego solicitando que acabe cualquier tarea
         * pendiente.
         */
        
        System.err.println("Esperando a que finalicen todos los hilos de atención a clientes...");
        // Esperamos 60 segundos en la primera fase y otros 60 en la segunda fase
        server.shutdownAndAwaitTermination(60, 60);
        
        // Finaliza el servidor, liberamos recursos
        System.err.println("Ya he atendido a " + NCLIENTES + "clientes, fin del programa");
        System.err.println("Liberando el socket del servidor...");
        try {
        	server.closeServer();
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
        
        // Por último, imprimimos todos los resultados recibidos
        for (Future<String> task : server.resultList) {
        	if(task.isDone()) {
        		try {
        			System.out.println("Resultado: " + task.get());
        		} catch (InterruptedException | ExecutionException ex) {
        			System.out.print("El hilo fué interrumpido o se produjo "+
        		                     "un fallo durante la ejecución.");
        			ex.printStackTrace();
        		}
        	} else {
        		System.out.println("La tarea no terminó.");
        	}
        }
        Enumeration<String> enu_user = bd.getSchedule().keys();
        Enumeration<String> enu_playlist = bd.getSchedule().elements();
		System.out.println("Playlist Registradas:\n");
		for(int i=0; i<bd.getSchedule().size(); i++)
		{
			System.out.println("\t -Usuario: "+ enu_user.nextElement() + " -Playlist: "+enu_playlist.nextElement());
		}
		
		
		Enumeration<String> enu_lista= playlists.getSchedule().keys();
        Enumeration<Playlist> enu_playlists = playlists.getSchedule().elements();
		for(int i=0; i<playlists.getSchedule().size(); i++)
		{
			String nombre_lista = enu_lista.nextElement();
			System.out.println("\n\n\t - "+ nombre_lista);
			for(int j=0; j<playlists.getSchedule().get(nombre_lista).getPeliculas().size(); j++)
			{
				System.out.println("\t\t *Pelicula "+(j+1)+": "+playlists.getSchedule().get(nombre_lista).getPeliculas().get(j));
			}
		}
        System.err.println("Finaliza el servidor.");
	}

}
