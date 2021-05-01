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
public class Client {// Clase del cliente
	
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
	
	//Programa que ejecutará cada cliente para conectarse al servidor.
    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 15000;
        
        Scanner scanner; //el scanner nos proporciona la capacidad de comunicarse con otros procesos, en concreto el servidor, al cual escuchará para obtener información de los datos solicitados. 
        PrintWriter out;// La variable out nos ayudará a enviar los datos de este mismo programa a otro proceso, en nuestro caso se lo enviaremos al servidor para que nos rebote informacion
        
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
        System.out.print("Introduce el usuario: ");
        // Leer línea desde consola
        try {//Mediante este try catch, vamos a solicitar al cliente que introduzca por teclado el nombre de usuario
        	s = stdin.nextLine();
        }
        catch (NoSuchElementException e) {
        	s = null;
        }
        // Enviar la línea al servidor
        out.println(s);//Una vez el cliente haya introducido el usuario, se lo enviaremos al servidor

        // Esperamos hasta que leemos una linea escrita por el servidor a traves del socket
        String line = null;
        try {//Mediante este try catch vamos a esperar a que el servidor nos respona si el usuario introducido anteriormente está registrado en la base de datos o no
        	line = scanner.nextLine();
        }
        catch (NoSuchElementException e) {
        	line = null;
        }
        // Imprimimos lo que nos devuelve el servidor
        System.out.println("\n"+line);
       //////////////////////////////////////////////////////////////////////////////
        
        Thread.sleep(2000); // Esperamos unos segundos 
        
        for (int i = 0; i < 50; ++i) System.out.println();
        //Entramos en el bucle del menu, el cual solo están disponibles los numeros 1,2,3 y exit para salir del bucle y acabar el programa.
        while(true)
        {
        
        System.out.println("*******************************************************************");
        System.out.println("MENU");

        System.out.println("\n\t(1) Leer el contenido de la playlist");
        System.out.println("\n\t(2) Añadir pelicula de la playlist");
        System.out.println("\n\t(3) Quitar pelicula de la playlist");
        System.out.println("\n\t(exit) Salir del programa.");
        System.out.print("\nSeleccione una accion: ");
        
        
     // Leer línea desde consola
        try {//Leemos la accion que desee el usuario
        	s= null;
        	s = stdin.nextLine();
        }
        catch (NoSuchElementException e) {
        	s = null;
        }
 
        System.out.println("*******************************************************************");
        for (int i = 0; i < 30; ++i) System.out.println();
        if(s.equals("exit"))//Si lo que el usuario a introducido es "exit" vamos a salir del bucle mediante break y se acabara el programa
        {
        	out.println(s);
        	break;
        }
        else if(s.equals("1"))//Si el cliente introduce un 1 se ejecuta la siquiente rutina
        {
        // Enviar la línea al servidor
        out.println(s);//Indicamos al servidor que ha introducido un 1
        
        
        try {
        	while(scanner.hasNext())//Esperamos a que el servidor nos rebote toda la informacion que tiene que mandarnos.
        		// Como este bloque me fallaba, ya que el servidor tiene que enviar muchas lineas, detecto si existe un token %%% en las lineas a enviar, si esto sucede significa que la linea que lo contenga será la ultima a enviar, muy chapuza pero efectivo para salir del paso.
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
        else if(s.equals("2"))// Mismo procedimiento si el cliente introduce un 2, a continuacion el cliente tendrá que introducir una pelicula para añadirla a la playlist a la que esta vinculado.
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
        
        else if(s.equals("3"))//Mismo procedimiento si el cliente introduce un 3 procederá a eliminar una de las peliculas que están alojadas dentro de la playlist a la que el cliente esta vinculado
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
        	while(ss==null)
        	{
        		System.out.print("Seleccione el numero de la pelicula a eliminar: ");
        	try {
            	
            	ss = stdin.nextLine();
            	if(isNumeric(ss)==false)
            	{
            		ss=null;
            		System.out.print("Introduce un numero valido!");
            	}
            	else
            	{
            		out.println(ss);
            		line = null;
                    try {
                    	line = scanner.nextLine();
                    }
                    catch (NoSuchElementException e) {
                    	line = null;
                    }
                    if(line.equals("OK"))
                    {
                    	break;
                    }
                    else
                    {
                    	System.out.println("Introduce un numero valido!\n");
                    	ss=null;
                    }
                    }
            		
            		
            }
            catch (NoSuchElementException e) {
            	ss = null;
            }
        	}
        	
        	
        }
        else
        	System.out.println("Introduce un numero valido!");
        Thread.sleep(3000);
        
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
