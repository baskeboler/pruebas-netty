package com.verifone.victor.pruebas;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class HashMessageEncoder extends MessageToMessageEncoder<HashResponse> {

	@Override
	protected void encode(ChannelHandlerContext ctx, HashResponse msg,
			List<Object> out) throws Exception {
		
		StringBuilder sb = new StringBuilder();
		sb.append("<hash-response><response-code>0</response-code><hash>");
		sb.append(msg.getHash()).append("</hash></hash-response>");
		ByteBuf buffer = Unpooled.copiedBuffer(sb.toString(), StandardCharsets.UTF_8);
		HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buffer);
		out.add(response);
	}

}
