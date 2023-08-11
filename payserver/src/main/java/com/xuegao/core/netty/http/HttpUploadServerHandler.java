/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.xuegao.core.netty.http;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.ServerCookieEncoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.core.netty.CmdHandler;
import com.xuegao.core.netty.ISendResponse;
import com.xuegao.core.netty.User;

public class HttpUploadServerHandler extends SimpleChannelInboundHandler<HttpObject> implements ISendResponse{

    private static final Logger logger = Logger.getLogger(HttpUploadServerHandler.class);

    //body最大长度 100M
    public static final int MAX_BODY_SIZE=100*1024*1024;
    
    private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); 

    private HttpRequestJSONObject httpRequestJsonObject=null;
    
    private String cmd="null";
    
	public HttpUploadServerHandler(CmdHandler cmdHandler) {
		super();
		this.cmdHandler = cmdHandler;
	}
	
	static {
        DiskFileUpload.deleteOnExitTemporaryFile = false; // should delete file on exit (in normal exit)
        DiskFileUpload.baseDirectory = null; // system temp directory
        DiskAttribute.deleteOnExitTemporaryFile = false; // should delete file on // exit (in normal exit)
        DiskAttribute.baseDirectory = null; // system temp directory
    }
	
	//--------------ccx user begin-----------
	public CmdHandler cmdHandler;
	
	public static final String CTX_USER="ctx_user";
	public static final String CTX_START_TIME="ctx_start_time";
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.debug("session removed:"+ctx.channel().toString());
		User sender=(User)ctx.attr(AttributeKey.valueOf(CTX_USER)).get();
		cmdHandler.sessionRemoved(sender,this);
		if(httpRequestJsonObject!=null&&!httpRequestJsonObject.isInHandler()){
			//没进到业务逻辑处理,则断线时回收。进到业务逻辑里，则由业务逻辑线程回收
			httpRequestJsonObject.release();
		}
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.debug("session created:"+ctx.channel().toString());
		User sender=new User(ctx,this);
		ctx.attr(AttributeKey.valueOf(CTX_USER)).set(sender);
		ctx.attr(AttributeKey.valueOf(CTX_START_TIME)).set(System.currentTimeMillis());
		cmdHandler.sessionCreated(sender,this);
		super.channelActive(ctx);
	}
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	cmdHandler.caughtDecodeException(getUser(ctx), cause);
    }
	
	public User getUser(ChannelHandlerContext ctx){
		return (User)ctx.attr(AttributeKey.valueOf(CTX_USER)).get();
	}
	//------------ccx user end------------		
		

    @Override
    public void messageReceived(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
    	
    	try {
    		if (msg instanceof HttpRequest) {
            	httpRequestJsonObject=new HttpRequestJSONObject();
            	HttpRequest request = (HttpRequest) msg;
            	httpRequestJsonObject.setHttpRequest(request);
                httpRequestJsonObject.setHttpUri(request.getUri());
                httpRequestJsonObject.setHttpMethod(request.getMethod().toString());
                if(request.getMethod()!=HttpMethod.GET&&request.getMethod()!=HttpMethod.POST){
                	getUser(ctx).sendAndDisconnect(null);
                	return;
                }
                for (Entry<String, String> entry : request.headers()) {
                    httpRequestJsonObject.getHttpHeaders().put(entry.getKey(), entry.getValue());
                }
                
                Set<Cookie> cookies;
                String value = request.headers().get(HttpHeaders.Names.COOKIE);
                if (value != null) {
                    cookies = CookieDecoder.decode(value);
                    for (Cookie cookie : cookies) {
                    	httpRequestJsonObject.getHttpCookies().put(cookie.getName(), cookie.getValue());
                    }
                }
               
                QueryStringDecoder decoderQuery = new QueryStringDecoder(request.getUri());
                Map<String, List<String>> uriAttributes = decoderQuery.parameters();
                for (Entry<String, List<String>> attr: uriAttributes.entrySet()) {
                    for (String attrVal: attr.getValue()) {
                    	httpRequestJsonObject.put(attr.getKey(), attrVal);
                    }
                }
                cmd=decoderQuery.path();
                // if GET Method: should not try to create a HttpPostRequestDecoder
                if (request.getMethod().equals(HttpMethod.GET)) {
                    // GET Method: should not try to create a HttpPostRequestDecoder
                    // So stop here
                	httpRequestJsonObject.setHasInHandler(true);
                    cmdHandler.handleRequest(getUser(ctx), cmd, httpRequestJsonObject);
                    return;
                }
                ByteBuf buffer_body=ctx.alloc().heapBuffer();
            	httpRequestJsonObject.setHttpBodyBuf(buffer_body);
            	HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(factory, request);
            	httpRequestJsonObject.setDecoder(decoder);
            	
//            	boolean close = request.headers().contains(CONNECTION, HttpHeaders.Values.CLOSE, true)
//                        || request.getProtocolVersion().equals(HttpVersion.HTTP_1_0)
//                        && !request.headers().contains(CONNECTION, HttpHeaders.Values.KEEP_ALIVE, true);
            }
           // check if the decoder was constructed before
            // if not it handles the form get
            HttpPostRequestDecoder decoder=httpRequestJsonObject.getDecoder();
            if (decoder != null) {
            	ByteBuf buffer_body=httpRequestJsonObject.getHttpBodyBuf();
                if (msg instanceof HttpContent) {
                    // New chunk is received
                    HttpContent chunk = (HttpContent) msg;
                    ByteBuf thiscontent=chunk.content();
                    thiscontent.markReaderIndex();
        			if (thiscontent.isReadable()) {
        				buffer_body.writeBytes(thiscontent);
        			}
        			thiscontent.resetReaderIndex();
        			decoder.offer(chunk);
                    if(buffer_body.readableBytes()>MAX_BODY_SIZE){
                    	JSONObject rs=new JSONObject();
                    	rs.put("error_code", -1);
                    	rs.put("msg", "upload file too big,connection closed!");
                    	getUser(ctx).sendAndDisconnect(rs);
                    	return;
                    }
                    if (chunk instanceof LastHttpContent) {
                    	List<InterfaceHttpData> list=decoder.getBodyHttpDatas();
                    	if(list!=null&&list.size()>0){
                    		for(InterfaceHttpData data:list){
                    			writeHttpData(data);
                    		}
                    	}
                    	httpRequestJsonObject.setHasInHandler(true);
                        cmdHandler.handleRequest(getUser(ctx), cmd, httpRequestJsonObject);
                    }
                }
            }
		} catch (Exception e) {
			throw e;
		}finally{
			//http解码器中，已经回收了
//			ReferenceCountUtil.release(msg);
		}
    }

    private void writeHttpData(InterfaceHttpData data) {
        if (data.getHttpDataType() == HttpDataType.Attribute) {
            Attribute attribute = (Attribute) data;
            String value;
            try {
                value = attribute.getValue();
                httpRequestJsonObject.put(attribute.getName(), value);
            } catch (IOException e1) {
            	logger.error("BODY Attribute: " + attribute.getHttpDataType().name() + ": "
                        + attribute.getName() + " Error while reading value: " + e1.getMessage() );
                logger.error(e1.getMessage(),e1);
                return;
            }
        } else {
            if (data.getHttpDataType() == HttpDataType.FileUpload) {
                FileUpload fileUpload = (FileUpload) data;
                if (fileUpload.isCompleted()) {
                	
                	httpRequestJsonObject.getHttpFileUploads().add(fileUpload);
                    // fileUpload.isInMemory();// tells if the file is in Memory
                    // or on File
                    // fileUpload.renameTo(dest); // enable to move into another
                    // File dest
                    // decoder.removeFileUploadFromClean(fileUpload); //remove
                    // the File of to delete file
                } else {
                    logger.error("\tFile to be continued but should not!\r\n");
                }
            }
        }
    }

	@Override
	public ChannelFuture sendMsg(User user, Object rs) {
		ByteBuf buf =null;
		String text="";
		HttpResponseJSONObject httpResponseJSONObject = null;
		if(null==rs){
			
		}else if(rs instanceof HttpResponseJSONObject){
			//text or file
			httpResponseJSONObject=(HttpResponseJSONObject) rs;
			if(httpResponseJSONObject.isFile()){
				return sendFile(user.getCtx(), httpResponseJSONObject.getFile(), httpResponseJSONObject.getFileName());
			}else if(httpResponseJSONObject.isText()){
				if(httpResponseJSONObject.getTextContent()!=null){
					text=httpResponseJSONObject.getTextContent();
				}
			}
		}else if(rs instanceof JSON){
			//text
			text=JSON.toJSONString(rs, true);
		}else{
			text=rs.toString();
		}
		buf=copiedBuffer(text, CharsetUtil.UTF_8);
		
        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
        
        response.headers().set(CONTENT_TYPE, "text/plain;charset=UTF-8");
		response.headers().set("Access-Control-Allow-Origin", "*");
		response.headers().set("Access-Control-Allow-Headers","Origin, X-Requested-With, Content-Type, Accept");
//		response.headers().set(CONNECTION, Values.CLOSE);
		response.headers().set(CONTENT_LENGTH, buf.readableBytes());
		
		if(httpResponseJSONObject!=null){
			List<Cookie> cookies=httpResponseJSONObject.getCookies();
			if(cookies.size()>0){
				for(Cookie cookie:cookies){
					response.headers().add(HttpHeaders.Names.SET_COOKIE,ServerCookieEncoder.encode(cookie));
				}
			}
		}
        // Write the response.
        ChannelFuture future=user.getCtx().channel().writeAndFlush(response);
		return future;
	}
	
	public ChannelFuture sendFile(ChannelHandlerContext  ctx,File file,String filename){
		RandomAccessFile raf;
		long fileLength=0;
        try {
            raf = new RandomAccessFile(file, "r");
            fileLength = raf.length();
        } catch (FileNotFoundException e) {
            return sendError(ctx, NOT_FOUND);
        }catch (IOException e) {
        	return sendError(ctx, HttpResponseStatus.NOT_FOUND);
		}
		HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
		response.headers().set(CONNECTION, Values.CLOSE);
		response.headers().set(Names.CONTENT_LENGTH,fileLength );
//		MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
		
//	    response.headers().set(CONTENT_TYPE, mimeTypesMap.getContentType(filename==null?file.getPath():filename));//file.getPath()
		response.headers().set(CONTENT_TYPE, getMimeType(filename==null?file.getPath():filename));
		if(filename!=null){
			response.headers().set("Content-Disposition", "attachment; filename=\"" + filename + "\";");
		}
//		HttpHeaders headers=response.headers();
//		setDateAndCacheHeaders(response, file);
//		if (isKeepAlive(request)) {
//			response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
//		}
	    // Write the initial line and the header.
        ctx.write(response);

        // Write the content.
        ChannelFuture sendFileFuture=ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise());
        sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
            @Override
            public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) {
                if (total < 0) { // total unknown
                    logger.debug("Transfer progress: " + progress);
                } else {
                	logger.debug("Transfer progress: " + progress + " / " + total);
                }
            }

            @Override
            public void operationComplete(ChannelProgressiveFuture future) throws Exception {
            	logger.debug("Transfer complete.");
            }
        });
        // Write the end marker
        ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        return lastContentFuture; 
	}
	
	private  ChannelFuture sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, status, Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.headers().set(CONNECTION, Values.CLOSE);
        // Close the connection as soon as the error message is sent.
        return ctx.writeAndFlush(response);
    }
	
	
	static Map<String, String> mimeMap=new HashMap<String, String>();
	
	public static String getMimeType(String filename){
		if(mimeMap.size()==0){
			synchronized (mimeMap) {
				if(mimeMap.size()==0){
					mimeMap.put("123", "application/vnd.lotus-1-2-3");
					mimeMap.put("3gp", "video/3gpp");
					mimeMap.put("aab", "application/x-authoware-bin");
					mimeMap.put("aam", "application/x-authoware-map");
					mimeMap.put("aas", "application/x-authoware-seg");
					mimeMap.put("ai", "application/postscript");
					mimeMap.put("aif", "audio/x-aiff");
					mimeMap.put("aifc", "audio/x-aiff");
					mimeMap.put("aiff", "audio/x-aiff");
					mimeMap.put("als", "audio/X-Alpha5");
					mimeMap.put("amc", "application/x-mpeg");
					mimeMap.put("ani", "application/octet-stream");
					mimeMap.put("asc", "text/plain");
					mimeMap.put("asd", "application/astound");
					mimeMap.put("asf", "video/x-ms-asf");
					mimeMap.put("asn", "application/astound");
					mimeMap.put("asp", "application/x-asap");
					mimeMap.put("asx", "video/x-ms-asf");
					mimeMap.put("au", "audio/basic");
					mimeMap.put("avb", "application/octet-stream");
					mimeMap.put("avi", "video/x-msvideo");
					mimeMap.put("awb", "audio/amr-wb");
					mimeMap.put("bcpio", "application/x-bcpio");
					mimeMap.put("bin", "application/octet-stream");
					mimeMap.put("bld", "application/bld");
					mimeMap.put("bld2", "application/bld2");
					mimeMap.put("bmp", "application/x-MS-bmp");
					mimeMap.put("bpk", "application/octet-stream");
					mimeMap.put("bz2", "application/x-bzip2");
					mimeMap.put("cal", "image/x-cals");
					mimeMap.put("ccn", "application/x-cnc");
					mimeMap.put("cco", "application/x-cocoa");
					mimeMap.put("cdf", "application/x-netcdf");
					mimeMap.put("cgi", "magnus-internal/cgi");
					mimeMap.put("chat", "application/x-chat");
					mimeMap.put("class", "application/octet-stream");
					mimeMap.put("clp", "application/x-msclip");
					mimeMap.put("cmx", "application/x-cmx");
					mimeMap.put("co", "application/x-cult3d-object");
					mimeMap.put("cod", "image/cis-cod");
					mimeMap.put("cpio", "application/x-cpio");
					mimeMap.put("cpt", "application/mac-compactpro");
					mimeMap.put("crd", "application/x-mscardfile");
					mimeMap.put("csh", "application/x-csh");
					mimeMap.put("csm", "chemical/x-csml");
					mimeMap.put("csml", "chemical/x-csml");
					mimeMap.put("css", "text/css");
					mimeMap.put("cur", "application/octet-stream");
					mimeMap.put("dcm", "x-lml/x-evm");
					mimeMap.put("dcr", "application/x-director");
					mimeMap.put("dcx", "image/x-dcx");
					mimeMap.put("dhtml", "text/html");
					mimeMap.put("dir", "application/x-director");
					mimeMap.put("dll", "application/octet-stream");
					mimeMap.put("dmg", "application/octet-stream");
					mimeMap.put("dms", "application/octet-stream");
					mimeMap.put("doc", "application/msword");
					mimeMap.put("dot", "application/x-dot");
					mimeMap.put("dvi", "application/x-dvi");
					mimeMap.put("dwf", "drawing/x-dwf");
					mimeMap.put("dwg", "application/x-autocad");
					mimeMap.put("dxf", "application/x-autocad");
					mimeMap.put("dxr", "application/x-director");
					mimeMap.put("ebk", "application/x-expandedbook");
					mimeMap.put("emb", "chemical/x-embl-dl-nucleotide");
					mimeMap.put("embl", "chemical/x-embl-dl-nucleotide");
					mimeMap.put("eps", "application/postscript");
					mimeMap.put("eri", "image/x-eri");
					mimeMap.put("es", "audio/echospeech");
					mimeMap.put("esl", "audio/echospeech");
					mimeMap.put("etc", "application/x-earthtime");
					mimeMap.put("etx", "text/x-setext");
					mimeMap.put("evm", "x-lml/x-evm");
					mimeMap.put("evy", "application/x-envoy");
					mimeMap.put("exe", "application/octet-stream");
					mimeMap.put("fh4", "image/x-freehand");
					mimeMap.put("fh5", "image/x-freehand");
					mimeMap.put("fhc", "image/x-freehand");
					mimeMap.put("fif", "image/fif");
					mimeMap.put("fm", "application/x-maker");
					mimeMap.put("fpx", "image/x-fpx");
					mimeMap.put("fvi", "video/isivideo");
					mimeMap.put("gau", "chemical/x-gaussian-input");
					mimeMap.put("gca", "application/x-gca-compressed");
					mimeMap.put("gdb", "x-lml/x-gdb");
					mimeMap.put("gif", "image/gif");
					mimeMap.put("gps", "application/x-gps");
					mimeMap.put("gtar", "application/x-gtar");
					mimeMap.put("gz", "application/x-gzip");
					mimeMap.put("hdf", "application/x-hdf");
					mimeMap.put("hdm", "text/x-hdml");
					mimeMap.put("hdml", "text/x-hdml");
					mimeMap.put("hlp", "application/winhlp");
					mimeMap.put("hqx", "application/mac-binhex40");
					mimeMap.put("htm", "text/html");
					mimeMap.put("html", "text/html");
					mimeMap.put("hts", "text/html");
					mimeMap.put("ice", "x-conference/x-cooltalk");
					mimeMap.put("ico", "application/octet-stream");
					mimeMap.put("ief", "image/ief");
					mimeMap.put("ifm", "image/gif");
					mimeMap.put("ifs", "image/ifs");
					mimeMap.put("imy", "audio/melody");
					mimeMap.put("ins", "application/x-NET-Install");
					mimeMap.put("ips", "application/x-ipscript");
					mimeMap.put("ipx", "application/x-ipix");
					mimeMap.put("it", "audio/x-mod");
					mimeMap.put("itz", "audio/x-mod");
					mimeMap.put("ivr", "i-world/i-vrml");
					mimeMap.put("j2k", "image/j2k");
					mimeMap.put("jad", "text/vnd.sun.j2me.app-descriptor");
					mimeMap.put("jam", "application/x-jam");
					mimeMap.put("jar", "application/java-archive");
					mimeMap.put("jnlp", "application/x-java-jnlp-file");
					mimeMap.put("jpe", "image/jpeg");
					mimeMap.put("jpeg", "image/jpeg");
					mimeMap.put("jpg", "image/jpeg");
					mimeMap.put("jpz", "image/jpeg");
					mimeMap.put("js", "application/x-javascript");
					mimeMap.put("jwc", "application/jwc");
					mimeMap.put("kjx", "application/x-kjx");
					mimeMap.put("lak", "x-lml/x-lak");
					mimeMap.put("latex", "application/x-latex");
					mimeMap.put("lcc", "application/fastman");
					mimeMap.put("lcl", "application/x-digitalloca");
					mimeMap.put("lcr", "application/x-digitalloca");
					mimeMap.put("lgh", "application/lgh");
					mimeMap.put("lha", "application/octet-stream");
					mimeMap.put("lml", "x-lml/x-lml");
					mimeMap.put("lmlpack", "x-lml/x-lmlpack");
					mimeMap.put("lsf", "video/x-ms-asf");
					mimeMap.put("lsx", "video/x-ms-asf");
					mimeMap.put("lzh", "application/x-lzh");
					mimeMap.put("m13", "application/x-msmediaview");
					mimeMap.put("m14", "application/x-msmediaview");
					mimeMap.put("m15", "audio/x-mod");
					mimeMap.put("m3u", "audio/x-mpegurl");
					mimeMap.put("m3url", "audio/x-mpegurl");
					mimeMap.put("ma1", "audio/ma1");
					mimeMap.put("ma2", "audio/ma2");
					mimeMap.put("ma3", "audio/ma3");
					mimeMap.put("ma5", "audio/ma5");
					mimeMap.put("man", "application/x-troff-man");
					mimeMap.put("map", "magnus-internal/imagemap");
					mimeMap.put("mbd", "application/mbedlet");
					mimeMap.put("mct", "application/x-mascot");
					mimeMap.put("mdb", "application/x-msaccess");
					mimeMap.put("mdz", "audio/x-mod");
					mimeMap.put("me", "application/x-troff-me");
					mimeMap.put("mel", "text/x-vmel");
					mimeMap.put("mi", "application/x-mif");
					mimeMap.put("mid", "audio/midi");
					mimeMap.put("midi", "audio/midi");
					mimeMap.put("mif", "application/x-mif");
					mimeMap.put("mil", "image/x-cals");
					mimeMap.put("mio", "audio/x-mio");
					mimeMap.put("mmf", "application/x-skt-lbs");
					mimeMap.put("mng", "video/x-mng");
					mimeMap.put("mny", "application/x-msmoney");
					mimeMap.put("moc", "application/x-mocha");
					mimeMap.put("mocha", "application/x-mocha");
					mimeMap.put("mod", "audio/x-mod");
					mimeMap.put("mof", "application/x-yumekara");
					mimeMap.put("mol", "chemical/x-mdl-molfile");
					mimeMap.put("mop", "chemical/x-mopac-input");
					mimeMap.put("mov", "video/quicktime");
					mimeMap.put("movie", "video/x-sgi-movie");
					mimeMap.put("mp2", "audio/x-mpeg");
					mimeMap.put("mp3", "audio/x-mpeg");
					mimeMap.put("mp4", "video/mp4");
					mimeMap.put("mpc", "application/vnd.mpohun.certificate");
					mimeMap.put("mpe", "video/mpeg");
					mimeMap.put("mpeg", "video/mpeg");
					mimeMap.put("mpg", "video/mpeg");
					mimeMap.put("mpg4", "video/mp4");
					mimeMap.put("mpga", "audio/mpeg");
					mimeMap.put("mpn", "application/vnd.mophun.application");
					mimeMap.put("mpp", "application/vnd.ms-project");
					mimeMap.put("mps", "application/x-mapserver");
					mimeMap.put("mrl", "text/x-mrml");
					mimeMap.put("mrm", "application/x-mrm");
					mimeMap.put("ms", "application/x-troff-ms");
					mimeMap.put("mts", "application/metastream");
					mimeMap.put("mtx", "application/metastream");
					mimeMap.put("mtz", "application/metastream");
					mimeMap.put("mzv", "application/metastream");
					mimeMap.put("nar", "application/zip");
					mimeMap.put("nbmp", "image/nbmp");
					mimeMap.put("nc", "application/x-netcdf");
					mimeMap.put("ndb", "x-lml/x-ndb");
					mimeMap.put("ndwn", "application/ndwn");
					mimeMap.put("nif", "application/x-nif");
					mimeMap.put("nmz", "application/x-scream");
					mimeMap.put("nokia-op-logo", "image/vnd.nok-oplogo-color");
					mimeMap.put("npx", "application/x-netfpx");
					mimeMap.put("nsnd", "audio/nsnd");
					mimeMap.put("nva", "application/x-neva1");
					mimeMap.put("oda", "application/oda");
					mimeMap.put("oom", "application/x-AtlasMate-Plugin");
					mimeMap.put("pac", "audio/x-pac");
					mimeMap.put("pae", "audio/x-epac");
					mimeMap.put("pan", "application/x-pan");
					mimeMap.put("pbm", "image/x-portable-bitmap");
					mimeMap.put("pcx", "image/x-pcx");
					mimeMap.put("pda", "image/x-pda");
					mimeMap.put("pdb", "chemical/x-pdb");
					mimeMap.put("pdf", "application/pdf");
					mimeMap.put("pfr", "application/font-tdpfr");
					mimeMap.put("pgm", "image/x-portable-graymap");
					mimeMap.put("pict", "image/x-pict");
					mimeMap.put("pm", "application/x-perl");
					mimeMap.put("pmd", "application/x-pmd");
					mimeMap.put("png", "image/png");
					mimeMap.put("pnm", "image/x-portable-anymap");
					mimeMap.put("pnz", "image/png");
					mimeMap.put("pot", "application/vnd.ms-powerpoint");
					mimeMap.put("ppm", "image/x-portable-pixmap");
					mimeMap.put("pps", "application/vnd.ms-powerpoint");
					mimeMap.put("ppt", "application/vnd.ms-powerpoint");
					mimeMap.put("pqf", "application/x-cprplayer");
					mimeMap.put("pqi", "application/cprplayer");
					mimeMap.put("prc", "application/x-prc");
					mimeMap.put("proxy", "application/x-ns-proxy-autoconfig");
					mimeMap.put("ps", "application/postscript");
					mimeMap.put("ptlk", "application/listenup");
					mimeMap.put("pub", "application/x-mspublisher");
					mimeMap.put("pvx", "video/x-pv-pvx");
					mimeMap.put("qcp", "audio/vnd.qcelp");
					mimeMap.put("qt", "video/quicktime");
					mimeMap.put("qti", "image/x-quicktime");
					mimeMap.put("qtif", "image/x-quicktime");
					mimeMap.put("r3t", "text/vnd.rn-realtext3d");
					mimeMap.put("ra", "audio/x-pn-realaudio");
					mimeMap.put("ram", "audio/x-pn-realaudio");
					mimeMap.put("rar", "application/x-rar-compressed");
					mimeMap.put("ras", "image/x-cmu-raster");
					mimeMap.put("rdf", "application/rdf+xml");
					mimeMap.put("rf", "image/vnd.rn-realflash");
					mimeMap.put("rgb", "image/x-rgb");
					mimeMap.put("rlf", "application/x-richlink");
					mimeMap.put("rm", "audio/x-pn-realaudio");
					mimeMap.put("rmf", "audio/x-rmf");
					mimeMap.put("rmm", "audio/x-pn-realaudio");
					mimeMap.put("rmvb", "audio/x-pn-realaudio");
					mimeMap.put("rnx", "application/vnd.rn-realplayer");
					mimeMap.put("roff", "application/x-troff");
					mimeMap.put("rp", "image/vnd.rn-realpix");
					mimeMap.put("rpm", "audio/x-pn-realaudio-plugin");
					mimeMap.put("rt", "text/vnd.rn-realtext");
					mimeMap.put("rte", "x-lml/x-gps");
					mimeMap.put("rtf", "application/rtf");
					mimeMap.put("rtg", "application/metastream");
					mimeMap.put("rtx", "text/richtext");
					mimeMap.put("rv", "video/vnd.rn-realvideo");
					mimeMap.put("rwc", "application/x-rogerwilco");
					mimeMap.put("s3m", "audio/x-mod");
					mimeMap.put("s3z", "audio/x-mod");
					mimeMap.put("sca", "application/x-supercard");
					mimeMap.put("scd", "application/x-msschedule");
					mimeMap.put("sdf", "application/e-score");
					mimeMap.put("sea", "application/x-stuffit");
					mimeMap.put("sgm", "text/x-sgml");
					mimeMap.put("sgml", "text/x-sgml");
					mimeMap.put("sh", "application/x-sh");
					mimeMap.put("shar", "application/x-shar");
					mimeMap.put("shtml", "magnus-internal/parsed-html");
					mimeMap.put("shw", "application/presentations");
					mimeMap.put("si6", "image/si6");
					mimeMap.put("si7", "image/vnd.stiwap.sis");
					mimeMap.put("si9", "image/vnd.lgtwap.sis");
					mimeMap.put("sis", "application/vnd.symbian.install");
					mimeMap.put("sit", "application/x-stuffit");
					mimeMap.put("skd", "application/x-Koan");
					mimeMap.put("skm", "application/x-Koan");
					mimeMap.put("skp", "application/x-Koan");
					mimeMap.put("skt", "application/x-Koan");
					mimeMap.put("slc", "application/x-salsa");
					mimeMap.put("smd", "audio/x-smd");
					mimeMap.put("smi", "application/smil");
					mimeMap.put("smil", "application/smil");
					mimeMap.put("smp", "application/studiom");
					mimeMap.put("smz", "audio/x-smd");
					mimeMap.put("snd", "audio/basic");
					mimeMap.put("spc", "text/x-speech");
					mimeMap.put("spl", "application/futuresplash");
					mimeMap.put("spr", "application/x-sprite");
					mimeMap.put("sprite", "application/x-sprite");
					mimeMap.put("spt", "application/x-spt");
					mimeMap.put("src", "application/x-wais-source");
					mimeMap.put("stk", "application/hyperstudio");
					mimeMap.put("stm", "audio/x-mod");
					mimeMap.put("sv4cpio", "application/x-sv4cpio");
					mimeMap.put("sv4crc", "application/x-sv4crc");
					mimeMap.put("svf", "image/vnd");
					mimeMap.put("svg", "image/svg-xml");
					mimeMap.put("svh", "image/svh");
					mimeMap.put("svr", "x-world/x-svr");
					mimeMap.put("swf", "application/x-shockwave-flash");
					mimeMap.put("swfl", "application/x-shockwave-flash");
					mimeMap.put("t", "application/x-troff");
					mimeMap.put("tad", "application/octet-stream");
					mimeMap.put("talk", "text/x-speech");
					mimeMap.put("tar", "application/x-tar");
					mimeMap.put("taz", "application/x-tar");
					mimeMap.put("tbp", "application/x-timbuktu");
					mimeMap.put("tbt", "application/x-timbuktu");
					mimeMap.put("tcl", "application/x-tcl");
					mimeMap.put("tex", "application/x-tex");
					mimeMap.put("texi", "application/x-texinfo");
					mimeMap.put("texinfo", "application/x-texinfo");
					mimeMap.put("tgz", "application/x-tar");
					mimeMap.put("thm", "application/vnd.eri.thm");
					mimeMap.put("tif", "image/tiff");
					mimeMap.put("tiff", "image/tiff");
					mimeMap.put("tki", "application/x-tkined");
					mimeMap.put("tkined", "application/x-tkined");
					mimeMap.put("toc", "application/toc");
					mimeMap.put("toy", "image/toy");
					mimeMap.put("tr", "application/x-troff");
					mimeMap.put("trk", "x-lml/x-gps");
					mimeMap.put("trm", "application/x-msterminal");
					mimeMap.put("tsi", "audio/tsplayer");
					mimeMap.put("tsp", "application/dsptype");
					mimeMap.put("tsv", "text/tab-separated-values");
					mimeMap.put("ttf", "application/octet-stream");
					mimeMap.put("ttz", "application/t-time");
					mimeMap.put("txt", "text/plain");
					mimeMap.put("ult", "audio/x-mod");
					mimeMap.put("ustar", "application/x-ustar");
					mimeMap.put("uu", "application/x-uuencode");
					mimeMap.put("uue", "application/x-uuencode");
					mimeMap.put("vcd", "application/x-cdlink");
					mimeMap.put("vcf", "text/x-vcard");
					mimeMap.put("vdo", "video/vdo");
					mimeMap.put("vib", "audio/vib");
					mimeMap.put("viv", "video/vivo");
					mimeMap.put("vivo", "video/vivo");
					mimeMap.put("vmd", "application/vocaltec-media-desc");
					mimeMap.put("vmf", "application/vocaltec-media-file");
					mimeMap.put("vmi", "application/x-dreamcast-vms-info");
					mimeMap.put("vms", "application/x-dreamcast-vms");
					mimeMap.put("vox", "audio/voxware");
					mimeMap.put("vqe", "audio/x-twinvq-plugin");
					mimeMap.put("vqf", "audio/x-twinvq");
					mimeMap.put("vql", "audio/x-twinvq");
					mimeMap.put("vre", "x-world/x-vream");
					mimeMap.put("vrml", "x-world/x-vrml");
					mimeMap.put("vrt", "x-world/x-vrt");
					mimeMap.put("vrw", "x-world/x-vream");
					mimeMap.put("vts", "workbook/formulaone");
					mimeMap.put("wav", "audio/x-wav");
					mimeMap.put("wax", "audio/x-ms-wax");
					mimeMap.put("wbmp", "image/vnd.wap.wbmp");
					mimeMap.put("web", "application/vnd.xara");
					mimeMap.put("wi", "image/wavelet");
					mimeMap.put("wis", "application/x-InstallShield");
					mimeMap.put("wm", "video/x-ms-wm");
					mimeMap.put("wma", "audio/x-ms-wma");
					mimeMap.put("wmd", "application/x-ms-wmd");
					mimeMap.put("wmf", "application/x-msmetafile");
					mimeMap.put("wml", "text/vnd.wap.wml");
					mimeMap.put("wmlc", "application/vnd.wap.wmlc");
					mimeMap.put("wmls", "text/vnd.wap.wmlscript");
					mimeMap.put("wmlsc", "application/vnd.wap.wmlscriptc");
					mimeMap.put("wmlscript", "text/vnd.wap.wmlscript");
					mimeMap.put("wmv", "audio/x-ms-wmv");
					mimeMap.put("wmx", "video/x-ms-wmx");
					mimeMap.put("wmz", "application/x-ms-wmz");
					mimeMap.put("wpng", "image/x-up-wpng");
					mimeMap.put("wpt", "x-lml/x-gps");
					mimeMap.put("wri", "application/x-mswrite");
					mimeMap.put("wrl", "x-world/x-vrml");
					mimeMap.put("wrz", "x-world/x-vrml");
					mimeMap.put("ws", "text/vnd.wap.wmlscript");
					mimeMap.put("wsc", "application/vnd.wap.wmlscriptc");
					mimeMap.put("wv", "video/wavelet");
					mimeMap.put("wvx", "video/x-ms-wvx");
					mimeMap.put("wxl", "application/x-wxl");
					mimeMap.put("x-gzip", "application/x-gzip");
					mimeMap.put("xar", "application/vnd.xara");
					mimeMap.put("xbm", "image/x-xbitmap");
					mimeMap.put("xdm", "application/x-xdma");
					mimeMap.put("xdma", "application/x-xdma");
					mimeMap.put("xdw", "application/vnd.fujixerox.docuworks");
					mimeMap.put("xht", "application/xhtml+xml");
					mimeMap.put("xhtm", "application/xhtml+xml");
					mimeMap.put("xhtml", "application/xhtml+xml");
					mimeMap.put("xla", "application/vnd.ms-excel");
					mimeMap.put("xlc", "application/vnd.ms-excel");
					mimeMap.put("xll", "application/x-excel");
					mimeMap.put("xlm", "application/vnd.ms-excel");
					mimeMap.put("xls", "application/vnd.ms-excel");
					mimeMap.put("xlt", "application/vnd.ms-excel");
					mimeMap.put("xlw", "application/vnd.ms-excel");
					mimeMap.put("xm", "audio/x-mod");
					mimeMap.put("xml", "text/xml");
					mimeMap.put("xmz", "audio/x-mod");
					mimeMap.put("xpi", "application/x-xpinstall");
					mimeMap.put("xpm", "image/x-xpixmap");
					mimeMap.put("xsit", "text/xml");
					mimeMap.put("xsl", "text/xml");
					mimeMap.put("xul", "text/xul");
					mimeMap.put("xwd", "image/x-xwindowdump");
					mimeMap.put("xyz", "chemical/x-pdb");
					mimeMap.put("yz1", "application/x-yz1");
					mimeMap.put("z", "application/x-compress");
					mimeMap.put("zac", "application/x-zaurus-zac");
					mimeMap.put("zip", "application/zip");
				}
			}
		}
		if(null!=filename){
			int index=filename.lastIndexOf(".");
			if(index!=-1){
				String tailprix=filename.substring(index+1);
				String type=mimeMap.get(tailprix);
				if(type!=null){
					return type;
				}
			}
		}
		return "application/octet-stream";
	}
	
//	public static void main(String[] args) {
//		File f=ClassPathUtil.findFile("Mime-Type.txt");
//		Map<String, String> map=FileUtil.readFile(f.getPath(), new LinkedHashMap(), "UTF-8");
//		for(String key:map.keySet()){
//			System.out.println("mimeMap.put(\""+key+"\", \""+map.get(key)+"\");");
//		}
//	}
	
}
