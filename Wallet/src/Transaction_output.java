import java.security.PublicKey;

public class Transaction_output {
	
	public String id;
	public PublicKey receiver;
	public float value;
	public String parent_txn_id;
	
	public Transaction_output(PublicKey receiver , float value , String parent_txn_id)
	{
		this.receiver = receiver;
		this.value = value;
		this.parent_txn_id = parent_txn_id;
		this.id = Sha256.apply_sha256(StringUtil.get_string_from_key(receiver)+
				Float.toString(value) +
				parent_txn_id);
				
	}
	
	public boolean is_mine(PublicKey public_key)
	{
		return (public_key == receiver);
	}

}
