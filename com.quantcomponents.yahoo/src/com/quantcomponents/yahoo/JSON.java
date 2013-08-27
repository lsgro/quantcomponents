package com.quantcomponents.yahoo;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class JSON {
	
	static abstract class Token {
		String value;
		private Token(String value) {
			this.value = value;
		}
		String getValue() {
			return value;
		}
		public String toString() {
			return getValue();
		}
	}
	
	static final class StringToken extends Token {
		StringToken(String value) { super(value); }
	}
	static final Token COLON = new Token(":") {};
	static final Token OPEN_BRACKET = new Token("[") {};
	static final Token CLOSED_BRACKET = new Token("]") {};
	static final Token OPEN_BRACE = new Token("{") {};
	static final Token CLOSED_BRACE = new Token("}") {};
	
	static class Lexer {
		private Token lastToken;
		private StringBuilder buffer;
		private Reader input;
		
		Lexer(Reader input) {
			this.input = input;
		}
		
		Token next() throws IOException, JSONException {
			for (;;) {
				if (lastToken != null) {
					return returnAndResetLastToken();
				}
				int c = input.read();
				if (c == -1) {
					if (buffer != null) {
						return new StringToken(returnAndResetBuffer());
					} else {
						return null;
					}
				}
				char ch = (char) c;
				switch (ch) {
				case ' ': case '\t': case '\n': case '\r': case '\f': case ',':
					if (buffer != null) {
						return new StringToken(returnAndResetBuffer());
					}
					break; // ignore blanks
				case ':': return lastTokenOrBuffer(COLON); 
				case '[': return lastTokenOrBuffer(OPEN_BRACKET); 
				case ']': return lastTokenOrBuffer(CLOSED_BRACKET); 
				case '{': return lastTokenOrBuffer(OPEN_BRACE);
				case '}': return lastTokenOrBuffer(CLOSED_BRACE); 
				case '"':return new StringToken(parseQuote('"', input));
				case '\'': return new StringToken(parseQuote('\'', input));
				default:
					if (buffer == null) {
						buffer = new StringBuilder();
					}
					buffer.append(ch);
				}
			}
		}
		
		private static String parseQuote(char quote, Reader is) throws IOException, JSONException {
			StringBuilder b = new StringBuilder();
			for (;;) {
				int c = is.read();
				if (c < 0) {
					throw new JSONException("Unexpected EOF: expecting closing quote");
				}
				char ch = (char) c;
				if (ch == quote) {
					return b.toString();
				} else {
					b.append(ch);
				}
			}
		}
		
		private Token lastTokenOrBuffer(Token last) {
			if (buffer != null) {
				lastToken = last;
				return new StringToken(returnAndResetBuffer());
			} else {
				return last;
			}
		}
		
		private String returnAndResetBuffer() {
			String value = buffer.toString();
			buffer = null;
			return value;
		}
		
		private Token returnAndResetLastToken() {
			Token value = lastToken;
			lastToken = null;
			return value;
		}
	}
	
	public static JSON parse(Reader input) throws IOException, JSONException {
		Lexer lexer = new Lexer(input);
		Token token = lexer.next();
		if (token == OPEN_BRACKET) {
			return JSONArray.parse(lexer);
		} else if (token == OPEN_BRACE) {
			return JSONMap.parse(lexer);
		} else if (token instanceof StringToken){
			return new JSONValue(((StringToken) token).getValue()); 
		} else {
			throw new JSONException("Unexpected token: '" + token + "' at beginning of JSON");
		}
	}

	static JSON parse(Lexer lexer) throws IOException, JSONException {
		return parse(lexer.next(), lexer);
	}
	
	static JSON parse(Token startToken, Lexer lexer) throws IOException, JSONException {
		for (;;) {
			if (startToken == OPEN_BRACKET) {
				return JSONArray.parse(lexer);
			} else if (startToken == OPEN_BRACE) {
				return JSONMap.parse(lexer);
			} else if (startToken instanceof StringToken){
				return new JSONValue(((StringToken) startToken).getValue()); 
			}
		}
	}
	
	public abstract JSON get(Object o);
	public abstract Object getValue();

	static class JSONArray extends JSON {
		private final List<JSON> array;
		JSONArray(List<JSON>  array) {
			this.array = new ArrayList<JSON>(array);
		}
		public List<JSON> getValue() {
			return Collections.unmodifiableList(array);
		}
		public JSON get(Object o) {
			Integer index = (Integer) o;
			return getValue().get(index);
		}
		public static JSON parse(Lexer lexer) throws IOException, JSONException {
			JSONArray json = new JSONArray(new ArrayList<JSON>());
			for (;;) {
				Token token = lexer.next();
				if (token instanceof StringToken) {
					json.array.add(new JSONValue(((StringToken) token).getValue()));
				} else if (token == CLOSED_BRACKET) {
					break;
				} else {
					json.array.add(JSON.parse(token, lexer));
				}
			}
			return json;
		}
	}
	
	static class JSONMap extends JSON {
		private final Map<String, JSON> map;
		JSONMap(Map<String, JSON> map) {
			this.map = new HashMap<String, JSON>(map);
		}
		public Map<String, JSON> getValue(){
			return Collections.unmodifiableMap(map);
		}
		public JSON get(Object o) {
			String key = (String) o;
			return getValue().get(key);
		}
		public static JSON parse(Lexer lexer) throws IOException, JSONException {
			JSONMap json = new JSONMap(new HashMap<String, JSON>());
			
			String key = null;
			JSON value;
			boolean expectingKey = true;
			for (;;) {
				Token token = lexer.next();
				if (token instanceof StringToken && expectingKey) {
					key = token.getValue();
				} else if (token == COLON) {
					expectingKey = false;
				} else if (token == CLOSED_BRACE) {
					break;
				} else {
					value = JSON.parse(token, lexer);
					json.map.put(key, value);
					expectingKey = true;
				} 
			}
			return json;
		}
	}
	
	static class JSONValue extends JSON {
		private final String value;
		JSONValue(String value) {
			this.value = value;
		}
		public String getValue(){
			return value;
		}
		public JSON get(Object o) {
			return this;
		}
	}
}
