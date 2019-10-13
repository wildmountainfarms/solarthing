package me.retrodaredevil.solarthing.packets.security.crypto;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

public final class SenderUtil {
	private SenderUtil(){ throw new UnsupportedOperationException(); }
	
	private static final Set<Character> invalidCharacterSet = new HashSet<>(Arrays.asList('/', '\\', ':', '*', '?', '"', '<', '>', '|'));
	
	public static String getInvalidSenderNameReason(String sender){
		if(sender.equals(".")){
			return "sender cannot equal '.'";
		}
		if(sender.equals("..")){
			return "sender cannot equal '..'";
		}
		if(sender.equals("con") || sender.startsWith("con.")){
			return "sender cannot collide with MS-DOS 'con'";
		}
		for (int i = 0; i < sender.length(); i++) {
			char c = sender.charAt(i);
			if (c < 0x20 || c > 0x7e || invalidCharacterSet.contains(c)) {
				return "sender had invalid character at index: " + i + " base64 sender: " + Base64.getEncoder().encodeToString(sender.getBytes());
			}
		}
		return null;
	}
}
