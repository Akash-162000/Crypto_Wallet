import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {
     
	public PrivateKey private_key;
	public PublicKey public_key;
	
	public HashMap<String , Transaction_output> unspent_txn_ops = new HashMap<String , Transaction_output>();
	
	public Wallet() {
		generate_keys();
	}
	
	public float get_balance()
	{
		float total = 0;
		
		for(Map.Entry<String , Transaction_output> item : Blockchain.unspent_txn_ops.entrySet())
		{
			Transaction_output unspent_txn_op = item.getValue();
			
			if(unspent_txn_op.is_mine(public_key))
			{
				unspent_txn_ops.put(unspent_txn_op.id , unspent_txn_op);
				
				total+=unspent_txn_op.value;
			}
			
		}
		
		return total;
	}
	
	public Transaction send_funds(PublicKey reciever , float value)
	{
		if(get_balance() < value)
		{
			System.out.println("Not enough funds to make transaction . Transaction discarded .");
			return null;
		}
		
		ArrayList<Transaction_input> inputs = new ArrayList<Transaction_input>();
		
		float total = 0;
		
		for(Map.Entry<String , Transaction_output> item : unspent_txn_ops.entrySet())
		{
			Transaction_output unspent_txn_op = item.getValue();
			
			total+=unspent_txn_op.value;
			inputs.add(new Transaction_input(unspent_txn_op.id));
			
			if(total>value) break;
			
		}
		
		Transaction new_txn = new Transaction(public_key , reciever , value , inputs );
		
		new_txn.generate_signature(private_key);
		
		for(Transaction_input input : inputs)
		{
			unspent_txn_ops.remove(input.txn_output_id);
		}
		
		return new_txn;
		
	}
	
	public void generate_keys() {
		
		try {
			
			KeyPairGenerator key_gen = KeyPairGenerator.getInstance("ECDSA","BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ec_spec = new ECGenParameterSpec("prime192v1");
			
			key_gen.initialize(ec_spec, random);
			KeyPair key_pair = key_gen.generateKeyPair();
			
			private_key = key_pair.getPrivate();
			public_key = key_pair.getPublic();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
}
