///A Simple Web Server (WebServer.java)

package http.server;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import javax.imageio.ImageIO;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * 
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 * 
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer {

	/**
	 * WebServer constructor.
	 */
	protected void start() {
		ServerSocket s;

		System.out.println("Webserver starting up on port 80");
		System.out.println("(press ctrl-c to exit)");
		try {
			// create the main server socket
			s = new ServerSocket(1234);
		} catch (Exception e) {
			System.out.println("Error: " + e);
			return;
		}

		System.out.println("Waiting for connection");
		for (;;) {
			try {
				// wait for a connection
				Socket remote = s.accept();
				// remote is now the connected socket
				System.out.println("Connection, sending data.");
				BufferedReader in = new BufferedReader(new InputStreamReader(remote.getInputStream()));
				PrintWriter out = new PrintWriter(remote.getOutputStream());
				BufferedOutputStream outputStream = new BufferedOutputStream(remote.getOutputStream());
				BufferedInputStream inputStream = new BufferedInputStream(remote.getInputStream());

				// read the data sent. We basically ignore it,
				// stop reading once a blank line is hit. This
				// blank line signals the end of the client HTTP
				// headers.
				String str = ".";
				String request = "";
				String ressource = "";
				while (str != null && !str.equals("")) {
					str = in.readLine();
					request = request + str + "\n";
					if (str.startsWith("GET") || str.startsWith("PUT") || str.startsWith("POST") || str.startsWith("HEAD")|| str.startsWith("DELETE")) {
						ressource = str;
					}
				}
				if (request.startsWith("GET")) {
					ressource = ressource.replace("GET /", "");
					ressource = ressource.replace(" HTTP/1.1", "");
					System.out.println(request);
					requestGET(ressource, out, outputStream);
					request = "";
					remote.close();
				} else if (request.startsWith("PUT")) {
					ressource = ressource.replace("PUT /", "");
					ressource = ressource.replace(" HTTP/1.1", "");
					System.out.println(request);
					requestPUT(ressource, out, outputStream, inputStream);
					request = "";
					remote.close();
				} else if (request.startsWith("POST")) {
					ressource = ressource.replace("POST /", "");
					ressource = ressource.replace(" HTTP/1.1", "");
					System.out.println(request);
					requestPOST(ressource, out, outputStream, inputStream);
					request = "";
					remote.close();
				} else if (request.startsWith("HEAD")) {
					System.out.println("HEAD ressource: " + ressource);
					ressource = ressource.replace("HEAD /", "");
					ressource = ressource.replace(" HTTP/1.1", "");
					System.out.println("HEAD request: " + request);
					requestHEAD(ressource, out);
					request = "";
					remote.close();
				} else if (request.startsWith("DELETE")) {
					ressource = ressource.replace("DELETE /", "");
					ressource = ressource.replace(" HTTP/1.1", "");
					System.out.println(request);
					requestDELETE(ressource, out);
					request = "";
					remote.close();
				}

			} catch (Exception e) {
				System.out.println("Error: " + e);
			}
		}
	}

	public String getContentType(String extension) {

		String contentType = null;

		if (extension.equals(".html") || extension.equals(".htm"))
			contentType = "text/html";

		else if (extension.equals(".jpeg") || extension.equals(".jpg") || extension.equals(".png") || extension.equals(".gif"))
			contentType = "image/" + extension.replace(".", "");

		else if (extension.equals(".css"))
			contentType = "text/css";

		else if (extension.equals(".pdf"))
			contentType = "application/pdf";
		
		else if (extension.equals(".zip"))
			contentType = "application/zip";

		else if (extension.equals(".odt"))
			contentType = "application/vnd.oasis.opendocument.text";

		else if (extension.equals(".mp4") || extension.equals(".mov"))
			contentType = "video/mp4";
		
		else if (extension.equals(".mp3")|| extension.equals(".m4a"))
			contentType = "audio";

		return contentType;

	}

	public void requestGET(String ressource, PrintWriter out, BufferedOutputStream outPutStream) {

		try {
			String filePath = "C:\\Users\\drape\\Documents\\4IF\\Programmation réseaux\\Prog_reseaux_HTTP\\TP-HTTP-Code\\lib\\"
					+ ressource;
			File file = new File(filePath);
			int fileLength = (int) file.length();
			String extension = "";
			if (ressource.contains(".")) {
				extension = ressource.substring(ressource.indexOf("."));
			}

			if (ressource.equals("")) {
				// send the headers
				out.println("HTTP/1.0 200 OK");
				out.println("Content-Type: text/html");
				out.println("Server: Bot");
				// this blank line signals the end of the headers
				out.println("");
				// Send the HTML page
				out.println("<H1>Welcome to the Ultra Mini-WebServer</H1>");
				out.flush();

			} else if (file.exists() && file.isFile()) {
				// send the headers

				out.println("HTTP/1.0 200 OK");
				out.println("Content-Type :" + getContentType(extension));
				out.println("Server: Bot");
				out.println("Content-Length: " + fileLength);
				out.println("");
				out.flush();
				try {

					// Content
					byte[] buffer = new byte[fileLength];
					FileInputStream fileInputStream = null;
					try {
						fileInputStream = new FileInputStream(file);
						fileInputStream.read(buffer);
					} finally {
						if (fileInputStream != null)
							fileInputStream.close();
					}
					outPutStream.write(buffer, 0, fileLength);

					outPutStream.flush();
				} catch (Exception e) {

				}

			} else {
				// send the headers
				out.println("HTTP/1.0 404 Not Found");
				out.println("Content-Type: text/html");
				out.println("Server: Bot");
				// this blank line signals the end of the headers
				out.println("");
				// Send the HTML page
				out.println("<H1>ERREUR 404 NOT FOUND </H1>");
				out.println("<H2>Fichier introuvable </H2>");
				out.flush();

			}
		} catch (Exception e) {
			try {
				out.println("HTTP/1.0 500 Internal Server Error");
				out.println("Content-Type: text/html");
				out.println("Server: Bot");
				// this blank line signals the end of the headers
				out.println("");
				// Send the HTML page
				out.println("<H1>ERREUR 500 Internal Server Error </H1>");
				out.println("<H2>Erreur interne du Serveur </H2>");
				out.flush();
			} catch (Exception ex) {

			}
		}
	}


	public void requestPOST(String ressource, PrintWriter out, BufferedOutputStream outPutStream, BufferedInputStream inputStream) {
		
		try {

			File file = new File("\\lib\\" + ressource);
			Boolean exists = file.exists();
			Boolean isFile = file.isFile();
			BufferedOutputStream fileOutput = new BufferedOutputStream(new FileOutputStream(file, file.exists()));
			int fileLength = (int) file.length();
			byte[] buffer = new byte[fileLength];

			while (inputStream.available() > 0) {
				int nbRead = inputStream.read(buffer);
				fileOutput.write(buffer, 0, nbRead);
			}
			fileOutput.flush();
			fileOutput.close();
			if(exists && isFile) {
				out.println("HTTP/1.0 200 OK");
				out.println("Content-Type: text/html");
				out.println("Server: Bot");
				// this blank line signals the end of the headers
				out.println("");
				// Send the HTML page
				out.println("<H1>Post Rï¿½ussi </H1>");
				out.flush();
			}else {
				out.println("HTTP/1.0 201 Created");
				out.println("Content-Type: text/html");
				out.println("Server: Bot");
				// this blank line signals the end of the headers
				out.println("");
				// Send the HTML page
				out.println("<H1>Post Rï¿½ussi </H1>");
				out.flush();
			}
			

		} catch (Exception e) {
			out.println("HTTP/1.0 500 Internal Server Error");
			out.println("Content-Type: text/html");
			out.println("Server: Bot");
			// this blank line signals the end of the headers
			out.println("");
			// Send the HTML page
			out.println("<H1>ERREUR 500 Internal Server Error </H1>");
			out.println("<H2>Erreur interne du Serveur </H2>");
			out.flush();
		}

	}

	public void requestPUT(String ressource, PrintWriter out, BufferedOutputStream outputStream, BufferedInputStream inputStream) {
		
	}

	public void requestHEAD(String ressource, PrintWriter out) {

		try {
			String filePath = "C:\\Users\\drape\\Documents\\4IF\\Programmation réseaux\\Prog_reseaux_HTTP\\TP-HTTP-Code\\lib\\"
					+ ressource;
			File file = new File(filePath);
			int fileLength = (int) file.length();
			String extension = "";
			if (ressource.contains(".")) {
				extension = ressource.substring(ressource.indexOf("."));
			}

			if (ressource.equals("")) {
				// send the headers
				out.println("HTTP/1.0 200 OK");
				out.println("Content-Type: text/html");
				out.println("Server: Bot");
				out.println("");
				out.flush();

			} else if (file.exists() && file.isFile()) {
				// send the headers

				out.println("HTTP/1.0 200 OK");
				out.println("Content-Type :" + getContentType(extension));
				out.println("Server: Bot");
				out.println("Content-Length: " + fileLength);
				out.println("");
				out.flush();
				
			} else {
				// send the headers
				out.println("HTTP/1.0 404 Not Found");
				out.println("Content-Type: text/html");
				out.println("Server: Bot");
				out.println("");
				out.flush();
			}
		} catch (Exception e) {
			try {
				out.println("HTTP/1.0 500 Internal Server Error");
				out.println("Content-Type: text/html");
				out.println("Server: Bot");
				out.println("");
				out.flush();
			} catch (Exception ex) {

			}
		}
	}

	public void requestDELETE(String ressource, PrintWriter out) {
		try {
			String filePath = "C:\\Users\\drape\\Documents\\4IF\\Programmation réseaux\\Prog_reseaux_HTTP\\TP-HTTP-Code\\lib\\"
					+ ressource;
			File file = new File(filePath);
			int fileLength = (int) file.length();
			String extension = "";
			if (ressource.contains(".")) {
				extension = ressource.substring(ressource.indexOf("."));
			}

			if (ressource.equals("")) {
				// send the headers
				out.println("HTTP/1.0 200 OK");
				out.println("Content-Type: text/html");
				out.println("Server: Bot");
				out.println("");
				out.println("<H1>You can't delete the main page.</H1>");
				out.flush();

			} else if (file.exists() && file.isFile()) {
				if (file.delete()) {
					// send the headers
					out.println("HTTP/1.0 200 OK");
					out.println("Content-Type :" + getContentType(extension));
					out.println("Server: Bot");
					out.println("Content-Length: " + fileLength);
					out.println("");
					out.println("<H1>File has been deleted.</H1>");
					out.flush();
				} else {
					// send the headers
					out.println("HTTP/1.0 403 Forbidden");
					out.println("Content-Type :" + getContentType(extension));
					out.println("Server: Bot");
					out.println("Content-Length: " + fileLength);
					out.println("");
					out.println("<H1>You don't have the right to delete the file.</H1>");
					out.flush();
				}
				
			} else {
				// send the headers
				out.println("HTTP/1.0 404 Not Found");
				out.println("Content-Type: text/html");
				out.println("Server: Bot");
				out.println("");
				out.println("<H1>File not found. It can't be deleted.</H1>");
				out.flush();
			}
		} catch (Exception e) {
			try {
				out.println("HTTP/1.0 500 Internal Server Error");
				out.println("Content-Type: text/html");
				out.println("Server: Bot");
				out.println("");
				out.flush();
			} catch (Exception ex) {

			}
		}
	}

	/**
	 * Start the application.
	 * 
	 * @param args Command line parameters are not used.
	 */
	public static void main(String args[]) {
		WebServer ws = new WebServer();
		ws.start();
	}
}
