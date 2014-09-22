package com.verifone.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;

import com.verifone.hashserver.NettyHashServer;



@ComponentScan
public class Main {
	
	NettyHashServer server;
	
	public Main() {
		// TODO Auto-generated constructor stub
		server = new NettyHashServer();
	}
	public static void main(String[] args) throws Exception {
		Main app = new Main();
		app.server.run();
	}
}
