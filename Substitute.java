import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Substitute implements SymCipher {

	private byte[] key;
	
	public Substitute() {
		ArrayList<Byte> tempKey = new ArrayList<Byte>();
		for(int i=0;i<256;i++)
			tempKey.add((byte) (127-i));
		Collections.shuffle(tempKey);
		key=new byte[256];
		for(int j=0;j<256;j++)
			key[j]=tempKey.get(j);
	}
	
	public Substitute(byte[] byteKey) {
		key=Arrays.copyOf(byteKey, byteKey.length);
	}

	@Override
	public byte[] getKey() {
		return key;
	}

	@Override
	public byte[] encode(String S) {
		byte[] data=S.getBytes();
		byte[] result=new byte[data.length];
		for(int i=0;i<data.length;i++)
			result[i]=key[data[i]&0xFF];
		return result;
	}

	@Override
	public String decode(byte[] bytes) {
		byte[] data=Arrays.copyOf(bytes, bytes.length);
		byte[] result=new byte[data.length];
		for(int i=0;i<data.length;i++) {
			int index=indexOf(data[i]);
			result[i]=(byte) (index);
		}
		return new String(result);
	}
	
	private int indexOf(byte b) {
		for(int i=0;i<key.length;i++) {
			if(b==key[i])
				return i;
		}
		return -1;
	}

}
