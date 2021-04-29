package es.urjc.ist.concurstream;


import java.io.*;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Clase que implementa un cliente de eco sencillo.
 * 
 * @author Felipe Ortega
 *
 */
public class Client {
	
	private static final String CHARSET_NAME = "UTF-8";
	public static boolean isNumeric(String cadena) {

        boolean resultado;

        try {
            Integer.parseInt(cadena);
            resultado = true;
        } catch (NumberFormatException excepcion) {
            resultado = false;
        }

        return resultado;
    }
    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 15000;
        Scanner scanner;
        PrintWriter out;
        
        // Conectar al servidor y abrir sockets y streams
        Socket socket = new Socket(host, port);
        
        // Configurar streams de E/S
        Scanner stdin = new Scanner(new BufferedInputStream(System.in), CHARSET_NAME);
        try {
            InputStream is = socket.getInputStream();
            scanner = new Scanner(new BufferedInputStream(is), CHARSET_NAME);
            out = new PrintWriter(socket.getOutputStream(), true);
        }
        catch (IOException ioe) {
        	socket.close(); stdin.close();
            throw new IllegalArgumentException("Imposible crear " + socket, ioe);
        }
        
        System.err.println("Connectado al servidor " + host + " en el puerto " + port);
        ///////////////////////////////////////////////////////////////////////////////////
        // Comunicación con el servidor a través del socket
        String s;
        System.out.print("Introduce el mensaje: ");
        // Leer línea desde consola
        try {
        	s = stdin.nextLine();
        }
        catch (NoSuchElementException e) {
        	s = null;
        }
        // Enviar la línea al servidor
        out.println(s);

        // Esperamos hasta que leemos una linea escrita por el servidor a traves del socket
        String line = null;
        try {
        	line = scanner.nextLine();
        }
        catch (NoSuchElementException e) {
        	line = null;
        }
        // Imprimimos lo que nos devuelve el servidor
        System.out.println(line);
       //////////////////////////////////////////////////////////////////////////////
        
        
        
        
        
        while(true)
        {
        
        System.out.println("*******************************************************************");
        System.out.println("MENU");

        System.out.println("\n\t-1: Leer el contenido de la playlist");
        System.out.println("\n\t-2: Añadir pelicula de la playlist");
        System.out.println("\n\t-3: Quitar pelicula de la playlist");
        System.out.print("\nSeleccione una accion: ");
        
     // Leer línea desde consola
        try {
        	s= null;
        	s = stdin.nextLine();
        }
        catch (NoSuchElementException e) {
        	s = null;
        }
 
        System.out.println("*******************************************************************");
        if(s.equals("exit"))
        {
        	out.println(s);
        	break;
        }
        if(s.equals("1"))
        {
        // Enviar la línea al servidor
        out.println(s);
        
        
        try {
        	while(scanner.hasNext())
        	{
        		
        		line = null;
        		line = scanner.nextLine();
        		if(line.indexOf("%%%")==-1)        		
        			System.out.println(line);
        		else
        		{
        			line = line.substring(0, line.indexOf("%%%"));
        			System.out.println(line);
        			break;
        		}
                
        	}
        }
        catch (NoSuchElementException e) {
        	
        	line = null;
        }
        
        
        
        }
        if(s.equals("2"))
        {
        	out.println(s);
        	System.out.print("Escribe la pelicula que quieres añadir:");
        	String ss= null;
        	try {
            	
            	ss = stdin.nextLine();
            }
            catch (NoSuchElementException e) {
            	ss = null;
            }
        	out.println(ss);
        }
        
        if(s.equals("3"))
        {
        	
        	out.println(s);
        	try {
            	while(scanner.hasNext())
            	{
            		
            		line = null;
            		line = scanner.nextLine();
            		if(line.indexOf("%%%")==-1)        		
            			System.out.println(line);
            		else
            		{
            			line = line.substring(0, line.indexOf("%%%"));
            			System.out.print(line);
            			break;
            		}
                    
            	}
            }
            catch (NoSuchElementException e) {
            	
            	line = null;
            }
        	
        	String ss= null;
        	try {
            	
            	ss = stdin.nextLine();
            }
            catch (NoSuchElementException e) {
            	ss = null;
            }
        
        	out.println(ss);
        }
        
        
        
        }
        
        
        
        Thread.sleep(1000);
        // Cerrar streams de E/S y después el socket
        System.err.println("Cerrando la conexión a " + host);
        out.close();
        scanner.close();
        socket.close();
        stdin.close();
        System.err.println("Recursos liberados, finaliza el programa cliente.");
    }
}
