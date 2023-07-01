/* https://github.com/caio-silva/hibp (C)2023 */
package com.github.caiosilva.hibp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.caiosilva.hibp.api.HIPB;
import com.github.caiosilva.hibp.api.HaveIBeenPwndBuilder;
import com.github.caiosilva.hibp.entity.APIAccount;
import com.github.caiosilva.hibp.entity.APIPlan;
import com.github.caiosilva.hibp.exception.HaveIBeenPwndException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Application {

	static boolean run = false;
	static Thread threadWhile;
	public static String logDirPath = System
			.getProperty( "user.home" ) + File.separator + ".hipb" + File.separator + "logs";

	public static void main( String[] args ) throws IOException {
		createLogDirectory();
		runServer();
	}

	private static void runServer() throws IOException {
		int port = 7777;
		String ip = "localhost";

		HttpServer server = getServer( ip, port );
		server.createContext( "/down", createShutdownHandler( server ) );
		server.createContext( "/logs", createLogFileHandler() );
		server.createContext( "/index", createIndexHtmlHandler() );
		server.createContext( "/static", createStaticFileHandler() );

		server.start();

		run = true;

		runMetered();

		System.out.println( "Server started on http://" + ip + ":" + port );
	}

	private static void runMetered() {
		HIPB hibp = getHIBP();

		threadWhile = new Thread( () -> {
			while ( run ) {
				try {
					hibp.getAllPastesForAccount( "account-exists@hibp-integration-tests.com" );
				} catch ( HaveIBeenPwndException ignore ) {
				}
			}
		} );

		threadWhile.start();
	}

	private static HIPB getHIBP() {
		final String apiKey = System.getenv( "HIBP_API_KEY" );
		APIAccount account = APIAccount.builder().key( apiKey ).plan( APIPlan.RPM10 ).build();
		return HaveIBeenPwndBuilder.create().withAccount( account ).build();
	}

	private static HttpServer getServer( String ip, int port ) throws IOException {
		InetSocketAddress address = new InetSocketAddress( ip, port );
		return HttpServer.create( address, 0 );
	}

	private static HttpHandler createShutdownHandler( HttpServer server ) {
		return new HttpHandler() {
			@Override
			public void handle( HttpExchange exchange ) throws IOException {
				server.stop( 0 );
				threadWhile.interrupt();
				System.out.println( "Server stopped" );
				System.exit( 0 );
			}
		};
	}

	private static void createLogDirectory() {
		File logDir = new File( logDirPath );
		if ( !logDir.exists() ) {
			boolean created = logDir.mkdirs();
			if ( !created ) {
				System.err.println( "Failed to create log directory: " + logDirPath );
			}
		}
	}

	public static HttpHandler createLogFileHandler() {
		return exchange -> {
			String formattedDate = LocalDate.now()
					.format( DateTimeFormatter.ofPattern( "yyyy-MM-dd" ) );
			String fileNamePattern = logDirPath + "/mixed-" + formattedDate + ".log";
			List<String> lines = readLogFile( logDirPath );
			String responseBody = String.join( "\n", lines );
			exchange.getResponseHeaders().put( "Content-Type", List.of( "text/plain" ) );
			exchange.sendResponseHeaders( 200, responseBody.getBytes().length );
			exchange.getResponseBody().write( responseBody.getBytes() );
			exchange.close();
		};
	}

//	private static List<String> readLogFile( String fileNamePattern ) {
//		List<String> lines = new ArrayList<>();
//		try {
//			// Get the current date in the format yyyy-MM-dd
//			String currentDate = LocalDate.now().format( DateTimeFormatter.ISO_DATE );
//			String fileName = fileNamePattern.replace( "%d{yyyy-MM-dd}", currentDate );
//			Path filePath = Paths.get( fileName );
//
//			if ( Files.exists( filePath ) ) {
//				lines = Files.readAllLines( filePath );
//			}
//		} catch ( IOException e ) {
//			// Handle the exception as needed
//			e.printStackTrace();
//		}
//		return lines;
//	}
private static List<String> readLogFile(String directoryPath) {
	List<String> lines = new ArrayList<>();

	try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directoryPath))) {
		List<Path> filePaths = new ArrayList<>();

		// Iterate over all files in the directory
		for (Path path : directoryStream) {
			if (Files.isRegularFile(path)) {
				filePaths.add(path);
			}
		}

		// Sort the file paths based on their last modified time (oldest to newest)
		filePaths.sort(Comparator.comparingLong(path -> {
			try {
				return Files.getLastModifiedTime(path).toMillis();
			} catch (IOException e) {
				e.printStackTrace();
				return 0L;
			}
		}));

		// Read the content of each file and add it to the lines list
		for (Path path : filePaths) {
			List<String> fileLines = Files.readAllLines(path);
			lines.addAll(fileLines);
		}
	} catch (IOException e) {
		e.printStackTrace();
	}

	return lines;
}
//	private static List<String> readLogFile(String fileNamePattern) {
//		List<String> lines = new ArrayList<>();
//		try {
//			// Extract the directory path from the file name pattern
//			String directoryPath = fileNamePattern.substring(0, fileNamePattern.lastIndexOf('/'));
//
//			// Get a stream of file paths in the directory
//			Path directory = Paths.get(directoryPath);
//			Stream<Path> filePaths = Files.list(directory);
//
//			// Filter and sort the file paths based on their names
//			List<Path> sortedFilePaths = filePaths
//					.filter(Files::isRegularFile)
//					.filter(path -> path.getFileName().toString().matches(fileNamePattern))
//					.sorted(Comparator.comparing(Path::getFileName))
//					.collect(Collectors.toList());
//
//			// Read the lines from each file and concatenate them
//			for (Path filePath : sortedFilePaths) {
//				List<String> fileLines = Files.readAllLines(filePath);
//				lines.addAll(fileLines);
//			}
//		} catch (IOException e) {
//			// Handle the exception as needed
//			e.printStackTrace();
//		}
//		return lines;
//	}

	public static HttpHandler createIndexHtmlHandler() {
		return exchange -> {
			// Set the Content-Type header
			exchange.getResponseHeaders().set( "Content-Type", "text/html" );

			// Read the content of the index.html file
			try ( InputStream inputStream = Application.class
					.getResourceAsStream( "/index.html" ) ) {
				if ( inputStream != null ) {
					byte[] fileBytes = inputStream.readAllBytes();

					// Set the response status and content length
					exchange.sendResponseHeaders( 200, fileBytes.length );

					// Write the file content to the response body
					exchange.getResponseBody().write( fileBytes );
				} else {
					// Resource not found
					String response = "Resource not found";
					exchange.sendResponseHeaders( 404, response.length() );
					exchange.getResponseBody().write( response.getBytes( StandardCharsets.UTF_8 ) );
				}
			} catch ( IOException e ) {
				// Error reading the resource
				String response = "Error reading resource";
				exchange.sendResponseHeaders( 500, response.length() );
				exchange.getResponseBody().write( response.getBytes( StandardCharsets.UTF_8 ) );
			}

			// Close the response
			exchange.close();
		};
	}

	public static HttpHandler createStaticFileHandler() {
		return exchange -> {
			String resourcePath = exchange.getRequestURI().getPath();

			try ( InputStream inputStream = Application.class
					.getResourceAsStream( resourcePath ) ) {
				if ( inputStream != null ) {
					exchange.sendResponseHeaders( 200, 0 );
					try ( OutputStream outputStream = exchange.getResponseBody() ) {
						byte[] buffer = new byte[8192];
						int bytesRead;
						while ( ( bytesRead = inputStream.read( buffer ) ) != -1 ) {
							outputStream.write( buffer, 0, bytesRead );
						}
					}
				} else {
					exchange.sendResponseHeaders( 404, 0 );
				}
			} catch ( IOException e ) {
				exchange.sendResponseHeaders( 500, 0 );
				e.printStackTrace();
			} finally {
				exchange.close();
			}
		};
	}
}
