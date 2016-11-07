

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Conectar {
	private Connection con; //Obj Conexion
	private String datosConexion;
	private ArrayList listaSuscriptores=new ArrayList();
	
	public ArrayList getSuscriptores(){
		return this.listaSuscriptores;
	}
	
	public Conectar() throws SQLException{
		this.datosConexion = "jdbc:sqlserver://crm-s:1433;" + "databaseName=SAI_CHEVROLET;user=ENGAGE_RANDAZZO; password=@SAENG@2008;";}

	public void Abrir(){
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			con = DriverManager.getConnection(datosConexion);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("NO SE PUDO CONECTAR A LA BASE DE DATOS");
		}
	}
	
	public void Cerrar(){
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
      
    public void consultaTablaSuscriptores() throws Exception{      	
    	String consultaSQL= "select Suscriptor,LOWER(email) from sai_chevrolet.dbo.CarteraAhorristas where email != '' and email != 'NULL' ";
      	Statement st=null;
      	ResultSet rs=null;
      	this.Abrir();
        try {
			st = con.createStatement();
			rs = st.executeQuery(consultaSQL);
			muestraDatos(rs);
		} 
        catch (SQLException e) {
			e.printStackTrace();
		}
        finally{
        	 if (rs != null) try { rs.close(); } catch(Exception e) {}
             if (st != null) try { st.close(); } catch(Exception e) {}
             if (con != null) try { con.close(); } catch(Exception e) {}
        }       
      }      
     
    private void muestraDatos(ResultSet r)throws Exception{
    	FileWriter archivoSuscriptores = null;//utilizo un fichero para guardar un registro de los suscriptores.
        PrintWriter pw = null; 	
    	int cantSuscriptores=0;
    	String text;
    	archivoSuscriptores = new FileWriter("E:/emailSuscripcionRandazzo/Suscriptores.txt");
        pw = new PrintWriter(archivoSuscriptores);
        pw.println("NOMBRE Y APELLIDO" + " - " +  " EMAIL ");//archivo
        while(r.next()){       	
        		text=r.getString(2);
        		Suscriptor sus= new Suscriptor(r.getString(1),text.replace(" ",""));
    			this.agregarSuscriptor(sus);
    			pw.println(r.getString(1) + " - " +  r.getString(2));//archivo
    			cantSuscriptores++;  		
    	}
    	pw.println("--------------------------------------");
    	pw.println("Total De Emails Enviados: " + cantSuscriptores);

    	if (null != archivoSuscriptores)
            archivoSuscriptores.close();
    }  
    
    public void agregarSuscriptor(Suscriptor s){
    	this.listaSuscriptores.add(s);    	
    }
          
    public void listarSuscriptores(){
      Suscriptor sus;
      int size=listaSuscriptores.size();
		for(int x=0; x<size; x++) {			
			System.out.println(((Suscriptor)listaSuscriptores.get(x)).getEmail());
		}
    }
}

	
	

