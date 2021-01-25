import java.util.Arrays;
import java.util.Random;

public class Add128 implements SymCipher {
	
	private byte[] key;

	public Add128() {
		Random r=new Random();
		key = new byte[128];
		r.nextBytes(key);
	}
	
	public Add128(byte[] byteKey) {
		key=Arrays.copyOf(byteKey, byteKey.length);
	}

	@Override
	public byte[] getKey() {
		return key;
	}

	@Override
	public byte[] encode(String S) {
	    byte[] data = S.getBytes();
	    byte[] result=new byte[data.length];
	    for (int i=0; i<data.length; i++) {
	    	result[i] = (byte) (data[i]+key[i % key.length]);
	    }
	    return result;
	}

	@Override
	public String decode(byte[] bytes) {
	    byte[] data = Arrays.copyOf(bytes, bytes.length);
	    byte[] result=new byte[bytes.length];
	    for (int i=0; i<data.length; i++) {
	    	result[i] = (byte) (data[i]-key[i % key.length]);
	    }
	    
	    return new String(result);
	}

}
