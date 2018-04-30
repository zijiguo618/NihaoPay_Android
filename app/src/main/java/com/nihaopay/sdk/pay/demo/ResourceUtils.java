package com.nihaopay.sdk.pay.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public final class ResourceUtils {

	public static final String DEFAULT_CHARSET = "UTF-8";

	public static InputStream getResourceAsStream(String resource, ClassLoader loader) {

		InputStream in = null;

		if (loader != null) {
			
			in = loader.getResourceAsStream(resource);
		} else {
			
			in = ClassLoader.getSystemResourceAsStream(resource);
		}

		return in;
	}

//	public static String getResourceAsString(String resource, ClassLoader loader) throws IOException {
//		InputStream in = null;
//		InputStreamReader reader = null;
//		try {
//			in = getResourceAsStream(resource, loader);
//			reader = new InputStreamReader(in);
//			StringBuffer buffer = new StringBuffer();
//			char[] c = new char[1024];
//			int size;
//			while ((size = reader.read(c)) != -1) {
//				buffer.append(c, 0, size);
//			}
//			return buffer.toString();
//		} finally {
//			IOUtils.close(reader);
//			IOUtils.close(in);
//		}
//	}

	public static Properties getResourceAsProperty(String resource, ClassLoader loader, String charset) {
		Properties props = new Properties();
		InputStream in = null;
		try {
			in = getResourceAsStream(resource, loader);
			props.load(new BufferedReader(new InputStreamReader(in, charset)));
			return props;
		} catch (IOException e) {
			Logger.error("Error load resource as property"+e.getMessage());
			throw new RuntimeException(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					Logger.error("Error closing InputStream for resource: " + resource+" "+e.getMessage());
				}
			}
		}
	}

	public static Properties getResourceAsProperty(String resource, ClassLoader loader) {
		return getResourceAsProperty(resource, loader, DEFAULT_CHARSET);
	}

	public static Properties getResourceAsProperty(String resource) {
		return getResourceAsProperty(resource, null);
	}

}