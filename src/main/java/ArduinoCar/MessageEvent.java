package ArduinoCar;

public class MessageEvent {

	enum Type {
		Char,
		Bytes,
		String
	}
	
	private String message;
	private byte[] bytes;
	private char character;
	final Type type;
	
	public MessageEvent(String message) {
		this.message = message;
		this.type = Type.String;
	}
	
	public MessageEvent(byte[] bytes) {
		this.bytes = bytes;
		this.type = Type.Bytes;
	}
	
	public MessageEvent(char character) {
		this.character = character;
		this.type = Type.Char;
	}

	public String getMessage() {
		return this.message;
	}
	
	public char getChar() {
		return this.character;
	}
	
	public byte[] getBytes() {
		return bytes;
	}
	
}
