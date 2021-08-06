import java.security.MessageDigest;


public class Sha256 {

	public static String apply_sha256(String input) {
		
		try {
			
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(input.getBytes("UTF-8"));
			
			StringBuilder hex_string = new StringBuilder();
			
			
			for(int i=0 ; i<hash.length ; i++)
			{
				String hex = Integer.toHexString(0xff & hash[i]);
				
				if(hex.length()==1)
				{
					hex_string.append('0');
					//to add the leading zeroes if they are dropped
					
				}
				
				hex_string.append(hex);
			}
			
			return hex_string.toString();
			
		}
		
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
	}
}
