import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

class RequestProcessor implements Runnable {
	private Socket socket = null;
	private OutputStream os = null;
	private BufferedReader in = null;
	private DataInputStream dis = null;
	private String msgToClient = "HTTP/1.1 200 OK\n" + "Server: HTTP server/0.1\n"
			+ "Access-Control-Allow-Origin: *\n\n";
	private JSONObject jsonObject = new JSONObject();

	public RequestProcessor(Socket Socket) {
		super();
		try {
			socket = Socket;
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			os = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {

		List<String> matches = new ArrayList<String>();
		
		String line = "";
		int left = 0;
		int right = 0;
		String operation = "";
		int result = 0;
		String expression = "";
		
		try {

			line = in.readLine();

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		Pattern pattern = Pattern.compile("[leftOperand|rightOperand]=[0-9]+");
		Matcher matcher = pattern.matcher(line);
		
		Pattern pattern2 = Pattern.compile("operation=[p|-|+|*|/|%]");
		Matcher matcher2 = pattern2.matcher(line);
		
		while (matcher.find()) {
	        matches.add(matcher.group(0));
	    }
		
		while (matcher2.find()) {
		    String output = matcher2.group(0);
		    String[] splitOperation = output.split("=");
		    operation = splitOperation[1];
		}
		
		String[] splitLeft = matches.get(0).split("=");
		String[] splitRight = matches.get(1).split("=");
		
		left = Integer.parseInt(splitLeft[1]);
		right = Integer.parseInt(splitRight[1]);
		
		if (operation.equals("p")) {
			result = left + right;
			expression = left + "+" + right;
		} else if (operation.equals("-")) {
			result = left - right;
			expression = left + "-" + right;
		} else if (operation.equals("*")) {
			result = left * right;
			expression = left + "*" + right;
		} else if (operation.equals("/")) {
			result = left / right;
			expression = left + "/" + right;
		} else if (operation.equals("%")) {
			result = left % right;
			expression = left + "%" + right;
		}
		
		System.out.println(left);
		System.out.println(right);
		System.out.println(operation);
		System.out.println(result);
		System.out.println(expression);
		
		jsonObject.put("result", result);
		jsonObject.put("expression", expression);

		String response = msgToClient + jsonObject.toString();
		try {
			os.write(response.getBytes());
			os.flush();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
