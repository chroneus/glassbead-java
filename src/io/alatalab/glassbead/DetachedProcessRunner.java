package io.alatalab.glassbead;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DetachedProcessRunner  {
private String command;
private String errorMessage="",outputMessage="";
private int exitValue;
public DetachedProcessRunner(String command) {
	
	
	this.command = command;

		StreamReader output,error;
		try{ 
        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(command);
        //  error 
        error = new StreamReader(proc.getErrorStream());            
        
        // output
        output = new StreamReader(proc.getInputStream());
            
        error.start();
        output.start();                        
        exitValue = proc.waitFor(); 
        errorMessage=error.getOutput();
        outputMessage=output.getOutput();
    } catch (Throwable t)
      {
    	errorMessage+=t.toString();
    //    t.printStackTrace();
      }
     error=null;
     output=null;

	}
	public String getCommand() {
		return command;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public String getOutputMessage() {
		return outputMessage;
	}
	public int getExitValue() {
		return exitValue;
	}
	public boolean isError(){
		return !errorMessage.isEmpty();
	}

//read stream from process	
class StreamReader extends Thread
	{
	    InputStream is;
	    StringBuffer out= new StringBuffer();
	    
	    StreamReader(InputStream is)
	    {
	        this.is = is;
	    }
	    
	    public void run()
	    {
	        try
	        {
	            InputStreamReader isr = new InputStreamReader(is);
	            BufferedReader br = new BufferedReader(isr);
	            String line=null;
	            while ( (line = br.readLine()) != null)
	            	out.append(line+"\r\n"); 
	            } catch (IOException ioe)
	              {
	                ioe.printStackTrace();  
	              }
	    }
	    public String getOutput(){
	    	return new String(out);
	    }
	}
	
	
}

