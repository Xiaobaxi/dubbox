package com.alibaba.dubbo.rpc.protocol.jaxrs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.apache.cxf.helpers.IOUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

@Produces({ "application/json", "application/*+json" })
@Consumes({ "application/json", "application/*+json" ,"application/fast-json"})
@Provider
public class FastJSONProvider implements MessageBodyWriter<Object>,MessageBodyReader<Object>{
//	private static final Logger LOG = LoggerFactory.getLogger(FastJSONProvider.class);
	private boolean enableReader=true;
	private String encoding="UTF-8";
	private SerializerFeature[]  feature=new SerializerFeature[0];
	private boolean writeClassNames = false;
	
	public FastJSONProvider(boolean enableRead,boolean writeClassNames){
		this.enableReader=enableRead;
		if(writeClassNames){
			setFeature(SerializerFeature.WriteClassName);
		}
	}
	
	static class ObjectJSON{
		private Object obj;
		private byte[] json;
	}
	
	final ThreadLocal<ObjectJSON> cache=new ThreadLocal<ObjectJSON>(){
		@Override
		protected ObjectJSON initialValue() {
			return new ObjectJSON();
		}
	};
	
	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public SerializerFeature[] getFeature() {
		return feature;
	}

	public void setFeature(SerializerFeature... feature) {
		this.feature = feature;
		// TODO 未引入common包
//		this.writeClassNames=ArrayUtils.contains(feature, SerializerFeature.WriteClassName);
	}
	
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return mediaType.getSubtype().endsWith("json");
	}

	public long getSize(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		ObjectJSON json=cache.get();
		json.obj=t;
		try {
			json.json=JSON.toJSONString(t,feature).getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return json.json.length;
	}

	public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
		if(writeClassNames){
			httpHeaders.putSingle("Content-Type", "application/fast-json");	
		}
		ObjectJSON json=cache.get();
		if(json.obj==t){
			IOUtils.copy(new ByteArrayInputStream(json.json), entityStream);
			json.obj=null;
			json.json=null;
		}else{
			JSON.writeJSONStringTo(t, new OutputStreamWriter(entityStream, encoding),feature);
		}
	}

	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		if(mediaType.getSubtype().endsWith("fast-json"))return true;
		return enableReader && mediaType.getSubtype().endsWith("json");
	}

	public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
		String s=IOUtils.toString(entityStream);
		return JSON.parseObject(s, genericType);
	}
}
