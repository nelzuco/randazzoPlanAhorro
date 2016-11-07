
import java.sql.Date;
import java.util.ArrayList;

import javax.swing.SingleSelectionModel;

public class main {
	public static void main(String[] args)throws Exception {
		// TODO Auto-generated method stub
		try {			
			String[] mes={"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};
			java.util.Date fecha = new java.util.Date();//creo una clase Date para que me de la fecha actual
			int m= fecha.getMonth(); //obtengo el numero de mes actual
			Conectar con= new Conectar();
			con.Abrir();
			con.consultaTablaSuscriptores();
			ArrayList sus= con.getSuscriptores();
		    int size=sus.size();	    	
	
		    for(int x=0; x<size; x++) {		    	
		    	HtmlMail mail = new HtmlMail();
		    	String contenidoHTML = HtmlMail.loadHTMLFile(((Suscriptor)sus.get(x)).getNyap(),mes[m]);//		    	
		    	mail.addContent(contenidoHTML);
		    	mail.addCID("image","E:/emailPlanCuotasRandazzo/kiara_1.jpg");
				mail.setTo(((Suscriptor)sus.get(x)).getEmail());//((Suscriptor)sus.get(x)).getEmail()
				mail.sendMultipart();
				Thread.sleep(70000);//thread sleep (4000) 4 segundos
		}			
			System.out.println("[ Finalizo Correctamente ]");
			} catch (Exception e) {
			e.printStackTrace();
			}
}


}
