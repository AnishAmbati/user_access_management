package uam_pro.uam_pro;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

public class FileUtils {
	public String readFileData(String filePath,@Context HttpServletRequest req) {
		InputStream in=null;
		String ar[]=filePath.split("/");
		String loc=ar[0];
		String fileName=ar[1];
		if(loc.equals("webapp"))
			in=req.getServletContext().getResourceAsStream(fileName);
		else if(loc.equals("resources"))
			in=getClass().getClassLoader().getResourceAsStream(fileName);
		Scanner sc=new Scanner(in);
		String data="";
		while(sc.hasNextLine())
			data+=sc.nextLine()+"\n";
		sc.close();
		return data;
	}
	
	public String addDataAfter(int n,String data,String filePath,@Context HttpServletRequest req) {
    	String fileData=new FileUtils().readFileData(filePath,req);
    	List<String> lines=new ArrayList<>();
    	int lineCount=0;
    	for(String line:fileData.split("\n")) {
    		if(n==lineCount) {
    			lines.add(data);
    			lines.add(line);
    		}
    		else
    			lines.add(line);
    		lineCount++;
    	}
    	String updatedData="";
    	for(String line:lines)
    		updatedData+=line;
    	return updatedData;
    }

}
