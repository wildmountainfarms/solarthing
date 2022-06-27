package me.retrodaredevil.solarthing.packets.security.crypto;

import com.fasterxml.jackson.core.Base64Variants;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.annotations.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@UtilityClass
public final class SenderUtil {
	private SenderUtil(){ throw new UnsupportedOperationException(); }

	private static final Set<Character> invalidCharacterSet = new HashSet<>(Arrays.asList('/', '\\', ':', '*', '?', '"', '<', '>', '|'));

	public static @Nullable String getInvalidSenderNameReason(String sender){
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
				// The charset doesn't matter too much here, but we use the UTF-8 charset because it guarantees that all characters can be encoded in a predictable way
				return "sender had invalid character at index: " + i + " base64 sender: " + Base64Variants.getDefaultVariant().encode(sender.getBytes(StandardCharsets.UTF_8));
			}
		}
		return null;
	}
}
