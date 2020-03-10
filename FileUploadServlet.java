package edu.qc.cs;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import java.sql.*;

@WebServlet("/FileUploadServlet")
@MultipartConfig (fileSizeThreshold=1024*1024*10,     // 10 MB 
					maxFileSize=1024*1024*50,          // 50 MB
					maxRequestSize=1024*1024*100)       // 100 MB
			   
public class FileUploadServlet extends HttpServlet {
	
	// Name of directory for file to be uploaded to
	private static final String UPLOAD_DIR = "uploads";
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
			// Gets absolute path of the web application
			String applicationPath = request.getServletContext().getRealPath("");
			
			// Constructs path of the directory to save uploaded file
			String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;
			
			// Creates the upload directory if it does not exists
			File fileSaveDir = new File(uploadFilePath);
			if (!fileSaveDir.exists()) {
				fileSaveDir.mkdirs();
			}
			
			String fileName = "";
			//Get all the parts from request and write it to the file on server
			for (Part part : request.getParts()) {
				fileName = getFileName(part);
				fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
				part.write(uploadFilePath + File.separator + fileName);
			}
			
			String content = new Scanner(new File(uploadFilePath + File.separator + fileName)).useDelimiter("\\Z").next();
			response.getWriter().write("File uploaded successfully");
			
			// Uploading file to database
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				Connection con = DriverManager.getConnection("jdbc:mysql://IP/?verifyServerCertificate=false&useSSL=true", user, pw);
			} 
			catch (Exception e) {
				System.out.println(e);
			}
      
      //request.setAttribute("message", "File uploaded successfully!");
      //getServletContext().getRequestDispatcher("/response.jsp").forward(
      //        request, response);
	}
	
	// Utility method to get file name from HTTP header content-disposition
	private String getFileName(Part part) {
      String contentDisp = part.getHeader("content-disposition");
      String[] tokens = contentDisp.split(";");
      for (String token : tokens) {
          if (token.trim().startsWith("filename")) {
              return token.substring(token.indexOf("=") + 2, token.length()-1);
          }
      }
      return "";
  }
  
  
    private void writeToResponse(HttpServletResponse resp, String results) throws IOException {
        PrintWriter writer = new PrintWriter(resp.getOutputStream());
        resp.setContentType("text/plain");

        if (results.isEmpty()) {
            writer.write("No results found.");
        } else {
            writer.write(results);
        }
        writer.close();
    }    
    
    
}


