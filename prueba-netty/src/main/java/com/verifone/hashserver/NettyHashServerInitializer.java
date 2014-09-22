package com.verifone.hashserver;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

import com.verifone.hashserver.ssl.SecureSslContextFactory;

public class NettyHashServerInitializer extends
		ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {

	    ChannelPipeline p = ch.pipeline();
		
	    SSLEngine engine = SecureSslContextFactory.getServerContext().createSSLEngine();
        engine.setUseClientMode(false);
        engine.setNeedClientAuth(true);
	    p.addLast("ssl", new SslHandler(engine));
	    
	    p.addLast(new HttpRequestDecoder());
        // Uncomment the following line if you don't want to handle HttpChunks.
        p.addLast(new HttpObjectAggregator(1048576));
        p.addLast(new HttpResponseEncoder());
        // Remove the following line if you don't want automatic content compression.
        //p.addLast(new HttpContentCompressor());
		
		p.addLast(new HashMessageDecoder());
		p.addLast(new HashMessageEncoder());
		p.addLast(new NettyHashServerHandler());
	}

}
