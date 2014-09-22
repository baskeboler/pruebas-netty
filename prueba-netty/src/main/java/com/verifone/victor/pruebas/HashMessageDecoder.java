package com.verifone.victor.pruebas;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.CharsetUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class HashMessageDecoder extends MessageToMessageDecoder<HttpRequest> {

	@Override
	protected void decode(ChannelHandlerContext ctx, HttpRequest msg,
			List<Object> out) throws Exception {
		HashRequest req = new HashRequest();

		ByteBuf buffer = ((FullHttpRequest)msg).content();
		String content = buffer.toString(CharsetUtil.UTF_8);
		content = getDataFromXmlString(content);
		req.setHttpRequest(msg);
		req.setData(content); // de donde saco el content?
		out.add(req);
	}
	
	private String getDataFromXmlString(String xml) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbFactory.newDocumentBuilder();
		Document doc = db.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
		return doc.getElementsByTagName("my-data").item(0).getTextContent();
		
	}



}
