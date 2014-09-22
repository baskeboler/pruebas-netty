package com.verifone.victor.pruebas;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettyHashServerHandler extends
		SimpleChannelInboundHandler<HashRequest> {

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HashRequest msg)
			throws Exception {
		// TODO Auto-generated method stub
		HashResponse response = new HashResponse();
		response.setHash("hash de ejemplo");
		ctx.write(response);
		ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);

	}

}
