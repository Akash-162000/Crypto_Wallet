import java.util.Date;
import java.util.ArrayList;

public class Block {

	String my_hash;
	String prev_hash;
	String merkle_root;
	
	public ArrayList<Transaction> txns = new ArrayList<Transaction>();
	
	
	private long time_created;
	private int nonce;
	
	public Block(String prev_hash) {
		
	
		this.prev_hash = prev_hash;
		this.time_created = new Date().getTime();
		this.my_hash = hash_calculate();
		}	

	public String hash_calculate() {
		String hash = Sha256.apply_sha256(prev_hash+
				                          Long.toString(time_created)+
				                          Integer.toString(nonce)+
				                          merkle_root);
		
		return hash;
	}
	
	public void mine_block(int count)
	{
		merkle_root = StringUtil.get_merkle_root(txns);
		String target = new String(new char[count]).replace('\0','0');
		// \0 is a null character
		
		while(!my_hash.substring(0,count).equals(target))
		{
			nonce++;
			my_hash = hash_calculate();
						
		}
		
		
		System.out.println("Block Mined!!! : " + my_hash);
	}
	
	public boolean add_transaction(Transaction txn)
	{
		if(txn == null) return false;
		
		if(prev_hash!= "0")
		{
			if(txn.process_transaction()!= true)
			{
				System.out.println("Transaction failed to process . Discarded ");
				return false;
			}
		}
		
		txns.add(txn);
		
		System.out.println("Transaction Successfully added to block");
		
		return true;
	}
}

