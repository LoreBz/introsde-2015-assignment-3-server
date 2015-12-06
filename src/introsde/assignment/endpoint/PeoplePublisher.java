package introsde.assignment.endpoint;

import introsde.assignment.soap.PeopleImpl;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.xml.ws.Endpoint;

public class PeoplePublisher {

	public static void main(String[] args) throws UnknownHostException {
		String PROTOCOL = "http://";
		String HOSTNAME = InetAddress.getLocalHost().getHostAddress();
		if (HOSTNAME.equals("127.0.0.1")) {
			HOSTNAME = "localhost";
		}
		String PORT = "6902";
		String BASE_URL = "/ws/people";

		if (String.valueOf(System.getenv("PORT")) != "null") {
			PORT = String.valueOf(System.getenv("PORT"));
		}

		String endpointUrl = PROTOCOL + HOSTNAME + ":" + PORT + BASE_URL;
		System.out.println("Starting People Service...");
		System.out.println("--> Published. Check out " + endpointUrl + "?wsdl");
		Endpoint.publish(endpointUrl, new PeopleImpl());
	}

}
