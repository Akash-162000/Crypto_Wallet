import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;

public class StringUtil {
	
	public static byte[] apply_ECDSA_Sign(PrivateKey private_key , String input)
	{
		
		Signature dsa;
		
		byte[] output = new byte[0];
		
		try {
			
			dsa = Signature.getInstance("ECDSA" , "BC");
			dsa.initSign(private_key);			 
			
			dsa.update(input.getBytes());
			
			output = dsa.sign();
			
			
		}
		
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
		return output;
	}
	
	public static boolean verify_ECDSA_Sign(PublicKey public_key , String data, byte[] signature)
	{
		try {
			Signature sign = Signature.getInstance("ECDSA" , "BC");
			sign.initVerify(public_key);
			sign.update(data.getBytes());
			
			return sign.verify(signature);
		}
		
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String get_string_from_key(Key key)
	{
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}
	
	public static String get_merkle_root(ArrayList<Transaction> txns)
	{
		int count = txns.size();
		
		ArrayList<String> previous_tree_layer = new ArrayList<String>();
		
		for(Transaction txn : txns)
		{
			previous_tree_layer.add(txn.transactionId);
		}
		
		ArrayList<String> tree_layer = previous_tree_layer;
		
		while(count > 1)
		{
			tree_layer = new ArrayList<String>();
			
			for(int i=1 ; i< previous_tree_layer.size(); i++)
			{
				tree_layer.add(Sha256.apply_sha256(previous_tree_layer.get(i-1) + previous_tree_layer.get(i)));
				
			}
			
			count = tree_layer.size();
			previous_tree_layer = tree_layer;
			
		}
		
		String merkle_root = (tree_layer.size() == 1)? tree_layer.get(0) : "";
		
		return merkle_root;
	}
	
	


}
