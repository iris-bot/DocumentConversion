package br.com.irisbot.apis.docconv;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.tika.Tika;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;

@WebServlet("/TextFromFile")
@MultipartConfig(	fileSizeThreshold=1024*1024*2, // 2MB
					maxFileSize=1024*1024*10,      // 10MB
					maxRequestSize=1024*1024*50)   // 50MB
public class TextFromFile extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public TextFromFile() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Part file = request.getPart("file");
		File f = File.createTempFile("docconv", "file");
		Files.copy(file.getInputStream(), f.toPath(), StandardCopyOption.REPLACE_EXISTING);
		String content = "";
		try{
			TikaInputStream tis = TikaInputStream.get(f.toPath());
			Tika tika = new Tika();

			BodyContentHandler handler = new BodyContentHandler();
	        Metadata metadata = new Metadata();
			ParseContext parseContext = new ParseContext();
			
			Parser parser = new AutoDetectParser(tika.getDetector());
			parser.parse(tis, handler, metadata, parseContext);
			
			content = handler.toString();

			try{
				tis.close();
				f.delete();
			}catch (Exception e) {}
			
		}catch (Exception e) {
			content = e.toString();
		}
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.getWriter().println(content);
	}

}
