package server;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author ivanns
 */
public class Server {

    /**
     * @param args the command line arguments
     */
    private static int valpo= 7200;
    public static void main(String[] args) throws IOException {
        try{
            ServerSocket skts = new ServerSocket(valpo);
            while (true){
                Socket cl= skts.accept();
                Clientes Tcl= new Clientes(cl);
                Tcl.start();
            }
        }
        catch(IOException e) {
            System.out.println(e);
        }
    }

    private static class Clientes extends Thread{
        private Socket cliente=null;
        private PrintWriter os=null;
        private String nombre;
        private String ipDir;
        private String puerto;
        public Clientes(Socket cl) {
            cliente=cl;
        }
        
        //aqui se establece una nueva conexion con el servidor y se establecen los parametros de conexion
        public void run(){
            try{
                BufferedReader in=new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                os = new PrintWriter(new OutputStreamWriter(cliente.getOutputStream(),"8859_1"),true);
                String ruta= "";//Se guarda la cadena de la peticion para luego procesarla y obtener la url
                int i =0;
                String next;
                ruta=in.readLine();
                do			
                {                    
                    if(i == 0) // aca en la primera linea, se sabe que fichero bajar.
                    {
                        i++;
                        StringTokenizer st = new StringTokenizer(ruta);
                        next= st.nextToken();
                       // System.out.println(st.countTokens());
                        if ((st.countTokens() >= 2) && next.equals("GET")) 
                        {
                            retornaFichero(st.nextToken()) ;
                        }
                        else if((st.countTokens()>=2)&&next.equals("POST")){
                            //database.setWritable(true);
                            //System.out.println(next);
                            //imprimirFichero(st.nextToken());
                            String currentLine =null;
                            do{
                                currentLine = in.readLine();
                                //System.out.println(currentLine);             
                                if((currentLine.indexOf("Content-Disposition:")) != -1){
                                    if(currentLine.indexOf("nombre")!= -1){//revisa si nombre=nombre con el form del html
                                        currentLine= in.readLine();//al hacer println se observa que existe muchas lineas de informacion innecearia
                                        currentLine= in.readLine();//hasta llegar al nombre buscado, se saltan todas esas lineas de informacion
                                        nombre=currentLine;
                                        //System.out.println(" Este es el nombre");
                                    }
                                    else if(currentLine.indexOf("ipDir")!= -1){//revisa si nombre=ipdir con el form del html
                                        currentLine= in.readLine();//al igual que arriba, se elimina informacion innecesaria
                                        currentLine= in.readLine();//
                                        ipDir=currentLine;
                                        //System.out.println(" esta es la IP");
                                    }
                                    else if(currentLine.indexOf("puerto")!= -1){//REVISA SI TIENE EL nombre=PUERTO PUESTO EN EL FORM DEL HTML
                                        currentLine= in.readLine();//
                                        currentLine= in.readLine();//
                                        puerto=currentLine;
                                        //System.out.println(" aqui guardp el nombre");
                                    }
                                }
                            }while(in.ready());
                            next= st.nextToken();
                            if(next.equals("/insertar.html")||next.equals("/mostrar.html")){
                                retornaFichero(next);
                            }
                            else if(next.equals("/menu.html")){
                                imprimirFichero(next,nombre,ipDir,puerto);
                            }
                           
                        }
                        else 
                        {
                            os.println("400 Petición Incorrecta") ;
                        }
                    }
                    ruta=in.readLine();

                }while (ruta != null && ruta.length() != 0);
                
            }catch(Exception e){
                System.out.println( e);
            }
            
        }
        void retornaFichero(String sfichero)
	{
            
            String opciones;
            if (sfichero.startsWith("/"))
            {
                    sfichero = sfichero.substring(1) ;
            }
            
            if (sfichero.endsWith("/") || sfichero.equals(""))
            {
                    sfichero = sfichero + "menu.html" ;
            }
            //Como no se pueden enviar los datos leidos desde contactos.txt directamente un archivo .html, hice la pagina dentro del .java
            try
            {
                //se leen los datos desde contactos.txt y se muestran
                if(sfichero.equals("mostrar.html")){
                    String contactos="";
                    File cont= new File("contactos.txt");
                    if(cont.exists()){
                        StringTokenizer s;
                        BufferedReader fLocal= new BufferedReader(new FileReader(cont));
                        String lin="";
                        String comienzo="<html>\n" 
                        + " <head>\n" 
                            +"  <title>Mostar Datos</title>\n" 
                            +"  <meta charset=\"UTF-8\">\n" 
                            +"  <meta name=\"viewport\" content=\"width=device-width\">\n" 
                        +"  </head>\n" 
                        +"  <body>\n"
                            +"<center><h1>Avion de Papel</h1><h2>Mostrar Datos</h2></center>"     
                            +"<form method=\"POST\" action=\"insertar.html\">\n" 
                                +"<center> <table>"
                                + "<table>"
                                + "<tr>"
                                + "<td><center>Nombre</center></td><td><center>Mensaje</center></td>"
                                + "<tr>"
                                    +"<td>"
                                    + "<center><select name=\"contactos\" multiple=\"multiple\">";
                                            while((lin=fLocal.readLine())!=null){
                                                s= new StringTokenizer(lin);
                                                contactos= contactos+"<option>"+ s.nextToken()+"</option>\n";
                                                }
                                            String fin= "</select>\n </center>"
                                    + "</td>"
                                    + "<td>"
                                    + "     <textarea rows=\"4\" cols=\"50\"></textarea>    \n" 
                                    + "</td>"
                                + "</tr>"
                                + "<tr>"
                                    + "<td><input type=\"submit\" value=\"Agregar Contacto\">\n</td>" 
                                + "</tr>"
                            +   "</form>\n" 
                        +   "</body>\n" 
                    +   "</html>";
                        os.println(comienzo+contactos+fin);
                        fLocal.close();
                        os.close();
                    }
                    else{
                        os.println("HTTP/1.0 400 ok");
                        os.close();
                    }
                }
                else{
                File mifichero = new File(sfichero) ;
                
                if (mifichero.exists()) 
                {
                    BufferedReader ficheroLocal = new BufferedReader(new FileReader(mifichero));
                    String linea = "";
                    do			
                    {
                        linea = ficheroLocal.readLine();
                        if (linea != null )
                        {
                            os.println(linea);
                        }
                    }
                    while (linea != null);
                    ficheroLocal.close();
                    os.close();
                }  //termina si es que el fichero existe
                else
                {	
                    os.println("HTTP/1.0 400 OK");
                    os.close();
                }
                }

            }
            catch(Exception e){
            }
        }
        
        //Funcion en la cual se escriben los datos del form en el archivo contactos.txt
        private void imprimirFichero(String nextToken,String nombre1,String ipDir1,String puerto1) {
            Writer writer = null;
            try {
                writer = new BufferedWriter(new OutputStreamWriter(
                      new FileOutputStream("contactos.txt",true), "utf-8"));
                        writer.write("\r\n");
                        writer.write(nombre1+ " ");
                        writer.write(ipDir1+ " ");
                        writer.write(puerto1);
            } catch (IOException ex) {
              // report
            } finally {
               try {writer.close();} catch (Exception ex) {}
            }
            retornaFichero(nextToken);
            //lee archivo si no existe
            
        }
    }
    
}
