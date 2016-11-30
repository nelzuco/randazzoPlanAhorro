

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class HtmlMail {
	/**
	* Algunas constantes
	*/
	static public int SIMPLE = 0;
	static public int MULTIPART = 1;
	/**
	* Algunos mensajes de error
	*/
	public static String ERROR_01_LOADFILE = "Error al cargar el fichero";
	public static String ERROR_02_SENDMAIL = "Error al enviar el mail";
	/**
	* Variables
	*/
	private Properties props = new Properties();
	private String host= "smtp.office365.com";
	private String protocol= "smtp";
	private String user="";
	private String password="";	
	private String from="";//DESDE
	private String addressCC="";//Con Copia - CC
	private String direccionRespuesta="";	//Direccion de Respuesta
	private String content,to;
	private String subject="Oferta Licitación - Plan de Ahorro Chevrolet"; //ASUNTO DEL EMAIL
	
	private String[] mes={"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};//calendario
	/**
	* MultiPart para crear mensajes compuestos
	*/
	MimeMultipart multipart = new MimeMultipart("related");
	// -----
	/**
	* Constructor
	* @param host nombre del servidor de correo
	* @param user usuario de correo
	* @param password password del usuario
	*/
	public HtmlMail()
	{
	props = new Properties();
	props.put("mail.smtp.host", this.host);
	props.put("mail.smtp.starttls.enable", "true");
	props.put("mail.smtp.port",587);//25
	props.put("smtp.office365.com",this.user);	
	props.put("mail.smtp.user", "");
	props.put("mail.smtp.auth", "true");
	props.setProperty("mail.transport.protocol", this.protocol);
	props.setProperty("mail.password", password); 
	}
	//-----
	/**
	* Muestra un mensaje de trazas
	*
	* @param metodo
	* nombre del metodo
	* @param mensaje
	* mensaje a mostrar
	*/
	static public void trazas(String metodo, String mensaje) {
	// TODO: reemplazar para usar Log4J
	System.out.println("[" + HtmlMail.class.getName() + "][" + metodo
	+ "]:[" + mensaje + "]");
	}
	// -----
	/**
	* Carga el contenido de un fichero de texto HTML en un String
	*
	* @param pathname
	* ruta del fichero
	* @return un String con el contenido del fichero
	* @throws Exception
	* Excepcion levantada en caso de error
	*/	
	static public String loadHTMLFile(String dirigidoA, String month) throws Exception
	{
	String pathname= "E:/emailPlanCuotasRandazzo/index-plan-cuotas.html"; //RUTA DEL HTML
	String content = "";
	File f = null;
	BufferedReader in = null;
	try
	{
		f = new File(pathname);
		if (f.exists())
		{
			long len_bytes = f.length();
			trazas("loadHTMLFile", "pathname:" + pathname + ", len:"+ len_bytes);
		}
		in = new BufferedReader(new FileReader(f));
		String str;
		while ((str = in.readLine()) != null) {
			// process(str);
			str = str.trim();
			if (str.contains("Hola")){ str= "Hola: " + dirigidoA +", ";}//agrega el nombre y apellido del suscriptor
			if (str.contains("mes")){ str="<em>" + month +"</em>es el d&iacute;a <em>" + "(dia)</em>, la podes descargar de la p&aacute;gina" ;}
	
			content = content + str;
		}
		in.close();
		return content;
	}
	catch (Exception e){
		String MENSAJE_ERROR = ERROR_01_LOADFILE + ": ['" + pathname + "'] : " + e.toString();
	throw new Exception(MENSAJE_ERROR);
	}
	finally
	{
		if (in != null) in.close();
	}
	}
		/**
		* Añade el contenido base al multipart
		* @throws Exception Excepcion levantada en caso de error
		*/
		public void addContentToMultipart() throws Exception
		{
		// first part (the html)
		BodyPart messageBodyPart = new MimeBodyPart();
		String htmlText = this.getContent();
		messageBodyPart.setContent(htmlText, "text/html");
		// add it
		this.multipart.addBodyPart(messageBodyPart);
		}
		// -----
		/**
		* Añade el contenido base al multipart
		* @param htmlText contenido html que se muestra en el mensaje de correo
		* @throws Exception Excepcion levantada en caso de error
		*/
		public void addContent(String htmlText) throws Exception
		{
		// first part (the html)
		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(htmlText, "text/html");
		// add it
		this.multipart.addBodyPart(messageBodyPart);
		}
		// -----
		/**
		* Añade al mensaje un cid:name utilizado para guardar las imagenes referenciadas en el HTML de la forma <img src=cid:name />
		* @param cidname identificador que se le da a la imagen. suele ser un string generado aleatoriamente.
		* @param pathname ruta del fichero que almacena la imagen
		 * @throws MessagingException 
		* @throws Exception excepcion levantada en caso de error
		*/
		
		//AGREGA IMAGEN CON UN ID EMBEBIDO EN EL HTML
		public void addCID(String cidname,String pathname) throws Exception
		{
			DataSource fds = new FileDataSource(pathname);
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setDataHandler(new DataHandler(fds));
			messageBodyPart.setHeader("Content-ID","<image>");
			this.multipart.addBodyPart(messageBodyPart);
		}
		// ----
		/**
		* Añade un attachement al mensaje de email
		* @param pathname ruta del fichero
		* @throws Exception excepcion levantada en caso de error
		*/
		public void addAttach(String pathname) throws Exception
		{
			File file = new File(pathname);
			BodyPart messageBodyPart = new MimeBodyPart();
			DataSource ds = new FileDataSource(file);
			messageBodyPart.setDataHandler(new DataHandler(ds));
			messageBodyPart.setFileName(file.getName());
			messageBodyPart.setDisposition(Part.ATTACHMENT);
			this.multipart.addBodyPart(messageBodyPart);
		}
		// ----
		/**
		* Envia un correo multipart
		* @throws Exception Excepcion levantada en caso de error
		*/
		public void sendMultipart() throws Exception
		{
		Session mailSession = Session.getDefaultInstance(this.props);
		mailSession.setDebug(true);
		Transport transport = mailSession.getTransport(this.protocol);
		MimeMessage message = new MimeMessage(mailSession);
		message.setSubject(this.getSubject());
		message.setFrom(new InternetAddress(this.getFrom()));
		message.setReplyTo(new javax.mail.Address[]{new javax.mail.internet.InternetAddress(this.getDireccionRespuesta())});//DIRECCION RESPUESTA
		message.addRecipients(Message.RecipientType.CC, this.getAddressCC());// DIRECCION CC
		message.addRecipients(Message.RecipientType.TO,this.getTo());		//DIRECCION TO
		// put everything together
		message.setContent(this.multipart);//se adjunta el HTML Y ADJUNTOS	
		transport.connect((String)props.get(this.host), this.password);
		transport.sendMessage(message,message.getAllRecipients());
		transport.close();
		}
		// -----
		/**
		* Envia un correo simple
		* @throws Exception Excepcion levantada en caso de error
		*/
		public void send() throws Exception
		{
		try
		{
		Session mailSession = Session.getDefaultInstance(this.props, null);
		mailSession.setDebug(true);
		Transport transport = mailSession.getTransport();
		MimeMessage message = new MimeMessage(mailSession);
		message.setSubject(this.getSubject());
		message.setFrom(new InternetAddress(this.getFrom()));//setea el email "desde"		
		message.setRecipients(Message.RecipientType.CC,this.getAddressCC() );//setea el CC		
		message.setReplyTo(new javax.mail.Address[]{new javax.mail.internet.InternetAddress(this.getDireccionRespuesta())});//setea la direccion de respuesta
		message.setContent(this.getContent(), "text/html");
		message.addRecipient(Message.RecipientType.TO,new InternetAddress(this.getTo()));	
		transport.connect();
		transport.sendMessage(message,
		message.getRecipients(Message.RecipientType.TO));
		transport.close();
		}
		catch(Exception e)
		{
		String MENSAJE_ERROR = ERROR_02_SENDMAIL+" : " + e.toString();
		throw new Exception(MENSAJE_ERROR);
		}
		}
				
		
		//GETTERS SETTERS
		public String getAddressCC() {
			return addressCC;
		}
		//-----
		public String getContent() {
		return content;
		}
		public void setContent(String content) {
		this.content = content;
		}
		public String getFrom() {
		return from;
		}
		public void setFrom(String from) {
		this.from = from;
		}
		public String getSubject() {
		return subject;
		}
		public void setSubject(String subject) {
		this.subject = subject;
		}
		public String getTo() {
		return to;
		}
		public void setTo(String to) {
		this.to = to;
		}
		public void setAddressCC(String addressCC) {
			this.addressCC = addressCC;
		}
		public String getDireccionRespuesta() {
			return direccionRespuesta;
		}
		public void setDireccionRespuesta(String direccionRespuesta) {
			this.direccionRespuesta = direccionRespuesta;
		}
		
		
		
		
}
		// end of class HTMLMail


